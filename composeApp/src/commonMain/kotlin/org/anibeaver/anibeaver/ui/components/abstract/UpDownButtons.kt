import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun UpDownButtons(
    onIncrement: (() -> Unit)? = null,
    onDecrement: (() -> Unit)? = null,
    modifier: Modifier = Modifier,
    upContent: @Composable (() -> Unit)? = null,
    downContent: @Composable (() -> Unit)? = null
) {
    Column(modifier = modifier.padding(start = 4.dp)) {
        Button(
            onClick = { onIncrement?.invoke() },
            enabled = onIncrement != null,
            modifier = Modifier
                .height(24.dp)
                .width(32.dp)
                .padding(bottom = 2.dp),
            contentPadding = PaddingValues(0.dp)
        ) {
            if (upContent != null) upContent() else Text("+", maxLines = 1)
        }
        Button(
            onClick = { onDecrement?.invoke() },
            enabled = onDecrement != null,
            modifier = Modifier
                .height(24.dp)
                .width(32.dp)
                .padding(top = 2.dp),
            contentPadding = PaddingValues(0.dp)
        ) {
            if (downContent != null) downContent() else Text("-", maxLines = 1)
        }
    }
}
