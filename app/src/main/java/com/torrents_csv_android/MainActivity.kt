package com.torrents_csv_android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.torrents_csv_android.ui.components.SearchActivity
import com.torrents_csv_android.ui.components.SearchViewModel
import com.torrents_csv_android.ui.theme.MainTheme

class MainActivity : ComponentActivity() {

    private val vm by viewModels<SearchViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MainTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    SearchActivity(vm)
                }
            }
        }
    }
}
