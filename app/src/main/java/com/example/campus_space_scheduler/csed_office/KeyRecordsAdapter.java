package com.example.campus_space_scheduler.csed_office;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.campus_space_scheduler.R;
import com.google.android.material.button.MaterialButton;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class KeyRecordsAdapter extends RecyclerView.Adapter<KeyRecordsAdapter.ViewHolder> {

    public interface OnRecordActionListener {
        void onMarkAsReturned(KeyRecordModel model);
    }

    private final List<KeyRecordModel> recordList;
    private final OnRecordActionListener actionListener;
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("MMM dd, hh:mm a", Locale.getDefault());

    public KeyRecordsAdapter(List<KeyRecordModel> recordList, OnRecordActionListener actionListener) {
        this.recordList = recordList;
        this.actionListener = actionListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_key_record, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        KeyRecordModel model = recordList.get(position);

        holder.tvSpaceName.setText(model.getSpaceName());
        holder.tvFacultyName.setText("Faculty: " + model.getFacultyName());
        
        if (model.getContactNumber() != null && !model.getContactNumber().trim().isEmpty()) {
            holder.tvContactNumber.setText("Contact: " + model.getContactNumber());
            holder.tvContactNumber.setVisibility(View.VISIBLE);
        } else {
            holder.tvContactNumber.setVisibility(View.GONE);
        }

        holder.tvHandoutTime.setText("Handed Out: " + dateFormat.format(new Date(model.getHandoutTime())));

        if ("RETURNED".equals(model.getStatus())) {
            holder.tvStatus.setText("RETURNED");
            holder.tvStatus.setBackgroundResource(R.drawable.bg_badge_primary);
            holder.tvStatus.getBackground().setTint(Color.parseColor("#4CAF50")); // Green
            
            holder.tvReturnTime.setText("Returned: " + dateFormat.format(new Date(model.getReturnTime())));
            holder.tvReturnTime.setVisibility(View.VISIBLE);
            holder.btnMarkReturned.setVisibility(View.GONE);
        } else {
            holder.tvStatus.setText("HANDED OUT");
            holder.tvStatus.setBackgroundResource(R.drawable.bg_badge_primary);
            holder.tvStatus.getBackground().setTint(Color.parseColor("#FF9800")); // Orange
            
            holder.tvReturnTime.setVisibility(View.GONE);
            holder.btnMarkReturned.setVisibility(View.VISIBLE);
        }

        holder.btnMarkReturned.setOnClickListener(v -> {
            if (actionListener != null) {
                actionListener.onMarkAsReturned(model);
            }
        });
    }

    @Override
    public int getItemCount() {
        return recordList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tvSpaceName;
        TextView tvStatus;
        TextView tvFacultyName;
        TextView tvContactNumber;
        TextView tvHandoutTime;
        TextView tvReturnTime;
        MaterialButton btnMarkReturned;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvSpaceName = itemView.findViewById(R.id.tvSpaceName);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvFacultyName = itemView.findViewById(R.id.tvFacultyName);
            tvContactNumber = itemView.findViewById(R.id.tvContactNumber);
            tvHandoutTime = itemView.findViewById(R.id.tvHandoutTime);
            tvReturnTime = itemView.findViewById(R.id.tvReturnTime);
            btnMarkReturned = itemView.findViewById(R.id.btnMarkReturned);
        }
    }
}
