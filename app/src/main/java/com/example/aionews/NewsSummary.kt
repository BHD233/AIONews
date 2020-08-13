package com.example.aionews

data class NewsSummary(var provider: String = "", var title: String = "", var link: String = "",
                       var imgSrc: String = "", var datePub: String = "", var category: String = "",
                       var imgRaw: ByteArray = ByteArray(0)
){
    fun revertDate(): String{
        var s: String = datePub
        var first: Int = s.indexOf("/", 0)
        var second: Int = s.indexOf("/", first + 1)
        var third: Int = s.indexOf(" ", second + 1)

        var a = s.substring(0, first)
        var b = s.substring(first + 1, second)
        var c = s.substring(second + 1, third)
        var d = s.substring(third, s.length)


        datePub = "$c/$b/$a$d"

        return datePub
    }
}