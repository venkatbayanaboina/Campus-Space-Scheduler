package com.example.campus_space_scheduler.csed_office;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.example.campus_space_scheduler.LoginActivity;
import com.example.campus_space_scheduler.DetailedViewScheduleActivity;
import com.example.campus_space_scheduler.databinding.PActivityCsedOfficeStaffBinding;
import com.example.campus_space_scheduler.csed_office.viewmodel.OfficeDashboardViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class CsedOfficeStaffActivity extends AppCompatActivity {
    private PActivityCsedOfficeStaffBinding binding;
    private OfficeDashboardViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // 1. Security Check: verify role before layout inflation for security
        verifyCsedRole();

        binding = PActivityCsedOfficeStaffBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(OfficeDashboardViewModel.class);

        setupToolbar();
        setupClickListeners();
    }

    private void verifyCsedRole() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            redirectToLogin();
            return;
        }

        FirebaseDatabase.getInstance().getReference("users").child(user.getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        String role = snapshot.child("role").getValue(String.class);
                        if (!"CSED Staff".equals(role)) {
                            Toast.makeText(CsedOfficeStaffActivity.this, "Access Denied: Missing CSED Staff Role", Toast.LENGTH_LONG).show();
                            redirectToLogin();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        redirectToLogin();
                    }
                });
    }

    private void redirectToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("CSED Office Hub");
        }
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupClickListeners() {
        binding.cardLiveStatus.setOnClickListener(v -> {
            Intent intent = new Intent(this, OfficeLiveStatusActivity.class);
            startActivity(intent);
        });

        binding.cardSchedule.setOnClickListener(v -> {
            Intent intent = new Intent(this, DetailedViewScheduleActivity.class);
            startActivity(intent);
        });

        binding.cardKeyRecords.setOnClickListener(v -> {
            Intent intent = new Intent(this, KeyRecordsActivity.class);
            startActivity(intent);
        });

        binding.cardHistory.setOnClickListener(v -> {
            Intent intent = new Intent(this, OfficeHistoryActivity.class);
            startActivity(intent);
        });
    }
}