package pl.vemu.zsme.data.repo

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.koin.core.annotation.Single
import pl.vemu.zsme.data.model.HtmlString
import pl.vemu.zsme.data.model.ImageUrl
import pl.vemu.zsme.data.service.ZSMEService
import javax.inject.Inject

@Single
class DetailRepo(private val zsmeService: ZSMEService) {

    suspend fun removeImgsFromContent(content: String): HtmlString =
        withContext(Dispatchers.Default) {
            val document = Jsoup.parse(content)
            document.select(".wp-block-image, .ngg-gallery-thumbnail-box, .wp-block-gallery")
                .remove()

            document.html()
        }

    suspend fun getImages(id: Int): List<ImageUrl> = zsmeService.getPhotos(id).map { it.url }
}
