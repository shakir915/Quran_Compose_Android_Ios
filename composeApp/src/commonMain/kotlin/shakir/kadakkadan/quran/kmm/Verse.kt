package shakir.kadakkadan.quran.kmm

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.text.font.FontFamily
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
import org.jetbrains.compose.resources.painterResource
import quranmulti.composeapp.generated.resources.Res
import quranmulti.composeapp.generated.resources.baseline_delete_24
import quranmulti.composeapp.generated.resources.baseline_share_24
import quranmulti.composeapp.generated.resources.baseline_video_library_24
import quranmulti.composeapp.generated.resources.kfgqpc_uthmanic_script_hafs_regular
import quranmulti.composeapp.generated.resources.roboto_medium


data class VerseData(
    val chapterNumber: Int,
    val ScrollToAyaNumber: Int?,
    val chapterName: String,
)

lateinit var chapterNumber: String


@OptIn(ExperimentalResourceApi::class)
suspend fun getShareText() = withContext(Dispatchers.IO) {
    val chaptersList = arrayListOf<String>()
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
    }

    val a = prefs.getStringOrNull("selected")?.split("|")
    var s = ""
    a?.forEach {
        try {
            println("hhghhhhh $it")
            val chapterNumber = it.split(":").get(0).toInt()
            val ayaIndices = it.split(":").get(1).split(",").map { it.toInt() }
            val chapterName = chaptersList.get(chapterNumber - 1)
            val at = getAyaths(chapterNumber.toString())


            var prev = -222

            s += "\u202A\n-----------------------------\n"
            s += "Quran:$chapterNumber ($chapterName)"
            s += "\n----------------------------- \u202A"
            ayaIndices.sorted().distinct().forEachIndexed { index, ayaIndxe ->
                if (prev == -222 || prev == ayaIndxe - 1 || index == ayaIndices.last()) {
                    s += "\n\n"
                } else {
                    s += "\u202A \n\n....\n\n \u202A"
                }


                s += "\u202B${at.first.get(ayaIndxe)}  {${
                    (ayaIndxe + 1).toArabicNumerals()
                }}\u202B"
                s += "\n\n"
                s += "\u202A" + (ayaIndxe + 1).toString() + ". " + at.second.get(ayaIndxe) + "\u202A"


                prev = ayaIndxe
            }

            s += "\n\n"
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    return@withContext s


}


@OptIn(ExperimentalResourceApi::class)
suspend fun getAyaths(chapterNumber: String) = withContext(Dispatchers.IO) {
    val ayaths = arrayListOf<String>()
    val ayathsTrans = arrayListOf<String>()
    var start = -1
    var end = -1
    Res.readBytes("files/ara-quranuthmani.txt").decodeToString().lines()
        .forEachIndexed { index, it ->
            val splits = it.split("|")
            if (splits[0] == chapterNumber) {
                ayaths.add(splits[2])
                if (start == -1) {
                    start = index
                }
                end = index


            }
        }
    Res.readBytes("files/malayalam_kunhi.txt").decodeToString().lines()
        .subList(start, end + 1).forEachIndexed { index, s ->
            ayathsTrans.add(s)
        }

    return@withContext ayaths to ayathsTrans
}


