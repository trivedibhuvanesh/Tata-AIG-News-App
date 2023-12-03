package com.example.tataaignewsapp.data.model

import androidx.room.TypeConverter

class SourceConverter {
    @TypeConverter
    fun fromSource(source: Source?): String {
        return "${source?.id ?: ""},${source?.name ?: ""}"

    }

    @TypeConverter
    fun toSource(source: String): Source {
        val sourceId = source.substringBefore(",")
        val sourceName = source.substringAfter(",")
        return Source(
            id = sourceId,
            name = sourceName
        )
    }
}