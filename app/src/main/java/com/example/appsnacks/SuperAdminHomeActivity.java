package com.example.appsnacks;

import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import Fragment.*;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
public class SuperAdminHomeActivity extends AppCompatActivity {
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_super_admin_home);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Mặc định hiển thị SuperAdminHomeFragment khi mới vào
        if (savedInstanceState == null) {
            loadFragment(new SuperAdminHomeFragment());
        }

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.nav_home) {
                    loadFragment(new SuperAdminHomeFragment());
                    return true;
                } else if (itemId == R.id.nav_profile) {
                    loadFragment(new ProfileFragment());
                    return true;
                } else if (itemId == R.id.nav_qlaccount) {
                    loadFragment(new AccountManagementFragment());
                    return true;
                } else if (itemId == R.id.nav_product) {
                    loadFragment(new ProductManagementFragment());
                    return true;
                } else if (itemId == R.id.nav_order) {
                    loadFragment(new OrderManagementFragment());
                    return true;
                }
                return false;
            }
        });
    }

    private void loadFragment(Fragment fragment) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.fragmentContainer, fragment);
        fragmentTransaction.commit();
    }

}