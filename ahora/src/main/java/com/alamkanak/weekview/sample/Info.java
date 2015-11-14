/***********************************************************************
*our group created this class (info) and the following methods in main
 * getEventClickFlag()/setEventClickFlag()/getEname()
 * getElocation()/getObjectEvent()/addEventToList(WeekViewEvent event)/
 * removeEventFromList(WeekViewEvent event)
*our group created the XML layout info and all interaction between info and main class
*the info class creates the user input form
*it calls the date and time pickers captures the user input
*and creates an WeekViewEvent and passes it to an array list
*List<WeekViewEvent> mEventList = new ArrayList<>() in the MainActivity
 * MainActivy calls the methods in the library to display the calendar
*************************************************************************
*The display is handled by alamkanak weekview library
*Created by Raquib-ul-Alam Kanak on 7/21/2014.
*Website: http://alamkanak.github.io/
*************************************************************************/

package com.alamkanak.weekview.sample;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBarActivity;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;

import com.alamkanak.weekview.WeekViewEvent;

import java.util.Calendar;


public class Info extends ActionBarActivity {
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
            return new DatePickerDialog(getActivity(), this, year, month, day);
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
            return new TimePickerDialog(getActivity(), this, hour, minute,
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

    public void backToMain(View view) {



        EditText edit1 = (EditText)findViewById(R.id.editText);
        evenName = edit1.getText().toString();
        EditText edit2 = (EditText) findViewById(R.id.editText2);
        evenLocation = edit2.getText().toString();


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



        WeekViewEvent event = new WeekViewEvent(1, evenName + "\n", evenLocation, startTime, endTime);

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

}


