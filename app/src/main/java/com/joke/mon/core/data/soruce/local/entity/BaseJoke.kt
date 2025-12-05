package com.joke.mon.core.data.soruce.local.entity

open class BaseJoke
(
    open val key: String,
    open val content: String,
    open val categoryId: Int,
    open val createdAt: String,
    open val keyword: String,
    open val isSaved: Boolean
)