package com.yogeshpaliyal.keypass.di.module

import android.content.Context
import android.content.SharedPreferences
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.yogeshpaliyal.keypass.AppDatabase
import com.yogeshpaliyal.keypass.MyApplication
import com.yogeshpaliyal.keypass.R
import com.yogeshpaliyal.keypass.utils.MySharedPreferences
import com.yogeshpaliyal.keypass.utils.getRandomString
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
    fun getDb(@ApplicationContext context: Context): AppDatabase{
        return Room.databaseBuilder(
            context,
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



    @Provides
    @Singleton
    fun getSharedPre(@ApplicationContext context: Context): SharedPreferences{
        return MySharedPreferences(context).sharedPref
    }

}