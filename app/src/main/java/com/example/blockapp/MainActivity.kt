package com.example.blockapp

import androidx.compose.foundation.lazy.items
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.blockapp.ui.theme.BlockAppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) //Android Setup
        enableEdgeToEdge() //Fullscreen
        setContent {    //UI:
            BlockAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    ListApps(modifier = Modifier.padding(innerPadding)) //Method Calling the list of all apps
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BlockAppTheme {
        Greeting("Android")
    }
}

@Composable
fun ListApps(modifier: Modifier = Modifier){ //List all apps that are executable
    val context = LocalContext.current
    var appNames by remember { mutableStateOf<List<String>>(emptyList()) } //initiating both context and the appnames list

    LaunchedEffect(Unit) { //Runs once!
        val paquete = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN).apply { addCategory(Intent.CATEGORY_LAUNCHER) } //Intent: What apps CAN we see? (Launchables)

        appNames = paquete.queryIntentActivities(intent, 0) //get the appnames through a stream
            .map { name -> name.loadLabel(paquete).toString() }
            .distinct()
            .sortedBy { name -> name.lowercase() } //sort the apps by their names
    }

    LazyColumn(modifier = modifier.padding(16.dp)){ //intro
        item{
            Text("Installed apps: ${appNames.size} :) ") //Not true, but rather installes apps that are launchable, does its job I guess
            Text("These are your apps:")
        }
        if(appNames.isEmpty()){ //Wait while appnames might still be loading
            item{
                Text("Loading...")
            }

        }
        else{
            //Display the app names
            items(appNames) { name ->
                Text(name)
            }
        }

    }

}

