package com.example.testrecycleviewcardview

import android.content.Intent
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.location_layout.view.*

class LocationViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){


    fun bindLocation(location:Location?){
        with(location!!){
            itemView.textViewLocationName.text = locationName
            itemView.textViewLocationAddress.text = locationAddress
            Picasso.get().load(locationImage).into(itemView.imageViewLocation)
            itemView.textViewOperation.text = locationOperation
        }
    }

}