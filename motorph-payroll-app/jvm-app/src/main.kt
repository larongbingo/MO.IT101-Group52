import androidx.compose.material.MaterialTheme
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import org.motorph.AppRouter
import org.motorph.data.loading.loadData
import org.motorph.initKoin

fun main() = application {
    initKoin()
    loadData()
    Window(onCloseRequest = ::exitApplication) {
        MaterialTheme {
            AppRouter()
        }
    }
}