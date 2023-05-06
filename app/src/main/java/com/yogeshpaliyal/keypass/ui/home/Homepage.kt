package com.yogeshpaliyal.keypass.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yogeshpaliyal.keypass.ui.home.components.AccountsList
import com.yogeshpaliyal.keypass.ui.home.components.SearchBar
import com.yogeshpaliyal.keypass.ui.redux.states.HomeState
import com.yogeshpaliyal.keypass.ui.redux.actions.NavigationAction
import com.yogeshpaliyal.keypass.ui.redux.actions.StateUpdateAction
import org.reduxkotlin.compose.rememberDispatcher

/*
* @author Yogesh Paliyal
* techpaliyal@gmail.com
* https://techpaliyal.com
* created on 31-01-2021 09:25
*/

@Composable()
fun Homepage(
    mViewModel: DashboardViewModel = viewModel(),
    homeState: HomeState
) {
    val tag = homeState.tag
    val keyword = homeState.keyword
    val sortField = homeState.sortField
    val sortAscendingOrder = homeState.sortAscending

    val listOfAccountsLiveData by mViewModel.mediator.observeAsState()

    val dispatchAction = rememberDispatcher()

    LaunchedEffect(tag, keyword, sortField, sortAscendingOrder, block = {
        mViewModel.queryUpdated(keyword, tag, sortField, sortAscendingOrder)
    })

    Column(modifier = Modifier.fillMaxSize()) {
        SearchBar(keyword, {
            dispatchAction(StateUpdateAction(homeState.copy(keyword = it)))
        }) { field, order ->
            dispatchAction(

                StateUpdateAction(
                    homeState.copy(
                        sortField = field.value,
                        sortAscending = order == SortingOrder.Ascending
                    )
                )

            )
        }

        AnimatedVisibility(tag != null) {
            LazyRow(
                modifier = Modifier.padding(vertical = 8.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                content = {
                    item {
                        AssistChip(onClick = { }, label = {
                            Text(text = tag ?: "")
                        }, trailingIcon = {
                                IconButton(onClick = {
                                    dispatchAction(NavigationAction(HomeState(), true))
                                }) {
                                    Icon(
                                        painter = rememberVectorPainter(image = Icons.Rounded.Close),
                                        contentDescription = ""
                                    )
                                }
                            })
                    }
                }
            )
        }

        AccountsList(listOfAccountsLiveData)
    }
}
