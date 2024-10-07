package com.paranoid.privacylock.util

import android.content.Context
import android.graphics.Typeface
import android.text.SpannableString
import android.text.Spanned
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import androidx.core.content.ContextCompat

fun spannableString(numberText: String, minutesText: String): CharSequence{
    val spannableString = SpannableString(numberText + minutesText)

    spannableString.setSpan(
        RelativeSizeSpan(3.0f), // 4 times larger
        0,
        numberText.length,
        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    )

    spannableString.setSpan(
        StyleSpan(Typeface.BOLD),
        0,
        numberText.length,
        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    )

    spannableString.setSpan(
        RelativeSizeSpan(0.7f), // normal size
        numberText.length,
        spannableString.length,
        Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
    )

    return spannableString
}

fun Int.dpToPx(context: Context): Int {
    val density = context.resources?.displayMetrics?.density
    return (this * (density ?: 1f)).toInt()
}

fun Context.getColorCompat(colorResId: Int): Int {
    return ContextCompat.getColor(applicationContext, colorResId)
}
