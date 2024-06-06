package com.example.kitchen.lists

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.kitchen.DownloadImageTask
import com.example.kitchen.R
import com.example.kitchen.models.Course
import com.example.kitchen.models.Dish
import com.google.android.material.card.MaterialCardView

class CoursesAdapter constructor(private val courses: List<Course>) : RecyclerView.Adapter<CoursesAdapter.MyViewHolder>(){
    class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val imageView: ImageView = itemView.findViewById(R.id.iv_course_image)
        val tvTitle: TextView = itemView.findViewById(R.id.tv_course_title)
        val container: MaterialCardView = itemView.findViewById(R.id.mcv_course_container)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context)
                .inflate(R.layout.course_item, parent, false)
        return MyViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return courses.count()
    }

    @RequiresApi(Build.VERSION_CODES.S)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.imageView.setImageResource(courses[position].imageId)
        holder.tvTitle.text = courses[position].name
        holder.container.setOnClickListener {
            courses[position].clickCallBack()
        }
    }
}