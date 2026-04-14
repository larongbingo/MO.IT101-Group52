import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.motorph.initKoin

fun main() = application {
    initKoin()
    Window(onCloseRequest = ::exitApplication) {
        Screen()
    }
}