@Composable
fun Verse(screenData: VerseData?) {
    if (screenData != null) {



        var loaded by remember { mutableStateOf(false) }
        var bismi by remember { mutableStateOf("") }
        val ayaths = mutableStateListOf<String>()
        val ayathsTrans = mutableStateListOf<String>()

        chapterNumber = screenData.chapterNumber.toString()
        val chapterName = screenData.chapterName ?: ""
        val kfgqpc_uthmanic_script_hafs_regular =
            FontFamily(org.jetbrains.compose.resources.Font(Res.font.kfgqpc_uthmanic_script_hafs_regular))
        val roboto_medium =
            FontFamily(org.jetbrains.compose.resources.Font(Res.font.roboto_medium))
        var otherSuraAyas = 0
        LaunchedEffect("") {
            if (chapterNumber != "9")
                bismi = getAyaths("1").first.first()
            val at = getAyaths(chapterNumber)
            ayaths.clear()
            ayathsTrans.clear()
            ayaths.addAll(at.first)
            ayathsTrans.addAll(at.second)


            var initSelected = try {
                val a = prefs.getStringOrNull("selected")?.split("|")
                val f = a?.find { it.startsWith("$chapterNumber:") }
                otherSuraAyas = 0
                a?.forEach {
                    if (it != f) {
                        otherSuraAyas += it.split(":").get(1).split(",").size
                    }
                }
                (f?.split(":")?.getOrNull(1)?.split(",")?.map { it.toInt() } ?: emptyList())
            } catch (e: Exception) {
                emptyList()
            }
            loaded = true
        }


        val lazyListState = rememberLazyListState()


        val scope = rememberCoroutineScope()


        val selected = remember { mutableStateListOf<Int>() }


        val clipboardManager = LocalClipboardManager.current

        var selectionEnabledByLongPress by remember { mutableStateOf(false) }

        LaunchedEffect(key1 = "start") {
            // Scroll to the desired item index
            if (screenData?.ScrollToAyaNumber != null)
                scope.launch {
                    lazyListState.scrollToItem(screenData.ScrollToAyaNumber!!)
                }
        }


        // A surface container using the 'background' color from the theme
        Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF121316)) {
            if (loaded)
                Column {
                    Row(
                        horizontalArrangement = Arrangement.End,
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
                                }


                        )

                        Text(
                            (chapterNumber) + " " + chapterName, color = Color.White,

                            fontSize = 16.sp,
                            modifier = Modifier.padding(horizontal = 8.dp)

                        )

                        Spacer(modifier = Modifier.weight(1.0f)) // Fills remaining height


                        if (prefs.getBoolean("enable_mail", false)) Text(
                            text = "mail",
                            textAlign = TextAlign.Right,
                            modifier = Modifier
                                .padding(8.dp)
                                .clickable {

                                    println(" clickable clicked ")

                                    scope.launch {


                                        try {


                                            fun openEmailApp(
                                                subject: String,
                                                recipients: Array<String>,
                                                message: String,
                                                cc: Array<String>? = null,
                                                bcc: Array<String>? = null,
                                                packageName: String
                                            ) {
                                                //todo
//                                            val intent =
//                                                Intent(Intent.ACTION_SENDTO).apply {
//                                                    data = Uri.parse("mailto:")
//                                                    putExtra(
//                                                        Intent.EXTRA_EMAIL,
//                                                        recipients
//                                                    )
//                                                    putExtra(
//                                                        Intent.EXTRA_SUBJECT,
//                                                        subject
//                                                    )
//                                                    putExtra(Intent.EXTRA_TEXT, message)
//                                                    cc?.let {
//                                                        putExtra(
//                                                            Intent.EXTRA_CC,
//                                                            it
//                                                        )
//                                                    }
//                                                    bcc?.let {
//                                                        putExtra(
//                                                            Intent.EXTRA_BCC,
//                                                            it
//                                                        )
//                                                    }
//                                                }
//
//                                            val emailAppIntent = Intent.createChooser(
//                                                intent,
//                                                "Choose an Email Client"
//                                            )
//                                            emailAppIntent.setPackage(packageName)
//
//                                            try {
//                                                startActivity(emailAppIntent)
//                                            } catch (e: Exception) {
//                                                emailAppIntent.setPackage(null)
//                                                startActivity(emailAppIntent)
//                                            }
                                            }


                                            var s = getShareText()

                                            val splits = s
                                                .split("-----------------------------")
                                                .filter {
                                                    it.contains("Quran:")
                                                }
                                                .map {
                                                    it
                                                        .trimIndent()
                                                        .trim()
                                                        .trimIndent()
                                                        .trim()
                                                }


                                            val recipients =
                                                arrayOf("nazmiyapallikkara@gmail.com")
                                            val cc = arrayOf<String>()
                                            val bcc = arrayOf(
                                                "nazmiyapallikkara03@gmail.com",
                                                "nazmiyapallikkara1@gmail.com"
                                            )
                                            val subject = splits.joinToString(",")
                                            val message = s

                                            openEmailApp(
                                                subject,
                                                recipients,
                                                message,
                                                cc,
                                                bcc,
                                                "com.google.android.gm"
                                            )
                                        } catch (e: Exception) {
                                            e.printStackTrace()
                                        }
                                    }

                                },
                            fontFamily = kfgqpc_uthmanic_script_hafs_regular,
                            color = Color.White
                        )

                        Text(
                            (otherSuraAyas + selected.size).toString(), color = Color.White,

                            fontSize = 10.sp,
                            modifier = Modifier.align(alignment = Alignment.Top)

                        )

                        Image(
                            painter = painterResource(Res.drawable.baseline_share_24),
                            contentDescription = "Share Clipboard Content",
//                                    textAlign = TextAlign.Right,
                            modifier = Modifier
                                .padding(12.dp)
                                .clickable {


                                    if (selected.size + otherSuraAyas == 0) {
                                        showToast("Long-press to select an aya ")

                                    } else {



                                        scope.launch {
                                            //todo
//                                        var s = getShareText()
//                                        val sendIntent = Intent()
//                                        sendIntent.action = Intent.ACTION_SEND
//                                        sendIntent.putExtra(Intent.EXTRA_TEXT, s)
//                                        sendIntent.type = "text/plain"
//
//                                        val shareIntent =
//                                            Intent.createChooser(sendIntent, null)
//                                        startActivity(shareIntent)

                                        }


                                    }


                                },
                            //fontFamily = kfgqpc_uthmanic_script_hafs_regular,
                        )





                        if (false)
                            Image(
                                painter = painterResource(Res.drawable.baseline_video_library_24),
                                contentDescription = "Create Video",
//                                    textAlign = TextAlign.Right,
                                modifier = Modifier
                                    .padding(12.dp)
                                    .clickable {

                                        //  permission()

                                    },
//                                    fontFamily = kfgqpc_uthmanic_script_hafs_regular,
                            )


                        Image(
                            painter = painterResource(Res.drawable.baseline_delete_24),
                            contentDescription = "Clear Clipboard ",
//                                    textAlign = TextAlign.Right,
                            modifier = Modifier
                                .padding(12.dp)
                                .clickable {
                                    otherSuraAyas = 0
                                    selected.clear()
                                    prefs
                                        .remove("selected")

                                },
//                                    fontFamily = kfgqpc_uthmanic_script_hafs_regular,
                        )


                    }
                    LazyColumn(
                        state = lazyListState,
                        modifier = Modifier.fillMaxSize()


                    ) {
                        items(if (chapterNumber == "1") ayaths.size else ayaths.size + 1) { indexPlus1 ->

                            if (indexPlus1 == 0) {
                                Text(
                                    text =
                                    if (chapterNumber == "1") "أعوذُ بِٱللَّهِ مِنَ ٱلشَّيۡطَٰنِ ٱلرَّجِيمِ" else
                                        bismi,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(8.dp)
                                        .align(Alignment.CenterHorizontally),
                                    fontFamily = kfgqpc_uthmanic_script_hafs_regular,
                                    fontSize = 11.sp,
                                    color = Color.White,
                                    textAlign = TextAlign.Center


                                )
                            } else {

                                val index = indexPlus1 - 1

                                // /* "\u06DD"*/,
                                Column(modifier = Modifier
                                    .pointerInput(Unit) {


                                        fun tapppp() {
                                            println("a ${selected.joinToString()}")
                                            if (selectionEnabledByLongPress) {
                                                if (selected.contains(index)) {
                                                    selected.remove(index)
                                                } else {
                                                    selected.add(index)
                                                }

                                                var s = prefs.getString("selected", "") ?: ""
                                                var c = prefs
                                                    .getStringOrNull("selected")
                                                    ?.split("|")
                                                    ?.find { it.startsWith("$chapterNumber:") }
                                                val cNew = "$chapterNumber:${
                                                    selected.joinToString(",")
                                                }"
                                                if (c != null) {
                                                    s = s.replace(c, cNew)
                                                } else {
                                                    if (s.isBlank()) s = cNew
                                                    else s = s + "|" + cNew
                                                }

                                                println("a ${selected.joinToString()}")
                                                println("sssssssssss $s")
                                                prefs
                                                    .putString("selected", s)

                                            }


                                        }

                                        detectTapGestures(onLongPress = {
                                            selectionEnabledByLongPress = true
                                            tapppp()
                                        }, onTap = {
                                            tapppp()
                                        }

                                        )
                                    }
//                                            .clickable {
//
//                                            }
                                    .background(
                                        if (selectionEnabledByLongPress && selected.contains(
                                                index
                                            )
                                        ) Color.White.copy(alpha = .1f)
                                        else Color(0)
                                    )


                                ) {
                                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
                                        Text(
                                            text = "${ayaths.get(index)} ${
                                                (index + 1).toArabicNumerals()
                                            }",
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .padding(8.dp)
                                                .align(Alignment.End),
                                            fontFamily = kfgqpc_uthmanic_script_hafs_regular,
                                            fontSize = (prefs.getInt("font_size_arabic", 20)).sp,
                                            lineHeight = 1.4.em,
                                            color = Color.White


                                        )
                                    }
                                    CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Ltr) {
                                        Text(
                                            (index + 1).toString() + ". " + ayathsTrans.get(
                                                index
                                            ),
                                            modifier = Modifier.padding(8.dp),
                                            color = Color.White,
                                            lineHeight = 1.4.em,
                                            fontSize = prefs.getInt("font_size_malayalam", 16).sp
                                        )
                                    }

                                }
                            }
                        }


                    }
                }


        }


    }


}


fun Int.toArabicNumerals(): String {
    val arabicNumerals = arrayOf("٠", "١", "٢", "٣", "٤", "٥", "٦", "٧", "٨", "٩")
    val digits = this.toString().toCharArray()
    val result = StringBuilder()

    for (digit in digits) {
        val arabicDigit = arabicNumerals[digit.toInt() - '0'.toInt()]
        result.append(arabicDigit)
    }

    return result.toString()
}