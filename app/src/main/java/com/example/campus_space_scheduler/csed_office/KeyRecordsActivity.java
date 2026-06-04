package com.example.campus_space_scheduler.csed_office;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campus_space_scheduler.R;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class KeyRecordsActivity extends AppCompatActivity {

    private RecyclerView rvKeyRecords;
    private View loadingLayout;
    private View emptyLayout;
    private View errorLayout;
    private FloatingActionButton fabAddRecord;

    private DatabaseReference dbRef;
    private List<KeyRecordModel> recordList = new ArrayList<>();
    private KeyRecordsAdapter adapter;

    // For caching space list
    private List<String> spaceIds = new ArrayList<>();
    private List<String> spaceNames = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_key_records);

        setupToolbar();
        initViews();
        setupRecyclerView();

        dbRef = FirebaseDatabase.getInstance().getReference("key_records");
        fetchSpaces();
        loadKeyRecords();

        fabAddRecord.setOnClickListener(v -> showAddRecordDialog());
    }

    private void setupToolbar() {
        MaterialToolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Key Handover Log");
        }
        toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void initViews() {
        rvKeyRecords = findViewById(R.id.rvKeyRecords);
        loadingLayout = findViewById(R.id.loadingLayout);
        emptyLayout = findViewById(R.id.emptyLayout);
        errorLayout = findViewById(R.id.errorLayout);
        fabAddRecord = findViewById(R.id.fabAddRecord);
    }

    private void setupRecyclerView() {
        rvKeyRecords.setLayoutManager(new LinearLayoutManager(this));
        adapter = new KeyRecordsAdapter(recordList, model -> markKeyAsReturned(model));
        rvKeyRecords.setAdapter(adapter);
    }

    private void showState(View stateView) {
        loadingLayout.setVisibility(View.GONE);
        emptyLayout.setVisibility(View.GONE);
        errorLayout.setVisibility(View.GONE);
        rvKeyRecords.setVisibility(View.GONE);

        if (stateView != null) {
            stateView.setVisibility(View.VISIBLE);
        } else {
            rvKeyRecords.setVisibility(View.VISIBLE);
        }
    }

    private void fetchSpaces() {
        FirebaseDatabase.getInstance().getReference("spaces")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        spaceIds.clear();
                        spaceNames.clear();
                        for (DataSnapshot ds : snapshot.getChildren()) {
                            String id = ds.getKey();
                            String name = ds.child("roomName").getValue(String.class);
                            if (id != null && name != null) {
                                spaceIds.add(id);
                                spaceNames.add(name);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        // Silent fail
                    }
                });
    }

    private void loadKeyRecords() {
        showState(loadingLayout);
        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                recordList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    KeyRecordModel model = ds.getValue(KeyRecordModel.class);
                    if (model != null) {
                        model.setId(ds.getKey());
                        recordList.add(model);
                    }
                }
                
                // Sort to show Handed Out records at the top, then newest handout times
                Collections.sort(recordList, (r1, r2) -> {
                    if ("HANDED OUT".equals(r1.getStatus()) && !"HANDED OUT".equals(r2.getStatus())) {
                        return -1;
                    }
                    if (!"HANDED OUT".equals(r1.getStatus()) && "HANDED OUT".equals(r2.getStatus())) {
                        return 1;
                    }
                    return Long.compare(r2.getHandoutTime(), r1.getHandoutTime());
                });

                adapter.notifyDataSetChanged();

                if (recordList.isEmpty()) {
                    showState(emptyLayout);
                } else {
                    showState(null); // Shows recycler view
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                showState(errorLayout);
            }
        });
    }

    private void showAddRecordDialog() {
        if (spaceNames.isEmpty()) {
            Toast.makeText(this, "Loading spaces... please try again in a moment.", Toast.LENGTH_SHORT).show();
            fetchSpaces();
            return;
        }

        View dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_key_record, null);
        Spinner spinnerSpaces = dialogView.findViewById(R.id.spinnerSpaces);
        EditText etFacultyName = dialogView.findViewById(R.id.etFacultyName);
        EditText etContactNumber = dialogView.findViewById(R.id.etContactNumber);

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_dropdown_item,
                spaceNames
        );
        spinnerSpaces.setAdapter(spinnerAdapter);

        new MaterialAlertDialogBuilder(this)
                .setView(dialogView)
                .setPositiveButton("Save Log", (dialog, which) -> {
                    String facultyName = etFacultyName.getText().toString().trim();
                    String contactNumber = etContactNumber.getText().toString().trim();
                    int selectedPosition = spinnerSpaces.getSelectedItemPosition();

                    if (facultyName.isEmpty()) {
                        Toast.makeText(KeyRecordsActivity.this, "Faculty Name cannot be empty!", Toast.LENGTH_LONG).show();
                        return;
                    }

                    if (selectedPosition < 0 || selectedPosition >= spaceIds.size()) {
                        Toast.makeText(KeyRecordsActivity.this, "Please select a valid classroom/space.", Toast.LENGTH_LONG).show();
                        return;
                    }

                    String spaceId = spaceIds.get(selectedPosition);
                    String spaceName = spaceNames.get(selectedPosition);

                    saveKeyHandoverLog(spaceId, spaceName, facultyName, contactNumber);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void saveKeyHandoverLog(String spaceId, String spaceName, String facultyName, String contactNumber) {
        String key = dbRef.push().getKey();
        if (key == null) return;

        KeyRecordModel model = new KeyRecordModel(
                key,
                spaceId,
                spaceName,
                facultyName,
                contactNumber,
                "HANDED OUT",
                System.currentTimeMillis(),
                0
        );

        dbRef.child(key).setValue(model)
                .addOnSuccessListener(aVoid -> Toast.makeText(KeyRecordsActivity.this, "Key handover logged successfully!", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(KeyRecordsActivity.this, "Failed to save key log. Try again.", Toast.LENGTH_LONG).show());
    }

    private void markKeyAsReturned(KeyRecordModel model) {
        if (model.getId() == null) return;

        Map<String, Object> updates = new HashMap<>();
        updates.put("status", "RETURNED");
        updates.put("returnTime", System.currentTimeMillis());

        dbRef.child(model.getId()).updateChildren(updates)
                .addOnSuccessListener(aVoid -> Toast.makeText(KeyRecordsActivity.this, "Key marked as returned.", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> Toast.makeText(KeyRecordsActivity.this, "Failed to update record. Try again.", Toast.LENGTH_LONG).show());
    }
}
