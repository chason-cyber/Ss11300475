package tw.edu.pu.csim.tcyang.s11300475

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random
import androidx.compose.ui.geometry.Offset // 新增 Offset 引入

// ==================================================================================
// 遊戲狀態 DTO: 用於服務圖示的動態狀態
// ==================================================================================
data class GameIconState(
    // 服務圖示的資源 ID (假設您有 role4, role5, ...)
    val resourceId: Int = R.drawable.service0,
    // Y 軸偏移量 (DP 單位)
    val yOffsetDp: Dp = 0.dp,
    // X 軸偏移量 (DP 單位, 用於拖曳)
    val xOffsetDp: Dp = 0.dp
)

data class ExamUiState(
    val authorInfo: String = "作者：資管二A 陳宇謙",
    val screenWidthPx: Int = 0,
    val screenHeightPx: Int = 0,
    val score: Int = 0,
    val roleIconSizePx: Int = 300,
    // 新增：掉落中的服務圖示狀態
    val gameIconState: GameIconState = GameIconState()
)

// ==================================================================================
// ViewModel 邏輯
// ==================================================================================
class ExamViewModel(application: Application) : AndroidViewModel(application) {

    // 遊戲圖示列表 (假設您有三個服務圖示)
    private val serviceIcons = listOf(
        R.drawable.service0, // 替換為您實際的服務圖示名稱 (如: R.drawable.icon_a)
        R.drawable.service1,
        R.drawable.service2
    )

    // 遊戲狀態
    var uiState by mutableStateOf(ExamUiState())
        private set

    // 動畫 Job
    private var gameJob: Job? = null

    // 固定的掉落速度 (20px) 和間隔 (0.1s)
    private val DROP_SPEED_PX = 20
    private val DROP_INTERVAL_MS = 100L // 0.1s

    init {
        getScreenDimensions()
        // ViewModel 初始化後，開始遊戲
        startGameLoop()
    }

    private fun getScreenDimensions() {
        // ... (讀取螢幕尺寸邏輯不變)
        val resources = getApplication<Application>().resources
        val displayMetrics = resources.displayMetrics

        val width = displayMetrics.widthPixels
        val height = displayMetrics.heightPixels

        uiState = uiState.copy(
            screenWidthPx = width,
            screenHeightPx = height
        )
    }

    // ====================== 遊戲邏輯與控制 ======================

    /**
     * 重新初始化一個新的服務圖示，位於螢幕上方中央
     */
    private fun resetGameIcon(application: Application) {
        // 隨機選擇一個圖示
        val randomIconId = serviceIcons.random()

        // 獲取螢幕寬度的 Dp 單位
        val screenWidthDp = with(application.resources.displayMetrics) {
            uiState.screenWidthPx / density
        }.dp

        // 獲取圖示尺寸的 Dp 單位 (假設圖示寬度與高度是 100dp，需要自行調整)
        // 由於題目未給服務圖示尺寸，暫定 100dp，並置於水平中央
        val iconSizeDp = 100.dp

        // 設置初始水平位置 (水平中央)
        val initialXOffsetDp = (screenWidthDp / 2f) - (iconSizeDp / 2f)

        // 重設圖示狀態：回到頂部，水平居中
        uiState = uiState.copy(
            gameIconState = GameIconState(
                resourceId = randomIconId,
                yOffsetDp = 0.dp, // 從螢幕上方開始
                xOffsetDp = initialXOffsetDp
            )
        )
    }

    /**
     * 開始遊戲循環 (圖示掉落動畫)
     */
    private fun startGameLoop() {
        // 確保先生成第一個圖示
        resetGameIcon(getApplication())

        // 啟動 Coroutine 進行掉落動畫
        gameJob?.cancel() // 取消之前的 Job
        gameJob = viewModelScope.launch {
            while (true) {
                delay(DROP_INTERVAL_MS) // 每 0.1 秒
                updateDropPosition()
            }
        }
    }

    /**
     * 更新掉落圖示的 Y 軸位置
     */
    private fun updateDropPosition() {
        val application = getApplication<Application>()
        val density = application.resources.displayMetrics.density

        // 將 20px 轉換為 Dp
        val dropDp = (DROP_SPEED_PX / density).dp

        val currentYDp = uiState.gameIconState.yOffsetDp
        val newYDp = currentYDp + dropDp

        // 將螢幕高度 (px) 轉換為 Dp
        val screenHeightDp = (uiState.screenHeightPx / density).dp

        // 檢查是否碰到螢幕底部
        if (newYDp.value >= screenHeightDp.value) {
            // 碰到底部，重置並生成新圖示
            resetGameIcon(application)
        } else {
            // 繼續往下掉
            uiState = uiState.copy(
                gameIconState = uiState.gameIconState.copy(yOffsetDp = newYDp)
            )
        }
    }

    /**
     * 更新圖示的水平拖曳位置
     * @param xChangeDp 拖曳造成的 X 軸變化量
     */
    fun updateDragPosition(xChangeDp: Dp) {
        val newXDp = uiState.gameIconState.xOffsetDp + xChangeDp

        // 可以加入邊界檢查以防止拖出螢幕範圍
        // (這裡為了簡潔暫時省略邊界檢查)

        uiState = uiState.copy(
            gameIconState = uiState.gameIconState.copy(xOffsetDp = newXDp)
        )
    }

    // ====================== ViewModelFactory (不變) ======================

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val application = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]
                ExamViewModel(application as Application)
            }
        }
    }
}