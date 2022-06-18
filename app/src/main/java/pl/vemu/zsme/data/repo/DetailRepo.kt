package pl.vemu.zsme.data.repo

import org.jsoup.Jsoup
import pl.vemu.zsme.data.model.DetailModel
import pl.vemu.zsme.data.model.PostModel
import javax.inject.Inject

class DetailRepo @Inject constructor() {

    suspend fun getDetail(postModel: PostModel): DetailModel {
        val document = Jsoup.parse(postModel.content)
        val imgs = document.select("img")
        document.select(".wp-block-image, .ngg-gallery-thumbnail-box, .wp-block-gallery").remove()
        return if (imgs.isEmpty()) DetailModel(postModel.content, null)
        else DetailModel(
            html = document.html(),
            images = imgs.map {
                it.attr("src")
            })
    }
}