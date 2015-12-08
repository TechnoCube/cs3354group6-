package com.alamkanak.weekview.sample;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
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
 * Monthly view class.
 * Displays a monthly calendar, clicking on a day displays the first event of that day.
 */
public class MonthlyView extends Activity implements CalendarView.OnDateChangeListener, OnClickListener {

    // Widget Variables
    private TextView monthlyEventTitle;
    private TextView monthlyEventStartTime;
    private TextView monthlyEventEndTime;
    private TextView monthlyEventDescription;
    private Button monthlyBackButton;
    private CalendarView monthlyCalendar;
    private Spinner monthlySpinner;

    /**
     * Overridden onCreate method.
     * Creates monthly calendar view.
     * @param savedInstanceState: Bundle passed by android
     * @return void
     */
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
        monthlyBackButton = (Button) findViewById(R.id.monthlyBackButton);
//        monthlySpinner = (Spinner) findViewById(R.id.monthlySpinner);

//        // Drop down spinner
//        ArrayAdapter monthlyAdapter = ArrayAdapter.createFromResource(this, R.array.dropDownList, android.R.layout.simple_spinner_item);
//        monthlyAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//        monthlySpinner.setAdapter(monthlyAdapter);
//        monthlySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//                if (position > 0) {
//                    Intent spinnerIntent;
//
//                    //AgendaView
//                    if (position == 1) {
//                        spinnerIntent = new Intent(MonthlyView.this, MainActivity.class);
//                        startActivity(spinnerIntent);
//                    }
//                    //DailyView
//                    if (position == 2) {
//                        spinnerIntent = new Intent(MonthlyView.this, MainActivity.class);
//                        startActivity(spinnerIntent);
//                    }
//                    //WeeklyView
//                    if (position == 3) {
//                        spinnerIntent = new Intent(MonthlyView.this, MainActivity.class);
//                        startActivity(spinnerIntent);
//                    }
//                    //MonthlyView
//                    if (position == 4) {
//                        spinnerIntent = new Intent(MonthlyView.this, MonthlyView.class);
//                        startActivity(spinnerIntent);
//                    }
//                }
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//            }
//        });

        // Setting listeners
        monthlyBackButton.setOnClickListener(this);
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

        if (currentList == null || currentList.isEmpty()) {
            // No events today, set empty
            monthlyEventTitle.setText(" ");
            monthlyEventStartTime.setText(" ");
            monthlyEventEndTime.setText(" ");
            monthlyEventDescription.setText(" ");

            // Set blank background color
            monthlyEventTitle.setBackgroundColor(getResources().getColor(R.color.blank));
            monthlyEventStartTime.setBackgroundColor(getResources().getColor(R.color.blank));
            monthlyEventEndTime.setBackgroundColor(getResources().getColor(R.color.blank));
            monthlyEventDescription.setBackgroundColor(getResources().getColor(R.color.blank));
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

            // Set background color based on category
            monthlyEventTitle.setBackgroundColor(first.getColor());
            monthlyEventStartTime.setBackgroundColor(first.getColor());
            monthlyEventEndTime.setBackgroundColor(first.getColor());
            monthlyEventDescription.setBackgroundColor(first.getColor());
        }
    }

    /**
     * Overridden onClick method.
     * Responds to a click on button.
     * @param back: View object, action is only taken if view is back button.
     * @return void
     */
    @Override
    public void onClick(View back) {
        if (back.getId() == R.id.monthlyBackButton)
            startActivity(new Intent(this, MainActivity.class));
    }

    /**
     * Populates event manager.
     * Transfers events from a static array list into the event manager.
     * @return void
     */
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
            receivedEvent.setColor(transferEvent.getColor());

            EventManager.addEvent(receivedEvent);
        }
    }

    /**
     * Overridden onDestroy method.
     * Closes event manager on destruction.
     * @return void
     */
    @Override
    public void onDestroy() {
        EventManager.noFileEnd(); // End static event manager

        super.onDestroy();
    }

    /**
     * Overridden onSelectedDayChange method.
     * Updates text widgets based on calendar selection.
     * @param view: CalendarView object
     * @param year: Selected calendar year
     * @param month: Selected calendar month
     * @param dayOfMonth: Selected calendar day
     * @return void
     */
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

        if (selectedList == null || selectedList.isEmpty()) {
            // No events on selected day, set empty
            monthlyEventTitle.setText(" ");
            monthlyEventStartTime.setText(" ");
            monthlyEventEndTime.setText(" ");
            monthlyEventDescription.setText(" ");

            // Set blank background color
            monthlyEventTitle.setBackgroundColor(getResources().getColor(R.color.blank));
            monthlyEventStartTime.setBackgroundColor(getResources().getColor(R.color.blank));
            monthlyEventEndTime.setBackgroundColor(getResources().getColor(R.color.blank));
            monthlyEventDescription.setBackgroundColor(getResources().getColor(R.color.blank));
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

            // Set background color based on category
            monthlyEventTitle.setBackgroundColor(first.getColor());
            monthlyEventStartTime.setBackgroundColor(first.getColor());
            monthlyEventEndTime.setBackgroundColor(first.getColor());
            monthlyEventDescription.setBackgroundColor(first.getColor());
        }
    }
}