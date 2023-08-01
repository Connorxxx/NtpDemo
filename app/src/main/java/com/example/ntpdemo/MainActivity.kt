package com.example.ntpdemo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.ntpdemo.ui.theme.NTPDemoTheme
import com.example.ntpdemo.viewmodels.MainViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            NTPDemoTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting()
                }
            }
        }
    }
}

@Composable
fun Greeting(vm: MainViewModel = hiltViewModel()) {
    Column(Modifier.fillMaxSize()) {
        Text(text = vm.time, Modifier.padding(24.dp))
        Button(onClick = { vm.syncTimeWithNtpServer() }, Modifier.padding(24.dp)) {
            Text(text = "get time")
        }
        Button(onClick = { vm.closeSocket() }, Modifier.padding(24.dp)) {
            Text(text = "Close")
        }
    }
}
