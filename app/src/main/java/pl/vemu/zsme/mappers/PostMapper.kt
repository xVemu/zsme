package pl.vemu.zsme.mappers

import pl.vemu.zsme.data.Content
import pl.vemu.zsme.data.Excerpt
import pl.vemu.zsme.data.PostEntity
import pl.vemu.zsme.data.Title
import pl.vemu.zsme.model.Post
import pl.vemu.zsme.util.EntityMapper
import javax.inject.Inject

class PostMapper @Inject constructor() : EntityMapper<PostEntity, Post> {
    override fun mapFromEntity(entity: PostEntity) = Post(
            id = entity.id,
            date = entity.date,
            link = entity.link,
            title = entity.title.rendered,
            content = entity.content.rendered,
            excerpt = entity.excerpt.rendered,
            author = entity.author,
            featuredMedia = entity.featuredMedia,
            categories = entity.categories,
    )

    override fun mapToEntity(model: Post) = PostEntity(
            id = model.id,
            date = model.date,
            link = model.link,
            title = Title(model.title),
            content = Content(model.content),
            excerpt = Excerpt(model.excerpt),
            author = model.author,
            featuredMedia = model.featuredMedia,
            categories = model.categories,
    )

    fun mapFromEntityList(entities: List<PostEntity>) = entities.map { mapFromEntity(it) }
}