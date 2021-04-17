package com.yogeshpaliyal.keypass

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.yogeshpaliyal.keypass.data.AccountModel
import com.yogeshpaliyal.keypass.db.DbDao
import com.yogeshpaliyal.keypass.utils.getRandomString


/*
* @author Yogesh Paliyal
* techpaliyal@gmail.com
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

    companion object {
        private lateinit var _instance: AppDatabase

        fun getInstance(): AppDatabase {
            if (!this::_instance.isInitialized)


                synchronized(this) {
                    _instance = Room.databaseBuilder(
                        MyApplication.instance,
                        AppDatabase::class.java,
                        MyApplication.instance.getString(R.string.app_name)
                    ).addMigrations(object : Migration(3, 4) {
                        override fun migrate(database: SupportSQLiteDatabase) {
                            database.execSQL("ALTER TABLE `account` ADD COLUMN `unique_id` TEXT")
                            database.query("select id,unique_id from `account` where unique_id IS NULL")
                                ?.use {
                                    while (it.moveToNext()) {
                                        val id = it.getInt(0)
                                        database.execSQL("update `account` set `unique_id` = '${getRandomString()}' where `id` = '$id'")
                                    }
                                }
                        }
                    })
                        .build()
                }


            return _instance
        }
    }


}