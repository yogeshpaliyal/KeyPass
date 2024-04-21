package com.yogeshpaliyal.common.di.module

import android.content.Context
import android.database.sqlite.SQLiteException
import androidx.room.Room
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.yogeshpaliyal.common.AppDatabase
import com.yogeshpaliyal.common.DB_VERSION_3
import com.yogeshpaliyal.common.DB_VERSION_4
import com.yogeshpaliyal.common.DB_VERSION_5
import com.yogeshpaliyal.common.DB_VERSION_6
import com.yogeshpaliyal.common.DB_VERSION_7
import com.yogeshpaliyal.common.R
import com.yogeshpaliyal.common.utils.getRandomString
import com.yogeshpaliyal.common.utils.getUserSettingsOrNull
import com.yogeshpaliyal.common.utils.setDatabasePassword
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.runBlocking
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
        val dbNameEncrypted = "$dbName.encrypted"

        val builder = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            dbNameEncrypted
        )

        var userEnteredPassphrase: String
        var isMigratedFromNonEncryption = false

        runBlocking {
            val userSettings = context.getUserSettingsOrNull()
            if (userSettings?.dbPassword == null) {
                userEnteredPassphrase = getRandomString()
                isMigratedFromNonEncryption = true
                context.setDatabasePassword(userEnteredPassphrase)
            } else {
                userEnteredPassphrase = userSettings.dbPassword
            }
        }

        SQLiteDatabase.loadLibs(context)

        if (isMigratedFromNonEncryption) {
            context.migrateNonEncryptedToEncryptedDb(dbName, dbNameEncrypted, userEnteredPassphrase)
        }

        val passphrase: ByteArray = SQLiteDatabase.getBytes(userEnteredPassphrase.toCharArray())
        val factory = SupportFactory(passphrase)
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

        builder.addMigrations(object : Migration(DB_VERSION_6, DB_VERSION_7) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Move type = 2, to Default 0, move password to secret
                database.execSQL("ALTER TABLE `account` ADD COLUMN `secret` TEXT DEFAULT NULL")
                database.execSQL("UPDATE `account` SET secret = password, password = null, type = 1 WHERE type = 2")
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
        return builder.build()
    }

    private fun Context.migrateNonEncryptedToEncryptedDb(nonEncryptedDbName: String, encryptedDbName: String, userEnteredPassphrase: String) {
        try {
            val oldDb = getDatabasePath(nonEncryptedDbName)
            val database = SQLiteDatabase.openOrCreateDatabase(oldDb, "", null)
            val encryptedDbPath = getDatabasePath(encryptedDbName).path
            database.rawExecSQL(
                "ATTACH DATABASE '$encryptedDbPath' AS encrypted KEY '$userEnteredPassphrase'"
            )
            database.rawExecSQL("select sqlcipher_export('encrypted')")
            database.rawExecSQL("DETACH DATABASE encrypted")
            database.close()
            oldDb.delete()
        } catch (e: SQLiteException) {
            e.printStackTrace()
        }
    }
}
