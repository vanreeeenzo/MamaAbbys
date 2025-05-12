package com.example.mamaabbys;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class EditPricesAdapter extends RecyclerView.Adapter<EditPricesAdapter.ProductViewHolder> {
    private List<Product> products;
    private List<Product> updatedProducts;

    public EditPricesAdapter(List<Product> products) {
        this.products = products;
        this.updatedProducts = new ArrayList<>();
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_edit_price, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Product product = products.get(position);
        holder.productNameText.setText(product.getName());
        holder.priceEditText.setText(String.valueOf(product.getPrice()));
        
        holder.priceEditText.setOnFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus) {
                try {
                    float newPrice = Float.parseFloat(holder.priceEditText.getText().toString());
                    if (newPrice != product.getPrice()) {
                        product.setPrice(newPrice);
                        if (!updatedProducts.contains(product)) {
                            updatedProducts.add(product);
                        }
                    }
                } catch (NumberFormatException e) {
                    holder.priceEditText.setText(String.valueOf(product.getPrice()));
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public List<Product> getUpdatedProducts() {
        return updatedProducts;
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        TextView productNameText;
        EditText priceEditText;

        ProductViewHolder(View itemView) {
            super(itemView);
            productNameText = itemView.findViewById(R.id.productNameText);
            priceEditText = itemView.findViewById(R.id.priceEditText);
        }
    }
} 