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

@Composable
fun ExamScreen(
    viewModel: ExamViewModel = viewModel(factory = ExamViewModel.Factory)
) {
    val uiState = viewModel.uiState

    val density = LocalDensity.current

    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                // 黃色背景
                .background(Color(0xFFFFFF00)) // 實作黃色背景
                .padding(innerPadding),
        ) {

            with(density) {

                val pxToDp = uiState.roleIconSizePx.toDp() // 300px 轉 Dp
                val offsetHalfScreen = (uiState.screenHeightPx / 2f).toDp() // 螢幕高度一半

                // =========================================================================
                // 區塊 1: 中央內容 (圖片與文字置中)
                // =========================================================================
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.Center), // 圖片與文字置中
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    // 原始的圓形圖片
                    Image(
                        painter = painterResource(id = R.drawable.happy),
                        contentDescription = "服務大考驗圖片",
                        modifier = Modifier
                            .size(150.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                    )

                    // 間距高度 10dp
                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = "瑪利亞基金會服務大考驗",
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        text = uiState.authorInfo, // 顯示系級與姓名
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )

                    // 間距高度 10dp
                    Spacer(modifier = Modifier.height(10.dp))

                    // 螢幕寬高 (px)
                    Text(
                        text = "螢幕大小: ${uiState.screenWidthPx} * ${uiState.screenHeightPx} ", // 讀取螢幕寬高 px
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )

                    // 間距高度 10dp
                    Spacer(modifier = Modifier.height(10.dp))

                    // 成績 (來自 ViewModel)
                    Text(
                        text = "成績: ${uiState.score}分",
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center
                    )
                }

                // =========================================================================
                // 區塊 2: 隨機掉落的服務圖示 (第四題新增)
                // =========================================================================
                Image(
                    painter = painterResource(id = uiState.gameIconState.resourceId),
                    contentDescription = "掉落中的服務圖示",
                    // 假設服務圖示大小為 100dp，需要與 ViewModel 中的 iconSizeDp 一致
                    modifier = Modifier
                        .size(100.dp)

                        // 應用 X 軸和 Y 軸偏移，控制圖示的掉落和水平位置
                        .offset(
                            x = uiState.gameIconState.xOffsetDp,
                            y = uiState.gameIconState.yOffsetDp
                        )

                        // 實作水平拖曳功能
                        .pointerInput(Unit) {
                            detectHorizontalDragGestures { change, dragAmount ->
                                change.consume() // 避免與其他手勢衝突
                                // 將拖曳的像素量轉換為 Dp，傳回給 ViewModel
                                viewModel.updateDragPosition(dragAmount.toDp())
                            }
                        }
                )


                // =========================================================================
                // 區塊 3: 角色圖示 (絕對定位)
                // =========================================================================

                // 1. 嬰幼兒圖示 (左邊，切齊螢幕 1/2 下方)
                Image(
                    painter = painterResource(id = R.drawable.role0),
                    contentDescription = "嬰幼兒",
                    modifier = Modifier
                        .size(pxToDp) // 300px 尺寸
                        .align(Alignment.BottomStart) // 貼齊左下角
                        .offset(y = -offsetHalfScreen) // 向上偏移量 = 螢幕高度一半
                )

                // 2. 兒童圖示 (右邊，切齊螢幕 1/2 下方)
                Image(
                    painter = painterResource(id = R.drawable.role1),
                    contentDescription = "兒童",
                    modifier = Modifier
                        .size(pxToDp) // 300px 尺寸
                        .align(Alignment.BottomEnd) // 貼齊右下角
                        .offset(y = -offsetHalfScreen) // 向上偏移量 = 螢幕高度一半
                )

                // 3. 成人圖示 (左邊，切齊螢幕底部)
                Image(
                    painter = painterResource(id = R.drawable.role2),
                    contentDescription = "成人",
                    modifier = Modifier
                        .size(pxToDp) // 300px 尺寸
                        .align(Alignment.BottomStart) // 貼齊左下角
                )

                // 4. 一般民眾圖示 (右邊，切齊螢幕底部)
                Image(
                    painter = painterResource(id = R.drawable.role3),
                    contentDescription = "一般民眾",
                    modifier = Modifier
                        .size(pxToDp) // 300px 尺寸
                        .align(Alignment.BottomEnd) // 貼齊右下角
                )
            }
        }
    }
}