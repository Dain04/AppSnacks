package Adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.appsnacks.R;
import java.util.List;

import ModelClass.User;

public class MenuRolesAdapter extends RecyclerView.Adapter<MenuRolesAdapter.MenuRolesViewHolder> {
    private List<User> users;
    private OnAdminSelectedListener listener;
    private int selectedPosition = 0; // Default to first item

    public interface OnAdminSelectedListener {
        void onAdminSelected(User user);
    }

    public MenuRolesAdapter(List<User> users, OnAdminSelectedListener listener) {
        this.users = users;
        this.listener = listener;
        // Tự động chọn danh mục đầu tiên khi khởi tạo
        if(!users.isEmpty() && listener != null) {
            listener.onAdminSelected(users.get(0));
        }
    }

    @NonNull
    @Override
    public MenuRolesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_menu_roles_ac, parent, false);
        return new MenuRolesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MenuRolesViewHolder holder, int position) {
        User user = users.get(position);

        // Modify this part to handle different scenarios
        if (holder.tvIdAdmin != null) {
            // Preferably use role, but fall back to id_admin if role is null
            String displayText = user.getRole() != null ? user.getRole() :
                    (user.getId_Nguoi_Dung() != null ? user.getId_Nguoi_Dung() : "Unnamed Role");

            holder.tvIdAdmin.setText(displayText);
        }

        // Update color and background
        if (selectedPosition == position) {
            if (holder.tvIdAdmin != null) {
                holder.tvIdAdmin.setTextColor(holder.itemView.getContext().getColor(android.R.color.holo_red_dark));
            }
            holder.itemView.setBackgroundResource(R.drawable.category_background);
        } else {
            if (holder.tvIdAdmin != null) {
                holder.tvIdAdmin.setTextColor(holder.itemView.getContext().getColor(android.R.color.black));
            }
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
                listener.onAdminSelected(user);
            }
        });
    }

    @Override
    public int getItemCount() {
        return users != null ? users.size() : 0;
    }

    public class MenuRolesViewHolder extends RecyclerView.ViewHolder {
        TextView tvIdAdmin;

        public MenuRolesViewHolder(@NonNull View itemView) {
            super(itemView);
            // Add null check during initialization
            tvIdAdmin = itemView.findViewById(R.id.tvIdAdmin);
        }
    }
}