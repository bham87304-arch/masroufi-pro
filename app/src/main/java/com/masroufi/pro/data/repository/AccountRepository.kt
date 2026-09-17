package com.masroufi.pro.data.repository

import com.masroufi.pro.data.local.dao.AccountDao
import com.masroufi.pro.data.local.entity.AccountEntity
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AccountRepository @Inject constructor(
    private val accountDao: AccountDao
) {
    fun getAllAccounts(): Flow<List<AccountEntity>> = accountDao.getAllAccounts()

    suspend fun getAccountById(id: String): AccountEntity? = accountDao.getAccountById(id)

    suspend fun getDefaultAccount(): AccountEntity? = accountDao.getDefaultAccount()

    suspend fun insertAccount(account: AccountEntity) {
        accountDao.insertAccount(account)
    }

    suspend fun updateAccount(account: AccountEntity) {
        accountDao.updateAccount(account)
    }

    suspend fun deleteAccount(id: String) {
        accountDao.deleteAccount(id)
    }
}
