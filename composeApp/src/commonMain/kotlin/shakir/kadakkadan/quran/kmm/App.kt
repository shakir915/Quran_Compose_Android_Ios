package shakir.kadakkadan.quran.kmm

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.russhwolf.settings.Settings
import org.jetbrains.compose.ui.tooling.preview.Preview


val pref: Settings = Settings()
var showSettings = mutableStateOf(false)
var verseData = mutableStateOf<VerseData?>(null)


@Composable
@Preview
fun App() {



    MaterialTheme {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF121316))
                .windowInsetsPadding(WindowInsets.safeDrawing)

        ) {
            Home()
            if (showSettings.value)
                SettingsUI()
            if (verseData.value!=null)
                Verse(verseData.value)


        }

//        return@MaterialTheme
//
//        var showContent by remember { mutableStateOf(false) }
//        Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
//            Button(onClick = { showContent = !showContent }) {
//                Text("Click me!")
//            }
//            AnimatedVisibility(showContent) {
//                val greeting = remember { Greeting().greet() }
//                Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
//                    Image(painterResource(Res.drawable.compose_multiplatform), null)
//                    Text("Compose: $greeting")
//                }
//            }
//        }
    }
}