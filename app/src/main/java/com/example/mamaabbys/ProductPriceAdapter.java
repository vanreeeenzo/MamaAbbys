package com.example.mamaabbys;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.textfield.TextInputEditText;
import java.util.List;

public class ProductPriceAdapter extends RecyclerView.Adapter<ProductPriceAdapter.ProductPriceViewHolder> {
    private List<ProductPriceItem> products;
    private OnPriceChangeListener listener;
    private boolean isUpdating = false;
    private static final float MIN_PRICE = 0.01f;

    public interface OnPriceChangeListener {
        void onPriceChanged(ProductPriceItem item);
    }

    public ProductPriceAdapter(List<ProductPriceItem> products, OnPriceChangeListener listener) {
        this.products = products;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductPriceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product_price, parent, false);
        return new ProductPriceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductPriceViewHolder holder, int position) {
        ProductPriceItem item = products.get(position);
        holder.productName.setText(item.getName());
        holder.productCategory.setText(item.getCategory());
        
        // Set initial price without triggering text change
        isUpdating = true;
        holder.priceEditText.setText(String.format("%.2f", item.getPrice()));
        holder.currentPrice = item.getPrice();
        isUpdating = false;

        // Remove previous listeners
        if (holder.textWatcher != null) {
            holder.priceEditText.removeTextChangedListener(holder.textWatcher);
        }
        if (holder.focusChangeListener != null) {
            holder.priceEditText.setOnFocusChangeListener(null);
        }

        // Create new text watcher
        holder.textWatcher = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                if (isUpdating) return;
                // Store the current price for validation
                holder.currentPrice = item.getPrice();
            }
        };

        // Create focus change listener
        holder.focusChangeListener = (v, hasFocus) -> {
            if (!hasFocus) {
                try {
                    String priceText = holder.priceEditText.getText().toString();
                    if (!priceText.isEmpty()) {
                        float newPrice = Float.parseFloat(priceText);
                        if (newPrice < MIN_PRICE) {
                            // Show error message and revert to previous price
                            Toast.makeText(v.getContext(), 
                                "Price must be greater than zero", 
                                Toast.LENGTH_SHORT).show();
                            isUpdating = true;
                            holder.priceEditText.setText(String.format("%.2f", holder.currentPrice));
                            isUpdating = false;
                            return;
                        }
                        
                        if (newPrice != holder.currentPrice) {
                            item.setPrice(newPrice);
                            if (listener != null) {
                                listener.onPriceChanged(item);
                            }
                        }
                    }
                } catch (NumberFormatException e) {
                    // If invalid input, revert to previous price
                    isUpdating = true;
                    holder.priceEditText.setText(String.format("%.2f", holder.currentPrice));
                    isUpdating = false;
                }
            }
        };

        holder.priceEditText.addTextChangedListener(holder.textWatcher);
        holder.priceEditText.setOnFocusChangeListener(holder.focusChangeListener);
    }

    @Override
    public int getItemCount() {
        return products.size();
    }

    public void updateProducts(List<ProductPriceItem> newProducts) {
        this.products = newProducts;
        notifyDataSetChanged();
    }

    static class ProductPriceViewHolder extends RecyclerView.ViewHolder {
        TextView productName;
        TextView productCategory;
        TextInputEditText priceEditText;
        TextWatcher textWatcher;
        View.OnFocusChangeListener focusChangeListener;
        float currentPrice;

        ProductPriceViewHolder(View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.productName);
            productCategory = itemView.findViewById(R.id.productCategory);
            priceEditText = itemView.findViewById(R.id.priceEditText);
        }
    }
} 