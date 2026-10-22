package com.example.airdrophub

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView

data class Site(val name: String, val url: String)

// Add or remove sites here
val sites = listOf(
    Site("Airdrops.io", "https://airdrops.io"),
    Site("CryptoRank", "https://cryptorank.io/drophunting"),
    Site("AirdropAlert", "https://airdropalert.com"),
    Site("DappRadar", "https://dappradar.com/airdrops"),
)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { MaterialTheme { AirdropHub() } }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AirdropHub() {
    val activity = LocalContext.current as Activity
    var selected by remember { mutableIntStateOf(0) }
    val webViews = remember { mutableMapOf<Int, WebView>() }

    BackHandler {
        val wv = webViews[selected]
        if (wv != null && wv.canGoBack()) wv.goBack() else activity.finish()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Airdrop Hub") },
                actions = {
                    IconButton(onClick = { webViews[selected]?.reload() }) {
                        Icon(Icons.Default.Refresh, contentDescription = "Refresh")
                    }
                }
            )
        }
    ) { pad ->
        Column(Modifier.padding(pad).fillMaxSize()) {
            ScrollableTabRow(selectedTabIndex = selected, edgePadding = 8.dp) {
                sites.forEachIndexed { i, s ->
                    Tab(selected = selected == i, onClick = { selected = i }, text = { Text(s.name) })
                }
            }
            key(selected) {
                AndroidView(
                    modifier = Modifier.fillMaxSize(),
                    factory = { ctx ->
                        webViews.getOrPut(selected) {
                            WebView(ctx).apply {
                                settings.javaScriptEnabled = true
                                settings.domStorageEnabled = true
                                webViewClient = WebViewClient()
                                loadUrl(sites[selected].url)
                            }
                        }
                    }
                )
            }
        }
    }
}
