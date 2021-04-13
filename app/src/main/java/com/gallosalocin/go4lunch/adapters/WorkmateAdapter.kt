package com.gallosalocin.go4lunch.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.gallosalocin.go4lunch.R
import com.gallosalocin.go4lunch.adapters.WorkmateAdapter.WorkmateViewHolder
import com.gallosalocin.go4lunch.databinding.ItemWorkmateBinding
import com.gallosalocin.go4lunch.models.Workmate
import com.squareup.picasso.Picasso

class WorkmateAdapter(
        options: FirestoreRecyclerOptions<Workmate?>,
        private val onItemClickListener: (Workmate) -> Unit
) : FirestoreRecyclerAdapter<Workmate, WorkmateViewHolder>(options) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): WorkmateViewHolder {
        val binding = ItemWorkmateBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return WorkmateViewHolder(binding)
    }

    override fun onBindViewHolder(holder: WorkmateViewHolder, position: Int, model: Workmate) {
        val currentWorkmate = getItem(position)
        holder.bind(currentWorkmate, onItemClickListener)
    }

    class WorkmateViewHolder(private val binding: ItemWorkmateBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
                workmate: Workmate,
                onItemClickListener: (Workmate) -> Unit,
        ) {

            binding.apply {

                if (workmate.chosenRestaurantId == "") {
                    tvText.setTextColor(Color.parseColor("#FF7E7373"))
                    tvText.text = root.context.resources.getString(R.string.workmate_not_decided, workmate.name)
                } else {
                    tvText.setTextColor(Color.parseColor("#000000"))
                    tvText.text = root.context.resources.getString(R.string.workmate_where_eating, workmate.name, workmate.chosenRestaurantName)
                }

                Picasso.get().load(workmate.image).into(civIcon)

                root.setOnClickListener {
                    onItemClickListener.invoke(workmate)
                }
            }
        }
    }
}