package pl.vemu.zsme.data.model

data class DetailModel(
    val postModel: PostModel,
    val html: String,
    val images: List<String>? = null,
)