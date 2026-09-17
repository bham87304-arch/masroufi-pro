package com.masroufi.pro.data.local.database

import com.masroufi.pro.data.local.dao.AccountDao
import com.masroufi.pro.data.local.dao.CategoryDao
import com.masroufi.pro.data.local.entity.AccountEntity
import com.masroufi.pro.data.local.entity.CategoryEntity
import com.masroufi.pro.data.model.TransactionType
import javax.inject.Inject

class DatabaseSeeder @Inject constructor(
    private val categoryDao: CategoryDao,
    private val accountDao: AccountDao
) {
    suspend fun seedDefaultCategories() {
        if (categoryDao.getCategoryCount() == 0) {
            val categories = listOf(
                // Expense
                CategoryEntity(name = "Food & Shopping", nameAr = "تسوق وأغذية", nameFr = "Alimentation & Courses", icon = "shopping_cart", color = 0xFFE91E63, type = TransactionType.EXPENSE, isDefault = true),
                CategoryEntity(name = "Housing", nameAr = "سكن", nameFr = "Logement", icon = "home", color = 0xFF9C27B0, type = TransactionType.EXPENSE, isDefault = true),
                CategoryEntity(name = "Transport", nameAr = "نقل", nameFr = "Transport", icon = "directions_car", color = 0xFF673AB7, type = TransactionType.EXPENSE, isDefault = true),
                CategoryEntity(name = "Communications", nameAr = "اتصالات", nameFr = "Communications", icon = "phone", color = 0xFFFF5722, type = TransactionType.EXPENSE, isDefault = true),
                CategoryEntity(name = "Restaurants", nameAr = "مطاعم", nameFr = "Restaurants", icon = "restaurant", color = 0xFFFF9800, type = TransactionType.EXPENSE, isDefault = true),
                CategoryEntity(name = "Clothing", nameAr = "ملابس", nameFr = "Vêtements", icon = "checkroom", color = 0xFF795548, type = TransactionType.EXPENSE, isDefault = true),
                CategoryEntity(name = "Health", nameAr = "صحة", nameFr = "Santé", icon = "medical_services", color = 0xFFF44336, type = TransactionType.EXPENSE, isDefault = true),
                CategoryEntity(name = "Entertainment", nameAr = "ترفيه", nameFr = "Divertissement", icon = "sports_esports", color = 0xFF00BCD4, type = TransactionType.EXPENSE, isDefault = true),
                CategoryEntity(name = "Education", nameAr = "تعليم", nameFr = "Éducation", icon = "school", color = 0xFF3F51B5, type = TransactionType.EXPENSE, isDefault = true),
                CategoryEntity(name = "Personal Care", nameAr = "عناية شخصية", nameFr = "Soins personnels", icon = "spa", color = 0xFFE040FB, type = TransactionType.EXPENSE, isDefault = true),
                CategoryEntity(name = "Travel", nameAr = "سفر", nameFr = "Voyages", icon = "flight", color = 0xFF009688, type = TransactionType.EXPENSE, isDefault = true),
                CategoryEntity(name = "Other", nameAr = "أخرى", nameFr = "Autres", icon = "more_horiz", color = 0xFF607D8B, type = TransactionType.EXPENSE, isDefault = true),
                
                // Income
                CategoryEntity(name = "Salary", nameAr = "راتب", nameFr = "Salaire", icon = "payments", color = 0xFF4CAF50, type = TransactionType.INCOME, isDefault = true),
                CategoryEntity(name = "Freelance", nameAr = "عمل حر", nameFr = "Freelance", icon = "work", color = 0xFF8BC34A, type = TransactionType.INCOME, isDefault = true),
                CategoryEntity(name = "Gifts", nameAr = "هدايا", nameFr = "Cadeaux", icon = "card_giftcard", color = 0xFFFF4081, type = TransactionType.INCOME, isDefault = true),
                CategoryEntity(name = "Investments", nameAr = "استثمارات", nameFr = "Investissements", icon = "trending_up", color = 0xFF2196F3, type = TransactionType.INCOME, isDefault = true),
                CategoryEntity(name = "Savings", nameAr = "ادخار", nameFr = "Épargne", icon = "savings", color = 0xFF00E676, type = TransactionType.INCOME, isDefault = true),
                CategoryEntity(name = "Other Income", nameAr = "دخل آخر", nameFr = "Autres revenus", icon = "account_balance", color = 0xFF90A4AE, type = TransactionType.INCOME, isDefault = true)
            )
            categoryDao.insertCategories(categories)
        }
    }

    suspend fun seedDefaultAccount() {
        if (accountDao.getAccountCount() == 0) {
            val account = AccountEntity(
                name = "Cash",
                currency = "DZD",
                icon = "account_balance_wallet",
                color = 0xFF2E7D32,
                initialBalance = 0.0,
                isDefault = true
            )
            accountDao.insertAccount(account)
        }
    }
}
