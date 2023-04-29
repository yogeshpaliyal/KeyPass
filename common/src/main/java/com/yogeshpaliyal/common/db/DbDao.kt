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
interface DbDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateAccount(vararg accountModel: AccountModel)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateAccount(accountModel: List<AccountModel>)

    @Query("SELECT * FROM account ORDER BY title ASC")
    fun getAllAccounts(): LiveData<List<AccountModel>>

    @Query("SELECT * FROM account ORDER BY title ASC")
    suspend fun getAllAccountsList(): List<AccountModel>

    @Query(
        "SELECT * FROM account " +
                "WHERE " +
                "CASE WHEN :tag IS NOT NULL " +
                "THEN tags = :tag " +
                "ELSE 1 END " +
                "AND ((username LIKE '%'||:query||'%' ) " +
                "OR (title LIKE '%'||:query||'%' ) " +
                "OR (notes LIKE '%'||:query||'%' )) " +
                "ORDER BY" +
                " CASE" +
                "    WHEN :sortingField = 'username' THEN username" +
                "    WHEN :sortingField = 'title' THEN title" +
                "    WHEN :sortingField = 'notes' THEN notes" +
                "  END ASC"
    )
    fun getAllAccountsAscending(
        query: String?,
        tag: String?,
        sortingField: String?
    ): List<AccountModel>


    @Query(
        "SELECT * FROM account " +
                "WHERE " +
                "CASE WHEN :tag IS NOT NULL " +
                "THEN tags = :tag " +
                "ELSE 1 END " +
                "AND ((username LIKE '%'||:query||'%' ) " +
                "OR (title LIKE '%'||:query||'%' ) " +
                "OR (notes LIKE '%'||:query||'%' )) " +
                "ORDER BY" +
                " CASE" +
                "    WHEN :sortingField = 'username' THEN username" +
                "    WHEN :sortingField = 'title' THEN title" +
                "    WHEN :sortingField = 'notes' THEN notes" +
                "  END DESC"
    )
    fun getAllAccountsDescending(
        query: String?,
        tag: String?,
        sortingField: String?
    ): List<AccountModel>

    @Query("SELECT * FROM account WHERE id = :id")
    suspend fun getAccount(id: Long?): AccountModel?

    @Query("SELECT * FROM account WHERE unique_id = :uniqueId")
    suspend fun getAccount(uniqueId: String?): AccountModel?

    @Query("SELECT DISTINCT tags FROM account")
    fun getTags(): Flow<List<String>>

    @Query("DELETE from account WHERE id = :id")
    suspend fun deleteAccount(id: Long?)

    @Query("DELETE from account WHERE unique_id = :uniqueId")
    suspend fun deleteAccount(uniqueId: String?)

    @Delete
    suspend fun deleteAccount(accountModel: AccountModel)
}
