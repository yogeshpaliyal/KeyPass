package com.yogeshpaliyal.keypass.ui.redux.states

import android.net.Uri

sealed class DialogState()

object ValidateKeyPhrase : DialogState()
object ForgotKeyPhraseState : DialogState()

sealed class RestoreBackupState(val fileUri: Uri) : DialogState()

data class RestoreKeyPassBackupState(val fileUrii: Uri) : RestoreBackupState(fileUrii)
data class RestoreChromeBackupState(val fileUrii: Uri) : RestoreBackupState(fileUrii)
data class RestoreKeePassBackupState(val fileUrii: Uri) : RestoreBackupState(fileUrii)
