package me.lwb.adapter.demo.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import me.lwb.adapter.demo.R

/**
 * Created by luowenbin on 22/5/2023.
 */
open class LegacyActionAdapter(vararg val list: Pair<CharSequence, Runnable>) : RecyclerView.Adapter<LegacyActionAdapter.MyViewHolder>() {
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val actionView: TextView = itemView.findViewById(R.id.action)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.item_action, parent, false)
        return MyViewHolder(itemView)
    }
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val item = list[position]
        holder.actionView.text = item.first
        holder.actionView.setOnClickListener { item.second.run() }
    }
    override fun getItemCount(): Int = list.size
}