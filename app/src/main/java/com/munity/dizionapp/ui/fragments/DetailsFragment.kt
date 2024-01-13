package com.munity.dizionapp.ui.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.munity.dizionapp.databinding.FragmentDetailsBinding
import com.munity.dizionapp.viewmodels.DictionaryViewModel

class DetailsFragment : Fragment() {
    private val dictionaryVM: DictionaryViewModel by activityViewModels()
    private lateinit var detailsBinding: FragmentDetailsBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        detailsBinding = FragmentDetailsBinding.inflate(inflater, container, false)
        return detailsBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        detailsBinding.viewModelDetails = dictionaryVM
        detailsBinding.lifecycleOwner = viewLifecycleOwner
    }
}