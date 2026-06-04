package com.example.campus_space_scheduler.csed_office;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campus_space_scheduler.R;
import com.example.campus_space_scheduler.booking_user.Booking;
import com.example.campus_space_scheduler.databinding.ActivityOfficeLiveStatusBinding;
import com.example.campus_space_scheduler.databinding.ItemOfficeLiveStatusBinding;

import java.util.ArrayList;
import java.util.List;

public class OfficeLiveStatusActivity extends AppCompatActivity {
    private ActivityOfficeLiveStatusBinding binding;
    private LiveStatusAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOfficeLiveStatusBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupToolbar();
        setupRecyclerView();
        
        // Mock data or ViewModel integration would go here
        // For demonstration, adding mock objects
        List<Booking> mockLiveList = new ArrayList<>();
        mockLiveList.add(new Booking("2026-04-07", "10:30 - 11:30", "CSED Lab 1", "Occupiied"));
        mockLiveList.add(new Booking("2026-04-07", "11:30 - 12:30", "CSED Lab 2", "Available"));
        
        adapter.setList(mockLiveList);
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Live Occupancy");
        }
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        binding.rvLiveStatus.setLayoutManager(new LinearLayoutManager(this));
        adapter = new LiveStatusAdapter();
        binding.rvLiveStatus.setAdapter(adapter);
    }

    private class LiveStatusAdapter extends RecyclerView.Adapter<LiveStatusAdapter.ViewHolder> {
        private List<Booking> list = new ArrayList<>();

        public void setList(List<Booking> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemOfficeLiveStatusBinding itemBinding = ItemOfficeLiveStatusBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false);
            return new ViewHolder(itemBinding);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Booking booking = list.get(position);
            holder.binding.tvRoomName.setText(booking.getPurpose()); // Assuming purpose holds room name for mock
            holder.binding.tvCurrentActivity.setText(booking.getStatus());
            holder.binding.tvTimeLeft.setText(booking.getTimeSlot());

            if ("Available".equalsIgnoreCase(booking.getStatus())) {
                holder.binding.statusIndicator.setBackgroundTintList(
                        getColorStateList(android.R.color.holo_green_light));
            } else {
                holder.binding.statusIndicator.setBackgroundTintList(
                        getColorStateList(android.R.color.holo_red_light));
            }
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            ItemOfficeLiveStatusBinding binding;

            public ViewHolder(ItemOfficeLiveStatusBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }
}
