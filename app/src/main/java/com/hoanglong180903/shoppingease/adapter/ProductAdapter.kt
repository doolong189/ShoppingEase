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
import com.hoanglong180903.shoppingease.listener.OnClickItemProduct
import com.hoanglong180903.shoppingease.model.Brand
import com.hoanglong180903.shoppingease.model.Product
import com.hoanglong180903.shoppingease.utils.Utils

class ProductAdapter(
    private val context : Context,
    private val listener : OnClickItemProduct
) : RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {
    private var list: List<Product> = listOf()
    class ProductViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val image : ImageView = itemView.findViewById(R.id.item_product_imageView)
        val tvName : TextView = itemView.findViewById(R.id.item_product_tvName)
        val tvPrice : TextView = itemView.findViewById(R.id.item_product_tvPrice)
        val tvBrand : TextView = itemView.findViewById(R.id.item_product_tvBrand)

        fun onBind(item : Product){
            val brand : Brand = item.idBrand!!
            tvName.text = item.name
            tvPrice.text = Utils.formatPrice(item.price)  + " đ"
//            tvPrice.text = item.price.toString()
            tvBrand.text = brand.name
            Glide.with(itemView).load(item.image)
                .placeholder(R.drawable.ic_launcher_foreground)
                .into(image)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val itemView = LayoutInflater.from(context).inflate(R.layout.item_product,parent,false)
        return ProductViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.onBind(list[position])
        holder.itemView.setOnClickListener {
            listener.onClickItem(list[position]._id)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setProducts(products: List<Product>) {
        list = products
        notifyDataSetChanged()
    }
}