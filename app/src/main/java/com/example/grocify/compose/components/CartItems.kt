package com.example.grocify.compose.components

import androidx.compose.foundation.border
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
import androidx.compose.material.icons.twotone.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.AndroidViewModel
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import com.example.grocify.model.Product
import com.example.grocify.ui.theme.BlueDark
import com.example.grocify.ui.theme.BlueLight
import com.example.grocify.ui.theme.BlueMedium
import com.example.grocify.util.anyToDouble
import com.example.grocify.util.checkName
import com.example.grocify.viewmodels.CartViewModel

@Composable
fun CartItems(
    product: Product,
    viewModel: AndroidViewModel,
    flagCart: String,
    productMarked: Boolean = false,
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
            Modifier
                .fillMaxWidth()
                .border(5.dp, if(product.threshold == 0) Color.Transparent else BlueDark, RoundedCornerShape(20.dp)),
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
                    model = product.image,
                    contentDescription = product.name,
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
                        text = checkName(product.name),
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(bottom = 10.dp)
                    )
                    if (product.threshold != 0) {
                        Text(
                            text = "Omaggio ${product.quantity}",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.W400,
                            color = Color.Black,
                        )
                    } else
                        if(product.price != 0.0) {
                            if(product.discount != 0.0){
                                val originalPrice = String.format("%.2f", anyToDouble(product.price)).replace(',', '.') + "€"
                                val discountedPrice = String.format("%.2f" , anyToDouble(product.price * (100.0 - product.discount) / 100.0)).replace(',', '.') + "€"

                                Text(
                                    buildAnnotatedString {
                                        withStyle(
                                            style = SpanStyle(
                                                fontSize = 12.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.Red, // Color for original price
                                                textDecoration = TextDecoration.LineThrough // Strikethrough style
                                            )
                                        ) {
                                            append(originalPrice)
                                        }
                                        append(" ") // Add space between prices
                                        withStyle(
                                            style = SpanStyle(
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.Black // Color for discounted price
                                            )
                                        ) {
                                            append(discountedPrice)
                                        }
                                        withStyle(
                                            style = SpanStyle(
                                                fontSize = 13.sp,
                                                color = Color.Gray,
                                                fontWeight = FontWeight.Bold
                                            )
                                        ) {
                                            append("/${product.quantity}")
                                        }
                                    },
                                )
                            }
                            else{
                                Text(
                                    buildAnnotatedString {
                                        withStyle(
                                            style = SpanStyle(
                                                fontSize = 15.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.Black,
                                            ),
                                        ) {
                                            append("${product.price}€")
                                        }
                                        withStyle(
                                            style = SpanStyle(
                                                fontSize = 13.sp,
                                                color = Color.Gray,
                                                fontWeight = FontWeight.Bold
                                            )
                                        ) {
                                            append("/${product.quantity}")
                                        }
                                    },
                                )
                            }
                        }else
                            Text(
                                text = ("${product.units} x ${product.quantity}"),
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
                        if(product.threshold == 0) {
                            ItemsQuantitySelector(
                                product,
                                viewModel as CartViewModel,
                                flagCart
                            )
                        }
                        IconButton(
                            onClick = { (viewModel as CartViewModel).removeFromCart(product, flagCart) },
                            Modifier
                                .padding(5.dp)
                                .size(22.dp)
                        ) {
                            Icon(
                                Icons.TwoTone.Delete,
                                contentDescription = "Localized description",
                                tint = Color.Red,
                            )
                        }
                    }else{
                        Checkbox(
                            checked = productMarked,
                            enabled = productMarked,
                            onCheckedChange = {  },
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