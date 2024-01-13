package com.munity.dizionapp.dictionaries

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import java.lang.Exception
import java.util.Locale

class Repubblica : IDictionary {
    companion object{
        private const val TAG = "Repubblica"
    }
    
    private lateinit var voce: Element

    override var lemma: String? = null
    override var gramm: String? = null
    override var accezioni: Array<String>? = null
    override var sottoAccezioni: Map<Int, Array<String>?>? = null
    override var pronuncia: String? = null
    override var sillabazione: String? = null
    override var plurale: String? = null

    override suspend fun findWord(wordToBeFound: String): Int {
        val capitalLetter = wordToBeFound[0].titlecase(Locale.ROOT)
        
        try {
            val doc: Document = withContext(Dispatchers.IO) {
                Jsoup.connect("https://dizionari.repubblica.it/Italiano/$capitalLetter/$wordToBeFound.html")
                    .get()
            }

            voce = doc.getElementById("voce")
            voce.getElementsByClass("copy").remove()

            setProperties(voce)
            return 0
        } catch (ex: Exception) {
            println("$TAG --- findWord: ${ex.message.toString()}")
            return -1
        }
    }

    private fun setProperties(voce: Element) {
        lemma = voce.getElementsByClass("lemma")[0].html()
        gramm = voce.getElementsByClass("gram")[0].html()
        sillabazione = voce.getElementsByClass("sillab")[0].html()
        pronuncia = voce.getElementsByClass("sillab")[0].html().replace("-", "")
        plurale = voce.getElementsByClass("fless").html()

        setAccezioni(voce)
    }

    private fun setAccezioni(voce: Element) {
        var definizione = voce.getElementsByClass("defin").html() + "</div>"
        if (voce.getElementsByClass("acc").size > 0) {
            val listString = mutableListOf<String>()

            val nAcc = voce.getElementsByClass("acc").size
            var startIndex = 0
            var lastIndex = 0
            var def: String

            repeat (nAcc - 1) {
                startIndex = definizione.indexOf("<span class=\"acc\">")
                lastIndex = definizione.indexOf("<span class=\"acc\">", startIndex + 20)

                def = definizione.substring(startIndex, lastIndex)
                definizione = definizione.removeRange(startIndex, lastIndex - 1)
                listString.add(def)
            }

            startIndex = definizione.indexOf("<span class=\"acc\">")
            lastIndex = definizione.indexOf("</div>")

            def = definizione.substring(startIndex, lastIndex)
            listString.add(def)

            accezioni = listString.toTypedArray()
        } else {
            accezioni = listOf(definizione).toTypedArray()
        }
    }
}