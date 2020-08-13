package com.example.aionews

data class NewsProvider(var ID: Int = 0, var rssFile: String = "", var title: String = "", var isCheck: Boolean= false){
    companion object{
        val listNewsProvider: List<NewsProvider> = listOf(

            NewsProvider(0, "tuoitre.txt" , "Tuổi trẻ online", false),

            NewsProvider(1, "thanhnien.txt", "Báo Thanh Niên", false),

            NewsProvider(2, "vnexpress.txt", "VNExpress", false),

            NewsProvider(3, "vtvnews.txt", "VTV News", false),

            NewsProvider(4, "nld.txt", "Báo Người lao động", false),

            NewsProvider(5, "b24h.txt", "24h", false),

            NewsProvider(6, "gdvtd.txt", "Báo Giáo dục và Thời đại", false),

            NewsProvider(7, "tns.txt", "Trải Nghiệm Số", false),

            NewsProvider(8, "ict.txt", "ICTNews", false),

            NewsProvider(9, "tinhte.txt", "Tinh Tế", false)
        )
    }

    fun getListProvider(): List<NewsProvider>{
        return listNewsProvider
    }
}

data class Rss(var rss: String = "", var category: String = "")