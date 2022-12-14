package com.hoc081988.democoroutineschannelresult.ui.notifications

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView

class TextResultAdapter :
  ListAdapter<String, TextResultAdapter.VH>(object : DiffUtil.ItemCallback<String>() {
    override fun areItemsTheSame(oldItem: String, newItem: String) = oldItem == newItem
    override fun areContentsTheSame(oldItem: String, newItem: String) = oldItem == newItem
  }) {

  override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
    return VH(
      LayoutInflater
        .from(parent.context)
        .inflate(android.R.layout.simple_list_item_1, parent, false) as TextView
    )
  }

  override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(getItem(position))

  class VH(private val textView: TextView) : RecyclerView.ViewHolder(textView) {
    fun bind(item: String) {
      textView.text = item
    }
  }

}