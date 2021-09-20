package com.fx.exchange_experiment.core.db

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery

//@author Paul Okeke
abstract class BaseDao<T> {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertItems(vararg items: T)

    @Update
    abstract suspend fun updateItems(vararg items: T)

    @Delete
    abstract suspend fun deleteItems(vararg items: T)

    @RawQuery
    protected abstract suspend fun deleteAll(query: SupportSQLiteQuery): Int

}