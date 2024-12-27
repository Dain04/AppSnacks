package ModelClass;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class SanPham {
    // Các thuộc tính
    private String ten_the_loai;
    private int so_luong_ton;
    private double gia_ban;
    private String ten_san_pham;
    private String mo_ta;
    private String hinh_anh;
    private String ma_san_pham;
    private String trang_thai;
    private String ngay_tao;



    // Constructor không tham số (yêu cầu của Firebase)
    public SanPham() {
        this.hinh_anh = "";
        this.trang_thai = "Còn hàng";
        this.ngay_tao = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
    }

    // Constructor đầy đủ tham số
    public SanPham(String ma_san_pham, String ten_san_pham, String ten_the_loai,
                   int so_luong_ton, double gia_ban, String mo_ta) {
        if (so_luong_ton < 0 || gia_ban < 0) {
            throw new IllegalArgumentException("Số lượng và giá không được âm");
        }

        this.ma_san_pham = ma_san_pham;
        this.ten_san_pham = ten_san_pham;
        this.ten_the_loai = ten_the_loai;
        this.so_luong_ton = so_luong_ton;
        this.gia_ban = gia_ban;
        this.mo_ta = mo_ta;
        this.hinh_anh = "";
        this.trang_thai = "Còn hàng";
        this.ngay_tao = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(new Date());
    }


    // Getter và Setter
    public String getTen_the_loai() {
        return ten_the_loai;
    }

    public void setTen_the_loai(String ma_the_loai) {
        this.ten_the_loai = ma_the_loai;
    }


    public String getTen_san_pham() {
        return ten_san_pham;
    }

    public void setTen_san_pham(String ten_san_pham) {
        this.ten_san_pham = ten_san_pham;
    }

    public String getMo_ta() {
        return mo_ta;
    }

    public void setMo_ta(String mo_ta) {
        this.mo_ta = mo_ta;
    }

    public String getHinh_anh() {
        return hinh_anh;
    }

    public void setHinh_anh(String hinh_anh) {
        this.hinh_anh = hinh_anh;
    }

    public String getMa_san_pham() {
        return ma_san_pham;
    }

    public void setMa_san_pham(String ma_san_pham) {
        this.ma_san_pham = ma_san_pham;
    }

    public String getTrang_thai() {
        return trang_thai;
    }

    public void setTrang_thai(String trang_thai) {
        this.trang_thai = trang_thai;
    }

    public String getNgay_tao() {
        return ngay_tao;
    }
    public void setNgay_tao(String ngay_tao) {
        this.ngay_tao = ngay_tao;
    }

    public int getSo_luong_ton() {
        return so_luong_ton;
    }

    public void setSo_luong_ton(int so_luong_ton) {
        this.so_luong_ton = so_luong_ton;
    }

    public double getGia_ban() {
        return gia_ban;
    }

    public void setGia_ban(double gia_ban) {
        this.gia_ban = gia_ban;
    }

    public boolean isConHang() {
        return so_luong_ton > 0;
    }
}