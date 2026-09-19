package com.masroufi.pro.data.sync

import android.util.Log
import com.masroufi.pro.data.local.dao.AccountDao
import com.masroufi.pro.data.local.dao.CategoryDao
import com.masroufi.pro.data.local.dao.TransactionDao
import com.masroufi.pro.data.local.entity.AccountEntity
import com.masroufi.pro.data.local.entity.CategoryEntity
import com.masroufi.pro.data.local.entity.TransactionEntity
import com.masroufi.pro.data.model.TransactionType
import io.github.jan.supabase.SupabaseClient
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.postgrest.postgrest
import io.github.jan.supabase.postgrest.query.Columns
import kotlinx.serialization.Serializable
import javax.inject.Inject
import javax.inject.Singleton

@Serializable
data class RemoteTransaction(
    val id: String,
    val user_id: String,
    val type: String,
    val amount: Double,
    val currency: String,
    val category_id: String,
    val account_id: String,
    val note: String? = null,
    val date: Long,
    val created_at: Long,
    val updated_at: Long,
    val is_deleted: Boolean = false
)

@Serializable
data class RemoteCategory(
    val id: String,
    val user_id: String,
    val name: String,
    val name_ar: String = "",
    val name_fr: String = "",
    val icon: String,
    val color: Long,
    val type: String,
    val is_default: Boolean = false,
    val sort_order: Int = 0,
    val updated_at: Long = 0,
    val is_deleted: Boolean = false
)

@Serializable
data class RemoteAccount(
    val id: String,
    val user_id: String,
    val name: String,
    val currency: String = "DZD",
    val icon: String = "account_balance_wallet",
    val color: Long = 0xFF2E7D32,
    val initial_balance: Double = 0.0,
    val is_default: Boolean = false,
    val updated_at: Long = 0,
    val is_deleted: Boolean = false
)

data class SyncResult(
    val pushed: Int = 0,
    val pulled: Int = 0,
    val errors: List<String> = emptyList(),
    val success: Boolean = true
)

