package com.alamkanak.weekview.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CalendarView;
import android.widget.Spinner;
import android.widget.TextView;

import com.alamkanak.weekview.WeekViewEvent;
import com.alamkanak.weekview.sample.Event;
import com.alamkanak.weekview.sample.EventManager;
import com.alamkanak.weekview.sample.MainActivity;

import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Monthly view class, displays a monthly calendar,
 * clicking on a day displays the first event of that day
 */

public class MonthlyView extends Activity implements CalendarView.OnDateChangeListener {

    // Widget Variables
    private TextView monthlyEventTitle;
    private TextView monthlyEventStartTime;
    private TextView monthlyEventEndTime;
    private TextView monthlyEventDescription;
    private CalendarView monthlyCalendar;
    private Spinner monthlySpinner;

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly_view);

        // Assign references to widgets
        monthlyEventTitle = (TextView) findViewById(R.id.monthlyEventTitle);
        monthlyEventStartTime = (TextView) findViewById(R.id.monthlyEventStartTime);
        monthlyEventEndTime = (TextView) findViewById(R.id.monthlyEventEndTime);
        monthlyEventDescription = (TextView) findViewById(R.id.monthlyEventDescription);
        monthlyCalendar = (CalendarView) findViewById(R.id.monthlyCalendar);
        monthlySpinner = (Spinner) findViewById(R.id.monthlySpinner);

        // Drop down spinner
        ArrayAdapter monthlyAdapter = ArrayAdapter.createFromResource(this, R.array.dropDownList, android.R.layout.simple_spinner_item);
        monthlyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        monthlySpinner.setAdapter(monthlyAdapter);
        monthlySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) {
                    Intent spinnerIntent;

                    //AgendaView
                    if (position == 1) {
                        spinnerIntent = new Intent(MonthlyView.this, MainActivity.class);
                        startActivity(spinnerIntent);
                    }
                    //DailyView
                    if (position == 2) {
                        spinnerIntent = new Intent(MonthlyView.this, MainActivity.class);
                        startActivity(spinnerIntent);
                    }
                    //WeeklyView
                    if (position == 3) {
                        spinnerIntent = new Intent(MonthlyView.this, MonthlyView.class);
                        startActivity(spinnerIntent);
                    }
                    //MonthlyView
                    if (position == 4) {
                        spinnerIntent = new Intent(MonthlyView.this, MainActivity.class);
                        startActivity(spinnerIntent);
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        // Setting listener
        monthlyCalendar.setOnDateChangeListener(this);

        // Get current time and start static event manager
        Date today = new Date(); // Current time
        Date midnight = new Date(); // Current day's end
        EventManager.noFileStart(); // Start event manager

        // Set end of day to proper time
        midnight.setHours(23);
        midnight.setMinutes(59);
        midnight.setSeconds(59);

        // Retrieve list of today's events
        transferList();
        LinkedList<Event> currentList = EventManager.getBetweenTimes(today, midnight);

        if (currentList.isEmpty()) {
            // No events today, set empty
            monthlyEventTitle.setText("");
            monthlyEventStartTime.setText("");
            monthlyEventEndTime.setText("");
            monthlyEventDescription.setText("");
        }
        else {
            // Get first event and set data
            Event first = currentList.getFirst();
            monthlyEventTitle.setText(first.getTitle());
            monthlyEventStartTime.setText("Start: " + first.getStartDay() + first.getStartMonth() + first.getStartYear()
                    + ", " + first.getStartHour() + ":" + first.getStartMinute());
            monthlyEventEndTime.setText("End: " + first.getEndDay() + first.getEndMonth() + first.getEndYear()
                    + ", " + first.getEndHour() + ":" + first.getEndMinute());
            monthlyEventDescription.setText(first.getDescription());
        }
    }

    // Method to populate event manager using events arraylist
    private void transferList() {
        Iterator<WeekViewEvent> transferIterator = MainActivity.getmEventList().iterator();
        WeekViewEvent transferEvent;
        Event receivedEvent;

        // Transfer from WeekViewEvent list to EventManager
        while(transferIterator.hasNext()) {
            transferEvent = transferIterator.next();
            receivedEvent = new Event();

            receivedEvent.setStartTime(transferEvent.getStartTime().getTime());
            receivedEvent.setEndTime(transferEvent.getEndTime().getTime());
            receivedEvent.setTitle(transferEvent.getName());
            receivedEvent.setDescription(transferEvent.getLocation());

            EventManager.addEvent(receivedEvent);
        }
    }

    @Override
    public void onDestroy() {
        EventManager.noFileEnd(); // End static event manager

        super.onDestroy();
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onSelectedDayChange(CalendarView view, int year, int month, int dayOfMonth){
        Date selectedDay = new Date(); // Chosen day
        Date selectedMidnight; // End of chosen day

        // Set selected day as date
        selectedDay.setYear(year - 1900);
        selectedDay.setMonth(month);
        selectedDay.setDate(dayOfMonth);
        selectedDay.setHours(0);
        selectedDay.setMinutes(0);
        selectedDay.setSeconds(0);

        // Set selected end of day
        selectedMidnight = new Date(selectedDay.getTime());
        selectedMidnight.setHours(23);
        selectedMidnight.setMinutes(59);
        selectedMidnight.setSeconds(59);

        // Retrieve list of selected day's events
        LinkedList<Event> selectedList = EventManager.getBetweenTimes(selectedDay, selectedMidnight);

        if (selectedList.isEmpty()) {
            // No events on selected day, set empty
            monthlyEventTitle.setText("");
            monthlyEventStartTime.setText("");
            monthlyEventEndTime.setText("");
            monthlyEventDescription.setText("");
        }
        else {
            // Get first event and set data
            Event first = selectedList.getFirst();
            monthlyEventTitle.setText(first.getTitle());
            monthlyEventStartTime.setText("Start: " + first.getStartDay() + first.getStartMonth() + first.getStartYear()
                    + ", " + first.getStartHour() + ":" + first.getStartMinute());
            monthlyEventEndTime.setText("End: " + first.getEndDay() + first.getEndMonth() + first.getEndYear()
                    + ", " + first.getEndHour() + ":" + first.getEndMinute());
            monthlyEventDescription.setText(first.getDescription());
        }
    }
}