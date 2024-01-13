package com.munity.dizionapp.viewmodels

import android.text.Spanned
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.munity.dizionapp.dictionaries.CorriereDellaSera
import com.munity.dizionapp.dictionaries.IDictionary
import com.munity.dizionapp.dictionaries.Repubblica
import com.munity.dizionapp.utils.StringUtil

class DictionaryViewModel : ViewModel() {
    private val _results: MutableLiveData<List<IDictionary>> by lazy {
        MutableLiveData<List<IDictionary>>()
    }
    val results: LiveData<List<IDictionary>>
        get() = _results

    val wordDetails: MutableLiveData<IDictionary> by lazy {
        MutableLiveData<IDictionary>()
    }

    val wordDetailsAcc: MutableLiveData<Spanned> by lazy {
        MutableLiveData<Spanned>(StringUtil.getSpannedAccezioni(wordDetails.value))
    }

    val isSearching: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(false)
    }

    val noResultFound: MutableLiveData<Boolean> by lazy {
        MutableLiveData<Boolean>(false)
    }


    init {
        _results.value = listOf()
    }

    suspend fun findWord(wordToBeFound: String) {
        val successfulDictionaries = mutableListOf<IDictionary>()

        if (wordToBeFound.isNotEmpty()) {
            val dictionaries = listOf(Repubblica(), CorriereDellaSera())

            dictionaries.forEach {
                if (it.findWord(wordToBeFound) == 0) {
                    successfulDictionaries.add(it)
                }
            }
        }

        _results.postValue(successfulDictionaries)
    }
}