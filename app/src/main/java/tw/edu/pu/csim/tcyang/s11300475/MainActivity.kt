package tw.edu.pu.csim.tcyang.s11300475

import android.content.pm.ActivityInfo
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import tw.edu.pu.csim.tcyang.s11300475.ui.theme.S11300475Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // 強迫直式螢幕 (已修正為 PORTRAIT)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

        // 隱藏狀態列及巡覽列（您之前也隱藏了 navigationBars，這裡建議補上）
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.hide(WindowInsetsCompat.Type.statusBars())
        windowInsetsController.hide(WindowInsetsCompat.Type.navigationBars()) // 建議補上以符合需求

        setContent {
            // 使用正確的主題名稱
            S11300475Theme {
                // 使用正確的畫面 Composable 名稱
                ExamScreen()
            }
        }
    }
}
// 原本多餘或不用的程式碼 (如 Greeting 函式) 已移除