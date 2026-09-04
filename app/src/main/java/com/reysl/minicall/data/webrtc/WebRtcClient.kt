package com.reysl.minicall.data.webrtc

import android.content.Context
import android.util.Log
import org.webrtc.DataChannel
import org.webrtc.IceCandidate
import org.webrtc.MediaConstraints
import org.webrtc.MediaStream
import org.webrtc.PeerConnection
import org.webrtc.PeerConnectionFactory
import org.webrtc.SdpObserver
import org.webrtc.SessionDescription

class WebRtcClient(
    private val context: Context
) {
    private var factory: PeerConnectionFactory? = null
    private var peerConnection: PeerConnection? = null

    private var dataChannel: DataChannel? = null

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
            Log.d("WebRTC", "iceConnectionState: $newState")
        }

        override fun onIceConnectionReceivingChange(receiving: Boolean) {
        }

        override fun onIceGatheringChange(newState: PeerConnection.IceGatheringState?) {
            Log.d("WebRTC", "iceGatheringState: $newState")
        }

        override fun onIceCandidate(candidate: IceCandidate?) {
            Log.d(
                "WebRTC",
                "iceCandidate: ${candidate?.sdpMid} ${candidate?.sdpMLineIndex} ${candidate?.sdp}"
            )
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

    private val sdpObserver = object : SdpObserver {
        override fun onCreateSuccess(sdp: SessionDescription?) {
            val connection = peerConnection ?: return
            val description = sdp ?: return

            Log.d("WebRTC", "SDP created: ${description.description}")

            connection.setLocalDescription(
                this,
                description
            )
        }

        override fun onSetSuccess() {
            Log.d("WebRTC", "Local SDP successfully set")
        }

        override fun onCreateFailure(error: String?) {
        }

        override fun onSetFailure(error: String?) {
        }
    }

    fun createPeerConnection(): Result<Unit> {
        if (peerConnection != null) return Result.success(Unit)

        return runCatching {
            val rtcConfig = PeerConnection.RTCConfiguration(
                listOf(
                    PeerConnection.IceServer
                        .builder("stun:stun.l.google.com:19302")
                        .createIceServer()
                )
            )

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

    fun createOffer(): Result<Unit> {
        val connection = peerConnection
            ?: return Result.failure(IllegalStateException("PeerConnection not initialized"))

        val dataChannelResult = createDataChannel()

        if (dataChannelResult.isFailure) return Result.failure(
            dataChannelResult.exceptionOrNull() ?: IllegalStateException("DataChannel failed")
        )

        return runCatching {
            val constraints = MediaConstraints()

            connection.createOffer(sdpObserver, constraints)
        }
    }

    private fun createDataChannel(): Result<Unit> {
        if (dataChannel != null) return Result.success(Unit)

        val connection = peerConnection
            ?: return Result.failure(IllegalStateException("PeerConnection not initialized"))

        return runCatching {
            dataChannel = connection.createDataChannel("DataChannel", DataChannel.Init())

            checkNotNull(dataChannel)
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