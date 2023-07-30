package com.yogeshpaliyal.common.importer

import androidx.annotation.Keep
import com.google.gson.annotations.SerializedName

@Keep
data class ChromeAccount(
    @SerializedName("name")
    val name: String,
    @SerializedName("username")
    val username: String,
    @SerializedName("url")
    val url: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("note")
    val note: String
)
