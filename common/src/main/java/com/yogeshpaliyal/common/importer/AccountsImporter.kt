package com.yogeshpaliyal.common.importer

import com.yogeshpaliyal.common.data.AccountModel
import java.io.File

interface AccountsImporter {
    fun getImporterTitle() : String

    fun allowedMimeType() : String

    fun readFile(file: File): List<AccountModel>
}