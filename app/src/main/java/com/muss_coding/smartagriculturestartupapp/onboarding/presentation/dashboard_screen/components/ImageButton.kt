import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight

@Composable
fun ImageButton(
    modifier: Modifier = Modifier,
    imagePainter: Painter,
    buttonText: String,
    onClick: () -> Unit,
    showGreenBorder: Boolean = false
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp) // You can change the size as needed
            .clip(RoundedCornerShape(16.dp)) // Rounded corners
            .then(modifier)
            //.border(2.dp, Color.Green, RoundedCornerShape(16.dp)) // Green border
            .clickable { onClick() }
    ) {
        Image(
            painter = imagePainter,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(Color(0x88000000)) // Semi-transparent black overlay
        )
        Text(
            text = buttonText,
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            fontSize = 20.sp, // You can change the font size as needed
            modifier = Modifier.align(Alignment.Center)
        )
    }
}
