package com.example.kitchen.lists

import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.RenderEffect
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.core.graphics.drawable.toBitmap
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.RecyclerView
import com.example.kitchen.DownloadImageTask
import com.example.kitchen.R
import com.example.kitchen.models.Dish
import com.google.android.material.card.MaterialCardView
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min


class DishesAdapter constructor(
    private val dishes: List<Dish>,
    private val likesCount: List<Int>) : RecyclerView.Adapter<DishesAdapter.MyViewHolder>(){
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val imageView: ImageView = itemView.findViewById(R.id.iv_dish_image)
        val tvTitle: TextView = itemView.findViewById(R.id.tv_dish_title)
        val tvLikeCount: TextView = itemView.findViewById(R.id.tv_dish_like_count)
        val tvCookingTime: TextView = itemView.findViewById(R.id.tv_dish_cooking_time)
        val tvPreview: TextView = itemView.findViewById(R.id.tv_dish_preview_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.dish_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return dishes.count()
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        if (dishes[position].image.isNotEmpty())
            DownloadImageTask(holder.imageView)
                .execute("https://gkeqyqnfnwgcbpgbnxkq.supabase.co/storage/v1/object/public/kitchen_dish_image/${dishes[position].image}")

        holder.tvTitle.text = dishes[position].name
        holder.tvLikeCount.text = likesCount[position].toString()
        holder.tvCookingTime.text = "${dishes[position].cookingTime} мин."
        holder.tvPreview.text = dishes[position].name[0].toString()
    }
}