package com.yogeshpaliyal.common.worker

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.datastore.preferences.core.*
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.yogeshpaliyal.common.utils.dataStore
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.lang.Exception

@HiltWorker
class SharedPrefMigrationWorker @AssistedInject constructor(
    @Assisted val context: Context,
    @Assisted workerParams: WorkerParameters,
    private val sharedPreferences: SharedPreferences
) :
    CoroutineWorker(context, workerParams) {
    override suspend fun doWork(): Result {
        try {
            withContext(Dispatchers.IO) {
                for (mutableEntry in sharedPreferences.all) {
                    context.dataStore.edit { editor ->
                        val value = mutableEntry.value
                        when (value) {
                            is String -> editor[stringPreferencesKey(mutableEntry.key)] = value
                            is Boolean -> editor[booleanPreferencesKey(mutableEntry.key)] = value
                            is Long -> editor[longPreferencesKey(mutableEntry.key)] = value
                            is Int -> editor[intPreferencesKey(mutableEntry.key)] = value
                            is Double -> editor[doublePreferencesKey(mutableEntry.key)] = value
                            is Float -> editor[floatPreferencesKey(mutableEntry.key)] = value
                        }
                        sharedPreferences.edit {
                            remove(mutableEntry.key)
                        }
                    }
                }
            }
        }catch (e:Exception){
            e.printStackTrace()
        }
        return Result.success()
    }
}