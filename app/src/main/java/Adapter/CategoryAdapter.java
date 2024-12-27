package Adapter;
import android.widget.TextView;
import androidx.recyclerview.widget.RecyclerView;
import java.util.List;
import ModelClass.TheLoai;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;

import com.example.appsnacks.R;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {
    private List<TheLoai> theLoais;
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(TheLoai theLoai);
        void onEditClick(TheLoai theLoai);
        void onDeleteClick(TheLoai theLoai);
    }

    public CategoryAdapter(List<TheLoai> theLoais, OnItemClickListener listener) {
        this.theLoais = theLoais;
        this.listener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CategoryViewHolder holder, int position) {
        TheLoai theLoai = theLoais.get(position);
        holder.bind(theLoai);
        holder.itemView.findViewById(R.id.btnEditCategory).setOnClickListener(v -> listener.onEditClick(theLoai));
        holder.itemView.findViewById(R.id.btnDeleteCategory).setOnClickListener(v->{
            if (listener != null) {
                listener.onDeleteClick(theLoai);
            }
        });
    }

    @Override
    public int getItemCount() {
        return theLoais.size();
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {
        TextView tvCategoryCode, tvCategoryName, tvCategoryDescription;


        public CategoryViewHolder(@NonNull View itemView) {
            super(itemView);
            tvCategoryCode = itemView.findViewById(R.id.tvCategoryCode);
            tvCategoryName = itemView.findViewById(R.id.tvCategoryName);
            tvCategoryDescription = itemView.findViewById(R.id.tvCategoryDescription);
        }

        public void bind(final TheLoai theLoai) {
            tvCategoryCode.setText("Mã TL: " + theLoai.getMa_the_loai());
            tvCategoryName.setText("Tên: " + theLoai.getTen_the_loai());
            tvCategoryDescription.setText("Mô tả: " + theLoai.getMo_ta());

            itemView.setOnClickListener(v -> listener.onItemClick(theLoai));
        }
    }
}