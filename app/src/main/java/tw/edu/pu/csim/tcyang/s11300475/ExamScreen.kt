package tw.edu.pu.csim.tcyang.s11300475

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

@Composable
fun ExamScreen(
    viewModel: ExamViewModel = viewModel(factory = ExamViewModel.Factory)
) {
    // 訂閱來自 ViewModel 的 UI 狀態
    val uiState = viewModel.uiState

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                // 黃色背景
                .background(Color(0xFFFFFF00))
                .padding(innerPadding),
            // 圖片與文字置中 (垂直和水平置中)
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            // 載入圓形圖片 (R.drawable.happy)
            Image(
                painter = painterResource(id = R.drawable.happy),
                contentDescription = "服務大考驗圖片",
                modifier = Modifier
                    .size(150.dp) // 圖片尺寸
                    .clip(CircleShape) // 裁剪成圓形
                    .background(Color.White)
            )

            // 文字間距高度 10dp
            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "瑪利亞基金會服務大考驗",
                fontSize = 18.sp,
                textAlign = TextAlign.Center
            )



            // 顯示系級與姓名 (來自 ViewModel)
            Text(
                text = uiState.authorInfo,
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )

            // 間距 10dp
            Spacer(modifier = Modifier.height(10.dp))

            // 顯示螢幕寬高 (px) (來自 ViewModel)
            Text(
                text = "螢幕大小: ${uiState.screenWidthPx} * ${uiState.screenHeightPx} ",
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )

            // 間距 10dp
            Spacer(modifier = Modifier.height(10.dp))

            // 成績 (來自 ViewModel)
            Text(
                text = "成績: ${uiState.score}分",
                fontSize = 16.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}