package com.cy.voyasl;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class SliderAdapter extends RecyclerView.Adapter<SliderAdapter.SliderViewHolder> {
    private List<ImageItem> imageItems;
    private LayoutInflater inflater;

    public SliderAdapter(Context context, List<ImageItem> imageItems) {
        this.inflater = LayoutInflater.from(context);
        this.imageItems = imageItems;
    }

    @NonNull
    @Override
    public SliderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.slider_item, parent, false);
        return new SliderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SliderViewHolder holder, int position) {
        ImageItem item = imageItems.get(position);
        Glide.with(holder.itemView)
                .load(item.getImageUrl())
                .into(holder.imageView);
        holder.textView.setText(item.getDescription());
        holder.itemView.setOnClickListener(v -> Toast.makeText(v.getContext(), "Item ID: " + item.getId(), Toast.LENGTH_SHORT).show());
    }

    @Override
    public int getItemCount() {
        return imageItems.size();
    }

    public class SliderViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView textView;

        public SliderViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageSlide);
            textView = itemView.findViewById(R.id.tvDescription);
        }
    }
}
