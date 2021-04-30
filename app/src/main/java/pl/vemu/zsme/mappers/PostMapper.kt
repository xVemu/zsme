package pl.vemu.zsme.mappers

import pl.vemu.zsme.data.*
import pl.vemu.zsme.model.PostModel
import pl.vemu.zsme.util.EntityMapper
import javax.inject.Inject

class PostMapper @Inject constructor() : EntityMapper<PostEntity, PostModel> {
    override fun mapFromEntity(entity: PostEntity) = PostModel(
            id = entity.id,
            date = entity.date,
            link = entity.link,
            title = entity.title.rendered,
            content = entity.content.rendered,
            excerpt = entity.excerpt.rendered,
            author = entity.embedded.author[0].name,
            thumbnail = entity.embedded.wpFeaturedmedia[0].mediaDetails.sizes.thumbnail.sourceUrl,
            category = entity.embedded.category[0][0].name,
    )

    override fun mapToEntity(model: PostModel) = PostEntity(
            id = model.id,
            date = model.date,
            link = model.link,
            title = Title(model.title),
            content = Content(model.content),
            excerpt = Excerpt(model.excerpt),
            embedded = Embedded(
                    author = arrayListOf(Author(0, model.author)),
                    wpFeaturedmedia = arrayListOf(WpFeaturedmedia(MediaDetails(Sizes(Thumbnail(model.thumbnail))))),
                    category = arrayListOf(arrayListOf(Category(0, model.category))),
            ),
    )

    fun mapFromEntityList(entities: List<PostEntity>) = entities.map { mapFromEntity(it) }
}