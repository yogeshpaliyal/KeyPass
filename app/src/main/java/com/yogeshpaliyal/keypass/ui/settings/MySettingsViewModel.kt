package com.yogeshpaliyal.keypass.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MySettingsViewModel @Inject constructor(
    application: Application,
    val appDb: com.yogeshpaliyal.common.AppDatabase
) : AndroidViewModel(application) {
    private val appDao = appDb.getDao()
}