@Singleton
class SyncManager @Inject constructor(
    private val supabase: SupabaseClient,
    private val transactionDao: TransactionDao,
    private val categoryDao: CategoryDao,
    private val accountDao: AccountDao
) {
    companion object {
        private const val TAG = "SyncManager"
    }

    private fun getUserId(): String? {
        return supabase.auth.currentUserOrNull()?.id
    }

    suspend fun fullSync(): SyncResult {
        val userId = getUserId() ?: return SyncResult(success = false, errors = listOf("Not signed in"))
        Log.d(TAG, "Starting full sync for user: $userId")

        val errors = mutableListOf<String>()
        var pushed = 0
        var pulled = 0

        try {
            // Push local changes first
            val pushResult = pushChanges(userId)
            pushed = pushResult.pushed
            errors.addAll(pushResult.errors)

            // Then pull remote changes
            val pullResult = pullChanges(userId)
            pulled = pullResult.pulled
            errors.addAll(pullResult.errors)

        } catch (e: Exception) {
            Log.e(TAG, "Sync failed", e)
            errors.add("Sync error: ${e.message}")
        }

        val result = SyncResult(pushed = pushed, pulled = pulled, errors = errors, success = errors.isEmpty())
        Log.d(TAG, "Sync complete: pushed=$pushed, pulled=$pulled, errors=${errors.size}")
        return result
    }

    private suspend fun pushChanges(userId: String): SyncResult {
        var pushed = 0
        val errors = mutableListOf<String>()

        // Push transactions
        try {
            val unsyncedTx = transactionDao.getUnsyncedTransactions()
            val unsyncedDeletedTx = transactionDao.getUnsyncedDeletedTransactions()
            val allUnsynced = unsyncedTx + unsyncedDeletedTx

            if (allUnsynced.isNotEmpty()) {
                val remoteTx = allUnsynced.map { it.toRemote(userId) }
                supabase.postgrest["transactions"].upsert(remoteTx)
                allUnsynced.forEach { transactionDao.markTransactionAsSynced(it.id) }
                pushed += allUnsynced.size
                Log.d(TAG, "Pushed ${allUnsynced.size} transactions")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to push transactions", e)
            errors.add("Transaction push failed: ${e.message}")
        }

        // Push categories
        try {
            val unsyncedCat = categoryDao.getUnsyncedCategories()
            if (unsyncedCat.isNotEmpty()) {
                val remoteCat = unsyncedCat.map { it.toRemote(userId) }
                supabase.postgrest["categories"].upsert(remoteCat)
                unsyncedCat.forEach { categoryDao.markAsSynced(it.id) }
                pushed += unsyncedCat.size
                Log.d(TAG, "Pushed ${unsyncedCat.size} categories")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to push categories", e)
            errors.add("Category push failed: ${e.message}")
        }

        // Push accounts
        try {
            val unsyncedAcc = accountDao.getUnsyncedAccounts()
            if (unsyncedAcc.isNotEmpty()) {
                val remoteAcc = unsyncedAcc.map { it.toRemote(userId) }
                supabase.postgrest["accounts"].upsert(remoteAcc)
                unsyncedAcc.forEach { accountDao.markAsSynced(it.id) }
                pushed += unsyncedAcc.size
                Log.d(TAG, "Pushed ${unsyncedAcc.size} accounts")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to push accounts", e)
            errors.add("Account push failed: ${e.message}")
        }

        return SyncResult(pushed = pushed, errors = errors)
    }

    private suspend fun pullChanges(userId: String): SyncResult {
        var pulled = 0
        val errors = mutableListOf<String>()

        // Pull transactions
        try {
            val remoteTx = supabase.postgrest["transactions"]
                .select { filter { eq("user_id", userId) } }
                .decodeList<RemoteTransaction>()

            remoteTx.forEach { remote ->
                val local = transactionDao.getTransactionById(remote.id)
                if (local == null || remote.updated_at > local.updatedAt) {
                    transactionDao.upsertTransaction(remote.toLocal())
                    pulled++
                }
            }
            Log.d(TAG, "Pulled transactions: checked ${remoteTx.size}")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to pull transactions", e)
            errors.add("Transaction pull failed: ${e.message}")
        }

        // Pull categories
        try {
            val remoteCat = supabase.postgrest["categories"]
                .select { filter { eq("user_id", userId) } }
                .decodeList<RemoteCategory>()

            remoteCat.forEach { remote ->
                val local = categoryDao.getCategoryById(remote.id)
                if (local == null || remote.updated_at > local.updatedAt) {
                    categoryDao.upsertCategory(remote.toLocal())
                    pulled++
                }
            }
            Log.d(TAG, "Pulled categories: checked ${remoteCat.size}")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to pull categories", e)
            errors.add("Category pull failed: ${e.message}")
        }

        // Pull accounts
        try {
            val remoteAcc = supabase.postgrest["accounts"]
                .select { filter { eq("user_id", userId) } }
                .decodeList<RemoteAccount>()

            remoteAcc.forEach { remote ->
                val local = accountDao.getAccountById(remote.id)
                if (local == null || remote.updated_at > local.updatedAt) {
                    accountDao.upsertAccount(remote.toLocal())
                    pulled++
                }
            }
            Log.d(TAG, "Pulled accounts: checked ${remoteAcc.size}")
        } catch (e: Exception) {
            Log.e(TAG, "Failed to pull accounts", e)
            errors.add("Account pull failed: ${e.message}")
        }

        return SyncResult(pulled = pulled, errors = errors)
    }

    // Extension functions to convert between local and remote models

    private fun TransactionEntity.toRemote(userId: String) = RemoteTransaction(
        id = id, user_id = userId, type = type.name, amount = amount,
        currency = currency, category_id = categoryId, account_id = accountId,
        note = note, date = date, created_at = createdAt,
        updated_at = updatedAt, is_deleted = isDeleted
    )

    private fun RemoteTransaction.toLocal() = TransactionEntity(
        id = id, type = TransactionType.valueOf(type), amount = amount,
        currency = currency, categoryId = category_id, accountId = account_id,
        note = note, date = date, createdAt = created_at,
        updatedAt = updated_at, isSynced = true, isDeleted = is_deleted
    )

    private fun CategoryEntity.toRemote(userId: String) = RemoteCategory(
        id = id, user_id = userId, name = name, name_ar = nameAr,
        name_fr = nameFr, icon = icon, color = color, type = type.name,
        is_default = isDefault, sort_order = sortOrder,
        updated_at = updatedAt, is_deleted = isDeleted
    )

    private fun RemoteCategory.toLocal() = CategoryEntity(
        id = id, name = name, nameAr = name_ar, nameFr = name_fr,
        icon = icon, color = color, type = TransactionType.valueOf(type),
        isDefault = is_default, sortOrder = sort_order,
        updatedAt = updated_at, isSynced = true, isDeleted = is_deleted
    )

    private fun AccountEntity.toRemote(userId: String) = RemoteAccount(
        id = id, user_id = userId, name = name, currency = currency,
        icon = icon, color = color, initial_balance = initialBalance,
        is_default = isDefault, updated_at = updatedAt, is_deleted = isDeleted
    )

    private fun RemoteAccount.toLocal() = AccountEntity(
        id = id, name = name, currency = currency, icon = icon,
        color = color, initialBalance = initial_balance,
        isDefault = is_default, updatedAt = updated_at,
        isSynced = true, isDeleted = is_deleted
    )
}
