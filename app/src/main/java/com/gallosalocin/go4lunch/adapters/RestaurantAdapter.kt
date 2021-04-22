package com.gallosalocin.go4lunch.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.gallosalocin.go4lunch.BuildConfig
import com.gallosalocin.go4lunch.R
import com.gallosalocin.go4lunch.adapters.RestaurantAdapter.RestaurantViewHolder
import com.gallosalocin.go4lunch.databinding.ItemRestaurantBinding
import com.gallosalocin.go4lunch.models.RestaurantResult
import com.squareup.picasso.Picasso
import java.text.MessageFormat

class RestaurantAdapter(
        private val onItemClickListener: (RestaurantResult) -> Unit,
) : ListAdapter<RestaurantResult, RestaurantViewHolder>(DiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RestaurantViewHolder {
        val binding = ItemRestaurantBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return RestaurantViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RestaurantViewHolder, position: Int) {
        val currentRestaurantResult = getItem(position)
        holder.bind(currentRestaurantResult, onItemClickListener)

    }

    class RestaurantViewHolder(private val binding: ItemRestaurantBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(
                restaurantResult: RestaurantResult,
                onItemClickListener: (RestaurantResult) -> Unit,
        ) {

            binding.apply {

                if (restaurantResult.restaurantPhotos != null) {
                    val imageUrl: String = getPhoto(restaurantResult.restaurantPhotos!![0].photoReference,
                            restaurantResult.restaurantPhotos!![0].width, BuildConfig.ApiKey)
                    Picasso.get().load(imageUrl).placeholder(R.drawable.ic_no_image).fit().centerCrop().into(ivRestaurantIcon)
                } else {
                    ivRestaurantIcon.setImageResource(R.drawable.ic_broken_image)
                }
                if (restaurantResult.restaurantOpeningHours != null) {
                    if (restaurantResult.restaurantOpeningHours!!.isOpenNow) {
                        tvRestaurantTimetable.setTextColor(Color.parseColor("#009933"))
                        tvRestaurantTimetable.setText(R.string.open_now)
                    } else {
                        tvRestaurantTimetable.setTextColor(Color.parseColor("#ff0000"))
                        tvRestaurantTimetable.setText(R.string.closed)
                    }
                } else {
                    tvRestaurantTimetable.setTextColor(Color.parseColor("#000000"))
                    tvRestaurantTimetable.setText(R.string.not_specified)
                }

                tvRestaurantName.text = restaurantResult.name
                tvRestaurantAddress.text = restaurantResult.address
                tvRestaurantDistance.text = MessageFormat.format("{0}m", restaurantResult.distance)
                tvRestaurantNumber.text = MessageFormat.format("({0})", restaurantResult.workmates)
                rbRestaurantRating.rating = restaurantResult.rating

                root.setOnClickListener {
                    onItemClickListener.invoke(restaurantResult)
                }
            }
        }

        private fun getPhoto(photoReference: String?, maxWidth: Int, key: String?): String {
            val url = StringBuilder()
            url.append("https://maps.googleapis.com/maps/api/place/photo?")
            url.append("maxwidth=")
            url.append(maxWidth)
            url.append("&photoreference=")
            url.append(photoReference)
            url.append("&key=")
            url.append(key)
            return url.toString()
        }
    }

    class DiffCallback : DiffUtil.ItemCallback<RestaurantResult>() {
        override fun areItemsTheSame(oldItem: RestaurantResult, newItem: RestaurantResult) =
                oldItem.placeId == newItem.placeId

        override fun areContentsTheSame(oldItem: RestaurantResult, newItem: RestaurantResult) =
                oldItem == newItem
    }
}