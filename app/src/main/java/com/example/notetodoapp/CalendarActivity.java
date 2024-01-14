package com.example.notetodoapp;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CalendarActivity extends AppCompatActivity {
    CalendarView calendarView;
    EditText eventEditText;
    ListView eventsListView;

    DatabaseReference databaseRef;
    ValueEventListener eventListener;
    ArrayAdapter<Event> eventsAdapter;
    ArrayList<Event> eventsList;

    SimpleDateFormat dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
    private String previousSelectedDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        databaseRef = FirebaseDatabase.getInstance().getReference("events")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid());

        calendarView = findViewById(R.id.calendarView);
        eventEditText = findViewById(R.id.eventEditText);
        eventsListView = findViewById(R.id.eventsListView);
        previousSelectedDate = dateFormatter.format(new Date());

        eventsList = new ArrayList<>();
        eventsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, eventsList);
        eventsListView.setAdapter(eventsAdapter);

        setupDateChangeListener();
        setupListViewClickListener();
        setupAddEventButtonClickListener();
    }

    private void setupDateChangeListener() {
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            String selectedDate = dateFormatter.format(new Date(year - 1900, month, dayOfMonth));

            if (!previousSelectedDate.equals(selectedDate)) {
                if (previousSelectedDate != null && eventListener != null) {
                    DatabaseReference oldDateRef = databaseRef.child(previousSelectedDate);
                    oldDateRef.removeEventListener(eventListener);
                }

                previousSelectedDate = selectedDate;
                loadEvents(selectedDate);
            }
        });
    }

    private void setupListViewClickListener() {
        eventsListView.setOnItemClickListener((parent, view, position, id) -> {
            Event selectedEvent = eventsAdapter.getItem(position);
            if (selectedEvent != null) {
                showEventEditDialog(previousSelectedDate, selectedEvent.getId(), selectedEvent.getContent());
            }
        });
    }


    private void loadEvents(String selectedDate) {
        if (eventListener != null && !selectedDate.equals(previousSelectedDate)) {
            DatabaseReference oldDateRef = databaseRef.child(previousSelectedDate);
            oldDateRef.removeEventListener(eventListener);
        }

        // Обновляем reference для новой даты
        DatabaseReference dateRef = databaseRef.child(selectedDate);

        eventListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                eventsList.clear();
                for (DataSnapshot eventSnapshot : snapshot.getChildren()) {
                    String eventId = eventSnapshot.getKey();
                    String eventContent = eventSnapshot.getValue(String.class);
                    eventsList.add(new Event(eventId, eventContent));
                }
                eventsAdapter.notifyDataSetChanged();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Utility.showToast(CalendarActivity.this, "Ошибка при загрузке событий");
            }

        };
        dateRef.addValueEventListener(eventListener);
    }

    private void showTimePickerDialog(final String selectedDate, final String eventContent, final String eventId) {
        Calendar currentTime = Calendar.getInstance();
        int hour = currentTime.get(Calendar.HOUR_OF_DAY);
        int minute = currentTime.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                (view, hourOfDay, chosenMinute) -> {
                    if (eventId == null) {
                        saveEvent(selectedDate, eventContent, hourOfDay, chosenMinute);
                    } else {
                        updateEvent(selectedDate, eventId, eventContent, hourOfDay, chosenMinute);
                    }
                }, hour, minute, true);

        timePickerDialog.show();
    }


    private void saveEvent(String selectedDate, String eventContent, int hourOfDay, int minute) {
        DatabaseReference dateRef = databaseRef.child(selectedDate);
        String eventId = dateRef.push().getKey();
        if (eventId != null && !eventId.trim().isEmpty()) {
            Calendar calendar = Calendar.getInstance();
            String[] dateParts = selectedDate.split("-");
            calendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateParts[0]));
            calendar.set(Calendar.MONTH, Integer.parseInt(dateParts[1]) - 1);
            calendar.set(Calendar.YEAR, Integer.parseInt(dateParts[2]));
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);

            dateRef.child(eventId).setValue(eventContent);

            EventScheduler.scheduleEventNotification(CalendarActivity.this, eventContent, calendar.getTimeInMillis());
            eventEditText.setText("");
        }
    }



    private void updateEvent(String selectedDate, String eventId, String eventContent, int hourOfDay, int minute) {
        DatabaseReference dateRef = databaseRef.child(selectedDate);
        Map<String, Object> eventUpdates = new HashMap<>();
        eventUpdates.put(eventId, eventContent);
        dateRef.updateChildren(eventUpdates);
    }

    private void deleteEvent(String selectedDate, String eventId) {
        DatabaseReference dateRef = databaseRef.child(selectedDate);
        dateRef.child(eventId).removeValue();
    }

    private void setupAddEventButtonClickListener() {
        Button addEventButton = findViewById(R.id.addEventButton);
        addEventButton.setOnClickListener(v -> {
            String eventContent = eventEditText.getText().toString();
            if (!eventContent.isEmpty()) {
                showTimePickerDialog(previousSelectedDate, eventContent, null);
            }
        });
    }

    private void showEventEditDialog(final String selectedDate, final String eventId, final String eventContent) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Редактировать событие");
        final View customLayout = getLayoutInflater().inflate(R.layout.dialog_edit_event, null);
        builder.setView(customLayout);
        final EditText editEventEditText = customLayout.findViewById(R.id.editEventEditText);
        editEventEditText.setText(eventContent);
        builder.setPositiveButton("Сохранить", (dialog, which) -> {
            String newEventContent = editEventEditText.getText().toString();
            if (!newEventContent.isEmpty()) {
                showTimePickerDialog(selectedDate, newEventContent, eventId);
            }
        });
        builder.setNegativeButton("Удалить", (dialog, which) -> deleteEvent(selectedDate, eventId));
        builder.setNeutralButton("Отмена", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

}
