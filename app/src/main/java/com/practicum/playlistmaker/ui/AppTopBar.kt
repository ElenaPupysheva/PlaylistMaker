import androidx.annotation.StringRes
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material3.*
import androidx.compose.material3.TopAppBarDefaults.topAppBarColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.practicum.playlistmaker.ui.theme.YsFontFamily

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppTopBar(title: String) {
    TopAppBar(
        windowInsets = WindowInsets(0),
        colors = topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        ),
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium.copy(fontFamily = YsFontFamily),
                maxLines = 1
            )
        }
    )
}

@Composable
fun AppTopBar(@StringRes titleRes: Int) = AppTopBar(title = stringResource(titleRes))