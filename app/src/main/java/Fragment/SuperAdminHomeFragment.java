package Fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.appsnacks.R;
import com.google.firebase.database.FirebaseDatabase;

public class SuperAdminHomeFragment extends Fragment {
    private Button btnqlcategory, btnqlac, btnqlorder, btnqlsanpham, btnqlkhuyenmai;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_super_admin_home, container, false);
        // Khởi tạo Firebase Database với URL chính xác

        // Ánh xạ nút
        btnqlcategory = view.findViewById(R.id.btnqlcategory);
        btnqlac = view.findViewById(R.id.btnqlac);
        btnqlorder = view.findViewById(R.id.btnqlorder);
        btnqlsanpham = view.findViewById(R.id.btnqlsanpham);
        btnqlkhuyenmai = view.findViewById(R.id.btnqlkhuyenmai);

        // Phương thức chung để chuyển fragment
        setupFragmentNavigation(btnqlcategory, new CategoryFragment());
        setupFragmentNavigation(btnqlsanpham, new ProductManagementFragment());
         setupFragmentNavigation(btnqlac, new AccountManagementFragment());
        // Thêm các nút khác tương tự ở đây

        return view;
    }

    // Phương thức chung để chuyển fragment
    private void setupFragmentNavigation(Button button, Fragment targetFragment) {
        button.setOnClickListener(v -> {
            FragmentManager fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragmentContainer, targetFragment);
            fragmentTransaction.addToBackStack(null); // Cho phép quay lại fragment trước
            fragmentTransaction.commit();
        });
    }

}