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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
import com.yogeshpaliyal.common.constants.ScannerType
import com.yogeshpaliyal.common.utils.TOTPHelper
import com.yogeshpaliyal.keypass.R
import com.yogeshpaliyal.keypass.ui.detail.components.BottomBar
import com.yogeshpaliyal.keypass.ui.detail.components.Fields
import com.yogeshpaliyal.keypass.ui.redux.actions.CopyToClipboard
import com.yogeshpaliyal.keypass.ui.redux.actions.GoBackAction
import com.yogeshpaliyal.keypass.ui.redux.actions.NavigationAction
import com.yogeshpaliyal.keypass.ui.redux.actions.ToastAction
import com.yogeshpaliyal.keypass.ui.redux.states.PasswordGeneratorState
import org.reduxkotlin.compose.rememberDispatcher
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.net.MalformedURLException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

/*
* @author Yogesh Paliyal
* yogeshpaliyal.foss@gmail.com
* https://techpaliyal.com
* created on 31-01-2021 10:38
*/

@Composable
fun AccountDetailPage(
    id: Long?,
    viewModel: DetailViewModel = hiltViewModel()
) {
    val dispatchAction = rememberDispatcher()
    val generatedQrCodeBitmap = remember { mutableStateOf<Bitmap?>(null) }
    val showDialog = remember { mutableStateOf(false) }

    // task value state
    val accountModel = viewModel.accountModel.collectAsState().value

    // Set initial object
    LaunchedEffect(key1 = id) {
        viewModel.loadAccount(id)
    }

    val goBack: () -> Unit = {
        dispatchAction(GoBackAction)
    }

    val launcher = rememberLauncherForActivityResult(QRScanner()) {
        when (it.type) {
            ScannerType.Password -> {
                viewModel.setAccountModel(accountModel.copy(password = it.scannedText))
            }
            ScannerType.Secret -> {
                it.scannedText ?: return@rememberLauncherForActivityResult
                var totp: TOTPHelper? = null
                try {
                    totp = TOTPHelper(it.scannedText)
                } catch (e: MalformedURLException) {
                    dispatchAction(ToastAction(R.string.invalid_secret_key))
                    return@rememberLauncherForActivityResult
                }
                var newAccountModel = accountModel.copy(secret = totp.secret)

                if (newAccountModel.title.isNullOrEmpty()) {
                    newAccountModel = newAccountModel.copy(title = totp.label)
                }

                if (newAccountModel.username.isNullOrEmpty()) {
                    newAccountModel = newAccountModel.copy(username = totp.issuer)
                }
                viewModel.setAccountModel(newAccountModel)
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
                },
                openPasswordConfiguration = {
                    dispatchAction(NavigationAction(PasswordGeneratorState()))
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
                    viewModel.setAccountModel(newAccountModel)
                },
                copyToClipboardClicked = { value ->
                    dispatchAction(CopyToClipboard(value))
                },
                scanClicked = { scannerType ->
                    launcher.launch(scannerType)
                }
            )
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
                if (download.value) {
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
