package pl.vemu.zsme.ui.components

import android.view.View
import android.widget.TextView
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat

/*TODO*/
@Composable
fun HTMLText(
    html: String,
    modifier: Modifier = Modifier,
    centered: Boolean = false,
    color: Color = MaterialTheme.colorScheme.onSurface,
    textStyle: TextStyle = MaterialTheme.typography.bodyMedium
) {
    AndroidView(
        modifier = modifier.fillMaxWidth(),
        factory = { context ->
            TextView(context).apply {
                text = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_COMPACT).trimEnd()
                if (centered) textAlignment =
                    View.TEXT_ALIGNMENT_CENTER
                setTextColor(color.toArgb())
                textSize = textStyle.fontSize.value
                setLineSpacing(textStyle.lineHeight.value - textStyle.fontSize.value, 1F)
                letterSpacing = textStyle.letterSpacing.value * 0.0624F
            }
        },
        update = {
            it.text = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_COMPACT).trimEnd()
        }
    )
}
