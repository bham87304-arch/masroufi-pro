package com.masroufi.pro.data.model

/**
 * Represents a currency used in the application.
 */
data class Currency(val code: String, val name: String, val symbol: String) {
    companion object {
        fun getSupportedCurrencies(): List<Currency> = listOf(
            Currency("DZD", "Algerian Dinar", "د.ج"),
            Currency("USD", "US Dollar", "$"),
            Currency("EUR", "Euro", "€"),
            Currency("GBP", "British Pound", "£"),
            Currency("SAR", "Saudi Riyal", "ر.س"),
            Currency("AED", "UAE Dirham", "د.إ"),
            Currency("MAD", "Moroccan Dirham", "د.م"),
            Currency("TND", "Tunisian Dinar", "د.ت"),
            Currency("EGP", "Egyptian Pound", "ج.م"),
            Currency("TRY", "Turkish Lira", "₺"),
            Currency("CAD", "Canadian Dollar", "C$"),
            Currency("JPY", "Japanese Yen", "¥"),
            Currency("CNY", "Chinese Yuan", "¥"),
            Currency("INR", "Indian Rupee", "₹")
        )
    }
}
