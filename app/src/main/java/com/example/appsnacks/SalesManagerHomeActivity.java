package com.example.appsnacks;

import android.os.Bundle;
import android.view.MenuItem;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import Fragment.OrderManagementFragment;
import Fragment.ProductManagementFragment;
import Fragment.ProfileFragment;
import Fragment.StaffHomeFragment;

public class SalesManagerHomeActivity extends AppCompatActivity {

    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sales_manager_home);

        bottomNavigationView = findViewById(R.id.bottomNavigationView);

        // Mặc định hiển thị SalesManagerHomeFragment khi mới vào
        if (savedInstanceState == null) {
            loadFragment(new StaffHomeFragment());
        }

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int itemId = item.getItemId();

                if (itemId == R.id.nav_home) {
                    loadFragment(new StaffHomeFragment());
                    return true;
                } else if (itemId == R.id.nav_profile) {
                    loadFragment(new ProfileFragment());
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