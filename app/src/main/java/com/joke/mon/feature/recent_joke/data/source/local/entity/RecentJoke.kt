package com.joke.mon.feature.recent_joke.data.source.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.joke.mon.core.data.soruce.local.entity.BaseJoke

@Entity(tableName = "recent_jokes")
data class RecentJoke(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    override val key: String,
    override val content: String,
    override val categoryId: Int,
    override val createdAt: String,
    override val keyword: String,
    override val isSaved: Boolean
) : BaseJoke(key, content, categoryId, createdAt, keyword, isSaved)
