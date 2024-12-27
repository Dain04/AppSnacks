package Fragment;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.app.AlertDialog;
import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import com.example.appsnacks.R;
import java.util.ArrayList;
import Adapter.AdminAdapter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import Adapter.MenuRolesAdapter;
import ModelClass.User;

import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
public class AccountManagementFragment extends Fragment {
    private RecyclerView rycAccountAdmin;
    private AdminAdapter adminAdapter;
    private List<User> userList = new ArrayList<>();

    private DatabaseReference databaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account_management, container, false);

        rycAccountAdmin = view.findViewById(R.id.rycAccountAdmin);
        Button btnAddAdmin = view.findViewById(R.id.btnAddAdmin);

        // Khởi tạo Firebase Database với URL chính xác
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://appsnacks-f02da-default-rtdb.asia-southeast1.firebasedatabase.app");
        databaseReference = firebaseDatabase.getReference("nguoi_dung");

        // Setup RecyclerView
        rycAccountAdmin.setLayoutManager(new LinearLayoutManager(getContext()));
        loadRoles();
        // Fetch admin data from Firebase
        // Replace fetchAdminData() with loadAdmins()
        loadAdmins(view);
        btnAddAdmin.setOnClickListener(v -> {
            // Implement add admin logic
        });
        return view;
    }

    private void loadRoles() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                for(DataSnapshot childSnapshot : snapshot.getChildren()) {
                    User user = childSnapshot.getValue(User.class);
                    if(user.getRole()!=null){
                        userList.add(user);
                    }
                }
                Log.d("AccountManagement", "Tải vai trò thành công: " + userList.size());

            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("AccountManagement", "Lỗi tải vai trò: " + error.getMessage());
            }
        });

    }
    public void showEditManagerAccountAd(User existingUser) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_edit_role, null);

        EditText editNameAc = dialogView.findViewById(R.id.edtNameAc);
        Spinner spnRoles = dialogView.findViewById(R.id.spnRoles);

        // Điền thông tin tài khoản hiện có
        editNameAc.setText(existingUser.getTen());

        // Tạo adapter cho spinner roles
        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item,
                new String[]{"super_admin", "sales_manager", "customer"});
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnRoles.setAdapter(adapter);

        // Set role hiện tại cho spinner
        int selection = 0;
        String currentRole = existingUser.getRole();
        for(int i = 0; i < adapter.getCount(); i++) {
            if(adapter.getItem(i).equals(currentRole)) {
                selection = i;
                break;
            }
        }
        spnRoles.setSelection(selection);

        builder.setView(dialogView)
                .setTitle("Chỉnh sửa tài khoản")
                .setPositiveButton("Lưu", (dialog, which) -> {
                    String newName = editNameAc.getText().toString().trim();
                    String selectedRole = spnRoles.getSelectedItem().toString();

                    if(newName.isEmpty()) {
                        Toast.makeText(getContext(), "Vui lòng nhập tên", Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Cập nhật thông tin vào Firebase
                    DatabaseReference userRef = databaseReference.child(existingUser.getId_Nguoi_Dung());
                    userRef.child("ten").setValue(newName);
                    userRef.child("role").setValue(selectedRole)
                            .addOnSuccessListener(aVoid -> {
                                Toast.makeText(getContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                                // Reload data
                                loadAdmins(getView());
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(getContext(), "Lỗi: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            });
                })
                .setNegativeButton("Hủy", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }
    private void loadAdmins(View view) {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://appsnacks-f02da-default-rtdb.asia-southeast1.firebasedatabase.app");
        databaseReference = firebaseDatabase.getReference("nguoi_dung");

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                List<User> originalAdminList = new ArrayList<>();

                // Collect all admins
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    User user = dataSnapshot.getValue(User.class);
                    if (user != null) {
                        userList.add(user);
                        originalAdminList.add(user);
                    }
                }
                // Tạo danh sách các vai trò duy nhất
                List<User> rolesList = new ArrayList<>();
                // Add "All" role first
                User allRolesAdmin = new User();
                allRolesAdmin.setId_Nguoi_Dung("ALL");
                allRolesAdmin.setRole("Tất cả");
                rolesList.add(allRolesAdmin);

                // Add unique roles
                Set<String> uniqueRoles = new HashSet<>();
                for (User admin : originalAdminList) {
                    if (uniqueRoles.add(admin.getRole())) {
                            User roleAdmin = new User();
                        roleAdmin.setRole(admin.getRole());
                        rolesList.add(roleAdmin);
                    }
                }

                // Setup roles menu
                RecyclerView rycMenuRoles = view.findViewById(R.id.rycMenuRoles);
                rycMenuRoles.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

                MenuRolesAdapter menuRolesAdapter = new MenuRolesAdapter(rolesList, new MenuRolesAdapter.OnAdminSelectedListener() {
                    @Override
                    public void onAdminSelected(User selectedRole) {
                        filterAdmins(selectedRole, originalAdminList);
                    }
                });
                rycMenuRoles.setAdapter(menuRolesAdapter);

                adminAdapter = new AdminAdapter(userList, getContext());
                adminAdapter.setOnEditClickListener(new AdminAdapter.OnEditClickListener() {
                    @Override
                    public void onEditClick(User user) {
                        showEditManagerAccountAd(user);
                    }
                });
                rycAccountAdmin.setAdapter(adminAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("AccountManagement", "Error fetching admin data: " + error.getMessage());
                Toast.makeText(getContext(), "Lỗi tải dữ liệu: " + error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

        private void filterAdmins(User selectedRole, List<User> originalAdminList) {
        List<User> filteredList = new ArrayList<>();

        if (selectedRole.getId_Nguoi_Dung() != null && selectedRole.getId_Nguoi_Dung().equals("ALL")) {
            // If "All" is selected, show all accounts
            filteredList.addAll(originalAdminList);
        } else {
            // Lọc tài khoản theo vai trò đã chọn
            for (User admin : originalAdminList) {
                if (admin.getRole() != null && admin.getRole().equals(selectedRole.getRole())) {
                    filteredList.add(admin);
                }
            }
        }

        // Cập nhật danh sách để hiển thị
            userList.clear();
            userList.addAll(filteredList);

        // Thêm kiểm tra null để ngăn chặn NullPointerException
            if (adminAdapter != null) {
                adminAdapter.notifyDataSetChanged();
            } else {
                adminAdapter = new AdminAdapter(userList, getContext());
                adminAdapter.setOnEditClickListener(new AdminAdapter.OnEditClickListener() {
                    @Override
                    public void onEditClick(User user) {
                        showEditManagerAccountAd(user);
                    }
                });
                rycAccountAdmin.setAdapter(adminAdapter);
            }
    }
}