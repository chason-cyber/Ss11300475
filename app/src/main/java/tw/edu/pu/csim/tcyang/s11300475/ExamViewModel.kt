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
import androidx.compose.ui.geometry.Offset

// ==================================================================================
// 遊戲狀態 DTO: 用於服務圖示的動態狀態
// ==================================================================================
data class GameIconState(
    // 服務圖示的資源 ID (用於判斷圖示類型)
    val resourceId: Int = R.drawable.service0,
    // Y 軸偏移量 (DP 單位)
    val yOffsetDp: Dp = 0.dp,
    // X 軸偏移量 (DP 單位, 用於拖曳)
    val xOffsetDp: Dp = 0.dp,
    // 新增狀態：圖示是否已碰撞或掉落，用於停止移動
    val isSettled: Boolean = false
)

data class ExamUiState(
    val authorInfo: String = "作者：資管二A 陳宇謙",
    val screenWidthPx: Int = 0,
    val screenHeightPx: Int = 0,
    val score: Int = 0,
    val roleIconSizePx: Int = 300, // 300px (角色圖示固定大小)
    // 掉落中的服務圖示狀態
    val gameIconState: GameIconState = GameIconState(),
    // 碰撞結果訊息
    val collisionMessage: String = ""
)

// ==================================================================================
// ViewModel 邏輯
// ==================================================================================
class ExamViewModel(application: Application) : AndroidViewModel(application) {

    // 遊戲圖示列表
    private val serviceIcons = listOf(
        R.drawable.service0,
        R.drawable.service1,
        R.drawable.service2
    )

    // 服務圖示的固定尺寸 (DP 單位，對應 ExamScreen.kt 中的 100.dp)
    private val ICON_SIZE_DP = 100.dp

    // 角色圖示的固定尺寸 (DP 單位，由 300px 轉換而來)
    private var ROLE_ICON_SIZE_DP: Dp = 0.dp

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
        // 計算 300px 對應的 Dp 值
        ROLE_ICON_SIZE_DP = with(getApplication<Application>().resources.displayMetrics) {
            uiState.roleIconSizePx / density
        }.dp
        startGameLoop()
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

    // 重置服務圖示的位置和狀態
    private fun resetGameIcon(application: Application) {
        val randomIconId = serviceIcons.random()
        val density = application.resources.displayMetrics.density

        val screenWidthDp = (uiState.screenWidthPx / density).dp

        // 圖示初始 X 座標 (置中)
        val initialXOffsetDp = (screenWidthDp / 2f) - (ICON_SIZE_DP / 2f)

        uiState = uiState.copy(
            gameIconState = GameIconState(
                resourceId = randomIconId,
                yOffsetDp = 0.dp,
                xOffsetDp = initialXOffsetDp,
                isSettled = false // 重置狀態為未碰撞
            ),
            collisionMessage = ""
        )
    }

    // 開始遊戲循環
    private fun startGameLoop() {
        resetGameIcon(getApplication())

        gameJob?.cancel() // 取消之前的 Job
        gameJob = viewModelScope.launch {
            while (true) {
                delay(DROP_INTERVAL_MS) // 每 0.1 秒
                updateDropPosition()
            }
        }
    }

    // 更新掉落位置並檢查碰撞
    fun updateDropPosition() {
        if (uiState.gameIconState.isSettled) return // 圖示已停止，不更新

        val application = getApplication<Application>()
        val density = application.resources.displayMetrics.density

        val dropDp = (DROP_SPEED_PX / density).dp

        val currentYDp = uiState.gameIconState.yOffsetDp
        val newYDp = currentYDp + dropDp

        val screenHeightDp = (uiState.screenHeightPx / density).dp

        // 1. 檢查碰撞 (角色圖示)
        val collisionResult = checkCollision(newYDp)

        if (collisionResult != null) {
            val scoreToAdd = 10
            updateScoreAndMessage(scoreToAdd, "(碰撞 ${collisionResult}")

            // 讓圖示停在碰撞位置
            uiState = uiState.copy(
                gameIconState = uiState.gameIconState.copy(yOffsetDp = newYDp, isSettled = true)
            )

            // 延遲後重置圖示
            viewModelScope.launch {
                delay(DROP_INTERVAL_MS * 5) // 停留 0.5 秒讓使用者看到結果
                resetGameIcon(application)
            }
            return
        }

        // 2. 檢查掉落到底部
        // 圖示的底部 (newYDp + ICON_SIZE_DP) 達到螢幕高度
        if ((newYDp + ICON_SIZE_DP).value >= screenHeightDp.value) {
            val finalYDp = screenHeightDp - ICON_SIZE_DP // 圖示底部貼齊螢幕底部
            updateScoreAndMessage(0, "(掉到最下方)")

            // 讓圖示貼齊底部，並停止
            uiState = uiState.copy(
                gameIconState = uiState.gameIconState.copy(yOffsetDp = finalYDp, isSettled = true)
            )

            // 延遲後重置圖示
            viewModelScope.launch {
                delay(DROP_INTERVAL_MS * 5) // 停留 0.5 秒
                resetGameIcon(application)
            }
            return
        }

        // 3. 無碰撞/掉落，繼續移動
        uiState = uiState.copy(
            gameIconState = uiState.gameIconState.copy(yOffsetDp = newYDp)
        )
    }

