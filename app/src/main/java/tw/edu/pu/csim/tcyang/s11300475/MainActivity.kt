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

        // 1. 強制螢幕為直式 (PORTRAIT)
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

        // 2. 隱藏上方狀態列及下方巡覽列
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)
        windowInsetsController.hide(WindowInsetsCompat.Type.statusBars())
        windowInsetsController.hide(WindowInsetsCompat.Type.navigationBars())

        setContent {
            S11300475Theme {
                // 載入主要的 ExamScreen 畫面
                ExamScreen()
            }
        }
    }
}