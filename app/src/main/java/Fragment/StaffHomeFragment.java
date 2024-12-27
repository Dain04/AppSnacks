package Fragment;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.appsnacks.R;

public class StaffHomeFragment extends Fragment {

    private Button btnqlcategory, btnqlorder, btnqlsanpham, btnqlkhuyenmai;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_staff_home, container, false);

        // Ánh xạ nút
        btnqlcategory = view.findViewById(R.id.btnqlcategory);
        btnqlorder = view.findViewById(R.id.btnqlorder);
        btnqlsanpham = view.findViewById(R.id.btnqlsanpham);
        btnqlkhuyenmai = view.findViewById(R.id.btnqlkhuyenmai);

        // Phương thức chung để chuyển fragment
        setupFragmentNavigation(btnqlcategory, new CategoryFragment());
        setupFragmentNavigation(btnqlsanpham, new ProductManagementFragment());

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
