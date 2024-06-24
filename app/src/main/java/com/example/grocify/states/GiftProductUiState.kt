package com.example.grocify.states

import com.example.grocify.model.ProductType
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters

data class GiftProductUiState(
    val thresholdProducts: MutableList<ProductType> = mutableListOf<ProductType>(),
    val moneySpent: Float = 0.0f,
    val startOfMonth: LocalDate = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()),
    val endOfMonth:   LocalDate = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth()),
    val daysLeft: Long =  ChronoUnit.DAYS.between(LocalDate.now(), LocalDate.now().withDayOfMonth(LocalDate.now().lengthOfMonth())),
    val flagThreshold50:  Boolean= false,
    val flagThreshold100: Boolean= false,
    val flagThreshold200: Boolean= false,
    val orderId: String = ""
)