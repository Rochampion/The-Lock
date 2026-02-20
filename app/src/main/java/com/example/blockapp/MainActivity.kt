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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.blockapp.ui.theme.BlockAppTheme
import kotlinx.coroutines.selects.select
import kotlin.comparisons.compareBy

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState) //Android Setup
        enableEdgeToEdge() //Fullscreen
        setContent {    //UI:
            var selectedApp by remember {mutableStateOf<String?>(null)}
            BlockAppTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    if(selectedApp == null){
                        ListApps(modifier = Modifier.padding(innerPadding), onSelect = {selectedApp = it}) //Method Calling the list of all apps
                    }
                    else{
                        BlockSettings(modifier = Modifier.padding(innerPadding), onSelect = {selectedApp = it}, packageName = selectedApp!!)
                    }
                }
            }
        }
    }
}

@Composable
fun BlockSettings(modifier: Modifier = Modifier,onSelect : (String?) -> Unit, packageName : String){ //Menu of settings for each individual app
    val context = LocalContext.current
    val app = context.packageManager.getApplicationInfo(packageName, 0) //Context and app
    var textValue by remember { mutableStateOf("") }
    LazyColumn(modifier = modifier.fillMaxSize() //Lazycolumn for all of the text
        .padding(16.dp)) {
        item {
            Text("Your chosen app: \n \n${app.loadLabel(context.packageManager)}" , fontSize = 30.sp) //App you chose
            Text("\nBack to All apps", fontSize = 20.sp, modifier = Modifier.clickable{ //To go BACK to choosing menu
                onSelect(null)
            })

            Row(modifier = Modifier.fillMaxWidth() //Row for the hour limit per day, plan to do this for minutes too
                .padding(16.dp),
                verticalAlignment =Alignment.CenterVertically)
            {
                Text("Choose max limit per day:", fontSize = 20.sp)
                Spacer(Modifier.width(12.dp))
                OutlinedTextField(value = textValue, onValueChange ={ new-> //Input Field
                    textValue = new.take(2) //Arreglar Bug cuando quitas el ultimo digito: null check?
                    if(textValue.toInt()>24){
                        textValue = "24"
                    }

                },
                    modifier = Modifier.width(90.dp),
                    singleLine = true, //no \n
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done //Ime: Input Methode Editor, Mobile Keyboard. Imeaction: What enter text is in the keyboard, no actual change
                        //Arreglar: Despues de darle a Enter la caja sigue azul y en foco. Arreglar! (FocusManager)
                    ),
                    label = {Text("1-24")}
                )
            }

        }
    }
}
@Composable
fun ListApps(modifier: Modifier = Modifier,
             onSelect : (String) -> Unit){ //List all apps that are executable
    val context = LocalContext.current
    var appNames by remember { mutableStateOf<List<Pair<String, String>>>(emptyList()) } //initiating both context and the appnames list

    LaunchedEffect(Unit) { //Runs once!
        val paquete = context.packageManager
        val intent = Intent(Intent.ACTION_MAIN).apply { addCategory(Intent.CATEGORY_LAUNCHER) } //Intent: What apps CAN we see? (Launchables)

         appNames = paquete.queryIntentActivities(intent, 0) //get the appnames through a stream
            .map {name -> val paket = name.activityInfo.packageName
                val label = name.loadLabel(paquete).toString()
                paket to label }
            .distinctBy {it.first}
            .sortedWith ( compareBy(String.CASE_INSENSITIVE_ORDER) { it.second }) //sort the apps by their names
    }

    LazyColumn(modifier = modifier.fillMaxSize()
        .padding(16.dp)){ //Lazycolumns to display the apps
        item{
            Text("Installed apps: ${appNames.size} :) \n ") //Not true, but rather installes apps that are launchable, does its job
            Text("These are your apps: \n",
                fontSize = 30.sp)

        }
        if(appNames.isEmpty()){ //Wait while appnames might still be loading
            item{
                Text("Loading...")
            }
        }
        else{
            //Display the app names
            items(appNames) { name ->
                Text("${name.second} \n",
                    fontSize = 20.sp,
                    modifier = Modifier.clickable{
                        onSelect(name.first)
                    }
                )
            }
        }

    }

}



