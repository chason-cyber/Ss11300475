package tw.edu.pu.csim.tcyang.s11300475

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

data class ExamUiState(
    val authorInfo: String = "作者：資管二A 陳宇謙",
    val screenWidthPx: Int = 0,
    val screenHeightPx: Int = 0,
    val score: Int = 0,
    // 新增：角色圖示的固定尺寸 (300px)
    val roleIconSizePx: Int = 300
)

class ExamViewModel(application: Application) : AndroidViewModel(application) {

    var uiState by mutableStateOf(ExamUiState())
        private set

    init {
        getScreenDimensions()
    }

    private fun getScreenDimensions() {
        val resources = getApplication<Application>().resources
        val displayMetrics = resources.displayMetrics

        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels

        uiState = uiState.copy(
            screenWidthPx = width,
            screenHeightPx = height
        )
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]
                ExamViewModel(application as Application)
            }
        }
    }
}