package pl.vemu.zsme.data.repo

import org.jsoup.Jsoup
import pl.vemu.zsme.data.db.PostDAO
import pl.vemu.zsme.data.model.DetailModel
import javax.inject.Inject

class DetailRepo @Inject constructor(private val postDAO: PostDAO) {

    suspend fun getPostModelById(id: Int) = postDAO.getById(id)

    suspend fun getDetail(content: String): DetailModel {
        val document = Jsoup.parse(content)
        val imgs = document.select("img")
        document.select(".wp-block-image, .ngg-gallery-thumbnail-box, .wp-block-gallery").remove()
        return if (imgs.isEmpty()) DetailModel(null, content)
        else DetailModel(imgs.map {
            it.attr("src")
        }, document.html())
    }
}