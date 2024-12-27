package Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appsnacks.R;

import java.util.List;

import ModelClass.TheLoai;

public class MenuCategoryAdapter extends RecyclerView.Adapter<MenuCategoryAdapter.MenuCategoryViewHolder> {
    private List<TheLoai> categories;
    private OnCategorySelectedListener listener;
    private int selectedPosition = 0; // Default to first item

    public interface OnCategorySelectedListener {
        void onCategorySelected(TheLoai category);
    }

    public MenuCategoryAdapter(List<TheLoai> categories, OnCategorySelectedListener listener) {
        this.categories = categories;
        this.listener = listener;

        // Tự động chọn danh mục đầu tiên khi khởi tạo
        if (!categories.isEmpty() && listener != null) {
            listener.onCategorySelected(categories.get(0));
        }
    }

    @NonNull
    @Override
    public MenuCategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_menu_category, parent, false);
        return new MenuCategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuCategoryViewHolder holder, int position) {
        TheLoai category = categories.get(position);
        holder.tvCategoryName.setText(category.getTen_the_loai());

        // Cập nhật màu chữ và nền dựa trên lựa chọn
        if (selectedPosition == position) {
            holder.tvCategoryName.setTextColor(holder.itemView.getContext().getColor(android.R.color.holo_red_dark));
            holder.itemView.setBackgroundResource(R.drawable.category_background);
        } else {
            holder.tvCategoryName.setTextColor(holder.itemView.getContext().getColor(android.R.color.black));
            holder.itemView.setBackgroundResource(android.R.color.transparent);
        }

        holder.itemView.setOnClickListener(v -> {
            // Update selected position
            int previousSelected = selectedPosition;
            selectedPosition = holder.getAdapterPosition();

            // Notify item changes for visual update
            notifyItemChanged(previousSelected);
            notifyItemChanged(selectedPosition);

            // Callback to filter products
            if (listener != null) {
                listener.onCategorySelected(category);
            }
        });
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    public class MenuCategoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategoryName;

        public MenuCategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoryName = itemView.findViewById(R.id.tvMenuCategoryName);
        }
    }
}