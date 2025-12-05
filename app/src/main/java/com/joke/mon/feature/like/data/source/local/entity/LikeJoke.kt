package com.joke.mon.feature.like.data.source.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.joke.mon.core.data.soruce.local.entity.BaseJoke

@Entity(tableName = "like_jokes")
data class LikeJoke(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    override val key: String,
    override val content: String,
    override val categoryId: Int,
    override val createdAt: String,
    override val keyword: String,
    override val isSaved: Boolean
) : BaseJoke(key, content, categoryId, createdAt, keyword, isSaved)
