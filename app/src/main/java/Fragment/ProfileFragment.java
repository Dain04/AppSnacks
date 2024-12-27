package Fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appsnacks.LoginActivity;
import com.example.appsnacks.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import ModelClass.User;

public class ProfileFragment extends Fragment {
    private TextView tvId, tvEmail, tvTenDangNhap, tvRole, tvNgayTao;
    private Button btnLogoutAdmin;
    private DatabaseReference mDatabase;
    private ImageButton avatarButtonAd;
    private FirebaseAuth mAuth;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        // Initialize Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance()
                .getReference("nguoi_dung");

        // Ánh xạ các TextView
        tvId = view.findViewById(R.id.tvId);
        tvEmail = view.findViewById(R.id.tvEmail);
        tvTenDangNhap = view.findViewById(R.id.tvTenDangNhap);
        tvRole = view.findViewById(R.id.tvRole);
        tvNgayTao = view.findViewById(R.id.tvNgayTao);
        btnLogoutAdmin = view.findViewById(R.id.btnLogoutAdmin);
        avatarButtonAd=view.findViewById(R.id.avatarButtonAd);

        // Lấy thông tin user từ Firebase
        loadUserProfile();

        // Thiết lập sự kiện cho nút Logout
        btnLogoutAdmin.setOnClickListener(v -> performLogout());

        return view;
    }
    private void loadUserProfile() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Tìm user trong database dựa trên email
            mDatabase.orderByChild("email").equalTo(currentUser.getEmail())
                    .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.exists()) {
                                // Lấy user đầu tiên tìm được
                                DataSnapshot userSnapshot = snapshot.getChildren().iterator().next();
                                User user = userSnapshot.getValue(User.class);
                                if (user != null) {
                                    // Hiển thị thông tin
                                    tvId.setText("Mã: " + user.getId_Nguoi_Dung());
                                    tvEmail.setText("Email: " + user.getEmail());
                                    tvTenDangNhap.setText("Tên: " + user.getTen());
                                    tvRole.setText("Vai Trò: " + user.getRole());
                                    tvNgayTao.setText("Ngày Tạo: " + user.getNgay_Tao_Tai_Khoan());
                                }
                            } else {
                                Toast.makeText(getContext(),
                                        "Không tìm thấy thông tin người dùng",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(getContext(),
                                    "Lỗi khi tải thông tin: " + error.getMessage(),
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }
    private void performLogout() {
        // Đăng xuất khỏi Firebase
        if (mAuth.getCurrentUser() != null) {
            mAuth.signOut();
        }
        // Chuyển về màn hình đăng nhập
        Intent intent = new Intent(requireActivity(), LoginActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);

        // Thông báo đăng xuất
        Toast.makeText(requireActivity(), "Đăng xuất thành công", Toast.LENGTH_SHORT).show();

        // Đóng activity hiện tại
        requireActivity().finish();
    }
}