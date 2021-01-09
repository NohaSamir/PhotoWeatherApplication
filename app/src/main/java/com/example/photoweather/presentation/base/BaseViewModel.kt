package com.example.photoweather.presentation.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import kotlin.properties.Delegates

abstract class BaseViewModel<ViewState : BaseViewState, ViewAction : BaseAction>(initialState: ViewState) :
    ViewModel() {

    private val _stateMutableLiveData = MutableLiveData<ViewState>()
    val stateLiveData: LiveData<ViewState>
        get() = _stateMutableLiveData


    // Delegate will handle state event deduplication
    // (multiple states of the same type holding the same data will not be dispatched multiple times to LiveData stream)
    protected var state by Delegates.observable(initialState) { _, _, new ->
        _stateMutableLiveData.value = new
    }

    protected fun sendAction(viewAction: ViewAction) {
        state = onReduceState(viewAction)
    }

    protected open fun loadData() {}

    protected abstract fun onReduceState(viewAction: ViewAction): ViewState
}
