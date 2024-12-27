package Fragment;
import android.app.AlertDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.appsnacks.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

import Adapter.CategoryAdapter;
import ModelClass.TheLoai;

public class CategoryFragment extends Fragment {
    private RecyclerView recyclerView;
    private CategoryAdapter adapter;
    private List<TheLoai> categoryList = new ArrayList<>();
    private DatabaseReference mDatabase;
    private Button btnAddCategory;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category, container, false);

        // Khởi tạo Firebase Database với URL chính xác
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://appsnacks-f02da-default-rtdb.asia-southeast1.firebasedatabase.app");
        mDatabase = firebaseDatabase.getReference("the_loai");

        recyclerView = view.findViewById(R.id.recyclerViewCategories);
        btnAddCategory = view.findViewById(R.id.btnAddCategory);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Load danh sách thể loại
        loadCategories();

        // Nút thêm thể loại
        btnAddCategory.setOnClickListener(v -> showAddCategoryDialog());

        return view;
    }

    private void loadCategories() {
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryList.clear();
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    TheLoai category = childSnapshot.getValue(TheLoai.class);
                    if (category != null) {
                        categoryList.add(category);
                    }
                }

                // Kiểm tra nếu danh sách không rỗng
                if (!categoryList.isEmpty()) {
                    // Tạo và set adapter
                    adapter = new CategoryAdapter(categoryList, new CategoryAdapter.OnItemClickListener() {
                        @Override
                        public void onItemClick(TheLoai theLoai) {
                            showCategoryDetails(theLoai);
                        }

                        @Override
                        public void onEditClick(TheLoai theLoai) {
                            showEditCategoryDialog(theLoai);
                        }
                        @Override
                        public void onDeleteClick(TheLoai theLoai) {
                            showDeleteCategoryDialog(theLoai);
                        }
                    });

                    recyclerView.setAdapter(adapter);
                } else {
                    // Xử lý trường hợp danh sách rỗng
                    Log.d("CategoryFragment", "Không có dữ liệu thể loại");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("CategoryFragment", "Lỗi tải dữ liệu: " + error.getMessage());
            }
        });
    }

    private void showAddCategoryDialog() {
        // Hiển thị dialog để thêm thể loại mới
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_category, null);

        EditText edtCategoryName = dialogView.findViewById(R.id.edtCategoryName);
        EditText edtCategoryDesc = dialogView.findViewById(R.id.edtCategoryDesc);

        builder.setView(dialogView)
                .setTitle("Thêm Thể Loại Mới")
                .setPositiveButton("Thêm", (dialog, which) -> {
                    String name = edtCategoryName.getText().toString();
                    String desc = edtCategoryDesc.getText().toString();

                    // Tạo mã thể loại tự động
                    String newMaTL = "TL_" + String.format("%03d", categoryList.size() + 1);

                    TheLoai newCategory = new TheLoai(newMaTL, name, desc);
                    mDatabase.child(newMaTL).setValue(newCategory)
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getContext(), "Thêm thể loại thành công", Toast.LENGTH_SHORT).show();
                                    loadCategories();// tải lại danh sách thể loại sau khi thêm mới
                                }else {
                                    Toast.makeText(getContext(), "Thêm thể loại thất bại", Toast.LENGTH_SHORT).show();
                                }
                            });
                })
                .setNegativeButton("Hủy", null)
                .create()
                .show();
    }

    private void showEditCategoryDialog(TheLoai theLoai) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_category, null);

        EditText edtCategoryName = dialogView.findViewById(R.id.edtCategoryName);
        EditText edtCategoryDesc = dialogView.findViewById(R.id.edtCategoryDesc);

        // Điền thông tin thể loại hiện tại
        edtCategoryName.setText(theLoai.getTen_the_loai());
        edtCategoryDesc.setText(theLoai.getMo_ta());

        builder.setView(dialogView)
                .setTitle("Chỉnh sửa thể loại")
                .setPositiveButton("Lưu", (dialog, which) -> {
                    String updatedName = edtCategoryName.getText().toString().trim();
                    String updatedDesc = edtCategoryDesc.getText().toString().trim();
                    if (!updatedName.isEmpty() && !updatedDesc.isEmpty()) {
                        // Cập nhật Firebase
                        theLoai.setTen_the_loai(updatedName);
                        theLoai.setMo_ta(updatedDesc);
                        mDatabase.child(theLoai.getMa_the_loai()).setValue(theLoai)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(getContext(), "Cập nhật thành công", Toast.LENGTH_SHORT).show();
                                    loadCategories(); // Tải lại danh sách thể loại sau khi cập nhật
                                })
                                .addOnFailureListener(e ->
                                        Toast.makeText(getContext(), "Lỗi cập nhật: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                    } else {
                        Toast.makeText(getContext(), "Vui lòng nhập tên và mô tả", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .create()
                .show();
    }


    private void showCategoryDetails(TheLoai theLoai) {
        // Hiển thị chi tiết thể loại, có thể là một dialog hoặc chuyển sang fragment mới
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setTitle("Chi Tiết Thể Loại")
                .setMessage("Mã TL: " + theLoai.getMa_the_loai() +
                        "\nTên: " + theLoai.getTen_the_loai() +
                        "\nMô tả: " + theLoai.getMo_ta())
                .setPositiveButton("Đóng", null)
                .create()
                .show();
    }
    private void showDeleteCategoryDialog(TheLoai theLoai) {
        new AlertDialog.Builder(getContext())
                .setTitle("Xóa thể loại")
                .setMessage("Bạn có chắc muốn xóa thể loại"+theLoai.getTen_the_loai()+" không ?")
                .setPositiveButton("Xóa", (dialog, which) -> {
                    mDatabase.child(theLoai.getMa_the_loai()).removeValue()
                            .addOnCompleteListener(task -> {
                                if (task.isSuccessful()) {
                                    Toast.makeText(getContext(), "Xóa thành công", Toast.LENGTH_SHORT).show();
                                    loadCategories();// tải lại danh sách thể loại sau khi thêm mới
                                }else {
                                    Toast.makeText(getContext(), "Xóa thất bại", Toast.LENGTH_SHORT).show();
                                }
                                });
                            })
                .setNegativeButton("Hủy", null)
                .show();
    }
}