package pl.vemu.zsme.data.model

import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonTransformingSerializer
import kotlinx.serialization.json.jsonObject

typealias HtmlString = String
typealias ImageUrl = String

@JvmInline
@Serializable
value class DetailImage(@Serializable(with = DetailImageDeserializer::class) val url: ImageUrl)

private object DetailImageDeserializer : JsonTransformingSerializer<String>(String.serializer()) {
    override fun transformDeserialize(element: JsonElement): JsonElement =
        element.jsonObject["media_details"]?.jsonObject?.get("sizes")?.jsonObject?.get("full")?.jsonObject?.get("source_url")
            ?: throw RuntimeException("DetailImage is null")
}
