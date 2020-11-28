package com.gallosalocin.go4lunch.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.gallosalocin.go4lunch.R;
import com.gallosalocin.go4lunch.databinding.ItemWorkmateBinding;
import com.gallosalocin.go4lunch.models.Workmate;
import com.google.firebase.firestore.DocumentSnapshot;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class WorkmateAdapter extends FirestoreRecyclerAdapter<Workmate, WorkmateAdapter.WorkmateViewHolder> {

    private Context context;
    OnItemClickListener onItemClickListener;

    public WorkmateAdapter(@NonNull FirestoreRecyclerOptions<Workmate> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull WorkmateViewHolder holder, int position, Workmate model) {
        if (model.getChosenRestaurantId().isEmpty()) {
            holder.text.setTextColor(Color.parseColor("#FF7E7373"));
            holder.text.setText(context.getResources().getString(R.string.workmate_not_decided, model.getName()));
        } else {
            holder.text.setTextColor(Color.parseColor("#000000"));
            holder.text.setText(context.getResources().getString(R.string.workmate_where_eating, model.getName(), model.getChosenRestaurantName()));
        }
        Picasso.get().load(model.getImage()).into(holder.profileImage);
    }

    @NonNull
    @Override
    public WorkmateViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemWorkmateBinding binding = ItemWorkmateBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new WorkmateViewHolder(binding);
    }

    class WorkmateViewHolder extends RecyclerView.ViewHolder {

        CircleImageView profileImage;
        TextView text;

        public WorkmateViewHolder(@NonNull ItemWorkmateBinding binding) {
            super(binding.getRoot());

            profileImage = binding.civIcon;
            text = binding.tvText;

            binding.cvWorkmate.setOnClickListener(view -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && onItemClickListener != null) {
                    onItemClickListener.onItemClick(getSnapshots().getSnapshot(position));
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(DocumentSnapshot documentSnapshot);
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }
}
