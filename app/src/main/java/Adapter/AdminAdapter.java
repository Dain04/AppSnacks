package Adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.appsnacks.R;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import android.content.Context;
import ModelClass.User;

public class AdminAdapter extends RecyclerView.Adapter<AdminAdapter.AdminViewHolder> {
    private List<User> userList;
    private Context context;
    private OnEditClickListener editClickListener;
    // Tạo interface(tại không thể cast context thành fragment trưcj tiếp đc nên
    //tạo một interface để giao tiếp giữa Adapter và Fragment)
    public interface OnEditClickListener {
        void onEditClick(User user);
    }

    // Thêm setter cho listener
    public void setOnEditClickListener(OnEditClickListener listener) {
        this.editClickListener = listener;
    }

    // Constructor giữ nguyên
    public AdminAdapter(List<User> userList, Context context) {
        this.userList = userList;
        this.context = context;
    }

    @NonNull
    @Override
    public AdminViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_account_admin, parent, false);
        return new AdminViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminViewHolder holder, int position) {
        User user = userList.get(position);

        holder.tvIdAdmin.setText("ID: " + user.getId_Nguoi_Dung());
        holder.tvEmailAdmin.setText("Email: " + user.getEmail());
        holder.tvNameAdmin.setText("Tên: " + user.getTen());

        // Phân tích và định dạng ngày
        String formattedDate = formatDate(user.getNgay_Tao_Tai_Khoan());
        holder.tvNgayTaoAmin.setText("Ngày Tạo: " + formattedDate);

        holder.tvRoleAdmin.setText("Role: " + formatRole(user.getRole()));
        holder.btnDeleteAccount.setOnClickListener(v -> {
            // Implement delete logic
        });

        holder.btnEditAccount.setOnClickListener(v -> {
            if (editClickListener != null) {
                editClickListener.onEditClick(user);
            }
        });
    }
//kkkkk
    @Override
    public int getItemCount() {
        return userList.size();
    }
    //Phương pháp trợ giúp để định dạng vai trò
    private String formatRole(String role) {
        switch (role) {
            case "super_admin":
                return "Super Admin";
            case "sales_manager":
                return "Sales Manager";
            default:
                return role;
        }
    }

    // Phương pháp trợ giúp để định dạng ngày
    private String formatDate(String isoDate) {
        try {
            //Phân tích ngày ISO 8601
            SimpleDateFormat isoFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.getDefault());
            Date date = isoFormat.parse(isoDate);

            // Định dạng theo ý muốn output
            SimpleDateFormat outputFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
            return outputFormat.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
            return isoDate; // Trả về bản gốc nếu phân tích cú pháp không thành công
        }
    }

    static class AdminViewHolder extends RecyclerView.ViewHolder {
        TextView tvIdAdmin, tvEmailAdmin, tvNameAdmin,
                tvNgayTaoAmin, tvRoleAdmin;
        ImageButton btnDeleteAccount, btnEditAccount;
        ImageView imgAvatarAd;

        public AdminViewHolder(@NonNull View itemView) {
            super(itemView);
            tvIdAdmin = itemView.findViewById(R.id.tvIdAdmin);
            tvEmailAdmin = itemView.findViewById(R.id.tvEmailAdmin);
            tvNameAdmin = itemView.findViewById(R.id.tvNameAdmin);
            tvNgayTaoAmin = itemView.findViewById(R.id.tvNgayTaoAmin);
            tvRoleAdmin = itemView.findViewById(R.id.tvRoleAdmin);

            btnDeleteAccount = itemView.findViewById(R.id.btnDeleteAccount);
            btnEditAccount = itemView.findViewById(R.id.btnEditAccount);
            imgAvatarAd = itemView.findViewById(R.id.imgAvatarAd);
        }
    }
}