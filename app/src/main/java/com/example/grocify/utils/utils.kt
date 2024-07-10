package com.example.grocify.utils

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

/**
 * Format the expiry date as follows: mm/yyyy.
 * Takes the input string as 0824 or 08/24 and returns 08/24.
 * @param input the expiry date as a string
 * @return the formatted expiry date
 */
fun formatExpiryDate(input: String): String {
    val sanitizedInput = input.filter { it.isDigit() }.replace("/", "")
    val trimmedInput = if (sanitizedInput.length > 4) sanitizedInput.substring(0, 4) else sanitizedInput
    return trimmedInput.chunked(2).joinToString("/")
}


/**
 * Format the credit card number as follows: XXXX XXXX XXXX XXXX.
 * Takes the input string as 1234567890123456 and returns 1234 5678 9012 3456.
 * @param input the credit card number as a string
 * @return the formatted credit card number
 */
fun formatCreditCardNumber(input: String): String {
    val sanitizedInput = input.filter { it.isDigit() }.replace(" ", "")
    val trimmedInput = if (sanitizedInput.length > 16) sanitizedInput.substring(0, 16) else sanitizedInput
    return trimmedInput.chunked(4).joinToString(" ")
}


/**
 * Format a number removing non-numeric characters and truncating it to the specified length.
 * @param input as a String
 * @return the formatted number as a String
 */
fun formatNumber(input: String,truncateAt: Int): String{
    val sanitizedInput = input.filter { it.isDigit() }
    return if (sanitizedInput.length > truncateAt) sanitizedInput.substring(0, truncateAt) else sanitizedInput
}


/**
 * Calculate the new cursor position based on the
 * difference between the old and new formatted values.
 * @param oldValue the old value of the text field
 * @param newFormattedValue the new formatted value of the text field
 * @return the new cursor position
 */
fun calculateExpiryDateSelection(oldValue: TextFieldValue, newFormattedValue: String): TextRange {
    val oldCursorPos = oldValue.selection.end
    val sanitizedOldText = oldValue.text.replace("/", "")
    val diff = oldCursorPos - sanitizedOldText.length
    val newCursorPos = 1 + oldCursorPos + diff + if (newFormattedValue.contains("/") && !oldValue.text.contains("/")) 1 else 0
    return TextRange(newCursorPos.coerceIn(0, newFormattedValue.length))
}

/**
 * Calculate the new cursor position based on the
 * difference between the old and new formatted values.
 * @param oldValue the old value of the text field
 * @param newFormattedValue the new formatted value of the text field
 * @return the new cursor position
 */
fun calculateCardNumberSelection(oldValue: TextFieldValue, newFormattedValue: String): TextRange {
    val oldCursorPos = oldValue.selection.end
    val sanitizedOldText = oldValue.text.replace(" ", "")
    val diff = oldCursorPos - sanitizedOldText.length
    val newCursorPos = 1 + oldCursorPos + diff + (newFormattedValue.length - sanitizedOldText.length) / 4
    return TextRange(newCursorPos.coerceIn(0, newFormattedValue.length))
}

/**
 * Mask the credit card number with asterisks, leaving only the last four digits visible.
 */
fun maskCardNumber(number:String):String{
    val blocks = number.split(" ")

    val maskedBlocks = blocks.mapIndexed { index, block ->
        if (index < 3) "****" else block
    }

    return maskedBlocks.joinToString(" ")
}


/**
 * Functions for input validation
 */
fun isNotEmpty(value:String) : Boolean = value.isNotEmpty() && value.isNotBlank()

fun verifyEmail(email: String): Boolean = isNotEmpty(email) && isEmailValid(email)

fun isEmailValid(email: String): Boolean {
    val emailRegex = Regex("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}")
    return emailRegex.matches(email)
}

fun verifyPassword(password: String): Boolean = isNotEmpty(password) && password.length >= 6

fun verifyConfirmPassword(password: String, confirmPassword: String): Boolean = password == confirmPassword && isNotEmpty(confirmPassword)


fun dataClassToMap(data: Any): HashMap<String, Any?> {
    val map = hashMapOf<String, Any?>()
    data::class.members
        .filterIsInstance<kotlin.reflect.KProperty<*>>()
        .forEach { property ->
            map[property.name] = property.getter.call(data)
        }
    return map
}

/**
 * Check if the expire date is valid,
 * it should be in the format mm/yy and be a valid date.
 * @param expireDate the expire date as a string
 * @return true if the expire date is valid, false otherwise
 */
fun isValidExpireDate(expireDate: String): Boolean {

    if(!isNotEmpty(expireDate))
        return false

    val dateParts = expireDate.split("/")

    if(dateParts.size  != 2)
        return false

    val month = dateParts[0].toInt()
    val year = dateParts[1].toInt()

    return month in 1..12 && year in 23..70
}

/**
 * Check if the credit card number is valid.
 * It should be a 16-digit number and pass the Luhn algorithm.
 * @param number the credit card number as a string
 * @return true if the credit card number is valid, false otherwise
 */
fun isValidCreditCardNumber(number: String): Boolean {
    val sanitizedNumber = number.replace(" ", "")
    if (sanitizedNumber.length != 16 || !sanitizedNumber.all { it.isDigit() }) {
        return false
    }

    val reversedDigits = sanitizedNumber.reversed().map { it.toString().toInt() }
    val luhnSum = reversedDigits.mapIndexed { index, digit ->
        if (index % 2 == 1) {
            val doubled = digit * 2
            if (doubled > 9) doubled - 9 else doubled
        } else {
            digit
        }
    }.sum()

    return luhnSum % 10 == 0
}

/**
 * Check if the name is too long and add ... at the end if it is
 */
fun checkName(name: String?): String{
    if(name.toString().count() > 13){
        return name!!.substring(0, 13) + "..."
    }
    else{
        return name.toString()
    }
}


/**
 * Functions for casting values to different types
 */
fun anyToInt(value: Any?): Int? {
    return when (value) {
        is Int -> value
        is String -> value.toIntOrNull()
        is Number -> value.toInt()
        else -> null
    }
}

fun anyToDouble(value: Any?): Double? {
    return when (value) {
        is Double -> value
        is String -> value.toDoubleOrNull()
        is Number -> value.toDouble()
        else -> null
    }
}

/**
 * Function to parse date string into LocalDate object
 * @param dateString the date string to be parsed
 * @return the parsed LocalDate object
 */
fun parseDate(dateString: String): LocalDate {
    val dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
    return LocalDate.parse(dateString, dateFormatter)
}

/**
 * Function to parse time string into LocalTime object
 * @param timeString the time string to be parsed
 * @return the parsed LocalTime object
 */
fun parseTime(timeString: String): LocalTime {
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    return LocalTime.parse(timeString, timeFormatter)
}




