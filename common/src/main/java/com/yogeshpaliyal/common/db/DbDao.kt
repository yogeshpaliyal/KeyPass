package com.yogeshpaliyal.common.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.yogeshpaliyal.common.data.AccountModel
import kotlinx.coroutines.flow.Flow

/*
* @author Yogesh Paliyal
* techpaliyal@gmail.com
* https://techpaliyal.com
* created on 30-01-2021 21:43
*/

@Dao
abstract class DbDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertOrUpdateAccount(vararg accountModel: AccountModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun insertOrUpdateAccount(accountModel: List<AccountModel>)

    @Query("SELECT * FROM account ORDER BY title ASC")
    abstract fun getAllAccounts(): LiveData<List<AccountModel>>

    @Query("SELECT * FROM account ORDER BY title ASC")
    abstract suspend fun getAllAccountsList(): List<AccountModel>

    @Query("SELECT * FROM account WHERE CASE WHEN :tag IS NOT NULL THEN tags = :tag ELSE 1 END AND ((username LIKE '%'||:query||'%' ) OR (title LIKE '%'||:query||'%' ) OR (notes LIKE '%'||:query||'%' )) ORDER BY title ASC")
    abstract fun getAllAccounts(query: String?, tag: String?): LiveData<List<AccountModel>>

    @Query("SELECT * FROM account WHERE id = :id")
    abstract suspend fun getAccount(id: Long?): AccountModel?

    @Query("SELECT * FROM account WHERE unique_id = :uniqueId")
    abstract suspend fun getAccount(uniqueId: String?): AccountModel?

    @Query("SELECT DISTINCT tags FROM account")
    abstract fun getTags(): Flow<List<String>>

    @Query("DELETE from account WHERE id = :id")
    abstract suspend fun deleteAccount(id: Long?)

    @Query("DELETE from account WHERE unique_id = :uniqueId")
    abstract suspend fun deleteAccount(uniqueId: String?)

    @Delete
    abstract suspend fun deleteAccount(accountModel: AccountModel)
}
