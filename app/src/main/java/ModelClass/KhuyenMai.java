package ModelClass;

public class KhuyenMai {
    public KhuyenMai(String ma_khuyen_mai, String ten_khuyen_mai, String mo_ta, String ngay_bat_dau, String ngay_ket_thuc, String trang_thai, String ngay_tao) {
        this.ma_khuyen_mai = ma_khuyen_mai;
        this.ten_khuyen_mai = ten_khuyen_mai;
        this.mo_ta = mo_ta;
        this.ngay_bat_dau = ngay_bat_dau;
        this.ngay_ket_thuc = ngay_ket_thuc;
        this.trang_thai = trang_thai;
        this.ngay_tao = ngay_tao;
    }

    private String ma_khuyen_mai;
    private String ten_khuyen_mai;
    private String mo_ta;
    private String ngay_bat_dau;

    public String getMa_khuyen_mai() {
        return ma_khuyen_mai;
    }

    public void setMa_khuyen_mai(String ma_khuyen_mai) {
        this.ma_khuyen_mai = ma_khuyen_mai;
    }

    public String getTen_khuyen_mai() {
        return ten_khuyen_mai;
    }

    public void setTen_khuyen_mai(String ten_khuyen_mai) {
        this.ten_khuyen_mai = ten_khuyen_mai;
    }

    public String getMo_ta() {
        return mo_ta;
    }

    public void setMo_ta(String mo_ta) {
        this.mo_ta = mo_ta;
    }

    public String getNgay_bat_dau() {
        return ngay_bat_dau;
    }

    public void setNgay_bat_dau(String ngay_bat_dau) {
        this.ngay_bat_dau = ngay_bat_dau;
    }

    public String getNgay_ket_thuc() {
        return ngay_ket_thuc;
    }

    public void setNgay_ket_thuc(String ngay_ket_thuc) {
        this.ngay_ket_thuc = ngay_ket_thuc;
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

    private String ngay_ket_thuc;
    private String trang_thai;
    private String ngay_tao;

}
