package com.yogeshpaliyal.common

import androidx.room.Database
import androidx.room.RoomDatabase
import com.yogeshpaliyal.common.data.AccountModel
import com.yogeshpaliyal.common.db.DbDao

/*
* @author Yogesh Paliyal
* yogeshpaliyal.foss@gmail.com
* https://techpaliyal.com
* created on 30-01-2021 20:37
*/

const val DB_VERSION_3 = 3
const val DB_VERSION_4 = 4
const val DB_VERSION_5 = 5
const val DB_VERSION_6 = 6
const val DB_VERSION_7 = 7
const val DB_VERSION_8 = 8

@Database(
    entities = [AccountModel::class],
    version = DB_VERSION_8,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    // define DAO start
    abstract fun getDao(): DbDao
    // define DAO end
}
