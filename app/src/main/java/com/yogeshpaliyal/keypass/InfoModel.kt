package com.yogeshpaliyal.keypass

/*
* @author Yogesh Paliyal
* techpaliyal@gmail.com
* https://techpaliyal.com
* created on 23-01-2021 10:33
*/
data class InfoModel(
    var user_id: String,
    var username: String,
    var password: String,
    var notes: String,
    var insertedDate: Long,
    var lastUpdatedDate: Long
)
