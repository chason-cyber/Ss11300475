package tw.edu.pu.csim.tcyang.s11300475

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.runtime.LaunchedEffect

@Composable
fun ExamScreen(
    // 使用 Compose 提供的 viewModel() 函數來取得或創建 ViewModel 實例
    viewModel: ExamViewModel = viewModel(factory = ExamViewModel.Factory)
) {
    val uiState = viewModel.uiState

    // 取得當前密度，用於將像素 (px) 轉換為 Dp
    val density = LocalDensity.current

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                // 黃色背景
                .background(Color(0xFFFFFF00))
                .padding(innerPadding),
        ) {

            // ViewModel.init { startGameLoop() } 已經啟動遊戲迴圈，這裡無需額外啟動
            LaunchedEffect(Unit) {
                // Keep empty or remove if not strictly needed, as the game loop is in ViewModel.
            }

            with(density) {
                // 角色圖示尺寸 (300px 轉 Dp)
                val pxToDp = uiState.roleIconSizePx.toDp()
                // 螢幕高度一半 (px 轉 Dp)，用於計算上排角色的 offset
                val offsetHalfScreen = (uiState.screenHeightPx / 2f).toDp()

                // =========================================================================
                // 區塊 1: 中央內容 (圖片與文字置中)
                // =========================================================================
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    // 原始的圓形圖片
                    Image(
                        painter = painterResource(id = R.drawable.happy), // 假設 happy 是您的圖片資源
                        contentDescription = "服務大考驗圖片",
                        modifier = Modifier
                            .size(150.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "瑪利亞基金會服務大考驗",
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = uiState.authorInfo,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // 螢幕寬高 (px)
                    Text(
                        text = "螢幕大小: ${uiState.screenWidthPx} * ${uiState.screenHeightPx} ",
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // 成績與碰撞結果顯示
                    Text(
                        // 顯示成績與碰撞訊息 (由 ViewModel 提供格式化的訊息)
                        text = "成績: ${uiState.score}分 ${uiState.collisionMessage}",
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                    )
                }

                // =========================================================================
                // 區塊 2: 掉落中的服務圖示 (可拖曳)
                // =========================================================================
                Image(
                    painter = painterResource(id = uiState.gameIconState.resourceId),
                    contentDescription = "掉落中的服務圖示",
                    // 服務圖示大小為 100dp
                    modifier = Modifier
                        .size(100.dp)
                        .offset(
                            x = uiState.gameIconState.xOffsetDp,
                            y = uiState.gameIconState.yOffsetDp
                        )
                        // 水平拖曳手勢處理
                        .pointerInput(Unit) {
                            detectHorizontalDragGestures { change, dragAmount ->
                                change.consume()
                                // 將拖曳像素量轉換為 Dp 傳遞給 ViewModel
                                viewModel.updateDragPosition(dragAmount.toDp())
                            }
                        }
                )

                // =========================================================================
                // 區塊 3: 角色圖示 (靜態位置)
                // 每個角色圖示都是 300px (pxToDp)
                // =========================================================================

                // 角色 0: 嬰幼兒 (上排左邊)
                Image(
                    painter = painterResource(id = R.drawable.role0), // 假設 role0 是您的圖片資源
                    contentDescription = "嬰幼兒",
                    modifier = Modifier
                        .size(pxToDp)
                        .align(Alignment.BottomStart) // 先貼齊底部左邊
                        .offset(y = -offsetHalfScreen) // 再向上偏移螢幕高度一半
                )
                // 角色 1: 兒童 (上排右邊)
                Image(
                    painter = painterResource(id = R.drawable.role1), // 假設 role1 是您的圖片資源
                    contentDescription = "兒童",
                    modifier = Modifier
                        .size(pxToDp)
                        .align(Alignment.BottomEnd) // 先貼齊底部右邊
                        .offset(y = -offsetHalfScreen) // 再向上偏移螢幕高度一半
                )
                // 角色 2: 成人 (下排左邊)
                Image(
                    painter = painterResource(id = R.drawable.role2), // 假設 role2 是您的圖片資源
                    contentDescription = "成人",
                    modifier = Modifier
                        .size(pxToDp)
                        .align(Alignment.BottomStart) // 貼齊底部左邊
                )
                // 角色 3: 一般民眾 (下排右邊)
                Image(
                    painter = painterResource(id = R.drawable.role3), // 假設 role3 是您的圖片資源
                    contentDescription = "一般民眾",
                    modifier = Modifier
                        .size(pxToDp)
                        .align(Alignment.BottomEnd) // 貼齊底部右邊
                )
            }
        }
    }
}