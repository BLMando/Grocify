package com.example.grocify.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.AndroidViewModel
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.example.grocify.ui.theme.BlueLight
import com.example.grocify.ui.theme.BlueMedium
import com.example.grocify.util.anyToDouble
import com.example.grocify.util.checkName
import com.example.grocify.viewmodels.CartViewModel

@Composable
fun CartItems(
    id: String,
    name: String,
    price: Double = 0.0,
    quantity: String = "",
    image: String,
    units: Int,
    viewModel: AndroidViewModel,
    flagCart: String
) {
    Card(
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
        Row(
            Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Box(
                modifier = Modifier
                    .padding(12.dp)
                    .size(65.dp)
                    .clip(RoundedCornerShape(20.dp))
            ) {
                SubcomposeAsyncImage(
                    model = image,
                    contentDescription = name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .width(170.dp)
                        .height(80.dp)
                        .clip(RoundedCornerShape(20.dp))
                ) {
                    val state = painter.state
                    if (state is AsyncImagePainter.State.Loading || state is AsyncImagePainter.State.Error) {
                        CircularProgressIndicator(
                            modifier = Modifier.width(64.dp),
                            color = BlueMedium,
                            strokeCap = StrokeCap.Round
                        )
                    } else {
                        SubcomposeAsyncImageContent()
                    }
                }
            }
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(
                    modifier = Modifier.padding(start = 10.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = checkName(name),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                    if(price != 0.0)
                        Text(
                            text = (String.format("%.2f", anyToDouble(price)) + "€").replace(',', '.'),
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Light
                        )
                    else
                        Text(
                            text = ("$units x $quantity"),
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Light
                        )
                }

                Row(
                    modifier = Modifier.padding(end = 5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Absolute.Right
                ) {
                    if(flagCart.isNotEmpty()) {
                        ItemsQuantitySelector(
                            units,
                            id,
                            price,
                            viewModel as CartViewModel,
                            flagCart
                        )
                        IconButton(
                            onClick = { viewModel.removeFromCart(id, price, units, flagCart) },
                            Modifier
                                .padding(5.dp)
                                .size(18.dp)
                        ) {
                            Icon(
                                Icons.Filled.Delete,
                                contentDescription = "Localized description",
                                tint = Color.Red,
                            )
                        }
                    }else{
                        val checkedState = rememberSaveable { mutableStateOf(true) }
                        Checkbox(
                            checked = checkedState.value,
                            onCheckedChange = { checkedState.value = it },
                            modifier = Modifier
                                .padding(end = 10.dp),
                            colors = CheckboxDefaults.colors(
                                checkedColor = BlueLight,
                                uncheckedColor = BlueLight,
                                checkmarkColor = Color.White,
                            )
                        )
                    }
                }
            }
        }
    }
}