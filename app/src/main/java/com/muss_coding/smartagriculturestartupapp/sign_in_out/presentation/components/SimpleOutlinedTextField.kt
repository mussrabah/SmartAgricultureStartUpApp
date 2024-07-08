import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector

@Composable
fun SimpleOutlinedTextField(
    modifier: Modifier = Modifier,
    label: String,
    leadingIcon: ImageVector
) {
    var textFieldValue by rememberSaveable {
        mutableStateOf("")
    }
    OutlinedTextField(
        value = textFieldValue,
        onValueChange = {
                 textFieldValue = it
        },
        modifier = Modifier
            .fillMaxWidth(.85f),
        label = {
            Text(text = label)
        },
        leadingIcon = {
            Icon(
                imageVector = leadingIcon,
                contentDescription = leadingIcon.name
            )
        }
    )
}