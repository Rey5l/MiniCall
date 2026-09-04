package com.reysl.minicall.data.webrtc

import android.content.Context
import android.util.Log
import org.webrtc.DataChannel
import org.webrtc.IceCandidate
import org.webrtc.MediaStream
import org.webrtc.PeerConnection
import org.webrtc.PeerConnectionFactory

class WebRtcClient(
    private val context: Context
) {
    private var factory: PeerConnectionFactory? = null
    private var peerConnection: PeerConnection? = null

    fun initialize(): Result<Unit> {
        if (factory != null) return Result.success(Unit)

        return runCatching {
            initializePeerConnectionFactory()

            factory = PeerConnectionFactory.builder()
                .createPeerConnectionFactory()
        }
    }

    private val peerConnectionObserver = object : PeerConnection.Observer {
        override fun onSignalingChange(newState: PeerConnection.SignalingState?) {
        }

        override fun onIceConnectionChange(newState: PeerConnection.IceConnectionState?) {
        }

        override fun onIceConnectionReceivingChange(receiving: Boolean) {
        }

        override fun onIceGatheringChange(newState: PeerConnection.IceGatheringState?) {
        }

        override fun onIceCandidate(candidate: IceCandidate?) {
        }

        override fun onIceCandidatesRemoved(candidates: Array<out IceCandidate?>?) {
        }

        override fun onAddStream(stream: MediaStream?) {
        }

        override fun onRemoveStream(stream: MediaStream?) {
        }

        override fun onDataChannel(dataChannel: DataChannel?) {
        }

        override fun onRenegotiationNeeded() {
        }

        override fun onConnectionChange(newState: PeerConnection.PeerConnectionState?) {
            Log.d("WebRTC", "peerConnectionState: $newState")
        }
    }

    fun createPeerConnection(): Result<Unit> {
        if (peerConnection != null) return Result.success(Unit)

        return runCatching {
            val rtcConfig = PeerConnection.RTCConfiguration(emptyList())

            val peerConnectionFactory =
                factory ?: return Result.failure(IllegalStateException("WebRTC not initialized"))

            peerConnection = peerConnectionFactory.createPeerConnection(
                rtcConfig,
                peerConnectionObserver
            )

            checkNotNull(peerConnection) {
                "Failed to create PeerConnection"
            }
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