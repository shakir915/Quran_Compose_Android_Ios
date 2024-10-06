package shakir.kadakkadan.quran.kmm

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily

import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import multiplatform.network.cmptoast.showToast
import org.jetbrains.compose.resources.ExperimentalResourceApi
import org.jetbrains.compose.resources.Font
import org.jetbrains.compose.resources.painterResource
import quranmulti.composeapp.generated.resources.Res
import quranmulti.composeapp.generated.resources.baseline_settings_24
import quranmulti.composeapp.generated.resources.baseline_share_24
import quranmulti.composeapp.generated.resources.kfgqpc_uthmanic_script_hafs_regular

@OptIn(ExperimentalResourceApi::class)
@Composable
fun Home() {

 val coroutineScope=   rememberCoroutineScope()


    val showList = mutableStateListOf<String>()
    val ayaths = arrayListOf<String>()
    val ayathNumber = arrayListOf<String>()
    val suraNumber = arrayListOf<String>()
    val ayathsTrans = arrayListOf<String>()



    val showListSuperArabic = arrayListOf<String>()
    val showListSuperTrans = arrayListOf<String>()
    val chaptersList = arrayListOf<String>()
    val englishList = arrayListOf<String>()
    LaunchedEffect("") {


        val chapters = Json.parseToJsonElement(
            Res.readBytes("files/info.json").decodeToString()
        ).jsonObject.get("chapters")!!.jsonArray
        for (i in 0 until chapters.size) {
            chaptersList.add(
                chapters.get(i).jsonObject.get("arabicname")!!.jsonPrimitive.content.replace(
                    "سُوْرَةُ",
                    ""
                )
                    .trim()
            )
            englishList.add(chapters[i].jsonObject["name"].toString().trim())
        }
        showList.clear()
        showList.addAll(chaptersList)



        Res.readBytes("files/ara-quranuthmani.txt").decodeToString().lines()
            .forEachIndexed { index, it ->
                ayaths.add(it.split("|")[2])
                suraNumber.add(it.split("|")[0])
                ayathNumber.add(it.split("|")[1])

            }
        Res.readBytes("files/malayalam_kunhi.txt").decodeToString().lines()
            .forEachIndexed { index, s ->
                ayathsTrans.add(s)
            }


    }


    val kfgqpc_uthmanic_script_hafs_regular =
        FontFamily(Font(Res.font.kfgqpc_uthmanic_script_hafs_regular))


    val focusManager = LocalFocusManager.current
    val customTextSelectionColors = TextSelectionColors(
        handleColor = Color.Cyan,
        backgroundColor = Color.Cyan
    )


    val scope = rememberCoroutineScope()

    var searchText by remember { mutableStateOf("") }
    var searchResultCountText by remember { mutableStateOf("Showing 114 Chapters") }

    var superSearch by remember { mutableStateOf(false) }


    fun searchAyath() {
        searchResultCountText = "Searching..."
        scope.launch {
            showList.clear()
            showListSuperArabic.clear()
            showListSuperTrans.clear()
            superSearch = true
            focusManager.clearFocus()
            val searchText = searchText.trim()

            withContext(Dispatchers.IO) {
                val nnn = ayaths.mapIndexed { index, s ->
                    if (removeThashkeel(s).contains(searchText, ignoreCase = true))
                        index
                    else
                        -1
                }
                    .plus(
                        ayathsTrans.mapIndexed { index, s ->
                            if ((s).contains(searchText, ignoreCase = true))
                                index
                            else
                                -1
                        }
                    )
                    .filter { it != -1 }
                    .distinct()





                showList.addAll(
                    nnn.map {
                        suraNumber.get(it) + ":" + ayathNumber.get(it) + " " +
                                chaptersList.get(suraNumber.get(it).toInt() - 1)

                    }

                )

                showListSuperArabic.addAll(
                    nnn.map {
                        ayaths.get(it)
                    }
                )
                showListSuperTrans.addAll(
                    nnn.map {
                        ayathsTrans.get(it)
                    }
                )


            }
            searchResultCountText =
                "${showList.size} Search results for \"$searchText\""

        }


    }

    fun searchSura() {

        superSearch = false
        val searchText = searchText.trim()
        if (searchText.isBlank()) {
            showList.clear()
            showList.addAll(chaptersList)
            searchResultCountText = "Showing 114 Chapters"
        } else {
            showList.clear()






            showList.addAll(chaptersList.filter {
                removeThashkeel(it)
                    .startsWith(
                        searchText,
                        ignoreCase = true
                    )
            }.plus(
                chaptersList.filter {
                    removeThashkeel(it)
                        .contains(
                            searchText,
                            ignoreCase = true
                        )
                }
            )
                .plus(
                    englishList
                        .filter {
                            it.startsWith(
                                searchText,
                                ignoreCase = true
                            )
                        }
                        .map {
                            englishList.indexOf(it)
                        }
                        .map {
                            chaptersList.get(it)
                        }
                )
                .plus(
                    englishList
                        .filter {
                            it.contains(
                                searchText,
                                ignoreCase = true
                            )
                        }
                        .distinct()
                        .map {
                            englishList.indexOf(it)
                        }
                        .map {
                            chaptersList.get(it)
                        }
                )


                .distinct())
            searchResultCountText = "Showing Search Result : ${showList.size} Chapters"
        }



    }




    Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF121316)) {

        Column {
            Row(modifier = Modifier.height(56.dp)) {


                Box(
                    modifier = Modifier
                        .padding(8.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(color = Color.White.copy(alpha = .1f))
                        .align(alignment = Alignment.CenterVertically)
                        .weight(1f),
                ) {
                    Row(modifier = Modifier.align(Alignment.Center)) {

                        Icon(

                            Icons.Default.Search,
                            contentDescription = "Search icon",
                            tint = Color.White,
                            modifier = Modifier
                                .padding(start = 8.dp)
                                .align(alignment = Alignment.CenterVertically)
                        )
                        CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors)
                        {
                            BasicTextField(
                                cursorBrush = SolidColor(Color.Cyan),
                                textStyle = TextStyle(
                                    color = Color.White,
                                    textAlign = TextAlign.Start,
                                ),
                                modifier = Modifier
                                    .align(alignment = Alignment.CenterVertically)
                                    .fillMaxHeight()
                                    .fillMaxWidth()
                                    .wrapContentHeight(align = Alignment.CenterVertically)
                                    .padding(start = 8.dp, end = 8.dp),
                                maxLines = 1,
                                keyboardActions = KeyboardActions {
                                    searchAyath()
                                },

                                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                                value = searchText,
                                onValueChange = { newText ->
                                    searchText = newText
                                    searchSura()
                                })
                        }
                    }


                }
                Box(
                    modifier = Modifier
                        .clickable {
                            searchAyath()
                        }
                        .align(alignment = Alignment.CenterVertically)
                        .padding(8.dp)
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(8.dp)) // Adjust the corner radius as needed
                        .background(color = Color.White.copy(alpha = .1f))
                ) {
                    Text(
                        "Search\nAya(Arabic) /\ntranslation",
                        fontSize = 8.sp,
                        textAlign = TextAlign.Center,
                        lineHeight = 10.sp,
                        modifier = Modifier
                            .align(Alignment.Center)
                            .padding(start = 8.dp, end = 8.dp),
                        color = Color.White
                    )
                }
                if (superSearch)



                    Image(
                        painter = painterResource(Res.drawable.baseline_share_24),
                        contentDescription = "Share Clipboard Content",
//                                    textAlign = TextAlign.Right,
                        modifier = Modifier
                            .align(alignment = Alignment.CenterVertically)
                            .padding(12.dp)
                            .alpha(if (superSearch) 1f else .2f)
                            .clickable {
                                scope.launch {
                                    try {
                                        var s =
                                            showList
                                                .mapIndexed { index, s ->
                                                    showList.get(index) + "\n\n" +
                                                            showListSuperArabic.get(
                                                                index
                                                            ) + "\n\n" +
                                                            showListSuperTrans.get(
                                                                index
                                                            ) + ""
                                                }
                                                .joinToString("\n\n\n")


                                        //todo
//
//                                        val sendIntent = Intent()
//                                        sendIntent.action = Intent.ACTION_SEND
//                                        sendIntent.putExtra(Intent.EXTRA_TEXT, s)
//                                        sendIntent.type = "text/plain"
//
//                                        val shareIntent =
//                                            Intent.createChooser(sendIntent, null)
//                                        startActivity(shareIntent)
                                    } catch (e: Exception) {
                                        showToast(
                                            message = e.message ?: ""
                                        )

                                    }

                                }

                            },
                        //fontFamily = kfgqpc_uthmanic_script_hafs_regular,
                    )

                Image(
                    painter = painterResource(Res.drawable.baseline_settings_24),
                    contentDescription = "settings",
//                                    textAlign = TextAlign.Right,
                    modifier = Modifier
                        .align(alignment = Alignment.CenterVertically)
                        .padding(12.dp)
                        .alpha(1f)
                        .clickable {

                            showSettings.value = true


                        },
                    //fontFamily = kfgqpc_uthmanic_script_hafs_regular,
                )


            }


            Text(
                searchResultCountText,
                modifier = Modifier.padding(start = 8.dp, end = 8.dp),
                fontSize = 10.sp,
                color = Color.White
            )




                if (superSearch)
                    LazyColumn(modifier = Modifier.fillMaxSize()) {
                        items(showList.size) { index ->
                            Column(
                                modifier = Modifier.clickable {

                                    verseData.value=VerseData(
                                        chapterNumber = showList.get(index)
                                            .split(":")[0].toInt(),
                                        ScrollToAyaNumber = showList.get(index)
                                            .split(":")[1].split(" ")[0].toInt(),
                                        chapterName =  chaptersList.get(
                                            showList.get(index)
                                                .split(":")[0].toInt() - 1
                                        )


                                    )



                                }

                            ) {
                                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                                    Text(
                                        showList.get(index),
                                        modifier = Modifier.padding(8.dp),
                                        color = Color.White
                                    )
                                }
                                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                                    Text(
                                        text = showListSuperArabic.get(index),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp)
                                            .align(Alignment.End),
                                        fontFamily = kfgqpc_uthmanic_script_hafs_regular,
                                        fontSize = prefs.getInt(
                                            "font_size_arabic",
                                            16
                                        ).sp,
                                        color = Color.White,
                                        lineHeight = 1.4.em,


                                        )
                                }
                                CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                                    Text(
                                        showListSuperTrans.get(index),
                                        modifier = Modifier.padding(8.dp),
                                        color = Color.White,
                                        fontSize = prefs.getInt(
                                            "font_size_malayalam",
                                            16
                                        ).sp,
                                        lineHeight = 1.4.em,
                                    )
                                }

                                Box(
                                    modifier = Modifier.padding(16.dp),
                                ) {

                                }

                            }


                        }
                    }
                else {

                    LazyColumn(modifier = Modifier
                        .fillMaxSize()) {
                        items(showList.size) { index ->

                            Row(modifier = Modifier

                                .defaultMinSize(minWidth = 150.dp)
                                .clickable {
                                    if (!superSearch) {
                                        verseData.value=VerseData(
                                            chapterNumber = chaptersList.indexOf(
                                                showList.get(index)
                                            ) + 1,
                                            ScrollToAyaNumber = null,
                                            chapterName =  showList.get(index)
                                        )


                                    }


                                }) {


                                Text(
                                    if (!superSearch) (chaptersList.indexOf(
                                        showList.get(
                                            index
                                        )
                                    ) + 1).toString() else " ",
                                    modifier = Modifier

                                        .padding(8.dp),
                                    color = if (!superSearch) Color.White else Color.Transparent
                                )
                                Text(
                                    showList.get(index),
                                    modifier = Modifier.padding(8.dp),
                                    color = Color.White
                                )
                            }
                        }
                    }
                }


        }
    }
}


fun removeThashkeel(s: String): String {
    val regex = Regex("\\p{Mn}") // Matches any combining character (tashkeel)
    return regex.replace(s, "") // Replace all matches with an empty string
}





