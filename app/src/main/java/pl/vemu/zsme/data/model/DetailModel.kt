package pl.vemu.zsme.data.model

import com.google.gson.annotations.SerializedName

typealias HtmlString = String
typealias ImageUrl = String

@JvmInline
value class DetailImage(@SerializedName("source_url") val url: String)
