package com.yogeshpaliyal.keypass.worker

import android.content.Context
import android.content.SharedPreferences
import android.net.Uri
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.yogeshpaliyal.common.AppDatabase
import com.yogeshpaliyal.common.utils.backupAccounts
import com.yogeshpaliyal.common.utils.canUserAccessBackupDirectory
import com.yogeshpaliyal.common.utils.getBackupDirectory
import com.yogeshpaliyal.common.utils.overrideAutoBackup
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class AutoBackupWorker @AssistedInject constructor(
    @Assisted val appContext: Context,
    @Assisted params: WorkerParameters,
    val appDatabase: AppDatabase,
    val sp: SharedPreferences
) :
    CoroutineWorker(appContext, params) {
    override suspend fun doWork(): Result {
        return withContext(Dispatchers.IO) {

            if (appContext.canUserAccessBackupDirectory(sp)) {
                val selectedDirectory = Uri.parse(getBackupDirectory(sp))
                appContext.backupAccounts(
                    sp,
                    appDatabase,
                    selectedDirectory,
                    if (sp.overrideAutoBackup()) "key_pass_auto_backup" else null
                )
            }

            Result.success()
        }
    }
}
