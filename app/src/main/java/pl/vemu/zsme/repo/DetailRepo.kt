package pl.vemu.zsme.repo

import org.jsoup.Jsoup
import pl.vemu.zsme.model.DetailModel
import javax.inject.Inject

class DetailRepo @Inject constructor() {

    fun getDetail(content: String): DetailModel {
        val document = Jsoup.parse(content)
        val imgs = document.select("img")
        document.select(".wp-block-image, .wp-block-gallery").remove()
        return if (imgs.isEmpty()) DetailModel(null, content)
        else DetailModel(imgs.map {
            it.attr("src")
        }, document.html())
    }
}