package com.example.kitchen.lists

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import com.example.kitchen.R
import com.example.kitchen.models.Ingredient

class IngredientsAdapter (context: Context, dataArrayList: List<Ingredient?>?) :
    ArrayAdapter<Ingredient?>(context, R.layout.ingredient_item, dataArrayList!!) {
    override fun getView(position: Int, view: View?, parent: ViewGroup): View {
        var newView = view
        val ingredient = getItem(position)

        if (newView == null) {
            newView = LayoutInflater.from(context).inflate(R.layout.ingredient_item, parent, false)
        }

        val name = newView!!.findViewById<TextView>(R.id.tv_ingredient_item_name)
        val count = newView.findViewById<TextView>(R.id.tv_ingredient_item_count)

        name.text = ingredient!!.name
        count.text = ingredient.description

        return newView
    }
}