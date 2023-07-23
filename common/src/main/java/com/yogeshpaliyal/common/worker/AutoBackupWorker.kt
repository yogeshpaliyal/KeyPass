package com.yogeshpaliyal.common.worker

import android.content.Context
import android.net.Uri
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.yogeshpaliyal.common.AppDatabase
import com.yogeshpaliyal.common.utils.backupAccounts
import com.yogeshpaliyal.common.utils.canUserAccessBackupDirectory
import com.yogeshpaliyal.common.utils.getUserSettings
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class AutoBackupWorker @AssistedInject constructor(
    @Assisted val appContext: Context,
    @Assisted params: WorkerParameters,
    val appDatabase: AppDatabase
) :
    CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {
            if (appContext.canUserAccessBackupDirectory()) {
                val userSettings = appContext.getUserSettings()
                val selectedDirectory = Uri.parse(userSettings.backupDirectory)
                appContext.backupAccounts(
                    appDatabase,
                    selectedDirectory,
                    if (userSettings.overrideAutoBackup) "key_pass_auto_backup" else null
                )
            }

            Result.success()
        }
    }
}
