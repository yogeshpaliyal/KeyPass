package com.yogeshpaliyal.common.importer

import com.opencsv.CSVReader
import com.yogeshpaliyal.common.data.AccountModel
import java.io.File


class ChromeAccountImporter: AccountsImporter {
    override fun getImporterTitle(): String {
        return "Google Chrome"
    }

    override fun allowedMimeType(): String {
        return "text/csv"
    }


    override fun readFile(file: File): List<AccountModel> {
        val reader = CSVReader(file.reader())
        val myEntries: List<*> = reader.readAll()
        return listOf()
    }
}