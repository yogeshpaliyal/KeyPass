package com.yogeshpaliyal.common.di.module

import android.content.Context
import android.util.Log
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.yogeshpaliyal.common.AppDatabase
import com.yogeshpaliyal.common.DB_VERSION_3
import com.yogeshpaliyal.common.DB_VERSION_4
import com.yogeshpaliyal.common.DB_VERSION_5
import com.yogeshpaliyal.common.DB_VERSION_6
import com.yogeshpaliyal.common.R
import com.yogeshpaliyal.common.utils.getRandomString
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import net.sqlcipher.database.SQLiteDatabase
import net.sqlcipher.database.SupportFactory
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun getDb(@ApplicationContext context: Context): AppDatabase {

        val dbName = context.getString(R.string.app_name)
        val dbNameEncrypted = "${dbName}"

        val builder = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            dbNameEncrypted
        )

        val userEnteredPassphrase = "testingString"
        val passphrase: ByteArray = SQLiteDatabase.getBytes(userEnteredPassphrase.toCharArray())
        val factory = SupportFactory(passphrase);
        builder.openHelperFactory(factory)
        builder.addMigrations(object : Migration(DB_VERSION_3, DB_VERSION_4) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE `account` ADD COLUMN `unique_id` TEXT")
                database.query("select id,unique_id from `account` where unique_id IS NULL")
                    .use {
                        while (it.moveToNext()) {
                            val id = it.getInt(0)
                            val query =
                                "update `account` set `unique_id` = '${getRandomString()}' where `id` = '$id'"
                            database.execSQL(query)
                        }
                    }
            }
        })
        builder.addMigrations(object : Migration(DB_VERSION_4, DB_VERSION_5) {
            override fun migrate(database: SupportSQLiteDatabase) {
                database.execSQL("ALTER TABLE `account` ADD COLUMN `type` INT DEFAULT 0")
            }
        })
        builder.addMigrations(object : Migration(DB_VERSION_5, DB_VERSION_6) {
            override fun migrate(db: SupportSQLiteDatabase) {
                Log.d("ROOM_MIGRATION", "migrate: from db version 5 to 6")
                val database = SQLiteDatabase.openOrCreateDatabase(dbName,"", null);
                database.rawExecSQL(
                    "ATTACH DATABASE '${dbNameEncrypted}' AS encrypted KEY '${passphrase}'");
                database.rawExecSQL("select sqlcipher_export('encrypted')");
                database.rawExecSQL("DETACH DATABASE encrypted");
                database.close();
            }

        })
        return builder.build()
    }
}
