package com.oxy.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.Filter
import android.widget.Filterable
import androidx.annotation.LayoutRes

class FilterableAdapter<E>(
    private val elements: List<E>,
    @LayoutRes private val resId: Int,
    private val binder: (E, View) -> Unit,
    private val filter: (E, CharSequence) -> Boolean,
    private val converter: ((E) -> String)? = null
) : BaseAdapter(), Filterable {
    private var published = listOf<E>()

    override fun getCount(): Int = published.size

    override fun getItem(position: Int): E = published[position]

    override fun getItemId(position: Int): Long = getItem(position).hashCode().toLong()

    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val element = getItem(position)
        val view = LayoutInflater
            .from(parent.context)
            .inflate(resId, parent, false)
        binder(element, view)
        return view
    }

    override fun getFilter(): Filter = object : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults =
            FilterResults().apply {
                val r = elements.filter { filter(it, constraint) }
                this.values = r
                this.count = r.size
            }

        override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
            published = (results?.values as? List<E>) ?: emptyList()
            notifyDataSetChanged()
        }

        override fun convertResultToString(resultValue: Any?): CharSequence {
            return (resultValue as? E)?.let { converter?.invoke(it) } ?: super.convertResultToString(resultValue)
        }
    }
}