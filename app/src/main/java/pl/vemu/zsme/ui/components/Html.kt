package pl.vemu.zsme.ui.components

import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.text.TextStyle
import com.ireward.htmlcompose.HtmlText
import pl.vemu.zsme.data.model.HtmlString

@Composable
fun Html(
    html: HtmlString,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
) {
    val textColor =
        style.color.takeOrElse {
            LocalContentColor.current
        }

    HtmlText(text = html, style = style.copy(color = textColor), modifier = modifier)
}
