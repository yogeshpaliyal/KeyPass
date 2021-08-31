package com.yogeshpaliyal.keypass.utils

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelStoreOwner
import com.yogeshpaliyal.keypass.MyApplication

/*
CREATED BY : yogesh ON 06/08/20 12:06 PM
*/

class ViewModelFactory() :
    ViewModelProvider.NewInstanceFactory() {

    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        /* if (modelClass.isAssignableFrom(SingleNetworkCallViewModel::class.java)) {
             return SingleNetworkCallViewModel(apiHelper, dbHelper) as T
         }*/

        return super.create(modelClass)
    }
}

fun <T : AndroidViewModel> ViewModelStoreOwner.initViewModel(viewModel: Class<T>): T =
    ViewModelProvider(
        this,
        ViewModelProvider.AndroidViewModelFactory(MyApplication.instance)
    ).get(viewModel)

fun <T : ViewModel> ViewModelStoreOwner.initViewModel(viewModel: Class<T>): T = ViewModelProvider(
    this,
    ViewModelFactory()
).get(viewModel)
