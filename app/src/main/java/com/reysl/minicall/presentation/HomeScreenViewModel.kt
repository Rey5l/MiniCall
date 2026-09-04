package com.reysl.minicall.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.reysl.minicall.data.webrtc.WebRtcClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class HomeScreenViewModel(
    val webRtcClient: WebRtcClient
) : ViewModel() {

    companion object {
        fun provideFactory(webRtcClient: WebRtcClient): ViewModelProvider.Factory = viewModelFactory {
            initializer {
                HomeScreenViewModel(webRtcClient)
            }
        }
    }

    private val _uiState = MutableStateFlow(HomeScreenUIState())
    val uiState: StateFlow<HomeScreenUIState> = _uiState.asStateFlow()

    fun getPeerConnectionStatus() {
        val result = webRtcClient.initialize()

        result
            .onSuccess {
                _uiState.value = _uiState.value.copy(
                    connectionStatus = "WebRTC Initialized",
                    isWebRtcInitialized = true
                )
            }

            .onFailure {
                _uiState.value = _uiState.value.copy(
                    connectionStatus = it.message.toString(),
                    isWebRtcInitialized = false
                )
            }

        val peerConnectionResult = webRtcClient.createPeerConnection()
        peerConnectionResult
            .onSuccess {
                Log.d("WebRTC", "PeerConnection created")
            }
            .onFailure {
                Log.e("WebRTC", "PeerConnection failed")
            }

        val offerResult = webRtcClient.createOffer()
        offerResult
            .onSuccess {
                Log.d("WebRTC", "Offer created")
            }
            .onFailure {
                Log.e("WebRTC", "Offer failed")
            }
    }
}