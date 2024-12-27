package com.example.appsnacks;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.FirebaseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import ModelClass.User;

public class LoginActivity extends AppCompatActivity {
    private static final int RC_SIGN_IN = 9001;
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private DatabaseReference mDatabase;
    private EditText edtTaiKhoan, edtPassword, edtPhone,editRegisPhone;
    private Button btnEmailSignIn, btnGoogleSignIn, btnPhoneSignIn,btnRegisterIn;
    private String verificationId;
    private TextView tvLoginPhone, tvRegister,txtLogin,tvLoginPassword;
    private boolean isPhoneLoginView = false; // Để track view hiện tại
    private boolean isRegisterView = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Mặc định hiển thị layout email
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        if (mAuth.getCurrentUser() != null) {
            mAuth.signOut();
        }

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mGoogleSignInClient.signOut();

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        if (isRegisterView) {
            // Register layout
            editRegisPhone = findViewById(R.id.editRegisPhone);
            btnRegisterIn = findViewById(R.id.btnRegisterIn);
            btnGoogleSignIn = findViewById(R.id.btnGoogleSignIn);
            txtLogin = findViewById(R.id.txtLogin);
        } else if (isPhoneLoginView) {
            // Phone layout
            edtPhone = findViewById(R.id.edtPhone);
            btnPhoneSignIn = findViewById(R.id.btnPhoneSignIn);
            btnGoogleSignIn = findViewById(R.id.btnGoogleSignIn);
            tvRegister = findViewById(R.id.txtRegister);
            tvLoginPassword = findViewById(R.id.tvLoginPassword);
        } else {
            // Email layout
            edtTaiKhoan = findViewById(R.id.edtEmail);
            edtPassword = findViewById(R.id.edtPassword);
            btnEmailSignIn = findViewById(R.id.btnEmailSignIn);
            btnGoogleSignIn = findViewById(R.id.btnGoogleSignIn);
            tvLoginPhone = findViewById(R.id.tvLoginPhone);
            tvRegister = findViewById(R.id.txtRegister);
        }
    }

    private void setupClickListeners() {
        if (isRegisterView) {
            if (btnRegisterIn != null) {
                btnRegisterIn.setOnClickListener(v -> startPhoneAuth());
            }
            if (txtLogin != null) {
                txtLogin.setOnClickListener(v -> switchToEmailLogin());
            }
        } else if (isPhoneLoginView) {
            if (btnPhoneSignIn != null) {
                btnPhoneSignIn.setOnClickListener(v -> startPhoneAuth());
            }
            if (tvLoginPassword != null) {
                tvLoginPassword.setOnClickListener(v -> switchToEmailLogin());
            }
            if (tvRegister != null) {
                tvRegister.setOnClickListener(v -> switchToRegister());
            }
        } else {
            if (btnEmailSignIn != null) {
                btnEmailSignIn.setOnClickListener(v -> signInWithEmail());
            }
            if (tvLoginPhone != null) {
                tvLoginPhone.setOnClickListener(v -> switchToPhoneLogin());
            }
            if (tvRegister != null) {
                tvRegister.setOnClickListener(v -> switchToRegister());
            }
        }

        if (btnGoogleSignIn != null) {
            btnGoogleSignIn.setOnClickListener(v -> signInWithGoogle());
        }
    }

    private void switchToRegister() {
        isRegisterView = true;
        isPhoneLoginView = false;
        setContentView(R.layout.activity_register);
        initializeViews();
        setupClickListeners();
    }

    private void switchToPhoneLogin() {
        isPhoneLoginView = true;
        isRegisterView = false;
        setContentView(R.layout.activity_login_phone);
        initializeViews();
        setupClickListeners();
    }

    private void switchToEmailLogin() {
        isPhoneLoginView = false;
        isRegisterView = false;
        setContentView(R.layout.activity_login);
        initializeViews();
        setupClickListeners();
    }
    // Thêm xử lý nút Back
    @Override
    public void onBackPressed() {
        if (isPhoneLoginView || isRegisterView) {
            switchToEmailLogin();
        } else {
            super.onBackPressed();
        }
    }
    private void signInWithEmail() {
        String loginCredential = edtTaiKhoan.getText().toString().trim();
        String password = edtPassword.getText().toString().trim();

        if (loginCredential.isEmpty() || password.isEmpty()) {
            Toast.makeText(LoginActivity.this, "Vui lòng nhập đầy đủ thông tin!", Toast.LENGTH_SHORT).show();
            return;
        }

        DatabaseReference userRef = mDatabase.child("nguoi_dung");

        // Kiểm tra thông tin đăng nhập
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                boolean userFound = false;

                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);

                    if (user != null) {
                        // Kiểm tra xem thông tin đăng nhập khớp với email, số điện thoại hoặc tên đăng nhập
                        boolean credentialMatch = (user.getEmail() != null && user.getEmail().equals(loginCredential)) ||
                                (user.getSo_Dien_Thoai() != null && user.getSo_Dien_Thoai().equals(loginCredential)) ||
                                (user.getTen() != null && user.getTen().equals(loginCredential));

                        // Nếu tìm thấy người dùng với thông tin đăng nhập khớp
                        if (credentialMatch) {
                            userFound = true;
                            // Sử dụng email để đăng nhập vào Firebase Authentication
                            if (user.getEmail() != null && !user.getEmail().isEmpty()) {
                                mAuth.signInWithEmailAndPassword(user.getEmail(), password)
                                        .addOnCompleteListener(LoginActivity.this, task -> {
                                            if (task.isSuccessful()) {
                                                checkUserRoleAndNavigate(mAuth.getCurrentUser());
                                            } else {
                                                Toast.makeText(LoginActivity.this, "Sai mật khẩu!",
                                                        Toast.LENGTH_SHORT).show();
                                            }
                                        });
                            }
                            break;
                        }
                    }
                }

                if (!userFound) {
                    Toast.makeText(LoginActivity.this,
                            "Không tìm thấy tài khoản với thông tin đăng nhập này!",
                            Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LoginActivity.this,
                        "Lỗi kết nối: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        // Kiểm tra xem đã có user đăng nhập chưa
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            mAuth.signOut(); // Sign out khỏi Firebase
        }
        // Sign out khỏi Google
        mGoogleSignInClient.signOut();
    }
    private void signInWithGoogle() {
        Log.d("GoogleSignIn", "Starting Google sign in");
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        try {
            startActivityForResult(signInIntent, RC_SIGN_IN);
        } catch (Exception e) {
            Log.e("GoogleSignIn", "Error starting sign in", e);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                Toast.makeText(this, "Google sign in failed: " + e.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        checkUserRoleAndNavigate(mAuth.getCurrentUser());
                    } else {
                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private boolean isValidPhoneNumber(String phoneNumber) {
        // Loại bỏ khoảng trắng và dấu gạch ngang
        phoneNumber = phoneNumber.replaceAll("\\s+|-", "");

        // Kiểm tra độ dài và định dạng
        if (phoneNumber.startsWith("+84")) {
            return phoneNumber.length() == 12;
        } else if (phoneNumber.startsWith("0")) {
            return phoneNumber.length() == 10;
        }
        return false;
    }


    //////////////////////  bắt đầu đăng nhập bằng phone
    private void startPhoneAuth() {
        String phoneNumber = edtPhone.getText().toString().trim();
        if (!isValidPhoneNumber(phoneNumber)) {
            edtPhone.setError("Vui lòng nhập số điện thoại hợp lệ");
            return;
        }
        // Kiểm tra số điện thoại có trống không
        if (phoneNumber.isEmpty()) {
            edtPhone.setError("Vui lòng nhập số điện thoại vào ");
            return;
        }
        // Kiểm tra định dạng số điện thoại
        if (!phoneNumber.startsWith("+")) {
            phoneNumber = phoneNumber.startsWith("0")
                    ? "+84" + phoneNumber.substring(1)
                    : "+84" + phoneNumber;
        }
        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(mAuth)
                .setPhoneNumber(phoneNumber)
                .setTimeout(60L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                    @Override
                    public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                        // hideLoading();
                        signInWithPhoneAuthCredential(credential);
                    }
                    @Override
                    public void onVerificationFailed(@NonNull FirebaseException e) {
                        // hideLoading();
                        Toast.makeText(LoginActivity.this, "Verification failed: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                    @Override
                    public void onCodeSent(@NonNull String verId, @NonNull PhoneAuthProvider.ForceResendingToken token) {
                        // hideLoading();
                        verificationId = verId;
                        showOtpDialog();
                    }
                })
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }
    //////////////////////  hiện thị dialog xác minh OTP

    private void showOtpDialog() {
        Dialog dialog = new Dialog(this);
        // Thêm countdown timer
        new CountDownTimer(60000, 1000) {
            public void onTick(long millisUntilFinished) {
                // Update UI showing remaining time
            }
            public void onFinish() {
                dialog.dismiss();
                // Show message OTP expired
            }
        }.start();
        dialog.setContentView(R.layout.dialog_otp);

        EditText edtOtp = dialog.findViewById(R.id.edtOtp);
        Button btnVerify = dialog.findViewById(R.id.btnVerify);

        btnVerify.setOnClickListener(v -> {
            String code = edtOtp.getText().toString();
            PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
            dialog.dismiss();

            // Xác thực với Firebase
            mAuth.signInWithCredential(credential)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            FirebaseUser firebaseUser = task.getResult().getUser();
                            if (firebaseUser != null) {
                                checkPhoneNumberInDatabase(firebaseUser.getPhoneNumber(), firebaseUser);
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "Mã OTP không đúng.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        });
        dialog.show();
    }
    //////////////////// xác thực với Firebase
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser user = task.getResult().getUser();
                        checkIfPhoneUserExists(user);
                    } else {
                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    //////////////////////  kiểm tra số điện thoại có trong database
    private void checkPhoneNumberInDatabase(String phoneNumber, FirebaseUser firebaseUser) {
        DatabaseReference userRef = mDatabase.child("nguoi_dung");
        userRef.orderByChild("so_Dien_Thoai").equalTo(phoneNumber)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (!snapshot.exists()) {
                            // Số điện thoại chưa tồn tại trong database -> hiển thị dialog đăng ký
                            showRegistrationDialog(firebaseUser);
                        } else {
                            // Số điện thoại đã tồn tại -> chuyển đến màn hình tương ứng
                            DataSnapshot userSnapshot = snapshot.getChildren().iterator().next();
                            User existingUser = userSnapshot.getValue(User.class);
                            if (existingUser != null) {
                                navigateBasedOnRole(existingUser);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(LoginActivity.this,
                                "Lỗi kết nối database: " + error.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
    //////////////////////  tạo user mới với số điện thoại
    private void createNewUserWithPhoneNumber(FirebaseUser firebaseUser, String name, String email) {
        DatabaseReference userRef = mDatabase.child("nguoi_dung");

        userRef.orderByChild("email").equalTo(email)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            Toast.makeText(LoginActivity.this,
                                    "Email đã được sử dụng",
                                    Toast.LENGTH_SHORT).show();
                            showRegistrationDialog(firebaseUser); // Hiển thị lại dialog
                            return;
                        }

                        // Tạo ID mới
                        String newUserId = "ND_" + System.currentTimeMillis();

                        // Tạo user mới
                        User newUser = new User();
                        newUser.setId_Nguoi_Dung(newUserId);
                        newUser.setTen(name);
                        newUser.setEmail(email);
                        newUser.setSo_Dien_Thoai(firebaseUser.getPhoneNumber());
                        newUser.setRole("customer");
                        newUser.setNgay_Tao_Tai_Khoan(new SimpleDateFormat("dd/MM/yyyy",
                                Locale.getDefault()).format(new Date()));
                        newUser.setTrang_Thai("active");

                        // Lưu vào database
                        mDatabase.child("nguoi_dung").child(newUserId)
                                .setValue(newUser)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        Toast.makeText(LoginActivity.this,
                                                "Đăng ký thành công",
                                                Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(LoginActivity.this,
                                                HomeUserActivity.class));
                                        finish();
                                    } else {
                                        Toast.makeText(LoginActivity.this,
                                                "Đăng ký thất bại",
                                                Toast.LENGTH_SHORT).show();
                                    }
                                });
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(LoginActivity.this,
                                "Lỗi kết nối database: " + error.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
    ////////////////////  kiểm tra role và điều hướng
    private void navigateBasedOnRole(User user) {
        Intent intent;
        switch (user.getRole()) {
            case "super_admin":
                intent = new Intent(LoginActivity.this, SuperAdminHomeActivity.class);
                break;
            case "sales_manager":
                intent = new Intent(LoginActivity.this, SalesManagerHomeActivity.class);
                break;
            default:
                intent = new Intent(LoginActivity.this, HomeUserActivity.class);
                break;
        }
        startActivity(intent);
        finish();
    }
    private void checkIfPhoneUserExists(FirebaseUser firebaseUser) {
        DatabaseReference userRef = mDatabase.child("nguoi_dung").child(firebaseUser.getUid());
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    showRegistrationDialog(firebaseUser);
                } else {
                    checkUserRoleAndNavigate(firebaseUser);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(LoginActivity.this, "Database error: " + error.getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showRegistrationDialog(FirebaseUser firebaseUser) {
        Dialog dialog = new Dialog(this);
        dialog.setContentView(R.layout.dialog_register_phone);
        dialog.setCancelable(false); // Người dùng không thể đóng dialog bằng cách nhấn bên ngoài

        EditText edtName = dialog.findViewById(R.id.edtName);
        EditText edtEmail = dialog.findViewById(R.id.edtEmail);
        Button btnRegister = dialog.findViewById(R.id.btnRegister);

        btnRegister.setOnClickListener(v -> {
            String name = edtName.getText().toString().trim();
            String email = edtEmail.getText().toString().trim();

            if (name.isEmpty() || email.isEmpty()) {
                Toast.makeText(LoginActivity.this,
                        "Vui lòng nhập đầy đủ thông tin",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            // Kiểm tra định dạng email
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Toast.makeText(LoginActivity.this,
                        "Email không hợp lệ",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            dialog.dismiss();
            createNewUserWithPhoneNumber(firebaseUser, name, email);
        });

        dialog.show();
    }
    private void checkUserRoleAndNavigate(FirebaseUser firebaseUser) {
        if (firebaseUser == null) return;

        // Lấy reference đến node nguoi_dung
        DatabaseReference userRef = mDatabase.child("nguoi_dung");

        // Tìm user dựa trên email
        userRef.orderByChild("email").equalTo(firebaseUser.getEmail())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            // Email đã tồn tại, lấy user đầu tiên tìm được
                            DataSnapshot userSnapshot = snapshot.getChildren().iterator().next();
                            User existingUser = userSnapshot.getValue(User.class);

                            if (existingUser != null) {
                                // Điều hướng dựa trên role
                                Intent intent;
                                switch (existingUser.getRole()) {
                                    case "super_admin":
                                        intent = new Intent(LoginActivity.this, SuperAdminHomeActivity.class);
                                        break;
                                    case "sales_manager":
                                        intent = new Intent(LoginActivity.this, SalesManagerHomeActivity.class);
                                        break;
                                    default:
                                        intent = new Intent(LoginActivity.this, HomeUserActivity.class);
                                        break;
                                }
                                startActivity(intent);
                                finish();
                            }
                        } else {
                            // Email chưa tồn tại, tạo user mới với role customer
                            createNewUser(firebaseUser, firebaseUser.getDisplayName());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(LoginActivity.this, "Database error: " + error.getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void createNewUser(FirebaseUser firebaseUser, String name) {
        // Tham chiếu đến node nguoi_dung
        DatabaseReference userRef = mDatabase.child("nguoi_dung");

        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Set để lưu trữ các số ID người dùng hiện có
                Set<Integer> existingUserIds = new HashSet<>();

                // Duyệt và thu thập số ID của người dùng hiện có
                for (DataSnapshot userSnapshot : snapshot.getChildren()) {
                    User user = userSnapshot.getValue(User.class);
                    if (user != null && user.getId_Nguoi_Dung() != null &&
                            user.getId_Nguoi_Dung().startsWith("ND_")) {
                        try {
                            int idNumber = Integer.parseInt(user.getId_Nguoi_Dung().substring(3));
                            existingUserIds.add(idNumber);
                        } catch (NumberFormatException e) {
                            Log.w("UserCreation", "Invalid user ID format: " + user.getId_Nguoi_Dung());
                        }
                    }
                }

                // Tạo ID mới bằng cách tìm số nhỏ nhất chưa được sử dụng
                int newIdNumber = 1;
                while (existingUserIds.contains(newIdNumber)) {
                    newIdNumber++;
                }

                String currentDate = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
                        .format(new Date());

                // Tạo ID mới với tiền tố "ND_"
                String newUserId = "ND_" + newIdNumber;

                User newUser = new User();
                newUser.setId_Nguoi_Dung(newUserId);
                newUser.setTen(name);
                newUser.setEmail(firebaseUser.getEmail());
                newUser.setSo_Dien_Thoai(firebaseUser.getPhoneNumber());
                // Set role mặc định là customer
                newUser.setRole("customer");
                newUser.setNgay_Tao_Tai_Khoan(currentDate);
                newUser.setTrang_Thai("active");

                // Lưu người dùng mới vào Firebase
                mDatabase.child("nguoi_dung").child(newUserId)
                        .setValue(newUser)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                // User mới luôn chuyển đến HomeUserActivity vì role là customer
                                startActivity(new Intent(LoginActivity.this, HomeUserActivity.class));
                                finish();
                            } else {
                                Toast.makeText(LoginActivity.this, "Failed to create user profile",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.e("UserCreation", "Lỗi khi truy vấn người dùng: " + error.getMessage());
                Toast.makeText(LoginActivity.this, "Không thể tạo ID người dùng",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}