    // 核心：檢查服務圖示是否與四個角色圖示重疊
    private fun checkCollision(targetYDp: Dp): String? {
        val application = getApplication<Application>()
        val density = application.resources.displayMetrics.density

        // 掉落圖示的邊界 (DP 單位)
        val fallingIconLeft = uiState.gameIconState.xOffsetDp.value
        val fallingIconRight = fallingIconLeft + ICON_SIZE_DP.value
        val fallingIconTop = targetYDp.value
        val fallingIconBottom = fallingIconTop + ICON_SIZE_DP.value

        val screenHeightDp = (uiState.screenHeightPx / density).dp.value
        val screenWidthDp = (uiState.screenWidthPx / density).dp.value
        val roleSize = ROLE_ICON_SIZE_DP.value // 300px 轉換成的 Dp 值

        // *** 修正 Y 軸計算，與 ExamScreen.kt 的 offset 邏輯對應 ***
        val halfScreenY = screenHeightDp / 2f

        // 上排角色 (role0, role1)
        // 貼齊 Bottom (screenHeightDp) 後，向上偏移螢幕高度的一半 (halfScreenY)
        val roleYUpperBottom = halfScreenY
        val roleYUpper = roleYUpperBottom - roleSize // 上排角色頂部 Y 座標

        // 下排角色 (role2, role3)
        // 貼齊 Bottom (screenHeightDp)
        val roleYLowerBottom = screenHeightDp
        val roleYLower = roleYLowerBottom - roleSize // 下排角色頂部 Y 座標

        // X 軸計算 (貼齊 Start/End)
        val roleLeftStart = 0f
        val roleLeftEnd = roleSize
        val roleRightStart = screenWidthDp - roleSize
        val roleRightEnd = screenWidthDp

        // 1. 嬰幼兒 (上排/左邊)
        if (fallingIconBottom > roleYUpper && fallingIconTop < roleYUpperBottom &&
            fallingIconRight > roleLeftStart && fallingIconLeft < roleLeftEnd) {
            return "嬰幼兒圖示)"
        }
        // 2. 兒童 (上排/右邊)
        if (fallingIconBottom > roleYUpper && fallingIconTop < roleYUpperBottom &&
            fallingIconRight > roleRightStart && fallingIconLeft < roleRightEnd) {
            return "兒童圖示)"
        }
        // 3. 成人 (下排/左邊)
        if (fallingIconBottom > roleYLower && fallingIconTop < roleYLowerBottom &&
            fallingIconRight > roleLeftStart && fallingIconLeft < roleLeftEnd) {
            return "成人圖示)"
        }
        // 4. 一般民眾 (下排/右邊)
        if (fallingIconBottom > roleYLower && fallingIconTop < roleYLowerBottom &&
            fallingIconRight > roleRightStart && fallingIconLeft < roleRightEnd) {
            return "一般民眾圖示)"
        }

        return null
    }

    private fun updateScoreAndMessage(points: Int, message: String) {
        uiState = uiState.copy(
            score = uiState.score + points,
            collisionMessage = message
        )
    }

    // 更新水平拖曳位置
    fun updateDragPosition(xChangeDp: Dp) {
        val application = getApplication<Application>()
        val density = application.resources.displayMetrics.density
        val screenWidthDp = (uiState.screenWidthPx / density).dp

        val currentXDp = uiState.gameIconState.xOffsetDp
        val newXDp = currentXDp + xChangeDp
        val maxXDp = screenWidthDp - ICON_SIZE_DP

        // 限制 X 座標在螢幕範圍內
        val constrainedXDp = newXDp.coerceIn(0.dp, maxXDp)

        uiState = uiState.copy(
            gameIconState = uiState.gameIconState.copy(xOffsetDp = constrainedXDp)
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