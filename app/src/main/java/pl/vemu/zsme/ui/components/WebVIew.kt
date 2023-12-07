package pl.vemu.zsme.ui.components

import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
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

    // Prevents from crashing when clicked back button in app bar.
    AndroidView(modifier = Modifier.alpha(.99F), factory = { context ->
        WebView(context).apply {
            isVerticalScrollBarEnabled = false
            isHorizontalScrollBarEnabled = false
            setBackgroundColor(android.graphics.Color.TRANSPARENT)
            settings.apply {
                //noinspection SetJavaScriptEnabled
                javaScriptEnabled = true
                defaultFontSize = style.fontSize.value.toInt()
//                isAlgorithmicDarkeningAllowed = true implementation("androidx.webkit:webkit:1.9.0") TODO
//                isForceDarkAllowed = true
//                forceDark = true
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
