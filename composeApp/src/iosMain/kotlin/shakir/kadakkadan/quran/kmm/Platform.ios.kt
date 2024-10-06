package shakir.kadakkadan.quran.kmm
import platform.Foundation.NSString
import platform.UIKit.UIActivityViewController
import platform.UIKit.UIActivityItemProvider


import platform.UIKit.UIDevice

class IOSPlatform: Platform {




    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

actual fun getPlatform(): Platform = IOSPlatform()





