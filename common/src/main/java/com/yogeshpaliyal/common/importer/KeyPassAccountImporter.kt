package com.yogeshpaliyal.common.importer

import com.opencsv.CSVReader
import com.yogeshpaliyal.common.data.AccountModel
import java.io.File


class KeyPassAccountImporter: AccountsImporter {
    override fun getImporterTitle(): String {
        return "KeyPass Backup"
    }

    override fun allowedMimeType(): String {
        return "*/keypass"
    }

    override fun readFile(file: File): List<AccountModel> {
        val reader = CSVReader(file.reader())
        val myEntries: List<*> = reader.readAll()
        return listOf()
    }
}