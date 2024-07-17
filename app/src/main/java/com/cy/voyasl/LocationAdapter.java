package com.cy.voyasl;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pixplicity.easyprefs.library.Prefs;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class LocationAdapter extends RecyclerView.Adapter<LocationAdapter.ProductViewHolder> implements Filterable {

    private Context context;
    private List<LocationModel> productList;
    private List<LocationModel> productListFiltered;



    public LocationAdapter(Context context, List<LocationModel> productList) {
        this.context = context;
        this.productList = productList;
        this.productListFiltered = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_list, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        LocationModel product = productListFiltered.get(position);
        holder.mName.setText(product.getTitlee());
        Picasso.get().load(product.getImgUrl_11()).into(holder.mImg);

        holder.mImg.setOnClickListener(v -> {
            Intent intent = new Intent(context, viewLocationActivity.class);
            Prefs.putString("KTitle",product.getTitlee());
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return productListFiltered.size();
    }

    private void initView() {



    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {

        private ImageView mImg;
        private TextView mName;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            mImg = itemView.findViewById  (R.id.img);
            mName = itemView.findViewById  (R.id.name);

        }
    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                String charString = constraint.toString();
                if (charString.isEmpty()) {
                    productListFiltered = productList;
                } else {
                    List<LocationModel> filteredList = new ArrayList<>();
                    for (LocationModel row : productList) {
                        // Here you need to implement your filtering logic
                        if (row.getTitlee().toLowerCase().contains(charString.toLowerCase()) || row.getDescriptionn().equalsIgnoreCase(charString)) {
                            filteredList.add(row);
                        }
                    }

                    productListFiltered = filteredList;
                }

                FilterResults filterResults = new FilterResults();
                filterResults.values = productListFiltered;
                return filterResults;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                productListFiltered = (ArrayList<LocationModel>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}

