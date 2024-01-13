package com.munity.dizionapp.ui.fragments

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.TextView.OnEditorActionListener
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.munity.dizionapp.R
import com.munity.dizionapp.adapters.OnItemClickListener
import com.munity.dizionapp.adapters.ResultAdapter
import com.munity.dizionapp.databinding.FragmentHomeBinding
import com.munity.dizionapp.dictionaries.IDictionary
import com.munity.dizionapp.utils.StringUtil
import com.munity.dizionapp.viewmodels.DictionaryViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*


class HomeFragment : Fragment() {
    private val dictionaryVM: DictionaryViewModel by activityViewModels()
    private lateinit var homeBinding: FragmentHomeBinding
    private var atLeastOnce = false
    private lateinit var searchDialog: AlertDialog

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        homeBinding = FragmentHomeBinding.inflate(inflater, container, false)
        return homeBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        homeBinding.dictioViewModel = dictionaryVM
        homeBinding.lifecycleOwner = viewLifecycleOwner

        val searchDialogBuilder = AlertDialog.Builder(requireContext(), R.style.WrapContentDialog)
        searchDialogBuilder.setView(layoutInflater.inflate(R.layout.searching_dialog, null))
        searchDialogBuilder.create()

        searchDialog = searchDialogBuilder.create()

        //region RecyclerView setup

        val itemListener: OnItemClickListener = object : OnItemClickListener {
            override fun onItemClick(item: IDictionary?) {
                dictionaryVM.wordDetails.value = item
                findNavController().navigate(R.id.detailsFragment)
            }
        }
        val resultAdapter = ResultAdapter(dictionaryVM.results.value ?: emptyList(), itemListener)
        homeBinding.resultRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        homeBinding.resultRecyclerView.adapter = resultAdapter
        homeBinding.resultRecyclerView.addItemDecoration(ResultAdapter.MarginDecoration())

        //endregion

        //region LiveData Observers

        val resultObserver = Observer<List<IDictionary>> { resultDictionaries ->
            dictionaryVM.isSearching.value = false
            searchDialog.cancel()

            if (resultDictionaries != null)
                resultAdapter.updateData(resultDictionaries)
            if (resultDictionaries.isEmpty() && atLeastOnce) {
                dictionaryVM.noResultFound.value = true
                Toast.makeText(requireContext(), R.string.toastMessageNoResult, Toast.LENGTH_SHORT)
                    .show()
            }
        }
        dictionaryVM.results.observe(viewLifecycleOwner, resultObserver)

        val wordDetailsObserver = Observer<IDictionary> { detailedDictionary ->
            dictionaryVM.wordDetailsAcc.value = StringUtil.getSpannedAccezioni(detailedDictionary)
        }
        dictionaryVM.wordDetails.observe(viewLifecycleOwner, wordDetailsObserver)

        //endregion

        homeBinding.wordEditText.setOnEditorActionListener(OnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                performSearch()
                return@OnEditorActionListener true
            }
            false
        })
    }

    private fun performSearch() {
        dictionaryVM.isSearching.value = true
        dictionaryVM.noResultFound.value = false
        atLeastOnce = true
        hideKeyboard()

        searchDialog.show()

        var wordToBeFound = homeBinding.wordEditText.text.toString().lowercase(Locale.ROOT)
        wordToBeFound = StringUtil.stripAccents(wordToBeFound)

        CoroutineScope(Dispatchers.IO).launch {
            dictionaryVM.findWord(wordToBeFound)
        }
    }

    private fun hideKeyboard() {
        val inputManager =
            context?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager.hideSoftInputFromWindow(view?.windowToken, 0)
    }
}