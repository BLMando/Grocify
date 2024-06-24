package com.example.grocify.utils

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue

//Vengono rimossi gli slash (/) dal testo e i caratteri non numerici, lo tronca a 4 caratteri se necessario e aggiunge uno slash dopo i primi due caratteri.
fun formatExpiryDate(input: String): String {
    val sanitizedInput = input.filter { it.isDigit() }.replace("/", "")
    val trimmedInput = if (sanitizedInput.length > 4) sanitizedInput.substring(0, 4) else sanitizedInput
    return trimmedInput.chunked(2).joinToString("/")
}

//Vengono rimossi gli spazi dal testo e i caratteri non numerici, lo tronca a 16 caratteri se necessario e aggiunge spazi ogni 4 cifre.
fun formatCreditCardNumber(input: String): String {
    val sanitizedInput = input.filter { it.isDigit() }.replace(" ", "")
    val trimmedInput = if (sanitizedInput.length > 16) sanitizedInput.substring(0, 16) else sanitizedInput
    return trimmedInput.chunked(4).joinToString(" ")
}

//Vengono rimossi i caratteri non numerici dal testo, lo tronca a 3 cifre se necessario.
fun formatCVC(input: String): String{
    val sanitizedInput = input.filter { it.isDigit() }
    return if (sanitizedInput.length > 3) sanitizedInput.substring(0, 3) else sanitizedInput
}

//Vengono rimossi i caratteri non numerici dal testo, lo tronca a 2 cifre se necessario.
fun formatDiscount(input: String): String{
    val sanitizedInput = input.filter { it.isDigit() }
    return if (sanitizedInput.length > 2) sanitizedInput.substring(0, 2) else sanitizedInput
}


//Calcola la nuova posizione del cursore basandosi sulla differenza tra la lunghezza del testo originale e quello formattato, tenendo conto dello slash aggiunto.
fun calculateExpiryDateSelection(oldValue: TextFieldValue, newFormattedValue: String): TextRange {
    val oldCursorPos = oldValue.selection.end
    val sanitizedOldText = oldValue.text.replace("/", "")
    val diff = oldCursorPos - sanitizedOldText.length
    val newCursorPos = 1 + oldCursorPos + diff + if (newFormattedValue.contains("/") && !oldValue.text.contains("/")) 1 else 0
    return TextRange(newCursorPos.coerceIn(0, newFormattedValue.length))
}

//Calcola la nuova posizione del cursore basandosi sulla differenza tra la lunghezza del testo originale e quello formattato, tenendo conto degli spazi aggiunti.
fun calculateCardNumberSelection(oldValue: TextFieldValue, newFormattedValue: String): TextRange {
    val oldCursorPos = oldValue.selection.end
    val sanitizedOldText = oldValue.text.replace(" ", "")
    val diff = oldCursorPos - sanitizedOldText.length
    val newCursorPos = 1 + oldCursorPos + diff + (newFormattedValue.length - sanitizedOldText.length) / 4
    return TextRange(newCursorPos.coerceIn(0, newFormattedValue.length))
}


fun maskCardNumber(number:String):String{
    val blocks = number.split(" ")

    val maskedBlocks = blocks.mapIndexed { index, block ->
        if (index < 3) "****" else block
    }

    return maskedBlocks.joinToString(" ")
}


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

fun isValidExpireDate(expireDate: String): Boolean {

    if(!isNotEmpty(expireDate))
        return false

    val dateParts = expireDate.split("/")

    //se la data non è nel formato corretto dd/yy ritorno false
    if(dateParts.size  != 2)
        return false

    val month = dateParts[0].toInt()
    val year = dateParts[1].toInt()

    //il mese e l'anno devono essere compresi tra concettualmete corretti
    return month in 1..12 && year in 23..70
}

//Algoritmo di Luhn per la verifica della carta di credito
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

fun checkName(name: String?): String{
    if(name.toString().count() > 13){
        return name!!.substring(0, 13) + "..."
    }
    else{
        return name.toString()
    }
}