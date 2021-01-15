package com.example.photoweather.presentation.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlin.properties.Delegates

@ExperimentalCoroutinesApi
abstract class BaseViewModel<ViewState : BaseViewState, ViewAction : BaseAction>(initialState: ViewState) :
    ViewModel() {

    private val _mutableStateFlow = MutableStateFlow(initialState)

    val stateFlow: StateFlow<ViewState>
        get() = _mutableStateFlow

    val stateLiveData: LiveData<ViewState>
        get() = _mutableStateFlow.asLiveData()


    // Delegate will handle state event deduplication
    // (multiple states of the same type holding the same data will not be dispatched multiple times to LiveData stream)
    protected var state by Delegates.observable(initialState) { _, _, new ->
        _mutableStateFlow.value = new
    }

    protected fun sendAction(viewAction: ViewAction) {
        state = onReduceState(viewAction)
    }

    protected open fun loadData() {}

    protected abstract fun onReduceState(viewAction: ViewAction): ViewState
}
