package Fragment;
import static android.app.Activity.RESULT_OK;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.appsnacks.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import Adapter.ProductAdapter;
import ModelClass.ImgBBResponse;
import ModelClass.SanPham;
import ModelClass.TheLoai;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import android.Manifest;
import ModelClass.RetrofitClient;
import Adapter.MenuCategoryAdapter;
public class ProductManagementFragment extends Fragment {
    private AlertDialog addProductDialog;
    private AlertDialog currentDialog;
    private static final int PICK_IMAGE_REQUEST = 1;// Mã request dùng để chọn ảnh từ bộ nhớ.
    private Uri imageUri;
    private DatabaseReference categoryDatabase, productDatabase;
    private List<TheLoai> categoryList = new ArrayList<>();
    // Danh sách thể loại và sản phẩm.
    private List<SanPham> productList = new ArrayList<>();
    private ProductAdapter productAdapter;
    private MenuCategoryAdapter menuCategoryAdapter;
    private RecyclerView rycMenuCategories;
    private List<SanPham> originalProductList = new ArrayList<>(); // Lưu toàn bộ danh sách sản phẩm gốc

    @Nullable
    @Override
    //Đây là hàm khởi tạo giao diện của Fragment.
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_product_management, container, false);
        Log.d("ProductManagement", "View inflated successfully");

        // Kiểm tra quyền trước khi cho phép người dùng chọn ảnh
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    1);  // Request code
        }

        RecyclerView recyclerView = view.findViewById(R.id.rycProductAd);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        productAdapter = new ProductAdapter(productList, this, categoryList);
        recyclerView.setAdapter(productAdapter);

        // Setup horizontal menu categories RecyclerView
        rycMenuCategories = view.findViewById(R.id.rycMenuCategories);
        rycMenuCategories.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));

        // Load dữ liệu
        loadCategories();
        loadProducts();

        // Nút thêm sản phẩm
        view.findViewById(R.id.btnAddProduct).setOnClickListener(v -> showAddProductDialog());

        return view;
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Quyền đã được cấp, có thể tiếp tục thao tác với bộ nhớ
            } else {
                // Quyền bị từ chối, thông báo cho người dùng
                Toast.makeText(getContext(), "Cần quyền truy cập bộ nhớ để tải ảnh", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //load thể loại từ class thể loại để hiện list chọn thể loại khi thêm hoặc sửa san phẩm
    private void loadCategories() {
        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://appsnacks-f02da-default-rtdb.asia-southeast1.firebasedatabase.app");
        categoryDatabase = firebaseDatabase.getReference("the_loai");

        categoryDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                categoryList.clear();
                for (DataSnapshot childSnapshot : snapshot.getChildren()) {
                    TheLoai category = childSnapshot.getValue(TheLoai.class);
                    if (category != null) {
                        categoryList.add(category);
                    }
                }
                Log.d("ProductManagement", "Tải thể loại thành công: " + categoryList.size());

                // Add "Tất cả" (All) category as the first item
                TheLoai allCategory = new TheLoai("ALL", "Tất cả", "Tất cả sản phẩm");
                categoryList.add(0, allCategory);

                // Setup menu category adapter
                menuCategoryAdapter = new MenuCategoryAdapter(categoryList, new MenuCategoryAdapter.OnCategorySelectedListener() {
                    @Override
                    public void onCategorySelected(TheLoai category) {
                        filterProducts(category);
                    }
                });
                rycMenuCategories.setAdapter(menuCategoryAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ProductManagement", "Lỗi tải thể loại: " + error.getMessage());
            }
        });
    }
    private void filterProducts(TheLoai selectedCategory) {
        List<SanPham> filteredList = new ArrayList<>();

        if (selectedCategory.getMa_the_loai().equals("ALL")) {
            // Nếu chọn "Tất cả", hiển thị toàn bộ danh sách gốc
            filteredList.addAll(originalProductList);
        } else {
            // Lọc sản phẩm theo danh mục đã chọn
            for (SanPham product : originalProductList) {
                if (product.getTen_the_loai().equals(selectedCategory.getMa_the_loai())) {
                    filteredList.add(product);
                }
            }
        }

        // Cập nhật danh sách sản phẩm để hiển thị
        productList.clear();
        productList.addAll(filteredList);
        productAdapter.notifyDataSetChanged();
    }
    private void loadProducts() {
        try {
            FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance("https://appsnacks-f02da-default-rtdb.asia-southeast1.firebasedatabase.app");
            productDatabase = firebaseDatabase.getReference("san_pham");
            productDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    // Đảm bảo clear cả originalProductList
                    productList.clear();
                    originalProductList.clear();

                    for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                        SanPham sanPham = productSnapshot.getValue(SanPham.class);

                        if (sanPham != null) {
                            sanPham.setMa_san_pham(productSnapshot.getKey());
                            productList.add(sanPham);
                            originalProductList.add(sanPham); // Lưu vào danh sách gốc
                        }
                    }

                    // Cập nhật adapter trên main thread
                    if (getActivity() != null) {
                        getActivity().runOnUiThread(() -> {
                            productAdapter.notifyDataSetChanged();
                            Log.d("ProductManagement", "Đã tải " + productList.size() + " sản phẩm");
                        });
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Log.e("ProductManagement", "Lỗi tải sản phẩm: " + error.getMessage());
                }
            });
        } catch (Exception e) {
            Log.e("ProductManagement", "Ngoại lệ khi tải sản phẩm: " + e.getMessage(), e);
        }
    }

    private void showAddProductDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_product, null);

        EditText edtProductName = dialogView.findViewById(R.id.edtProductName);
        EditText edtProductStock = dialogView.findViewById(R.id.edtProductStock);
        EditText edtProductDesc = dialogView.findViewById(R.id.edtProductDesc);
        EditText edtProductPrice = dialogView.findViewById(R.id.edtProductPrice);
        Spinner spnCategories = dialogView.findViewById(R.id.spnCategories);
        ImageView imgProductPreview = dialogView.findViewById(R.id.imgProductPreview);

        // Đổ dữ liệu vào Spinner để thêm thể loại cho sản phẩm
        ArrayAdapter<TheLoai> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, categoryList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCategories.setAdapter(adapter);

        // Add image preview click listener
        imgProductPreview.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });

        addProductDialog = builder.setView(dialogView)
                .setPositiveButton("Thêm", (dialog, which) -> {
                    String productName = edtProductName.getText().toString().trim();
                    String productStock = edtProductStock.getText().toString().trim();
                    String productDesc = edtProductDesc.getText().toString().trim();
                    String productPrice = edtProductPrice.getText().toString().trim();
                    TheLoai selectedCategory = (TheLoai) spnCategories.getSelectedItem();

                    if (!productName.isEmpty() && !productStock.isEmpty() && selectedCategory != null) {
                        try {
                            // Chuyển đổi productStock và productPrice sang số
                            int stockValue = Integer.parseInt(productStock);
                            double priceValue = Double.parseDouble(productPrice);

                            // Tạo mã sản phẩm
                            String newProductId = productDatabase.push().getKey();
                            if (newProductId == null) {
                                newProductId = "SP_" + System.currentTimeMillis();
                            }

                            // Tạo đối tượng sản phẩm mới
                            SanPham newProduct = new SanPham(
                                    newProductId,
                                    productName,
                                    selectedCategory.getMa_the_loai(),
                                    stockValue,
                                    priceValue,
                                    productDesc
                            );

                            // Gọi hàm upload ảnh và thêm sản phẩm
                            uploadImageAndAddProduct(newProduct);
                        } catch (NumberFormatException e) {
                            Toast.makeText(getContext(), "Số lượng và giá phải là số hợp lệ", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null)
                .create();

        addProductDialog.show();
    }
    public  void showEditProductDialog(SanPham existingProduct) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_product, null);

        EditText edtProductName = dialogView.findViewById(R.id.edtProductName);
        EditText edtProductStock = dialogView.findViewById(R.id.edtProductStock);
        EditText edtProductDesc = dialogView.findViewById(R.id.edtProductDesc);
        EditText edtProductPrice = dialogView.findViewById(R.id.edtProductPrice);
        Spinner spnCategories = dialogView.findViewById(R.id.spnCategories);
        ImageView imgProductPreview = dialogView.findViewById(R.id.imgProductPreview);

        // Điền thông tin chi tiết sản phẩm hiện có
        edtProductName.setText(existingProduct.getTen_san_pham());
        edtProductStock.setText(String.valueOf(existingProduct.getSo_luong_ton()));
        edtProductDesc.setText(existingProduct.getMo_ta());
        edtProductPrice.setText(String.valueOf(existingProduct.getGia_ban()));

        // Set up category spinner
        ArrayAdapter<TheLoai> adapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, categoryList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnCategories.setAdapter(adapter);
        // Set thể loại hiện tại cho spnCategories
        for (int i = 0; i < categoryList.size(); i++) {
            if (categoryList.get(i).getMa_the_loai().equals(existingProduct.getTen_the_loai())) {
                spnCategories.setSelection(i);
                break;
            }
        }
        // Set up image preview
        if (existingProduct.getHinh_anh() != null && !existingProduct.getHinh_anh().isEmpty()) {
            Glide.with(getContext())
                    .load(existingProduct.getHinh_anh())
                    .placeholder(R.drawable.placeholder) // Ảnh placeholder nếu tải bị lỗi
                    .error(R.drawable.error_image) // Ảnh hiển thị nếu không tải được
                    .into(imgProductPreview);
        }
        // thêm ảnh vào ô xem trước
        imgProductPreview.setOnClickListener(v -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, PICK_IMAGE_REQUEST);
        });
        builder.setView(dialogView)
                .setTitle("Chỉnh sửa sản phẩm")
                .setPositiveButton("Cập nhật", (dialog, which) -> {
                    String productName = edtProductName.getText().toString().trim();
                    String productStock = edtProductStock.getText().toString().trim();
                    String productDesc = edtProductDesc.getText().toString().trim();
                    String productPrice = edtProductPrice.getText().toString().trim();
                    TheLoai selectedCategory = (TheLoai) spnCategories.getSelectedItem();

                    if (!productName.isEmpty() && !productStock.isEmpty() && selectedCategory != null) {
                        try {
                            int stockValue = Integer.parseInt(productStock);
                            double priceValue = Double.parseDouble(productPrice);

                            // Cập nhật thông tin chi tiết sản phẩm hiện có
                            existingProduct.setTen_san_pham(productName);
                            existingProduct.setSo_luong_ton(stockValue);
                            existingProduct.setMo_ta(productDesc);
                            existingProduct.setGia_ban(priceValue);
                            existingProduct.setTen_the_loai(selectedCategory.getMa_the_loai());
                            // Xử lý việc tải lên hình ảnh và cập nhật sản phẩm
                            uploadImageAndEditProduct(existingProduct);
                        } catch (NumberFormatException e) {
                            Toast.makeText(getContext(), "Số lượng và giá phải là số hợp lệ", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getContext(), "Vui lòng nhập đầy đủ thông tin", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Hủy", null);
         currentDialog = builder.create(); // Thay vì .show(), lưu dialog vào biến
        currentDialog.show();
    }
    private void uploadImageAndEditProduct(SanPham existingProduct) {
        if (imageUri != null) {
            // Tải hình ảnh lên ImgBB rồi cập nhật sản phẩm
            uploadImageToImgBBForEdit(imageUri, existingProduct);
        } else {
            // Nếu không có hình ảnh mới nào được chọn, hãy cập nhật sản phẩm luôn
            updateProductInDatabase(existingProduct);
        }
    }
    private void uploadImageToImgBBForEdit(Uri imageUri, SanPham existingProduct) {
        if (imageUri != null) {
            String API_KEY = "4532ce8ab2a1019ecc596c5f2772893b";

            ImgBBService service = RetrofitClient.getClient().create(ImgBBService.class);
            try {
                File file = new File(getRealPathFromURI(imageUri));
                RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);

                service.uploadImage(API_KEY, body)
                        .enqueue(new Callback<ImgBBResponse>() {
                            @Override
                            public void onResponse(Call<ImgBBResponse> call, Response<ImgBBResponse> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    String imageUrl = response.body().getData().getUrl();
                                    existingProduct.setHinh_anh(imageUrl);
                                    updateProductInDatabase(existingProduct);
                                } else {
                                    Log.e("ImgBBUpload", "Error: " + response.code() + " " + response.message());
                                    Toast.makeText(getContext(), "Lỗi upload ảnh: " + response.message(), Toast.LENGTH_SHORT).show();
                                }
                            }
                            @Override
                            public void onFailure(Call<ImgBBResponse> call, Throwable t) {
                                Log.e("ImgBBUpload", "Failure: " + t.getMessage(), t);
                                Toast.makeText(getContext(), "Lỗi upload ảnh: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            } catch (Exception e) {
                Log.e("ImgBBUpload", "Exception: " + e.getMessage(), e);
                Toast.makeText(getContext(), "Lỗi xử lý ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            updateProductInDatabase(existingProduct);
        }
    }

    private void updateProductInDatabase(SanPham product) {
        productDatabase.child(product.getMa_san_pham()).setValue(product)
                .addOnSuccessListener(unused -> {
                    Toast.makeText(getContext(), "Cập nhật sản phẩm thành công", Toast.LENGTH_SHORT).show();
                    loadProducts(); // Refresh the product list
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Lỗi khi cập nhật sản phẩm: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                    Log.e("ProductManagement", "Failed to update product", e);
                });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            imageUri = data.getData();

            // Kiểm tra xem dialog hiện tại là dialog thêm mới hay chỉnh sửa
            if (addProductDialog != null) {
                ImageView imgProductPreview = addProductDialog.findViewById(R.id.imgProductPreview);
                if (imgProductPreview != null) {
                    imgProductPreview.setImageURI(imageUri);
                }
            }

            if (currentDialog != null) {
                ImageView imgProductPreview = currentDialog.findViewById(R.id.imgProductPreview);
                if (imgProductPreview != null) {
                    imgProductPreview.setImageURI(imageUri);
                }
            }
        }
    }

    ////
    public interface ImgBBService {
        @Multipart
        @POST("1/upload")
        Call<ImgBBResponse> uploadImage(
                @Query("key") String apiKey,
                @Part MultipartBody.Part image
        );
    }

    private void uploadImageAndAddProduct(SanPham newProduct) {
        if (imageUri != null) {
            // Tải hình ảnh lên ImgBB và sau đó lưu sản phẩm
            uploadImageToImgBB(imageUri, newProduct);
        } else {
            // If no image is selected, save product directly
            saveProductToDatabase(newProduct);
        }
    }
    private void uploadImageToImgBB(Uri imageUri, SanPham newProduct) {
        if (imageUri != null) {
            String API_KEY = "4532ce8ab2a1019ecc596c5f2772893b";

            // Sử dụng getClient() không có tham số
            ImgBBService service = RetrofitClient.getClient().create(ImgBBService.class);
            try {
                File file = new File(getRealPathFromURI(imageUri));
                RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), file);
                MultipartBody.Part body = MultipartBody.Part.createFormData("image", file.getName(), requestFile);

                service.uploadImage(API_KEY, body)
                        .enqueue(new Callback<ImgBBResponse>() {
                            @Override
                            public void onResponse(Call<ImgBBResponse> call, Response<ImgBBResponse> response) {
                                if (response.isSuccessful() && response.body() != null) {
                                    String imageUrl = response.body().getData().getUrl();
                                    newProduct.setHinh_anh(imageUrl);
                                    saveProductToDatabase(newProduct);
                                } else {
                                    Log.e("ImgBBUpload", "Error: " + response.code() + " " + response.message());
                                    Toast.makeText(getContext(), "Lỗi upload ảnh: " + response.message(), Toast.LENGTH_SHORT).show();
                                }
                            }
                            @Override
                            public void onFailure(Call<ImgBBResponse> call, Throwable t) {
                                Log.e("ImgBBUpload", "Failure: " + t.getMessage(), t);
                                Toast.makeText(getContext(), "Lỗi upload ảnh: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
            } catch (Exception e) {
                Log.e("ImgBBUpload", "Exception: " + e.getMessage(), e);
                Toast.makeText(getContext(), "Lỗi xử lý ảnh: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            saveProductToDatabase(newProduct);
        }
    }

    // Helper method to get real path from URI
    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = { MediaStore.Images.Media.DATA };
        Cursor cursor = getActivity().getContentResolver().query(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String result = cursor.getString(column_index);
        cursor.close();
        return result;
    }
    private void saveProductToDatabase(SanPham newProduct) {
        // Truy vấn để lấy danh sách các mã sản phẩm
        productDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Set to store existing product ID numbers
                Set<Integer> existingProductIds = new HashSet<>();

                // Traverse and collect ID numbers of existing products
                for (DataSnapshot productSnapshot : snapshot.getChildren()) {
                    SanPham product = productSnapshot.getValue(SanPham.class);
                    if (product != null && product.getMa_san_pham() != null && product.getMa_san_pham().startsWith("SP_")) {
                        try {
                            int idNumber = Integer.parseInt(product.getMa_san_pham().substring(3));
                            existingProductIds.add(idNumber);
                        } catch (NumberFormatException e) {
                            Log.w("ProductManagement", "Invalid product ID format: " + product.getMa_san_pham());
                        }
                    }
                }

                // Generate a new unique ID by finding the smallest missing integer
                int newIdNumber = 1;
                while (existingProductIds.contains(newIdNumber)) {
                    newIdNumber++;
                }

                String newProductId = "SP_" + newIdNumber;
                newProduct.setMa_san_pham(newProductId);

                // Save the product to Firebase
                productDatabase.child(newProductId).setValue(newProduct)
                        .addOnSuccessListener(unused -> {
                            Toast.makeText(getContext(), "Sản phẩm được thêm thành công", Toast.LENGTH_SHORT).show();
                            loadProducts(); // Refresh the product list
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(getContext(), "Lỗi khi lưu sản phẩm: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            Log.e("ProductManagement", "Failed to save product", e);
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("ProductManagement", "Lỗi khi truy vấn sản phẩm: " + error.getMessage());
                Toast.makeText(getContext(), "Không thể kiểm tra mã sản phẩm", Toast.LENGTH_SHORT).show();
            }

        });
    }
}

