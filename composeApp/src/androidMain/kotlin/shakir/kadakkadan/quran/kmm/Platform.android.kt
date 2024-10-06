package shakir.kadakkadan.quran.kmm

import android.content.SharedPreferences
import android.os.Build
import com.russhwolf.settings.Settings
import com.russhwolf.settings.SharedPreferencesSettings

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"
}

actual fun getPlatform(): Platform = AndroidPlatform()

