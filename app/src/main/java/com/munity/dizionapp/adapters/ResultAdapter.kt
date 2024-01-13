package com.munity.dizionapp.adapters

import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.text.HtmlCompat
import androidx.recyclerview.widget.RecyclerView
import com.munity.dizionapp.databinding.ListItemResultBinding
import com.munity.dizionapp.dictionaries.IDictionary

class ResultAdapter(
    var dictionaries: List<IDictionary>,
    private val listener: OnItemClickListener
) :
    RecyclerView.Adapter<ResultAdapter.WordHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WordHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ListItemResultBinding.inflate(inflater, parent, false)

        return WordHolder(binding)
    }

    override fun onBindViewHolder(holder: WordHolder, position: Int) {
        holder.bind(dictionaries[position], listener)
    }

    override fun getItemCount(): Int = dictionaries.size

    fun updateData(dictionariesParam: List<IDictionary>) {
        dictionaries = dictionariesParam
        notifyDataSetChanged()
    }

    inner class WordHolder(private val binding: ListItemResultBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(dictionaryParam: IDictionary, listener: OnItemClickListener) {
            binding.dictionary = dictionaryParam

            var accezioni = dictionaryParam.accezioni?.joinToString() ?: ""

            if (accezioni.length > 317) {
                accezioni = accezioni.substring(0, 315)
            }

            binding.resultText.text = HtmlCompat.fromHtml(accezioni, HtmlCompat.FROM_HTML_MODE_LEGACY)
            binding.resultText.append("...")
            binding.executePendingBindings()

            itemView.setOnClickListener {
                listener.onItemClick(dictionaryParam)
            }
        }
    }

    class MarginDecoration : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            with(outRect) {
                if (parent.getChildAdapterPosition(view) == 0) {
                    top = 8
                }
                left = 8
                right = 8
                bottom = 8
            }
        }
    }
}

interface OnItemClickListener {
    fun onItemClick(item: IDictionary?)
}

