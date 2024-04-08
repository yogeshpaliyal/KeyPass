package com.yogeshpaliyal.keypass.exporter

import android.content.Context
import androidx.compose.runtime.Composable
import com.yogeshpaliyal.common.data.AccountModel
import java.io.File

interface AccountsExporter {
    fun getExporterTitle(): Int

    fun getExporterDesc(): Int

    fun export(context: Context, listOfAccounts: List<AccountModel>) : File?
}