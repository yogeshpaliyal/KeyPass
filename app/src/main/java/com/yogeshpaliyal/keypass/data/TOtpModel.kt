package com.yogeshpaliyal.keypass.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import com.yogeshpaliyal.keypass.R
import com.yogeshpaliyal.universal_adapter.listener.UniversalViewType
import com.yogeshpaliyal.universal_adapter.model.BaseDiffUtil

@Entity(tableName = "totps")
data class TOtpModel(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    @SerializedName("id")
    var id: Long? = null,

    @ColumnInfo(name = "unique_id")
    @SerializedName("unique_id")
    var uniqueId: String? = null,

    @Expose
    @SerializedName("account_name")
    val accountName: String? = null

) : BaseDiffUtil, UniversalViewType {
    override fun getLayoutId() = R.layout.item_totp
}
