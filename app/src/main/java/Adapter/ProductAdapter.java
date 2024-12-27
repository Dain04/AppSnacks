package Adapter;
import android.content.Context;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageButton;
import android.widget.ImageView;

import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.appsnacks.R;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;
import Fragment.ProductManagementFragment;

import ModelClass.SanPham;
import ModelClass.TheLoai;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.ProductViewHolder> {
    private List<SanPham> productList;
    private Context context;
    private List<TheLoai> categoryList;
    private ProductManagementFragment fragment;
    public void setProductList(List<SanPham> newProductList) {
        this.productList = newProductList;
        notifyDataSetChanged();
    }
    public ProductAdapter(List<SanPham> productList, ProductManagementFragment fragment, List<TheLoai> categoryList) {
        this.productList = productList;
        this.context = fragment.getContext(); // Lấy context từ fragment
        this.fragment = fragment; // Lưu reference tới fragment
        this.categoryList = categoryList;
    }
    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product_ad, parent, false);
        return new ProductViewHolder(view);
    }
    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        SanPham currentProduct = productList.get(position);

        holder.tvProductId.setText("Mã sản phẩm: " + currentProduct.getMa_san_pham());
        holder.tvProductName.setText("Tên sản phẩm: " + currentProduct.getTen_san_pham());
        for (TheLoai category : categoryList) {
            if (category.getMa_the_loai().equals(currentProduct.getTen_the_loai())) {
                holder.tvProductCategory.setText("Thể loại: " + category.getTen_the_loai());
                break;
            }
        }
        holder.tvProductStock.setText("Số lượng tồn: " + currentProduct.getSo_luong_ton());
        holder.tvProductPrice.setText(String.format("Giá: %,d VND", (long)currentProduct.getGia_ban()));
        holder.tvProductDescription.setText("Mô tả: " + currentProduct.getMo_ta());
        holder.tvProductStatus.setText("Trạng thái: " + currentProduct.getTrang_thai());
        holder.tvProductDate.setText("Ngày tạo: " + currentProduct.getNgay_tao());

        // Hiển thị ảnh
        if (currentProduct.getHinh_anh() != null && !currentProduct.getHinh_anh().isEmpty()) {
            Glide.with(context)
                    .load(currentProduct.getHinh_anh())
                    .placeholder(R.drawable.ic_launcher_foreground) // Ảnh placeholder
                    .error(android.R.drawable.ic_menu_gallery) // Ảnh lỗi
                    .into(holder.imgProduct);
        }

        // Xử lý sự kiện nút xóa
        holder.btnDeleteProduct.setOnClickListener(v -> {
            removeProduct(position);
        });

        // Xử lý sự kiện nút sửa
        holder.btnEditProduct.setOnClickListener(v -> {
            if (fragment != null) {
                fragment.showEditProductDialog(currentProduct);
            }
        });
    }
    @Override
    public int getItemCount() {
        return productList.size();
    }
    private void removeProduct(int position) {
        if (position < 0 || position >= productList.size()) {
            Log.e("DeleteProduct", "Invalid position");
            return;
        }
        SanPham product = productList.get(position);
        if (product == null || TextUtils.isEmpty(product.getMa_san_pham())) {
            Toast.makeText(context, "Mã sản phẩm không hợp lệ", Toast.LENGTH_SHORT).show();
            return;
        }
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://appsnacks-f02da-default-rtdb.asia-southeast1.firebasedatabase.app");
        DatabaseReference productRef = database.getReference("san_pham").child(product.getMa_san_pham());
        productRef.removeValue()
                .addOnSuccessListener(aVoid -> {
                    productList.remove(position);
                    notifyItemRemoved(position);
                    Toast.makeText(context, "Đã xóa sản phẩm", Toast.LENGTH_SHORT).show();
                })
                .addOnFailureListener(e -> {
                    Log.e("DeleteProduct", "Lỗi xóa sản phẩm: " + e.getMessage());
                    Toast.makeText(context, "Lỗi xóa sản phẩm: " + e.getMessage(), Toast.LENGTH_LONG).show();
                });
    }

    public void updateProductList(List<SanPham> newList) {
        productList.clear();
        productList.addAll(newList);
        notifyDataSetChanged();
    }
    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView imgProduct;
        TextView tvProductId, tvProductName, tvProductCategory,
                tvProductStock, tvProductDescription,
                tvProductStatus, tvProductDate,tvProductPrice;
        ImageButton btnDeleteProduct, btnEditProduct;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);

            imgProduct = itemView.findViewById(R.id.imgProductAd);
            tvProductId = itemView.findViewById(R.id.tvProductIdAd);
            tvProductName = itemView.findViewById(R.id.tvProductNameAd);
            tvProductCategory = itemView.findViewById(R.id.tvProductCategoryAd);
            tvProductStock = itemView.findViewById(R.id.tvProductStockAd);
            tvProductDescription = itemView.findViewById(R.id.tvProductDescriptionAd);
            tvProductStatus = itemView.findViewById(R.id.tvProductStatusAd);
            tvProductDate = itemView.findViewById(R.id.tvProductDateAd);
            tvProductPrice=itemView.findViewById(R.id.tvProductPriceAd);
            btnDeleteProduct = itemView.findViewById(R.id.btnDeleteProduct);
            btnEditProduct = itemView.findViewById(R.id.btnEditProduct);
        }
    }
}