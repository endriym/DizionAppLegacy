package com.munity.dizionapp.dictionaries

interface IDictionary {
    var lemma: String?

    /**
     * Parte del discorso
     */
    var gramm: String?

    var accezioni: Array<String>?

    var sottoAccezioni: Map<Int, Array<String>?>?

    var pronuncia: String?

    var sillabazione: String?
    
    var plurale: String?

    suspend fun findWord(wordToBeFound: String): Int
}