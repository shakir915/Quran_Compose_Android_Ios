package shakir.kadakkadan.quran.kmm



interface Platform {
    val name: String
}

expect fun getPlatform(): Platform



