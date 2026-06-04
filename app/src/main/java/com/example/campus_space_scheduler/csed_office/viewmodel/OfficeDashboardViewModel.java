package com.example.campus_space_scheduler.csed_office.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.campus_space_scheduler.booking_user.Booking;
import com.example.campus_space_scheduler.csed_office.data.OfficeRepository;
import java.util.List;

public class OfficeDashboardViewModel extends ViewModel {
    private final OfficeRepository repository;
    private final MutableLiveData<UIState<List<Booking>>> historyState = new MutableLiveData<>();

    public static class UIState<T> {
        public enum Status { LOADING, SUCCESS, EMPTY, ERROR }
        public final Status status;
        public final T data;
        public final Exception error;

        private UIState(Status status, T data, Exception error) {
            this.status = status;
            this.data = data;
            this.error = error;
        }

        public static <T> UIState<T> loading() { return new UIState<>(Status.LOADING, null, null); }
        public static <T> UIState<T> success(T data) { return new UIState<>(Status.SUCCESS, data, null); }
        public static <T> UIState<T> empty() { return new UIState<>(Status.EMPTY, null, null); }
        public static <T> UIState<T> error(Exception e) { return new UIState<>(Status.ERROR, null, e); }
    }

    public OfficeDashboardViewModel() {
        this.repository = new OfficeRepository();
    }

    public MutableLiveData<UIState<List<Booking>>> getHistoryState() {
        return historyState;
    }

    public void fetchHistory() {
        historyState.setValue(UIState.loading());
        repository.fetchHistory(new OfficeRepository.DataCallback<List<Booking>>() {
            @Override
            public void onSuccess(List<Booking> data) {
                if (data == null || data.isEmpty()) historyState.setValue(UIState.empty());
                else historyState.setValue(UIState.success(data));
            }

            @Override
            public void onError(Exception e) {
                historyState.setValue(UIState.error(e));
            }
        });
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        repository.removeListeners();
    }
}
