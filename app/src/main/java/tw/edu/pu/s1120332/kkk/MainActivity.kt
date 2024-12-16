package tw.edu.pu.s1120332.kkk

import android.content.Context
import android.media.MediaPlayer
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.platform.LocalContext
import tw.edu.pu.s1120332.kkk.ui.theme.KkkTheme
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            KkkTheme  {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Start(m = Modifier.padding(innerPadding))
                }
            }
        }
    }
}
// 讀取獎牌數量
fun loadMedalCount(context: Context): Int {
    val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    return sharedPreferences.getInt("medal_count", 0)
}

// 儲存獎牌數量
fun saveMedalCount(context: Context, medalCount: Int) {
    val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    with(sharedPreferences.edit()) {
        putInt("medal_count", medalCount)
        apply()
    }
}

// 主頁面組件
// 主頁面組件
@Composable
fun Start(m: Modifier) {
    val context = LocalContext.current

    // 在第一次組件創建時讀取獎牌數量
    var medalCount by remember { mutableStateOf(loadMedalCount(context)) }
    var showQuizPage by remember { mutableStateOf(false) }
    var showLearningPage by remember { mutableStateOf(false) }
    var showStartPage by remember { mutableStateOf(true) }
    var showScorePage by remember { mutableStateOf(false) }
    var showStampCollectionPage by remember { mutableStateOf(false) }
    var finalScore by remember { mutableStateOf(0) }

    // 用於循環播放音效
    var mediaPlayer: MediaPlayer? by remember { mutableStateOf(null) }

    // 播放音效，並在組件退出時釋放資源
    LaunchedEffect(showStartPage) {
        if (showStartPage) {
            // 只有在顯示初始頁面時才播放音效
            mediaPlayer = MediaPlayer.create(context, R.raw.cool).apply {
                isLooping = true
                start()
            }
        } else {
            // 停止音效播放並釋放資源
            mediaPlayer?.stop()
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    // 使用 DisposableEffect 在組件卸載時清理資源
    DisposableEffect(context) {
        onDispose {
            mediaPlayer?.stop()
            mediaPlayer?.release()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray)
    ) {
        Image(
            painter = painterResource(id = R.drawable.background),
            contentDescription = "背景圖",
            contentScale = ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )

        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            when {
                showStartPage -> {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = m
                    ) {
                        Button(
                            onClick = {
                                // 停止音效播放
                                mediaPlayer?.stop()
                                mediaPlayer?.release()
                                mediaPlayer = null

                                // 播放新的音效
                                val mediaPlayer = MediaPlayer.create(context, R.raw.quiz) // 替換為你的音效檔案
                                mediaPlayer.start()
                                mediaPlayer.setOnCompletionListener { mediaPlayer.release() }

                                // 切換頁面
                                showQuizPage = true
                                showStartPage = false
                            },
                            modifier = Modifier
                                .padding(16.dp)
                                .height(60.dp)
                                .width(200.dp)
                        ) {
                            Text(
                                "測驗",
                                style = TextStyle(
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        Button(
                            onClick = {
                                // 停止音效播放
                                mediaPlayer?.stop()
                                mediaPlayer?.release()
                                mediaPlayer = null

                                // 播放新的音效
                                val mediaPlayer = MediaPlayer.create(context, R.raw.learning)
                                mediaPlayer.start()
                                mediaPlayer.setOnCompletionListener { mediaPlayer.release() }

                                showLearningPage = true
                                showStartPage = false
                            },
                            modifier = Modifier
                                .padding(16.dp)
                                .height(60.dp)
                                .width(200.dp)
                        ) {
                            Text(
                                "學習",
                                style = TextStyle(
                                    fontSize = 24.sp, // 設定字體大小
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }

                        Button(
                            onClick = {
                                // 停止音效播放
                                mediaPlayer?.stop()
                                mediaPlayer?.release()
                                mediaPlayer = null

                                // 播放新的音效
                                val mediaPlayer = MediaPlayer.create(context, R.raw.view)
                                mediaPlayer.start()
                                mediaPlayer.setOnCompletionListener { mediaPlayer.release() }

                                showStampCollectionPage = true
                                showStartPage = false
                            },
                            modifier = Modifier
                                .padding(16.dp)
                                .height(60.dp)
                                .width(200.dp)
                        ) {
                            Text(
                                "集章",
                                style = TextStyle(
                                    fontSize = 24.sp, // 設定字體大小
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }

                showQuizPage -> {
                    QuizPage(onFinishQuiz = { score ->
                        finalScore = score
                        showQuizPage = false
                        showScorePage = true
                    })
                }
                showScorePage -> {
                    ScorePage(score = finalScore, onBackToStart = {
                        medalCount++  // 每次返回主頁時增加獎牌數量
                        saveMedalCount(context, medalCount)  // 保存獎牌數量
                        showScorePage = false
                        showStartPage = true
                    })
                }
                showLearningPage -> {
                    LearningPage(onFinish = {
                        showLearningPage = false
                        medalCount++  // 每次學習結束後增加一個獎牌
                        saveMedalCount(context, medalCount)  // 保存獎牌數量
                        showStartPage = true
                    })
                }
                showStampCollectionPage -> {
                    StampCollectionPage(
                        onBackToStart = { showStampCollectionPage = false; showStartPage = true },
                        medalCount = medalCount,
                        onRedeem = {
                            if (medalCount >= 10) {
                                medalCount -= 10  // 減少 10 個獎牌
                                saveMedalCount(context, medalCount)  // 保存獎牌數量
                            }
                        }
                    )
                }
            }
        }
    }
}




@Composable
fun ScorePage(score: Int, onBackToStart: () -> Unit) {
    val context = LocalContext.current
    val mediaPlayer = remember { mutableStateOf<MediaPlayer?>(null) }

    val message = when {
        score > 79 -> "太厲害了!" // 分數大於 79 顯示 "太厲害了!"
        score in 60..79 -> "不錯喔!" // 分數在 60 到 79 之間顯示 "不錯喔!"
        else -> "再加油~" // 分數小於 60 顯示 "再加油~"
    }

    // 播放音效
    LaunchedEffect(score) {
        mediaPlayer.value?.release()  // 停止並釋放舊的音效資源

        mediaPlayer.value = when {
            score > 79 -> MediaPlayer.create(context, R.raw.bingo2) // 分數大於 79 播放 "bingo2" 音效
            score in 60..79 -> MediaPlayer.create(context, R.raw.fine) // 分數在 60 到 79 播放 "fine" 音效
            else -> MediaPlayer.create(context, R.raw.sad) // 分數小於 60 播放 "sad" 音效
        }

        mediaPlayer.value?.start()  // 播放音效
        mediaPlayer.value?.setOnCompletionListener {
            mediaPlayer.value?.release() // 釋放資源
            mediaPlayer.value = null      // 清空 mediaPlayer
        }
    }

    // 顯示背景圖片
    Image(
        painter = painterResource(id = R.drawable.sb),
        contentDescription = "背景圖",
        contentScale = ContentScale.FillBounds,
        modifier = Modifier.fillMaxSize()
    )

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("得分: $score", style = TextStyle(fontSize = 32.sp, fontWeight = FontWeight.Bold))
            Spacer(modifier = Modifier.height(16.dp))
            Text(message, style = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.Medium))

            Spacer(modifier = Modifier.height(32.dp))

            Button(onClick = onBackToStart) {  // 返回主頁時呼叫 onBackToStart
                Text("返回主頁", style = TextStyle(fontSize = 20.sp))
            }
        }
    }
}




@Composable
fun LearningPage(onFinish: () -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // 用於播放成功音效的 MediaPlayer
    val mediaPlayer = remember { mutableStateOf<MediaPlayer?>(null) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.LightGray)
    ) {
        Image(
            painter = painterResource(id = R.drawable.cnmb),
            contentDescription = "背景圖",
            contentScale = if (expanded) ContentScale.Crop else ContentScale.FillBounds,
            modifier = Modifier.fillMaxSize()
        )
    }

    val fruits = listOf(
        Triple(R.drawable.durian, listOf(R.raw.durianc, R.raw.duriane, R.raw.duriant), "榴蓮"),
        Triple(R.drawable.apple, listOf(R.raw.applec, R.raw.applee, R.raw.applet), "蘋果"),
        Triple(R.drawable.avocado, listOf(R.raw.avocadoc, R.raw.avocadoe, R.raw.avocadot), "酪梨"),
        Triple(R.drawable.banana, listOf(R.raw.bananac, R.raw.bananae, R.raw.bananat), "香蕉"),
        Triple(R.drawable.cantaloupe, listOf(R.raw.cantaloupec, R.raw.cantaloupee, R.raw.cantaloupet), "哈密瓜"),
        Triple(R.drawable.cherries, listOf(R.raw.cherriesc, R.raw.cherriese, R.raw.cherriest), "櫻桃"),
        Triple(R.drawable.dragon, listOf(R.raw.dragonfruitc, R.raw.dragonfruite, R.raw.dragonfruitt), "火龍果"),
        Triple(R.drawable.grapes, listOf(R.raw.grapec, R.raw.grapee, R.raw.grapet), "葡萄"),
        Triple(R.drawable.grapefruit, listOf(R.raw.pomeloc, R.raw.pomeloe, R.raw.pomelot), "柚子"),
        Triple(R.drawable.lemon, listOf(R.raw.lemonc, R.raw.lemone, R.raw.lemont), "檸檬"),
        Triple(R.drawable.mango, listOf(R.raw.mangoc, R.raw.mangoe, R.raw.mangot), "芒果"),
        Triple(R.drawable.orange, listOf(R.raw.orangec, R.raw.orangee, R.raw.oranget), "橘子"),
        Triple(R.drawable.papaya, listOf(R.raw.papayac, R.raw.papayae, R.raw.papayat), "木瓜"),
        Triple(R.drawable.passion, listOf(R.raw.passionfruitc, R.raw.passionfruite, R.raw.passionfruitt), "百香果"),
        Triple(R.drawable.peach, listOf(R.raw.peachc, R.raw.peache, R.raw.peacht), "水蜜桃"),
        Triple(R.drawable.persimmon, listOf(R.raw.persimmonc, R.raw.persimmone, R.raw.persimmont), "柿子"),
        Triple(R.drawable.pineapple, listOf(R.raw.pineapplec, R.raw.pineapplee, R.raw.pineapplet), "鳳梨"),
        Triple(R.drawable.strawberry, listOf(R.raw.strawberryc, R.raw.strawberrye, R.raw.strawberryt), "草莓"),
        Triple(R.drawable.watermelon, listOf(R.raw.watermelonc, R.raw.watermelone, R.raw.watermelont), "西瓜"),
        Triple(R.drawable.tomato, listOf(R.raw.tomatoc, R.raw.tomatoe, R.raw.tomatot), "番茄")
    )
    val success = Triple(R.drawable.success, listOf(R.raw.success), "完成")
    val allItems = fruits + success
    var currentIndex by remember { mutableStateOf(0) }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            val currentFruit = allItems[currentIndex]
            val languages = if (currentFruit == success) listOf("完成") else listOf("中文", "英文", "台語")

            languages.forEachIndexed { langIndex, language ->
                Button(
                    onClick = {
                        // 釋放先前的 MediaPlayer 並創建新的
                        mediaPlayer.value?.release() // 釋放先前的 MediaPlayer
                        mediaPlayer.value = MediaPlayer.create(
                            context,
                            currentFruit.second.getOrElse(langIndex) { currentFruit.second[0] }
                        )
                        mediaPlayer.value?.start()  // 播放語音
                        mediaPlayer.value?.setOnCompletionListener {
                            it.release()
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp)
                ) {
                    Text(
                        text = if (language == "完成") language else "$language ${currentIndex + 1}",
                        style = TextStyle(
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }

            Image(
                painter = painterResource(id = currentFruit.first),
                contentDescription = "${currentFruit.third} 圖片",
                modifier = Modifier
                    .size(200.dp)
                    .padding(top = 20.dp)
            )
        }

        Row(
            modifier = Modifier.align(Alignment.BottomCenter).padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ){
            Button(
                onClick = { if (currentIndex > 0) currentIndex-- },
                enabled = currentIndex > 0
            ) {
                Text("上一個")
            }

            Spacer(modifier = Modifier.width(16.dp))

            Button(
                onClick = { if (currentIndex < allItems.size - 1) currentIndex++ },
                enabled = currentIndex < allItems.size - 1
            ) {
                Text("下一個")
            }

            if (currentIndex == allItems.size - 1) {
                Button(
                    onClick = {
                        // 停止並釋放音效
                        mediaPlayer.value?.let { player ->
                            if (player.isPlaying) {
                                player.stop()  // 停止音效
                            }
                            player.release()  // 釋放資源
                        }
                        mediaPlayer.value = null // 清空 mediaPlayer
                        onFinish() // 呼叫 onFinish 回到主頁
                    },
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text("結束")
                }
            }
        }
    }
}

@Composable
fun StampCollectionPage(onBackToStart: () -> Unit, medalCount: Int, onRedeem: () -> Unit) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                "我的獎牌",
                style = TextStyle(fontSize = 32.sp, fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = Modifier.height(32.dp))

            // 將獎牌數量按每行顯示最多5個進行分組
            val medalRows = (1..medalCount).chunked(5)

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                items(medalRows) { row ->
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(5.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        row.forEach {
                            Image(
                                painter = painterResource(id = R.drawable.medal),
                                contentDescription = "Medal",
                                modifier = Modifier.size(60.dp) // 可以調整大小
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // 當獎牌數量達到 10 個或更多時顯示「兌換」按鈕
            if (medalCount >= 10) {
                Button(
                    onClick = onRedeem,
                    modifier = Modifier.padding(8.dp)
                ) {
                    Text("兌換", style = TextStyle(fontSize = 20.sp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = onBackToStart) {
                Text("返回主頁", style = TextStyle(fontSize = 20.sp))
            }
        }
    }
}

// 測驗頁面組件
@Composable
fun QuizPage(onFinishQuiz: (Int) -> Unit) {
    val context = LocalContext.current
    val fruits = listOf(
        Pair(R.drawable.durian, "榴蓮"),
        Pair(R.drawable.apple, "蘋果"),
        Pair(R.drawable.avocado, "酪梨"),
        Pair(R.drawable.banana, "香蕉"),
        Pair(R.drawable.cantaloupe, "哈密瓜"),
        Pair(R.drawable.cherries, "櫻桃"),
        Pair(R.drawable.dragon, "火龍果"),
        Pair(R.drawable.grapes, "葡萄"),
        Pair(R.drawable.grapefruit, "柚子"),
        Pair(R.drawable.lemon, "檸檬"),
        Pair(R.drawable.mango, "芒果"),
        Pair(R.drawable.orange, "橘子"),
        Pair(R.drawable.papaya, "木瓜"),
        Pair(R.drawable.passion, "百香果"),
        Pair(R.drawable.peach, "水蜜桃"),
        Pair(R.drawable.persimmon, "柿子"),
        Pair(R.drawable.pineapple, "鳳梨"),
        Pair(R.drawable.strawberry, "草莓"),
        Pair(R.drawable.watermelon, "西瓜"),
        Pair(R.drawable.tomato, "番茄")
    )

    val questions = remember { generateQuestions(fruits) }
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var hasAnswered by remember { mutableStateOf(false) } // 控制是否已回答
    val buttonColors = remember { mutableStateMapOf<String, Color>() }
    var score by remember { mutableStateOf(0) } // 分數追蹤

    val currentQuestion = questions[currentQuestionIndex]

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                "${currentQuestionIndex + 1}/${questions.size}",
                style = TextStyle(
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold  // 加粗題數
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            Image(
                painter = painterResource(id = currentQuestion.imageId),
                contentDescription = "Fruit Image",
                modifier = Modifier.size(200.dp).padding(bottom = 20.dp)
            )

            currentQuestion.answers.forEach { answer ->
                val color = buttonColors[answer] ?: Color.Gray
                Button(
                    onClick = {
                        if (!hasAnswered) {
                            hasAnswered = true

                            currentQuestion.answers.forEach { option ->
                                buttonColors[option] = when {
                                    option == currentQuestion.correctAnswer -> Color.Green // 正確答案綠色
                                    else -> Color.Red // 錯誤答案紅色
                                }
                            }

                            // 播放音效並更新分數
                            val soundRes = if (answer == currentQuestion.correctAnswer) {
                                score += 10
                                R.raw.bingo // 正確音效
                            } else {
                                R.raw.wrong // 錯誤音效
                            }
                            val mediaPlayer = MediaPlayer.create(context, soundRes)
                            mediaPlayer.start()
                            mediaPlayer.setOnCompletionListener { mediaPlayer.release() }
                        }
                    },
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                        .height(50.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = color),
                    enabled = !hasAnswered || buttonColors.isNotEmpty() // 禁用按鈕選項
                ) {
                    Text(answer, style = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold))
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    if (currentQuestionIndex < questions.size - 1) {
                        currentQuestionIndex++
                        hasAnswered = false
                        buttonColors.clear()
                    } else {
                        // 播放滿分音效（若得分為 100 分）
                        if (score == questions.size * 10) {
                            val fullScoreMediaPlayer = MediaPlayer.create(context, R.raw.bingo2)
                            fullScoreMediaPlayer.start()
                            fullScoreMediaPlayer.setOnCompletionListener {
                                fullScoreMediaPlayer.release()
                            }
                        }
                        onFinishQuiz(score)
                    }
                },
                enabled = hasAnswered // 必須回答才能進入下一題
            ) {
                Text(if (currentQuestionIndex < questions.size - 1) "下一頁" else "完成測驗")
            }
        }
    }
}





// 題目資料類別
data class Question(
    val imageId: Int, // 圖片資源 ID
    val correctAnswer: String, // 正確答案
    val answers: List<String> // 答案選項
)

fun generateQuestions(fruits: List<Pair<Int, String>>): List<Question> {
    val availableFruits = fruits.shuffled().toMutableList() // 將水果洗牌後用作題庫
    val questions = mutableListOf<Question>()

    repeat(minOf(10, availableFruits.size)) { // 確保不超過題庫大小
        // 從剩餘水果中選擇正確答案
        val correctFruit = availableFruits.removeAt(0)

        // 從剩餘水果中選擇錯誤答案
        val incorrectAnswers = availableFruits.shuffled()
            .take(2) // 選兩個錯誤答案
            .map { it.second }

        // 將答案洗牌
        val answers = (incorrectAnswers + correctFruit.second).shuffled()

        // 創建新的問題
        questions.add(
            Question(
                imageId = correctFruit.first,
                correctAnswer = correctFruit.second,
                answers = answers
            )
        )
    }

    return questions
}


// 預覽函數
@Preview(showBackground = true)
@Composable
fun DefaultPreview() {
    KkkTheme {
        Start(m = Modifier) // 預覽主頁面
    }
}