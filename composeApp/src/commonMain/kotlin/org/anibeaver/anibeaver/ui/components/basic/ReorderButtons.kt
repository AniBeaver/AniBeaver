import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun ReorderButtons(
    onMoveUp: (() -> Unit)? = null,
    onMoveDown: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    UpDownButtons(
        onIncrement = onMoveUp,
        onDecrement = onMoveDown,
        modifier = modifier,
        upContent = { Icon(Icons.Filled.ArrowDropUp, contentDescription = "Move Up") },
        downContent = { Icon(Icons.Filled.ArrowDropDown, contentDescription = "Move Down") }
    )
}

