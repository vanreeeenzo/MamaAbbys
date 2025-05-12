package com.example.mamaabbys;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import java.util.List;

public class EditPricesAdapter extends RecyclerView.Adapter<EditPricesAdapter.ViewHolder> {
    private List<Product> products;
    private List<Product> updatedProducts;

    public EditPricesAdapter(List<Product> products) {
        this.products = products;
        this.updatedProducts = new ArrayList<>();
        for (Product p : products) {
            this.updatedProducts.add(new Product(p.getId(), p.getName(), p.getCategory(), p.getPrice()));
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_edit_price, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Product product = products.get(position);
        holder.productNameText.setText(product.getName());
        holder.priceInput.setText(String.format("%.2f", product.getPrice()));

        // Remove any existing TextWatcher
        if (holder.priceInput.getTag() != null) {
            holder.priceInput.removeTextChangedListener((TextWatcher) holder.priceInput.getTag());
        }

        TextWatcher textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    if (s.length() > 0) {
                        float newPrice = Float.parseFloat(s.toString());
                        if (newPrice > 0) {
                            updatedProducts.get(holder.getAdapterPosition()).setPrice(newPrice);
                        }
                    }
                } catch (NumberFormatException e) {
                    // Invalid input, don't update the price
                }
            }
        };

        holder.priceInput.addTextChangedListener(textWatcher);
        holder.priceInput.setTag(textWatcher);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public List<Product> getUpdatedProducts() {
        List<Product> changedProducts = new ArrayList<>();
        for (int i = 0; i < products.size(); i++) {
            Product original = products.get(i);
            Product updated = updatedProducts.get(i);
            if (original.getPrice() != updated.getPrice()) {
                changedProducts.add(updated);
            }
        }
        return changedProducts;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView productNameText;
        TextInputEditText priceInput;

        ViewHolder(View itemView) {
            super(itemView);
            productNameText = itemView.findViewById(R.id.productNameText);
            priceInput = itemView.findViewById(R.id.priceInput);
        }
    }
} 