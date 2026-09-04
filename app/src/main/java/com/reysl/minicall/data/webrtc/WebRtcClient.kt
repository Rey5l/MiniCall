package com.reysl.minicall.data.webrtc

import android.content.Context
import org.webrtc.PeerConnectionFactory

class WebRtcClient(
    private val context: Context
) {
    private var factory: PeerConnectionFactory? = null

    fun initialize(): Result<Unit> {
        if (factory != null) return Result.success(Unit)

        return runCatching {
            initializePeerConnectionFactory()

            factory = PeerConnectionFactory.builder()
                .createPeerConnectionFactory()
        }
    }

    private fun initializePeerConnectionFactory() {
        PeerConnectionFactory.initialize(
            PeerConnectionFactory.InitializationOptions
                .builder(context)
                .createInitializationOptions()
        )
    }
}