package com.example.grocify.compose.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.grocify.model.Product
import com.example.grocify.util.anyToInt
import com.example.grocify.viewmodels.CartViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ItemsQuantitySelector(
    product: Product,
    viewModel: CartViewModel,
    flagCart: String
){
    var state = product.units.toString()
    var isUpdating by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Card (
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 6.dp
        ),
        modifier = Modifier
            .padding(10.dp)
            .shadow(5.dp, shape = RoundedCornerShape(20.dp), ambientColor = Color.Black)
            .clip(RoundedCornerShape(20.dp))
    ) {
        Row (
            verticalAlignment = Alignment.CenterVertically,
        ){
            IconButton(
                onClick = {
                    if (!isUpdating && anyToInt(state)!! > 1) {
                        isUpdating = true
                        scope.launch {
                            viewModel.addValueToProductUnits(product, -1, flagCart)
                            state = viewModel.getUnitsByIdAndThreshold(product.id, product.threshold)
                            delay(250)  // Debounce delay
                            isUpdating = false
                        }
                    }
                },
                Modifier
                    .padding(5.dp)
                    .size(18.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Remove,
                    contentDescription = "decrease",
                )
            }
            Text(
                text = state,
                style = TextStyle(
                    fontSize = 15.sp
                ),
                modifier = Modifier.padding(5.dp)
            )
            IconButton(
                onClick = {
                    if (!isUpdating) {
                        isUpdating = true
                        scope.launch {
                            viewModel.addValueToProductUnits(product, 1, flagCart)
                            state = viewModel.getUnitsByIdAndThreshold(product.id, product.threshold)
                            delay(250)  // Debounce delay
                            isUpdating = false
                        }
                    }

                },
                Modifier
                    .padding(5.dp)
                    .size(18.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = "increase",
                )
            }
        }
    }
}