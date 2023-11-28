package pl.vemu.zsme.ui.components

import android.view.View
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
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
fun WebView(html: String, style: TextStyle = LocalTextStyle.current) {
    val textColor =
        style.color.takeOrElse {
            LocalContentColor.current
        }.toArgb()

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

            // Prevents from crashing when clicked back button in app bar.
            setLayerType(View.LAYER_TYPE_SOFTWARE, null)
            loadData(styledHtml, "text/html; charset=UTF-8", null)
        }
    }, update = {
        it.loadData(styledHtml, "text/html; charset=UTF-8", null)
    }, onRelease = {
        it.destroy()
    })
}
