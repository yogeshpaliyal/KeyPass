package com.yogeshpaliyal.keypass

import androidx.room.Database
import androidx.room.RoomDatabase
import com.yogeshpaliyal.keypass.data.AccountModel
import com.yogeshpaliyal.keypass.db.DbDao

/*
* @author Yogesh Paliyal
* yogeshpaliyal.foss@gmail.com
* https://techpaliyal.com
* created on 30-01-2021 20:37
*/
@Database(
    entities = [AccountModel::class],
    version = 4, exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    // define DAO start
    abstract fun getDao(): DbDao
    // define DAO end
}
