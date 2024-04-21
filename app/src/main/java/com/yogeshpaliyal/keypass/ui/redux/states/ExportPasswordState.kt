package com.yogeshpaliyal.keypass.ui.redux.states

import com.yogeshpaliyal.keypass.exporter.AccountsExporter

data class ExportPasswordState(
    var selectedExporter : AccountsExporter? = null
) : ScreenState()
