package com.example.lab05tipcalc

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lab05tipcalc.ui.theme.Lab05TipCalcTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Lab05TipCalcTheme {
                val snackbarHostState = remember { SnackbarHostState() }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
                ) { innerPadding ->
                    TipScreen(
                        snackbarHostState = snackbarHostState,
                        modifier = Modifier.padding(innerPadding)
                    )
                }
            }
        }
    }
}


fun calculateTip(orderAmount: Double, tipPercent: Float): Double {
    return orderAmount * tipPercent / 100.0
}

fun calculateDiscountPercent(dishCount: Int): Int {
    return when {
        dishCount <= 2 -> 3
        dishCount <= 5 -> 5
        dishCount <= 10 -> 7
        else -> 10
    }
}

fun calculateDiscountAmount(orderAmount: Double, discountPercent: Int): Double {
    return orderAmount * discountPercent / 100.0
}

fun calculateTotal(orderAmount: Double, tip: Double, discount: Double): Double {
    return orderAmount + tip - discount
}



@Composable
fun TipScreen(
    snackbarHostState: SnackbarHostState,
    modifier: Modifier = Modifier
) {
    var orderAmountText by remember { mutableStateOf("") }
    var dishCountText by remember { mutableStateOf("") }
    var tipPercent by remember { mutableFloatStateOf(0f) }
    var selectedDiscountPercent by remember { mutableIntStateOf(3) }
    var resultText by remember { mutableStateOf("") }

    val scope = rememberCoroutineScope()


    LaunchedEffect(dishCountText) {
        val dishCount = dishCountText.trim().toIntOrNull()
        if (dishCount != null && dishCount > 0) {
            selectedDiscountPercent = calculateDiscountPercent(dishCount)
            val orderAmount = orderAmountText.trim().toDoubleOrNull() ?: 0.0
            val discount = calculateDiscountAmount(orderAmount, selectedDiscountPercent)
            resultText = "Скидка: $discount"
        } else {
            resultText = ""
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        Text(text = "Сумма заказа:", fontSize = 16.sp)
        Spacer(modifier = Modifier.height(4.dp))
        TextField(
            value = orderAmountText,
            onValueChange = { orderAmountText = it },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFFFC0CB),
                unfocusedContainerColor = Color(0xFFFFC0CB)
            )
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(text = "Количество блюд:", fontSize = 16.sp)
        Spacer(modifier = Modifier.height(4.dp))
        TextField(
            value = dishCountText,
            onValueChange = { dishCountText = it },
            modifier = Modifier.fillMaxWidth(),
            colors = TextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFFFC0CB),
                unfocusedContainerColor = Color(0xFFFFC0CB)
            )
        )

        Spacer(modifier = Modifier.height(20.dp))

        Text(text = "Чаевые:", fontSize = 16.sp)
        Slider(
            value = tipPercent,
            onValueChange = { newValue ->
                tipPercent = newValue
                val orderAmount = orderAmountText.trim().toDoubleOrNull() ?: 0.0
                val tip = calculateTip(orderAmount, newValue)
                scope.launch {
                    snackbarHostState.showSnackbar("Чаевые: $tip")
                }
            },
            valueRange = 0f..25f,
            steps = 4,
            modifier = Modifier.fillMaxWidth()
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "0", fontSize = 14.sp)
            Text(text = "${tipPercent.toInt()}%", fontSize = 14.sp)
            Text(text = "25", fontSize = 14.sp)
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(text = "Скидка:", fontSize = 16.sp)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .selectableGroup(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            listOf(3, 5, 7, 10).forEach { percent ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    RadioButton(
                        selected = selectedDiscountPercent == percent,
                        onClick = null,
                        modifier = Modifier.selectable(
                            selected = selectedDiscountPercent == percent,
                            enabled = false,
                            role = Role.RadioButton,
                            onClick = null
                        )
                    )
                    Text(text = "$percent%", fontSize = 14.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        OutlinedTextField(
            value = resultText,
            onValueChange = { },
            readOnly = true,
            label = { Text("Скидка / Итого") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = {

            },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Итого", fontSize = 18.sp, fontWeight = FontWeight.Bold)
        }
    }
}