package com.gallosalocin.go4lunch.adapters;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.gallosalocin.go4lunch.BuildConfig;
import com.gallosalocin.go4lunch.R;
import com.gallosalocin.go4lunch.databinding.ItemRestaurantBinding;
import com.gallosalocin.go4lunch.models.RestaurantResult;
import com.squareup.picasso.Picasso;

import java.text.MessageFormat;
import java.util.List;

public class RestaurantAdapter extends RecyclerView.Adapter<RestaurantAdapter.RestaurantViewHolder> {
    private List<RestaurantResult> restaurantResultList;
    private OnItemClickListener mListener;

    public RestaurantAdapter() {
    }

    public void setData(List<RestaurantResult> restaurantResultList) {
        this.restaurantResultList = restaurantResultList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public RestaurantViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemRestaurantBinding binding = ItemRestaurantBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new RestaurantViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RestaurantViewHolder holder, int position) {
        RestaurantResult currentRestaurantResult = restaurantResultList.get(position);

        String imageUrl = "";
        if (currentRestaurantResult.getRestaurantPhotos() != null) {
            imageUrl = getPhoto(currentRestaurantResult.getRestaurantPhotos().get(0).getPhotoReference(),
                    currentRestaurantResult.getRestaurantPhotos().get(0).getWidth(), BuildConfig.ApiKey);
            Picasso.get().load(imageUrl).placeholder(R.drawable.ic_no_image).fit().centerCrop().into(holder.image);
        } else {
            holder.image.setImageResource(R.drawable.ic_broken_image);
        }

        holder.name.setText(currentRestaurantResult.getName());
        holder.address.setText(currentRestaurantResult.getAddress());
        if (currentRestaurantResult.getRestaurantOpeningHours() != null) {
            if (currentRestaurantResult.getRestaurantOpeningHours().isOpenNow()) {
                holder.timetable.setTextColor(Color.parseColor("#009933"));
                holder.timetable.setText(R.string.open_now);
            } else {
                holder.timetable.setTextColor(Color.parseColor("#ff0000"));
                holder.timetable.setText(R.string.closed);
            }
        } else {
            holder.timetable.setTextColor(Color.parseColor("#000000"));
            holder.timetable.setText(R.string.not_specified);
        }
        holder.rating.setRating(currentRestaurantResult.getRating());
        holder.distance.setText(MessageFormat.format("{0}m", currentRestaurantResult.getDistance()));
        holder.number.setText(MessageFormat.format("({0})", currentRestaurantResult.getWorkmates()));
    }

    @Override
    public int getItemCount() {
        return restaurantResultList.size();
    }

    public class RestaurantViewHolder extends RecyclerView.ViewHolder {

        TextView name;
        TextView address;
        TextView timetable;
        TextView distance;
        TextView number;
        RatingBar rating;
        ImageView image;

        public RestaurantViewHolder(@NonNull ItemRestaurantBinding binding) {
            super(binding.getRoot());

            name = binding.tvRestaurantName;
            address = binding.tvRestaurantAddress;
            timetable = binding.tvRestaurantTimetable;
            distance = binding.tvRestaurantDistance;
            number = binding.tvRestaurantNumber;
            rating = binding.rbRestaurantRating;
            image = binding.ivRestaurantIcon;

            binding.cvRestaurant.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && mListener != null) {
                    mListener.onItemClick(restaurantResultList.get(position));
                }
            });
        }
    }

    public String getPhoto(String photoReference, int maxWidth, String key) {
        StringBuilder url = new StringBuilder();
        url.append("https://maps.googleapis.com/maps/api/place/photo?");
        url.append("maxwidth=");
        url.append(maxWidth);
        url.append("&photoreference=");
        url.append(photoReference);
        url.append("&key=");
        url.append(key);

        return url.toString();
    }

    public interface OnItemClickListener {
        void onItemClick(RestaurantResult restaurantResult);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mListener = listener;
    }
}
