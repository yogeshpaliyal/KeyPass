package com.yogeshpaliyal.common.di.module

import android.content.Context
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.yogeshpaliyal.common.AppDatabase
import com.yogeshpaliyal.common.DB_VERSION_3
import com.yogeshpaliyal.common.DB_VERSION_4
import com.yogeshpaliyal.common.DB_VERSION_5
import com.yogeshpaliyal.common.R
import com.yogeshpaliyal.common.utils.getRandomString
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun getDb(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            context.getString(R.string.app_name)
        ).addMigrations(object : Migration(DB_VERSION_3, DB_VERSION_4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE `account` ADD COLUMN `unique_id` TEXT")
                database.query("select id,unique_id from `account` where unique_id IS NULL")
                    ?.use {
                        while (it.moveToNext()) {
                            val id = it.getInt(0)
                            val query = "update `account` set `unique_id` = '${getRandomString()}' where `id` = '$id'"
                            database.execSQL(query)
                        }
                    }
            }
        }).addMigrations(object : Migration(DB_VERSION_4, DB_VERSION_5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE `account` ADD COLUMN `type` INT DEFAULT 0")
            }
        })
            .build()
    }
}
