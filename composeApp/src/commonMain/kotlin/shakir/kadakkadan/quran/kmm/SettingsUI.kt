package shakir.kadakkadan.quran.kmm

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.launch
import multiplatform.network.cmptoast.showToast
import org.jetbrains.compose.resources.painterResource
import quranmulti.composeapp.generated.resources.Res
import quranmulti.composeapp.generated.resources.baseline_add_24
import quranmulti.composeapp.generated.resources.baseline_delete_24
import quranmulti.composeapp.generated.resources.baseline_remove_24
import quranmulti.composeapp.generated.resources.baseline_share_24
import quranmulti.composeapp.generated.resources.baseline_video_library_24
import quranmulti.composeapp.generated.resources.kfgqpc_uthmanic_script_hafs_regular

@Composable
fun SettingsUI(){
    val kfgqpc_uthmanic_script_hafs_regular =
        FontFamily(org.jetbrains.compose.resources.Font(Res.font.kfgqpc_uthmanic_script_hafs_regular))


    var font_size_malayalam by remember { mutableStateOf(prefs.getInt("font_size_malayalam",16)) }
    var font_size_arabic by remember { mutableStateOf(prefs.getInt("font_size_arabic",20)) }


        // A surface container using the 'background' color from the theme
        Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF121316)) {


            Column {

                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(color = Color.White.copy(alpha = .05f))
                ) {


                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Go Back",
                        tint = Color.White,
                        modifier = Modifier
                            .padding(12.dp).clickable {
                                verseData.value = null
                                showSettings.value=false
                            }


                    )

                    Text(
                        "Settings", color = Color.White,

                        fontSize = 16.sp,
                        modifier = Modifier.padding(horizontal = 8.dp)

                    )




                }

            Column(modifier = Modifier.padding(16.dp)) {


                Row {
                    Text(
                        "Font Size (Quran) $font_size_arabic",
                        modifier = Modifier.padding(8.dp),
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.weight(1f))
                    Image(
                        painter = painterResource(Res.drawable.baseline_remove_24),
                        contentDescription = "font minus size  arabic",
                        colorFilter = ColorFilter.tint(color = Color.White),
                        modifier = Modifier
                            .align(alignment = Alignment.CenterVertically)
                            .padding(12.dp)
                            .clickable {
                                if (font_size_arabic > 5)
                                    font_size_arabic--
                                prefs.putInt("font_size_arabic", font_size_arabic)

                            },
                        //fontFamily = kfgqpc_uthmanic_script_hafs_regular,
                    );

                    Image(
                        painter = painterResource(Res.drawable.baseline_add_24),
                        contentDescription = "font plus size arabic ",
                        colorFilter = ColorFilter.tint(color = Color.White),
                        modifier = Modifier
                            .align(alignment = Alignment.CenterVertically)
                            .padding(12.dp)
                            .clickable {
                                if (font_size_arabic < 50)
                                    font_size_arabic++
                                prefs.putInt("font_size_arabic", font_size_arabic)

                            },
                        //fontFamily = kfgqpc_uthmanic_script_hafs_regular,
                    )

                }



                Row {
                    Text(
                        "Font Size (Translation) $font_size_malayalam",
                        modifier = Modifier.padding(8.dp),
                        color = Color.White
                    )

                    Spacer(modifier = Modifier.weight(1f))
                    Image(
                        painter = painterResource(Res.drawable.baseline_remove_24),
                        contentDescription = "font minus size  malayalam",
                        colorFilter = ColorFilter.tint(color = Color.White),
                        modifier = Modifier
                            .align(alignment = Alignment.CenterVertically)
                            .padding(12.dp)
                            .clickable {
                                if (font_size_malayalam > 5)
                                    font_size_malayalam--
                                prefs.putInt("font_size_malayalam", font_size_malayalam)

                            },
                        //fontFamily = kfgqpc_uthmanic_script_hafs_regular,
                    );

                    Image(
                        painter = painterResource(Res.drawable.baseline_add_24),
                        contentDescription = "font plus size malayalam",
                        colorFilter = ColorFilter.tint(color = Color.White),
                        modifier = Modifier
                            .align(alignment = Alignment.CenterVertically)
                            .padding(12.dp)
                            .clickable {
                                if (font_size_malayalam < 50)
                                    font_size_malayalam++
                                prefs.putInt("font_size_malayalam", font_size_malayalam)
                            },
                        //fontFamily = kfgqpc_uthmanic_script_hafs_regular,
                    )

                }

                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                    Text(
                        text = "بِسۡمِ ٱللَّهِ ٱلرَّحۡمَٰنِ ٱلرَّحِيم",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .align(Alignment.End),
                        fontFamily = kfgqpc_uthmanic_script_hafs_regular,
                        fontSize = font_size_arabic.sp,
                        lineHeight = 1.4.em,
                        color = Color.White


                    )
                }


                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                    Text(
                        "പരമകാരുണികനും കരുണാനിധിയുമായ അല്ലാഹുവിന്റെ നാമത്തില്\u200D",
                        modifier = Modifier.padding(8.dp),
                        color = Color.White,

                        lineHeight = 1.4.em,
                        fontSize = font_size_malayalam.sp
                    )
                }

            }
        }


        }


}