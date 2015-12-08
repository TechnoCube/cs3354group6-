/***********************************************************************
 * our group created this class (info) and the following methods in main
 * getEventClickFlag()/setEventClickFlag()/getEname()
 * getElocation()/getObjectEvent()/addEventToList(WeekViewEvent event)/
 * removeEventFromList(WeekViewEvent event)
 * our group created the XML layout info and all interaction between info and main class
 * the info class creates the user input form
 * it calls the date and time pickers captures the user input
 * and creates an WeekViewEvent and passes it to an array list
 * List<WeekViewEvent> mEventList = new ArrayList<>() in the MainActivity
 * MainActivy calls the methods in the library to display the calendar
 * ************************************************************************
 * The display is handled by alamkanak weekview library
 * Created by Raquib-ul-Alam Kanak on 7/21/2014.
 * Website: http://alamkanak.github.io/
 *************************************************************************/

package com.alamkanak.weekview.sample;

import android.app.AlertDialog;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.ActionBarActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;


import com.alamkanak.weekview.WeekViewEvent;

import java.util.Calendar;


public class Info extends ActionBarActivity implements AdapterView.OnItemSelectedListener {
    public int myYear;
    public int startMonth;
    public int startDay;
    public int startHour;
    public int startMinute;
    public int endHour;
    public int endMinute;
    public int flag;
    public int amPm;
    public String evenName;
    public String evenLocation;
    public int color;
    public int repeatW = 0;
    public int repeatM = 0;
    public int repeatY = 0;
    public long reminderms;
    public NotificationManager notificationManager;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        //this is to edit an event not to add
        if(MainActivity.getEventClickFlag() != 0){
            EditText eD = (EditText) findViewById(R.id.editText);
            eD.setText(MainActivity.getEname());
            EditText eD1 = (EditText) findViewById(R.id.editText2);
            eD1.setText(MainActivity.getElocation());
            MainActivity.removeEventFromList(MainActivity.getObjectEvent());
            MainActivity.setEventClickFlag();


        }


    }

    public class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), AlertDialog.THEME_HOLO_LIGHT,this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            myYear = year;
            startMonth = month;
            startDay = day;
            String t = " year : " + year + " month : " + month + " day : " + day ;
            System.out.println(t);
        }
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getSupportFragmentManager(), "datePicker");
    }

    public class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR);
            int minute = c.get(Calendar.MINUTE);
            amPm = c.get(Calendar.AM_PM);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), AlertDialog.THEME_HOLO_LIGHT, this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));





        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            if(flag == 0 ) {
                startHour = hourOfDay;
                startMinute = minute;
            }else{

                endHour = hourOfDay;
                endMinute = minute;
            }
            String t = "hour of day :" + hourOfDay + " minute : " + minute + " flag : " + flag;
            System.out.println(t);
        }
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
        flag = 0;
    }

    public void showTimePickerDialog1(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getSupportFragmentManager(), "timePicker");
        flag = 1;
    }

    public void red(View view){
        color = 1;

    }

    public void yellow(View view){
        color = 2;

    }

    public void green(View view){
        color = 3;

    }

    public void blue(View view){
        color = 4;

    }



    public void backToMain(View view) {



        EditText edit1 = (EditText)findViewById(R.id.editText);
        evenName = edit1.getText().toString();
        EditText edit2 = (EditText) findViewById(R.id.editText2);
        evenLocation = edit2.getText().toString();

        if(repeatW == 1){

            Calendar check = Calendar.getInstance();
            check.set(Calendar.DAY_OF_MONTH, startDay);
            check.set(Calendar.MONTH, startMonth);
            check.set(Calendar.YEAR, myYear);
            int d = check.get(Calendar.DAY_OF_MONTH);

            for(int y =2015; y < 2017; y++ ) {
                for (int m = 0; m < 12; m++){
                    for(int w = 0; w < 5; w++){


                        Calendar startTimeW = Calendar.getInstance();
                        startTimeW.set(Calendar.HOUR_OF_DAY, startHour);
                        startTimeW.set(Calendar.MINUTE, startMinute);
                        startTimeW.set(Calendar.DAY_OF_WEEK, d);
                        startTimeW.set(Calendar.WEEK_OF_MONTH, w);
                        startTimeW.set(Calendar.MONTH, m);
                        startTimeW.set(Calendar.YEAR, y);

                        Calendar endTimeW = Calendar.getInstance();
                        endTimeW.set(Calendar.DAY_OF_MONTH, startDay);
                        endTimeW.set(Calendar.MONTH, m);
                        endTimeW.set(Calendar.YEAR, y);
                        endTimeW.set(Calendar.HOUR_OF_DAY, endHour);
                        endTimeW.set(Calendar.MINUTE, endMinute);
                        endTimeW.set(Calendar.DAY_OF_WEEK, d);
                        endTimeW.set(Calendar.WEEK_OF_MONTH, w);


                        WeekViewEvent eventM = new WeekViewEvent(1, evenName, evenLocation, startTimeW, endTimeW);
                        if (color == 1) {
                            eventM.setColor(getResources().getColor(R.color.event_color_02));
                        }
                        if (color == 2) {
                            eventM.setColor(getResources().getColor(R.color.event_color_04));
                        }
                        if (color == 3) {
                            eventM.setColor(getResources().getColor(R.color.event_color_03));
                        }
                        if (color == 4) {
                            eventM.setColor(getResources().getColor(R.color.event_color_01));
                        }

                        repeatM = 0;
                        MainActivity.addEventToList(eventM);

                    }

                }
            }
            MainActivity.main.finish();
            startActivity(new Intent(this, MainActivity.class));
            finish();

        }





        if(repeatM == 1){
            for(int y =2015; y < 2017; y++ ) {

                for (int m = 0; m < 12; m++) {

                    if( m == startMonth){

                    }
                    else {

                        Calendar startTimeM = Calendar.getInstance();
                        startTimeM.set(Calendar.HOUR_OF_DAY, startHour);
                        startTimeM.set(Calendar.DAY_OF_MONTH, startDay);
                        startTimeM.set(Calendar.MINUTE, startMinute);
                        startTimeM.set(Calendar.MONTH, m);
                        startTimeM.set(Calendar.YEAR, y);

                        Calendar endTimeM = Calendar.getInstance();
                        endTimeM.set(Calendar.DAY_OF_MONTH, startDay);
                        endTimeM.set(Calendar.MONTH, m);
                        endTimeM.set(Calendar.YEAR, y);
                        endTimeM.set(Calendar.HOUR_OF_DAY, endHour);
                        endTimeM.set(Calendar.MINUTE, endMinute);


                        WeekViewEvent eventM = new WeekViewEvent(1, evenName, evenLocation, startTimeM, endTimeM);
                        if(color == 1)
                        {
                            eventM.setColor(getResources().getColor(R.color.event_color_02));
                        }
                        if(color ==2){
                            eventM.setColor(getResources().getColor(R.color.event_color_04));
                        }
                        if(color == 3)
                        {
                            eventM.setColor(getResources().getColor(R.color.event_color_03));
                        }
                        if(color ==4){
                            eventM.setColor(getResources().getColor(R.color.event_color_01));
                        }

                        repeatM = 0;
                        MainActivity.addEventToList(eventM);
                    }

                }
            }
            MainActivity.main.finish();
            startActivity(new Intent(this, MainActivity.class));
            finish();

        }

        if(repeatY == 1){
            for(int y =2015; y < 2018; y++ ) {

                    if( y == myYear){

                    }
                    else {

                        Calendar startTimeM = Calendar.getInstance();
                        startTimeM.set(Calendar.HOUR_OF_DAY, startHour);
                        startTimeM.set(Calendar.DAY_OF_MONTH, startDay);
                        startTimeM.set(Calendar.MINUTE, startMinute);
                        startTimeM.set(Calendar.MONTH, startMonth);
                        startTimeM.set(Calendar.YEAR, y);

                        Calendar endTimeM = Calendar.getInstance();
                        endTimeM.set(Calendar.DAY_OF_MONTH, startDay);
                        endTimeM.set(Calendar.MONTH, startMonth);
                        endTimeM.set(Calendar.YEAR, y);
                        endTimeM.set(Calendar.HOUR_OF_DAY, endHour);
                        endTimeM.set(Calendar.MINUTE, endMinute);


                        WeekViewEvent eventM = new WeekViewEvent(1, evenName, evenLocation, startTimeM, endTimeM);
                        if(color == 1)
                        {
                            eventM.setColor(getResources().getColor(R.color.event_color_02));
                        }
                        if(color ==2){
                            eventM.setColor(getResources().getColor(R.color.event_color_04));
                        }
                        if(color == 3)
                        {
                            eventM.setColor(getResources().getColor(R.color.event_color_03));
                        }
                        if(color ==4){
                            eventM.setColor(getResources().getColor(R.color.event_color_01));
                        }

                        repeatM = 0;
                        MainActivity.addEventToList(eventM);
                    }

                }

            MainActivity.main.finish();
            startActivity(new Intent(this, MainActivity.class));
            finish();

        }

        Calendar startTime = Calendar.getInstance();
        startTime.set(Calendar.HOUR_OF_DAY, startHour);
        startTime.set(Calendar.DAY_OF_MONTH, startDay);
        startTime.set(Calendar.MINUTE, startMinute);
        startTime.set(Calendar.MONTH, startMonth);
        startTime.set(Calendar.YEAR, myYear);

        Calendar endTime = Calendar.getInstance();
        endTime.set(Calendar.DAY_OF_MONTH, startDay);
        endTime.set(Calendar.MONTH, startMonth);
        endTime.set(Calendar.YEAR, myYear);
        endTime.set(Calendar.HOUR_OF_DAY, endHour);
        endTime.set(Calendar.MINUTE, endMinute);

        WeekViewEvent event = new WeekViewEvent(1, evenName , evenLocation, startTime, endTime);

        if(color == 1)
        {
            event.setColor(getResources().getColor(R.color.event_color_02));
        }
        if(color ==2){
            event.setColor(getResources().getColor(R.color.event_color_04));
        }
        if(color == 3)
        {
            event.setColor(getResources().getColor(R.color.event_color_03));
        }
        if(color ==4){
            event.setColor(getResources().getColor(R.color.event_color_01));
        }


        reminderSet();
        MainActivity.addEventToList(event);
        MainActivity.main.finish();

        startActivity(new Intent(this, MainActivity.class));
        finish();
    }

    public void remove(View view){
        MainActivity.removeEventFromList(MainActivity.getObjectEvent());
        MainActivity.main.finish();
        startActivity(new Intent(this, MainActivity.class));
    }

    public void repeatWeek(View view){
        repeatW = 1;
    }

    public void repeatMonth(View view){
        repeatM = 1;}


    public void onNothingSelected(AdapterView<?> parent) {

        reminderms = -1;
    }

    public void repeatYear(View view){
        repeatY = 1;
    }


    public void reminderSet() {

        // Flag for whether or not a reminder needs to be created
        if (reminderms != -1) {

            Intent reminderIntent = new Intent("SET_REMINDER");
            reminderIntent.putExtra("hour", startHour);
            reminderIntent.putExtra("min", startMinute);
            reminderIntent.putExtra("title", evenName);

            PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, reminderIntent, 0);
            AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

            // Calculate reminder time in milliseconds
            Calendar reminderTime = Calendar.getInstance();
            reminderTime.set(Calendar.HOUR_OF_DAY, startHour);
            reminderTime.set(Calendar.DAY_OF_MONTH, startDay);
            reminderTime.set(Calendar.MINUTE, startMinute);
            reminderTime.set(Calendar.MONTH, startMonth);
            reminderTime.set(Calendar.YEAR, myYear);
            alarmManager.set(AlarmManager.RTC_WAKEUP, (reminderTime.getTimeInMillis() - reminderms), pendingIntent);
            Toast.makeText(this, "Reminder set", Toast.LENGTH_LONG).show();
        }
    }

    // Overridden spinner methods for reminder selection
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        // Switch statement to set the proper reminder time
        switch (pos) {
            case 0:
                reminderms = -1;
                break;
            case 1:
                reminderms = 0;
                break;
            case 2:
                reminderms = 1000 * 60;
                break;
            case 3:
                reminderms = 1000 * 60 * 5;
                break;
            case 4:
                reminderms = 1000 * 60 * 15;
                break;
            case 5:
                reminderms = 1000 * 60 * 30;
                break;
            case 6:
                reminderms = 1000 * 60 * 60;
                break;
            case 7:
                reminderms = 1000 * 60 * 60 * 2;
                break;
            case 8:
                reminderms = 1000 * 60 * 60 * 4;
                break;
            case 9:
                reminderms = 1000 * 60 * 60 * 12;
                break;
            case 10:
                reminderms = 1000 * 60 * 60 * 24;
                break;
            default:
                reminderms = -1;
                break;
        }
    }


}


