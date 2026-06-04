package com.example.campus_space_scheduler.csed_office;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campus_space_scheduler.booking_user.Booking;
import com.example.campus_space_scheduler.csed_office.viewmodel.OfficeDashboardViewModel;
import com.example.campus_space_scheduler.databinding.ActivityOfficeHistoryBinding;
import com.example.campus_space_scheduler.databinding.ItemOfficeHistoryBinding;

import java.util.ArrayList;
import java.util.List;

public class OfficeHistoryActivity extends AppCompatActivity {
    private ActivityOfficeHistoryBinding binding;
    private OfficeDashboardViewModel viewModel;
    private HistoryAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityOfficeHistoryBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        viewModel = new ViewModelProvider(this).get(OfficeDashboardViewModel.class);

        setupToolbar();
        setupRecyclerView();
        observeViewModel();

        viewModel.fetchHistory();
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Booking History");
        }
        binding.toolbar.setNavigationOnClickListener(v -> finish());
    }

    private void setupRecyclerView() {
        binding.rvHistory.setLayoutManager(new LinearLayoutManager(this));
        adapter = new HistoryAdapter();
        binding.rvHistory.setAdapter(adapter);
    }

    private void observeViewModel() {
        viewModel.getHistoryState().observe(this, state -> {
            switch (state.status) {
                case LOADING:
                    showLoading();
                    break;
                case SUCCESS:
                    showSuccess(state.data);
                    break;
                case EMPTY:
                    showEmpty();
                    break;
                case ERROR:
                    showError(state.error.getMessage());
                    break;
            }
        });
    }

    private void showLoading() {
        binding.loadingLayout.getRoot().setVisibility(View.VISIBLE);
        binding.emptyLayout.getRoot().setVisibility(View.GONE);
        binding.errorLayout.getRoot().setVisibility(View.GONE);
        binding.rvHistory.setVisibility(View.GONE);
    }

    private void showSuccess(List<Booking> data) {
        binding.loadingLayout.getRoot().setVisibility(View.GONE);
        binding.rvHistory.setVisibility(View.VISIBLE);
        adapter.setList(data);
    }

    private void showEmpty() {
        binding.loadingLayout.getRoot().setVisibility(View.GONE);
        binding.emptyLayout.getRoot().setVisibility(View.VISIBLE);
    }

    private void showError(String message) {
        binding.loadingLayout.getRoot().setVisibility(View.GONE);
        binding.errorLayout.getRoot().setVisibility(View.VISIBLE);
        binding.errorLayout.errorText.setText(message != null ? message : "Error fetching history");
        binding.errorLayout.btnRetry.setOnClickListener(v -> viewModel.fetchHistory());
    }

    private static class HistoryAdapter extends RecyclerView.Adapter<HistoryAdapter.ViewHolder> {
        private List<Booking> list = new ArrayList<>();

        public void setList(List<Booking> list) {
            this.list = list;
            notifyDataSetChanged();
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            ItemOfficeHistoryBinding itemBinding = ItemOfficeHistoryBinding.inflate(
                    LayoutInflater.from(parent.getContext()), parent, false);
            return new ViewHolder(itemBinding);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
            Booking booking = list.get(position);
            holder.binding.tvHistoryDate.setText(booking.getDate());
            holder.binding.tvHistorySpace.setText(booking.getSpaceName());
            holder.binding.tvHistoryPurpose.setText(booking.getPurpose());
            holder.binding.tvHistoryTime.setText("Slot: " + booking.getTimeSlot());
            holder.binding.tvHistoryStatus.setText(booking.getStatus().toUpperCase());
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        static class ViewHolder extends RecyclerView.ViewHolder {
            ItemOfficeHistoryBinding binding;

            public ViewHolder(ItemOfficeHistoryBinding binding) {
                super(binding.getRoot());
                this.binding = binding;
            }
        }
    }
}
