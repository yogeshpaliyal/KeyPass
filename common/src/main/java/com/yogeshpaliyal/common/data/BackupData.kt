package com.yogeshpaliyal.common.data

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

/*
* @author Yogesh Paliyal
* techpaliyal@gmail.com
* https://techpaliyal.com
* created on 23-02-2021 20:48
*/
data class BackupData(
    @SerializedName("version")
    @Expose
    val version: Int,
    @SerializedName("data")
    @Expose
    val data: List<AccountModel>
)
