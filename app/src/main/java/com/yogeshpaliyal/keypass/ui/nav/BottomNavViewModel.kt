package com.yogeshpaliyal.keypass.ui.nav

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

/*
* @author Yogesh Paliyal
* techpaliyal@gmail.com
* https://techpaliyal.com
* created on 31-01-2021 14:11
*/
@HiltViewModel
class BottomNavViewModel @Inject constructor(application: Application, val appDb: com.yogeshpaliyal.common.AppDatabase) : AndroidViewModel(application) {
    private val _navigationList: MutableLiveData<List<NavigationModelItem>> = MutableLiveData()
    private val tagsDb = appDb.getDao().getTags()

    private var tagsList: List<String> ? = null

    val navigationList: LiveData<List<NavigationModelItem>>
        get() = _navigationList

    init {
        postListUpdate()

        viewModelScope.launch {
            tagsDb.collect {
                tagsList = it
                postListUpdate()
            }
        }
    }

    /**
     * Set the currently selected menu item.
     *
     * @return true if the currently selected item has changed.
     */
    fun setNavigationMenuItemChecked(id: Int): Boolean {
        var updated = false
        NavigationModel.navigationMenuItems.forEachIndexed { index, item ->
            val shouldCheck = item.id == id
            if (item.checked != shouldCheck) {
                NavigationModel.navigationMenuItems[index] = item.copy(checked = shouldCheck)
                updated = true
            }
        }
        if (updated) postListUpdate()
        return updated
    }

    private fun postListUpdate() {
        val newList = if (tagsList.isNullOrEmpty().not()) {
            NavigationModel.navigationMenuItems + NavigationModelItem.NavDivider("Tags") + (tagsList?.filter { it != null }?.map { NavigationModelItem.NavEmailFolder(it) } ?: listOf())
        } else {
            NavigationModel.navigationMenuItems
        }

        _navigationList.value = newList
    }
}
