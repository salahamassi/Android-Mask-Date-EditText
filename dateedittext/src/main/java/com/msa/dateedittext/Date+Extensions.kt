package com.msa.dateedittext

import java.text.SimpleDateFormat
import java.util.*


fun String.toDate(format: String): Date? {
    val sdf = SimpleDateFormat(format, Locale.US)
    return sdf.parse(this)
}


fun Date.toString(format: String): String {
    val sdf = SimpleDateFormat(format, Locale.US)
    return sdf.format(this)
}

