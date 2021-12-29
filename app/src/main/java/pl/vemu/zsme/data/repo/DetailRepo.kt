package pl.vemu.zsme.data.repo

import org.jsoup.Jsoup
import pl.vemu.zsme.data.db.PostDAO
import pl.vemu.zsme.data.model.DetailModel
import javax.inject.Inject

class DetailRepo @Inject constructor(private val postDAO: PostDAO) {

    suspend fun getDetail(postModelId: Int): DetailModel {
        val postModel = postDAO.getById(postModelId)
        val document = Jsoup.parse(postModel.content)
        val imgs = document.select("img")
        document.select(".wp-block-image, .ngg-gallery-thumbnail-box, .wp-block-gallery").remove()
        return if (imgs.isEmpty()) DetailModel(postModel, postModel.content, null)
        else DetailModel(
            postModel = postModel,
            html = document.html(),
            images = imgs.map {
                it.attr("src")
            })
    }
}