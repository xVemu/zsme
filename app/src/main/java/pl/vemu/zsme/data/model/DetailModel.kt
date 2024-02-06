package pl.vemu.zsme.data.model

import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import java.lang.reflect.Type

typealias HtmlString = String
typealias ImageUrl = String

@JvmInline
value class DetailImage(val url: ImageUrl)

class DetailImageDeserializer : JsonDeserializer<DetailImage> {

    override fun deserialize(
        json: JsonElement,
        typeOfT: Type,
        context: JsonDeserializationContext,
    ): DetailImage {
        val content = json.asJsonObject["media_details"].asJsonObject["sizes"]
            .asJsonObject["full"].asJsonObject["source_url"].asString

        return DetailImage(content)
    }
}
