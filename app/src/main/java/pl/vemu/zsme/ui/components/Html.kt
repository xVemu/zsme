package pl.vemu.zsme.ui.components

import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.fromHtml
import pl.vemu.zsme.data.model.HtmlString

@Composable
fun Html(
    html: HtmlString,
    modifier: Modifier = Modifier,
    style: TextStyle = LocalTextStyle.current,
) {
    val string = AnnotatedString.fromHtml(html)

    Text(string, modifier, style = style)
}
