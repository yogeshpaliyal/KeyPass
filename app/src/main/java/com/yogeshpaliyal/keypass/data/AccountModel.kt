package com.yogeshpaliyal.keypass.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.yogeshpaliyal.universal_adapter.model.BaseDiffUtil


/*
* @author Yogesh Paliyal
* techpaliyal@gmail.com
* https://techpaliyal.com
* created on 30-01-2021 20:38
*/
@Entity(tableName = "account")
class AccountModel(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long? = null,
    @ColumnInfo(name = "title")
    var title: String? = null,
    @ColumnInfo(name = "username")
    var username: String? = null,
    @ColumnInfo(name = "password")
    var password: String? = null,
    @ColumnInfo(name = "site")
    var site: String? = null,
    @ColumnInfo(name = "notes")
    var notes: String? = null,
    @ColumnInfo(name = "tags")
    var tags: String? = null
) : BaseDiffUtil {

fun getInitials() = (title?.firstOrNull() ?: username?.firstOrNull() ?: site?.firstOrNull() ?: notes?.firstOrNull() ?: 'K').toString()

    override fun getDiffId(): Any? {
        return id
    }
}
