package com.hoanglong180903.shoppingease.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.bumptech.glide.Glide
import com.hoanglong180903.shoppingease.R
import com.hoanglong180903.shoppingease.listener.OnClickItemCategory
import com.hoanglong180903.shoppingease.listener.OnClickItemProduct
import com.hoanglong180903.shoppingease.model.Brand
import com.hoanglong180903.shoppingease.model.Product
import com.hoanglong180903.shoppingease.utils.Utils

class CategoryAdapter(
    private val context : Context,
    private val listener : OnClickItemCategory
) : RecyclerView.Adapter<CategoryAdapter.ProductViewHolder>() {
    private var list: List<Brand> = listOf()
    class ProductViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        private val image : ImageView = itemView.findViewById(R.id.item_category_imageView)
        private val tvName : TextView = itemView.findViewById(R.id.item_category_tvName)

        fun onBind(item : Brand){
            Glide.with(itemView).load(item.image)
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(image)
            tvName.text = item.name
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.item_category,parent,false)
        return ProductViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.onBind(list[position])
        holder.itemView.setOnClickListener {
            listener.onClickItemCategory(list[position]._id)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setBrands(brands: List<Brand>) {
        list = brands
        notifyDataSetChanged()
    }
}