package com.reysl.minicall

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.ui.Modifier
import com.reysl.minicall.data.webrtc.WebRtcClient
import com.reysl.minicall.presentation.HomeRoute
import com.reysl.minicall.presentation.HomeScreenViewModel
import com.reysl.minicall.ui.theme.MiniCallTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val webRtcClient = WebRtcClient(this.applicationContext)

        val viewModel: HomeScreenViewModel by viewModels {
            HomeScreenViewModel.provideFactory(webRtcClient)
        }

        setContent {
            MiniCallTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    HomeRoute(viewModel, innerPadding = innerPadding)
                }
            }
        }
    }
}

