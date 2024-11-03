package pl.vemu.zsme.util.mappers

import org.koin.core.annotation.Single
import pl.vemu.zsme.data.model.*
import pl.vemu.zsme.util.EntityMapper

@Single
class PostMapper : EntityMapper<PostEntity, PostModel> {
    override fun mapFromEntity(entity: PostEntity) = PostModel(
        id = entity.id,
        date = entity.date,
        link = entity.link,
        title = entity.title.rendered,
        content = entity.content.rendered,
        excerpt = entity.excerpt.rendered,
        author = entity.embedded.author[0].name,
        thumbnail = entity.embedded.wpFeaturedmedia?.get(0)?.mediaDetails?.sizes?.thumbnail?.sourceUrl,
        fullImage = entity.embedded.wpFeaturedmedia?.get(0)?.sourceUrl,
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
            author = listOf(Author(0, model.author)),
            wpFeaturedmedia = listOf(
                WpFeaturedmedia(
                    MediaDetails(
                        Sizes(
                            thumbnail = Image(model.thumbnail),
                        )
                    ),
                    model.fullImage,
                )
            ),
            category = listOf(listOf(Category(0, model.category))),
        ),
    )

    override fun mapFromEntityList(entities: List<PostEntity>): List<PostModel> =
        entities.map { mapFromEntity(it) }

    override fun mapToEntityList(models: List<PostModel>): List<PostEntity> =
        models.map { mapToEntity(it) }
}
