package com.yogeshpaliyal.common.data

import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import com.yogeshpaliyal.common.constants.AccountType
import com.yogeshpaliyal.common.utils.TOTPHelper

/*
* @author Yogesh Paliyal
* techpaliyal@gmail.com
* https://techpaliyal.com
* created on 30-01-2021 20:38
*/
@Keep
@Entity(tableName = "account")
data class AccountModel(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    @SerializedName("id")
    var id: Long? = null,

    @ColumnInfo(name = "title")
    @SerializedName("title")
    var title: String? = null,

    @ColumnInfo(name = "unique_id")
    @SerializedName("unique_id")
    var uniqueId: String? = null,

    @ColumnInfo(name = "username")
    @SerializedName("username")
    var username: String? = null,

    @ColumnInfo(name = "password")
    @SerializedName("password")
    var password: String? = null, // TOTP secret when type is TOTP

    @ColumnInfo(name = "site")
    @SerializedName("site")
    var site: String? = null,

    @ColumnInfo(name = "notes")
    @SerializedName("notes")
    var notes: String? = null,

    @ColumnInfo(name = "tags")
    @SerializedName("tags")
    var tags: String? = null,

    @AccountType
    @ColumnInfo(name = "type")
    @SerializedName("type")
    var type: Int? = AccountType.DEFAULT
) {

    fun getInitials() = (
        title?.firstOrNull() ?: username?.firstOrNull() ?: site?.firstOrNull()
            ?: notes?.firstOrNull() ?: 'K'
        ).toString()

    fun getOtp(): String = TOTPHelper.generate(password)

    fun getTOtpProgress() = TOTPHelper.progress.toInt()
}
