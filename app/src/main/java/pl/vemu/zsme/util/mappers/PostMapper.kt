package pl.vemu.zsme.util.mappers

import pl.vemu.zsme.data.model.*
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
        thumbnail = entity.embedded.wpFeaturedmedia?.get(0)?.mediaDetails?.sizes?.medium?.sourceUrl,
        fullImage = entity.embedded.wpFeaturedmedia?.get(0)?.mediaDetails?.sizes?.full?.sourceUrl,
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
            author = arrayListOf(Author(model.author)),
            wpFeaturedmedia = arrayListOf(
                WpFeaturedmedia(
                    MediaDetails(
                        Sizes(
                            medium = Image(model.thumbnail),
                            full = Image(model.fullImage)
                        )
                    )
                )
            ),
            category = arrayListOf(arrayListOf(Category(model.category))),
        ),
    )

    override fun mapFromEntityList(entities: List<PostEntity>): List<PostModel> =
        entities.map { mapFromEntity(it) }

    override fun mapToEntityList(models: List<PostModel>): List<PostEntity> =
        models.map { mapToEntity(it) }
}