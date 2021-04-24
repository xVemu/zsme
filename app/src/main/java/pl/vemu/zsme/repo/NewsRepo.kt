package pl.vemu.zsme.repo

import pl.vemu.zsme.db.PostDAO
import pl.vemu.zsme.mappers.PostMapper
import pl.vemu.zsme.model.Post
import pl.vemu.zsme.service.ZSMEService

class NewsRepo /*@Inject*/ constructor(
        private val postDAO: PostDAO,
        private val zsmeService: ZSMEService,
        private val postMapper: PostMapper,
) {

    suspend fun getPosts(): List<Post> {
        val postEntities = zsmeService.getPosts()
        val posts = postMapper.mapFromEntityList(postEntities)
        postDAO.insertAll(posts)
        return postDAO.getAll()
    }

}