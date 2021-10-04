package com.yogeshpaliyal.keypass.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.yogeshpaliyal.keypass.R
import com.yogeshpaliyal.keypass.constants.AccountType
import com.yogeshpaliyal.keypass.utils.TOTPHelper
import com.yogeshpaliyal.keypass.utils.getRandomString
import com.yogeshpaliyal.universal_adapter.listener.UniversalViewType
import com.yogeshpaliyal.universal_adapter.model.BaseDiffUtil

/*
* @author Yogesh Paliyal
* techpaliyal@gmail.com
* https://techpaliyal.com
* created on 30-01-2021 20:38
*/
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
    var uniqueId: String? = getRandomString(),

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
) : BaseDiffUtil, UniversalViewType {

    fun getInitials() = (
        title?.firstOrNull() ?: username?.firstOrNull() ?: site?.firstOrNull()
            ?: notes?.firstOrNull() ?: 'K'
        ).toString()

    override fun getDiffId(): Any? {
        return id
    }

    override fun getDiffBody(): Any? {
        return if (type == AccountType.TOTP) {
            super.getDiffBody()
        } else {
            Gson().toJson(this)
        }
    }

    fun getOtp() = TOTPHelper.generate(password)

    fun getTOtpProgress() = TOTPHelper.getProgress().toInt()

    override fun getLayoutId(): Int = if (type == AccountType.TOTP) R.layout.item_totp else R.layout.item_accounts
}
