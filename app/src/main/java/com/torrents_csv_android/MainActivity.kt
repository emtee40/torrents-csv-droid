package com.torrents_csv_android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import com.torrents_csv_android.ui.components.SearchActivity
import com.torrents_csv_android.ui.components.SearchViewModel
import com.torrents_csv_android.ui.theme.MainTheme

class MainActivity : ComponentActivity() {

    private val vm by viewModels<SearchViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MainTheme {
                SearchActivity(vm)
            }
        }
    }
}
