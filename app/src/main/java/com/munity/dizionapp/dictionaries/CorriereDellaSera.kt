package com.munity.dizionapp.dictionaries

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.lang.Exception
import java.util.*

class CorriereDellaSera : IDictionary {
    private var isDisambig = false

    companion object {
        private const val TAG = "CorriereDellaSera"
    }

    override var lemma: String? = null
    override var gramm: String? = null
    override var accezioni: Array<String>? = null
    override var sottoAccezioni: Map<Int, Array<String>?>? = null
    override var pronuncia: String? = null
    override var sillabazione: String? = null
    override var plurale: String? = null

    init {
        accezioni = listOf<String>().toTypedArray()
    }

    override suspend fun findWord(wordToBeFound: String): Int {
        val capitalLetter = wordToBeFound[0].titlecase(Locale.ROOT)

        try {
            val doc: Document = withContext(Dispatchers.IO) {
                Jsoup.connect("https://dizionari.corriere.it/dizionario_italiano/$capitalLetter/$wordToBeFound.shtml")
                    .get()
            }

            doc.getElementsByClass("chapter")[0].getElementsByClass("chapter-paragraph first").let {
                if (it[0].html().contains("Disambigua il termine")) {
                    handleDisambig(doc)
                    return 0
                }
            }

            setProperties(doc)

            return 0
        } catch (ex: Exception) {
            println("$TAG --- findWord: ${ex.message.toString()}")
            return -1
        }
    }

    private fun setProperties(doc: Element) {
        if (!isDisambig) {
            if (doc.getElementsByClass("ch_wd").html().contains("sup")) {
                doc.getElementsByTag("sup").remove()
            }
            lemma = doc.getElementsByClass("ch_wd")[0].html()
            gramm = doc.getElementsByClass("grl")[0].html()
            pronuncia = doc.getElementsByClass("pron").html().replace("-", "")
            sillabazione = doc.getElementsByClass("pron").html()
        }

        val voce = doc.getElementsByClass("chapter")[1]

        setAccezioni(voce)
    }

    private fun setAccezioni(voce: Element) {
        voce.getElementsByClass("chapter-paragraph first").remove()

        val accz = voce.getElementsByTag("dd")

        if (accz.size > 0) {
            val listAcc = mutableListOf<String>()

            accz.forEach {
                listAcc.add(it.html())
            }

            accezioni = if (isDisambig) {
                (accezioni!!.toList() + listAcc).toTypedArray()
            } else {
                listAcc.toTypedArray()
            }
        }

    }

    private suspend fun handleDisambig(voce: Element) {
        val disambig = voce.getElementsByClass("chapter-paragraph first")[0].getElementsByTag("a")

        disambig.forEach {
            var word = it.attr("href")
            val lastIndex = word.indexOf(".shtml")
            findWord(word.substring(5, lastIndex))
        }

        isDisambig = false
    }
}