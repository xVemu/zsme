package pl.vemu.zsme.ui.components

import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import pl.vemu.zsme.launchCustomTabs

@Composable
fun WebViewCompose(html: String, style: TextStyle = LocalTextStyle.current) {
    val textColor =
        style.color.takeOrElse {
            LocalContentColor.current
        }.toArgb()
    val linkColor = MaterialTheme.colorScheme.primary.toArgb()

    val styledHtml = """
        <style type="text/css">
        html, body {
            margin: 0px;
            padding: 0px;
            color: rgb(${textColor.red} ${textColor.green} ${textColor.blue});
        }
        table {
            border-collapse: collapse;
        }
        table, th, td {
            border: 1px solid;
        }
        a {
            color: rgb(${linkColor.red} ${linkColor.green} ${linkColor.blue});
        }
        </style>
    """ + html

    AndroidView(factory = { context ->
        WebView(context).apply {
            isVerticalScrollBarEnabled = false
            isHorizontalScrollBarEnabled = false
            setBackgroundColor(android.graphics.Color.TRANSPARENT)
            settings.apply {
                //noinspection SetJavaScriptEnabled
                javaScriptEnabled = true
                defaultFontSize = style.fontSize.value.toInt()
            }
            webViewClient = object : WebViewClient() {
                override fun shouldOverrideUrlLoading(
                    view: WebView?,
                    request: WebResourceRequest?,
                ): Boolean {
                    context.launchCustomTabs(request?.url?.toString() ?: return false)
                    return true
                }
            }

            loadData(styledHtml, "text/html; charset=UTF-8", null)
        }
    }, update = {
        it.loadData(styledHtml, "text/html; charset=UTF-8", null)
    }, onRelease = {
        it.destroy()
    })
}
