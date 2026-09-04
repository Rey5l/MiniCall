package com.reysl.minicall.presentation

data class HomeScreenUIState(
    val connectionStatus: String = "",
    val isWebRtcInitialized: Boolean = false
)
