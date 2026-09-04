package com.reysl.minicall.data.webrtc

sealed interface WebRtcState {
    data object Initializing : WebRtcState
    data object Ready : WebRtcState
    data class Error(val message: String) : WebRtcState
}