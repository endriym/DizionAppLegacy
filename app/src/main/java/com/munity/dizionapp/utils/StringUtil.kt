package com.munity.dizionapp.utils

import android.os.Build
import android.text.Html
import android.text.Spanned
import com.munity.dizionapp.dictionaries.IDictionary
import java.text.Normalizer

object StringUtil {
    fun getSpannedAccezioni(dictionary: IDictionary?): Spanned {
        var accezioneTot = ""

        dictionary?.accezioni?.forEach {
            accezioneTot += it
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            return Html.fromHtml(accezioneTot, Html.FROM_HTML_MODE_COMPACT)
        else
            return Html.fromHtml(accezioneTot)
    }

    fun stripAccents(s: String): String {
        var s = s
        s = Normalizer.normalize(s, Normalizer.Form.NFD)
        s = s.replace("[\\p{InCombiningDiacriticalMarks}]".toRegex(), "")
        return s.trim()
    }

}