package com.cy.voyasl;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pixplicity.easyprefs.library.Prefs;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class TaxiAdapter extends RecyclerView.Adapter<TaxiAdapter.ProductViewHolder> implements Filterable {

    private Context context;
    private List<AddTaxiModelview> productList;
    private List<AddTaxiModelview> productListFiltered;


    public TaxiAdapter(Context context, List<AddTaxiModelview> productList) {
        this.context = context;
        this.productList = productList;
        this.productListFiltered = productList;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.itemtaxi, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        AddTaxiModelview product = productListFiltered.get(position);
        holder.mName.setText(product.getNamed());
        Picasso.get().load(product.getImgUrl_1d()).into(holder.mProfileimg);


    }

    @Override
    public int getItemCount() {
        return productListFiltered.size();
    }

    private void initView() {



    }

    public static class ProductViewHolder extends RecyclerView.ViewHolder {

        private CircleImageView mProfileimg;
        private TextView mName;
        private LinearLayout mCompact;
        private ImageView mVehicalimg;
        private TextView mVtype;
        private TextView mPrice;


        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            mProfileimg = itemView.findViewById  (R.id.profileimg);
            mName = itemView.findViewById  (R.id.name);
            mCompact = itemView.findViewById  (R.id.Compact);
            mVehicalimg = itemView.findViewById  (R.id.vehicalimg);
            mVtype = itemView.findViewById  (R.id.vtype);
            mPrice = itemView.findViewById  (R.id.price);

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
                    List<AddTaxiModelview> filteredList = new ArrayList<>();
                    for (AddTaxiModelview row : productList) {
                        // Here you need to implement your filtering logic
                        if (row.getIDd().toLowerCase().contains(charString.toLowerCase()) || row.getNamed().equalsIgnoreCase(charString)) {
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
                productListFiltered = (ArrayList<AddTaxiModelview>) results.values;
                notifyDataSetChanged();
            }
        };
    }
}

