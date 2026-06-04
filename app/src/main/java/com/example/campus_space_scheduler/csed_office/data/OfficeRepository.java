package com.example.campus_space_scheduler.csed_office.data;

import androidx.annotation.NonNull;
import com.example.campus_space_scheduler.booking_user.Booking;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OfficeRepository {
    private final DatabaseReference bookingsRef;
    private final Map<String, ValueEventListener> listeners = new HashMap<>();

    public interface DataCallback<T> {
        void onSuccess(T data);
        void onError(Exception e);
    }

    public OfficeRepository() {
        this.bookingsRef = FirebaseDatabase.getInstance().getReference("bookings");
    }

    public void fetchHistory(DataCallback<List<Booking>> callback) {
        bookingsRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                List<Booking> history = new ArrayList<>();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Booking booking = ds.getValue(Booking.class);
                    if (booking != null) {
                        booking.setBookingId(ds.getKey());
                        // Filter for CSED spaces if necessary, for now fetching all
                        history.add(booking);
                    }
                }
                callback.onSuccess(history);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.toException());
            }
        });
    }

    public void listenToLiveStatus(String spaceName, DataCallback<Booking> callback) {
        ValueEventListener listener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                // Find the current active booking for this space
                // Logic based on current time/date
                // For simplicity, returning the latest snapshot for now
                for (DataSnapshot ds : snapshot.getChildren()) {
                    Booking b = ds.getValue(Booking.class);
                    if (b != null && b.getSpaceName().equalsIgnoreCase(spaceName)) {
                        callback.onSuccess(b);
                        return;
                    }
                }
                callback.onSuccess(null);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.onError(error.toException());
            }
        };
        bookingsRef.addValueEventListener(listener);
        listeners.put(spaceName, listener);
    }

    public void removeListeners() {
        for (Map.Entry<String, ValueEventListener> entry : listeners.entrySet()) {
            bookingsRef.removeEventListener(entry.getValue());
        }
        listeners.clear();
    }
}
