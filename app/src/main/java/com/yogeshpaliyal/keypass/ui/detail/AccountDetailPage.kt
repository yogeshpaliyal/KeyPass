package com.yogeshpaliyal.keypass.ui.detail

import android.graphics.Bitmap
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.hilt.navigation.compose.hiltViewModel
import com.google.gson.Gson
import com.yogeshpaliyal.common.constants.ScannerType
import com.yogeshpaliyal.common.data.AccountModel
import com.yogeshpaliyal.common.utils.TOTPHelper
import com.yogeshpaliyal.keypass.ui.detail.components.BottomBar
import com.yogeshpaliyal.keypass.ui.detail.components.Fields
import com.yogeshpaliyal.keypass.ui.redux.actions.CopyToClipboard
import com.yogeshpaliyal.keypass.ui.redux.actions.GoBackAction
import org.reduxkotlin.compose.rememberDispatcher
import java.io.*
import java.text.SimpleDateFormat
import java.util.*

/*
* @author Yogesh Paliyal
* yogeshpaliyal.foss@gmail.com
* https://techpaliyal.com
* created on 31-01-2021 10:38
*/

@Composable
fun AccountDetailPage(
    uniqueId: String?,
    viewModel: DetailViewModel = hiltViewModel()
) {
    val dispatchAction = rememberDispatcher()
    val generatedQrCodeBitmap = remember { mutableStateOf<Bitmap?>(null) }
    val showDialog = remember { mutableStateOf(false) }

    // task value state
    val (accountModel, setAccountModel) = remember {
        mutableStateOf(
            AccountModel()
        )
    }

    // Set initial object
    LaunchedEffect(key1 = uniqueId) {
        viewModel.loadAccount(uniqueId) {
            setAccountModel(it.copy())
        }
    }

    val goBack: () -> Unit = {
        dispatchAction(GoBackAction)
    }

    val launcher = rememberLauncherForActivityResult(QRScanner()) {
        when (it.type) {
            ScannerType.Password -> {
                setAccountModel(accountModel.copy(password = it.scannedText))
            }
            ScannerType.Secret -> {
                it.scannedText ?: return@rememberLauncherForActivityResult
                val totp = TOTPHelper(it.scannedText)
                var newAccountModel = accountModel.copy(secret = totp.secret)

                if (newAccountModel.title.isNullOrEmpty()) {
                    newAccountModel = newAccountModel.copy(title = totp.label)
                }

                if (newAccountModel.username.isNullOrEmpty()) {
                    newAccountModel = newAccountModel.copy(username = totp.issuer)
                }
                setAccountModel(newAccountModel)
            }
        }
    }
    Scaffold(
        bottomBar = {
            BottomBar(
                accountModel,
                backPressed = goBack,
                onDeleteAccount = {
                    viewModel.deleteAccount(accountModel, goBack)
                },
                generateQrCodeClicked = {
                    showDialog.value = true

                    val qrCodeBitmap = viewModel.generateQrCode(accountModel)
                    generatedQrCodeBitmap.value = qrCodeBitmap
                }
            ) {
                viewModel.insertOrUpdate(accountModel, goBack)
            }
        }
    ) { paddingValues ->
        Surface(modifier = Modifier.padding(paddingValues)) {
            Fields(
                accountModel = accountModel,
                updateAccountModel = { newAccountModel ->
                    setAccountModel(newAccountModel)
                },
                copyToClipboardClicked = { value ->
                    dispatchAction(CopyToClipboard(value))
                }
            ) {
                launcher.launch(it)
            }
            // Display the generated QR code bitmap in a popup
            if (showDialog.value) {
                val download = remember { mutableStateOf(false) }
                Dialog(onDismissRequest = { showDialog.value = false }) {
                    AlertDialog(
                        onDismissRequest = { showDialog.value = false },
                        title = {
                            Text("QR Code")
                        },
                        text = {
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp)
                            ) {
                                generatedQrCodeBitmap.value?.asImageBitmap()?.let {
                                    Image(
                                        bitmap = it,
                                        contentDescription = null,
                                        modifier = Modifier.size(150.dp)
                                    )
                                }
                            }
                        },
                        dismissButton = {
                            Button(onClick = {
                                download.value = true
                            }) {
                                Text("Download QR Code")
                            }
                        },
                        confirmButton = {
                            Button(onClick = { showDialog.value = false }) {
                                Text("Close")
                            }
                        }
                    )
                }
                if(download.value){
                    generatedQrCodeBitmap.value?.let { saveQRCodeImage(imageBitmap = it.asImageBitmap(), displayName = "QRCode") }
                }
            }
        }
    }
}

@Composable
fun saveQRCodeImage(imageBitmap: ImageBitmap, displayName: String) {
    val context = LocalContext.current

    val currentTimeMillis = System.currentTimeMillis()
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date(currentTimeMillis))

    val fileName = "$displayName-$timeStamp.png"
    val resolver = context.contentResolver

    val bitmap: Bitmap = imageBitmap.asAndroidBitmap()

    val imagesDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES)
    val image = File(imagesDir, fileName)

    try {
        val fos: OutputStream = FileOutputStream(image)
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
        fos.flush()
        fos.close()
        MediaStore.Images.Media.insertImage(resolver, image.absolutePath, fileName, null)
        Toast.makeText(context, "QR code saved to gallery", Toast.LENGTH_SHORT).show()
    } catch (e: Exception) {
        e.printStackTrace()
        Toast.makeText(context, "Error occurred while downloading QR code", Toast.LENGTH_SHORT).show()
    }
}