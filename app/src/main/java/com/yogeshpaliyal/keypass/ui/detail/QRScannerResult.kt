package com.yogeshpaliyal.keypass.ui.detail

import com.yogeshpaliyal.common.constants.ScannerType

data class QRScannerResult(@ScannerType val type: Int, val scannedText: String?)
