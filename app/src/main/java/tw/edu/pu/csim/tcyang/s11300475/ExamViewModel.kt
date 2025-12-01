package tw.edu.pu.csim.tcyang.s11300475

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory

data class ExamUiState(
    // 請修改成您的系級與姓名！
    val authorInfo: String = "作者：資管二A 陳宇謙",
    val screenWidthPx: Int = 0,
    val screenHeightPx: Int = 0,
    val score: Int = 0
)

class ExamViewModel(application: Application) : AndroidViewModel(application) {

    var uiState by mutableStateOf(ExamUiState())
        private set

    init {
        getScreenDimensions()
    }

    private fun getScreenDimensions() {
        // 讀取螢幕寬度高度 (px)
        val resources = getApplication<Application>().resources
        val displayMetrics = resources.displayMetrics

        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels

        // 更新 UI 狀態
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