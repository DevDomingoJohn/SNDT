package com.domin.sndt.info

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow

class InfoVM: ViewModel() {
    private val _state = MutableStateFlow(UIState())
    val state = _state.asStateFlow()
}