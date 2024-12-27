package ModelClass;

public class User {
    private String id_nguoi_dung;
    private String ten;
    private String email;
    private String mat_khau;
    private String role;
    private String so_dien_thoai;
    private String ngay_tao_tai_khoan;
    private String trang_thai;
    private Dia_Chi dia_chi;

    // Inner class for address
    public static class Dia_Chi {
        private String duong;
        private String phuong;
        private String quan;
        private String thanh_pho;

        // Constructors
        public Dia_Chi() {}

        public Dia_Chi(String duong, String phuong, String quan, String thanh_pho) {
            this.duong = duong;
            this.phuong = phuong;
            this.quan = quan;
            this.thanh_pho = thanh_pho;
        }

        // Getters and Setters
        public String getDuong() { return duong; }
        public void setDuong(String duong) { this.duong = duong; }

        public String getPhuong() { return phuong; }
        public void setPhuong(String phuong) { this.phuong = phuong; }

        public String getQuan() { return quan; }
        public void setQuan(String quan) { this.quan = quan; }

        public String getThanh_Pho() { return thanh_pho; }
        public void setThanh_Pho(String thanh_pho) { this.thanh_pho = thanh_pho; }
    }

    // Constructors
    public User() {}

    public User(String id_nguoi_dung, String ten, String email, String mat_khau, String role,
                String so_dien_thoai, String ngay_tao_tai_khoan, String trang_thai, Dia_Chi dia_chi) {
        this.id_nguoi_dung = id_nguoi_dung;
        this.ten = ten;
        this.email = email;
        this.mat_khau = mat_khau;
        this.role = role;
        this.so_dien_thoai = so_dien_thoai;
        this.ngay_tao_tai_khoan = ngay_tao_tai_khoan;
        this.trang_thai = trang_thai;
        this.dia_chi = dia_chi;
    }

    // Getters and Setters
    public String getId_Nguoi_Dung() { return id_nguoi_dung; }
    public void setId_Nguoi_Dung(String id_nguoi_dung) { this.id_nguoi_dung = id_nguoi_dung; }

    public String getTen() { return ten; }
    public void setTen(String ten) { this.ten = ten; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getMat_Khau() { return mat_khau; }
    public void setMat_Khau(String mat_khau) { this.mat_khau = mat_khau; }

    public String getRole() { return role; }
    public void setRole(String role) { this.role = role; }

    public String getSo_Dien_Thoai() { return so_dien_thoai; }
    public void setSo_Dien_Thoai(String so_dien_thoai) { this.so_dien_thoai = so_dien_thoai; }

    public String getNgay_Tao_Tai_Khoan() { return ngay_tao_tai_khoan; }
    public void setNgay_Tao_Tai_Khoan(String ngay_tao_tai_khoan) { this.ngay_tao_tai_khoan = ngay_tao_tai_khoan; }

    public String getTrang_Thai() { return trang_thai; }
    public void setTrang_Thai(String trang_thai) { this.trang_thai = trang_thai; }

    public Dia_Chi getDia_Chi() { return dia_chi; }
    public void setDia_Chi(Dia_Chi dia_chi) { this.dia_chi = dia_chi; }
}