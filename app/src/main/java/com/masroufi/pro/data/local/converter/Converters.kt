package com.masroufi.pro.data.local.converter

import androidx.room.TypeConverter
import com.masroufi.pro.data.model.TransactionType

class Converters {
    @TypeConverter
    fun fromTransactionType(value: TransactionType): String {
        return value.name
    }

    @TypeConverter
    fun toTransactionType(value: String): TransactionType {
        return enumValueOf<TransactionType>(value)
    }
}
