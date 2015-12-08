package com.alamkanak.weekview.sample;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.alamkanak.weekview.DateTimeInterpreter;
import com.alamkanak.weekview.WeekView;
import com.alamkanak.weekview.WeekViewEvent;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


/**
 * Created by Raquib-ul-Alam Kanak on 7/21/2014.
 * Website: http://alamkanak.github.io/
 */
public class MainActivity extends ActionBarActivity implements WeekView.MonthChangeListener,
        WeekView.EventClickListener, WeekView.EventLongPressListener {

    private static final int TYPE_DAY_VIEW = 1;
    private static final int TYPE_THREE_DAY_VIEW = 2;
    private static final int TYPE_WEEK_VIEW = 3;
    private int mWeekViewType = TYPE_THREE_DAY_VIEW;
    private WeekView mWeekView;
    Info info = new Info();
    public static int eventClickFlag = 0;
    public static List<WeekViewEvent> mEventList = new ArrayList<>();
    public static String eName;
    public static String eLocation;
    public static WeekViewEvent eventObject;
    public static Activity main;
    public static final String EVENT_FILE = "ahora_data";
    public static final int event_color_05 = 0x7b1a765c;
    public static final int event_color_06 = 0x7b0c005c;



    public static int getEventClickFlag() {
        return eventClickFlag;
    }

    public static void setEventClickFlag(){
        eventClickFlag = 0;
    }

    public static List<WeekViewEvent> getmEventList(){
        return  mEventList;
    }

//testing git hub
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        main = this;

        try {
            FileInputStream fis = this.openFileInput(EVENT_FILE);
            ObjectInputStream ois = new ObjectInputStream(fis);
            mEventList = (List<WeekViewEvent>) ois.readObject();
            ois.close();
            fis.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        // Get a reference for the week view in the layout.
        mWeekView = (WeekView) findViewById(R.id.weekView);

        // Show a toast message about the touched event.
        mWeekView.setOnEventClickListener(this);

        // The week view has infinite scrolling horizontally. We have to provide the events of a
        // month every time the month changes on the week view.
        mWeekView.setMonthChangeListener(this);

        // Set long press listener for events.
        mWeekView.setEventLongPressListener(this);

        // Set up a date time interpreter to interpret how the date and time will be formatted in
        // the week view. This is optional.
        setupDateTimeInterpreter(false);

        clearAll();

        if(mEventList.isEmpty()){
            addWeekEnds();
            holidays();
        }






    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        setupDateTimeInterpreter(id == R.id.action_week_view);
        switch (id) {
            case R.id.action_today:
                mWeekView.goToToday();
                return true;
            case R.id.action_day_view:
                if (mWeekViewType != TYPE_DAY_VIEW) {
                    item.setChecked(!item.isChecked());
                    mWeekViewType = TYPE_DAY_VIEW;
                    mWeekView.setNumberOfVisibleDays(1);

                    // Lets change some dimensions to best fit the view.
                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                }
                return true;
            case R.id.action_three_day_view:
                if (mWeekViewType != TYPE_THREE_DAY_VIEW) {
                    item.setChecked(!item.isChecked());
                    mWeekViewType = TYPE_THREE_DAY_VIEW;
                    mWeekView.setNumberOfVisibleDays(3);

                    // Lets change some dimensions to best fit the view.
                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, getResources().getDisplayMetrics()));
                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 12, getResources().getDisplayMetrics()));
                }
                return true;
            case R.id.action_week_view:
                if (mWeekViewType != TYPE_WEEK_VIEW) {
                    item.setChecked(!item.isChecked());
                    mWeekViewType = TYPE_WEEK_VIEW;
                    mWeekView.setNumberOfVisibleDays(7);

                    // Lets change some dimensions to best fit the view.
                    mWeekView.setColumnGap((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 2, getResources().getDisplayMetrics()));
                    mWeekView.setTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
                    mWeekView.setEventTextSize((int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, 10, getResources().getDisplayMetrics()));
                }
                return true;
            case R.id.action_Monthly_view: {
                startActivity(new Intent(this, MonthlyView.class));
            }
            return true;
        }

        if (item.getItemId() == R.id.add) {
            startActivity(new Intent(MainActivity.this, Info.class));
            return (true);
        }

//        if(item.getItemId()==R.id.clear){
//            clearAll();
//        }


        return super.onOptionsItemSelected(item);
    }

    /**
     * Set up a date time interpreter which will show short date values when in week view and long
     * date values otherwise.
     *
     * @param shortDate True if the date values should be short.
     */
    private void setupDateTimeInterpreter(final boolean shortDate) {
        mWeekView.setDateTimeInterpreter(new DateTimeInterpreter() {
            @Override
            public String interpretDate(Calendar date) {
                SimpleDateFormat weekdayNameFormat = new SimpleDateFormat("EEE", Locale.getDefault());
                String weekday = weekdayNameFormat.format(date.getTime());
                SimpleDateFormat format = new SimpleDateFormat(" M/d", Locale.getDefault());

                // All android api level do not have a standard way of getting the first letter of
                // the week day name. Hence we get the first char programmatically.
                // Details: http://stackoverflow.com/questions/16959502/get-one-letter-abbreviation-of-week-day-of-a-date-in-java#answer-16959657
                if (shortDate)
                    weekday = String.valueOf(weekday.charAt(0));
                return weekday.toUpperCase() + format.format(date.getTime());
            }

            @Override
            public String interpretTime(int hour) {
                return hour > 11 ? (hour - 12) + " PM" : (hour == 0 ? "12 AM" : hour + " AM");
            }
        });
    }


    @Override
    public List<WeekViewEvent> onMonthChange(int newYear, int newMonth) {


        List<WeekViewEvent> events = new ArrayList<WeekViewEvent>();


        return mEventList;



    }

    private String getEventTitle(Calendar time) {
        return String.format("Event of %02d:%02d %s/%d", time.get(Calendar.HOUR_OF_DAY), time.get(Calendar.MINUTE), time.get(Calendar.MONTH) + 1, time.get(Calendar.DAY_OF_MONTH));
    }

    @Override
    public void onEventClick(WeekViewEvent event, RectF eventRect) {
        Toast.makeText(MainActivity.this, "Clicked " + event.getName(), Toast.LENGTH_SHORT).show();
        eventClickFlag = 1;
        eName = event.getName();
        eLocation = event.getLocation();
        eventObject = event;
        startActivity(new Intent(MainActivity.this, Info.class));


    }

    public static String getEname() {

        return eName;
    }

    public static String getElocation() {
        return eLocation;
    }

    public static WeekViewEvent getObjectEvent(){
        return eventObject;
    }


    @Override
    public void onEventLongPress(WeekViewEvent event, RectF eventRect) {
        Toast.makeText(MainActivity.this, "Long pressed event: " + event.getName(), Toast.LENGTH_SHORT).show();

    }

    // This function will allow other classes to add events to the event list
    public static void addEventToList(WeekViewEvent event) {

        mEventList.add(event);
    }

    //remove object fom the list
    public static void removeEventFromList(WeekViewEvent event) {
        mEventList.remove(event);
    }

    public void onDestroy() {
        super.onDestroy();

        try {
            FileOutputStream fos = this.openFileOutput(EVENT_FILE, Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            oos.writeObject(mEventList);
            oos.close();
            fos.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public void addWeekEnds(){

    for(int y =2015; y < 2017; y++ ) {
            for (int m = 0; m < 12; m++) {

                Calendar check = Calendar.getInstance();
                check.set(Calendar.MONTH, m);
                int satNum = check.getActualMaximum(Calendar.WEEK_OF_MONTH);

                for (int i = 0; i < satNum; i ++) {
                Calendar startTime = Calendar.getInstance();
                startTime.set(Calendar.HOUR_OF_DAY, 0);
                startTime.set(Calendar.MINUTE, 0);
                startTime.set(Calendar.DAY_OF_WEEK, 7);
                startTime.set(Calendar.WEEK_OF_MONTH, i);
                startTime.set(Calendar.MONTH, m);
                startTime.set(Calendar.YEAR, y);



                Calendar endTime = Calendar.getInstance();
                endTime.set(Calendar.HOUR_OF_DAY, 24);
                endTime.set(Calendar.MINUTE, 0);
                endTime.set(Calendar.DAY_OF_WEEK, 7);
                endTime.set(Calendar.WEEK_OF_MONTH, i);
                endTime.set(Calendar.MONTH, m);
                endTime.set(Calendar.YEAR, y);



                WeekViewEvent event = new WeekViewEvent(1, "", "", startTime, endTime);
                event.setColor(event_color_05);
                addEventToList(event);
            }
        }
    }

        for(int y =2015; y < 2017; y++ ) {
            for (int m = 0; m < 12; m++) {

                Calendar check = Calendar.getInstance();
                check.set(Calendar.MONTH, m);
                int sunNum = check.getActualMaximum(Calendar.WEEK_OF_MONTH);

                for (int i = 0; i < sunNum; i ++) {
                    Calendar startTime = Calendar.getInstance();
                    startTime.set(Calendar.HOUR_OF_DAY, 0);
                    startTime.set(Calendar.MINUTE, 0);
                    startTime.set(Calendar.DAY_OF_WEEK, 1);
                    startTime.set(Calendar.WEEK_OF_MONTH, i);
                    startTime.set(Calendar.MONTH, m);
                    startTime.set(Calendar.YEAR, y);

                    Calendar endTime = Calendar.getInstance();
                    endTime.set(Calendar.HOUR_OF_DAY, 24);
                    endTime.set(Calendar.MINUTE, 0);
                    endTime.set(Calendar.DAY_OF_WEEK, 1);
                    endTime.set(Calendar.WEEK_OF_MONTH, i);
                    endTime.set(Calendar.MONTH, m);
                    endTime.set(Calendar.YEAR, y);

                    WeekViewEvent event = new WeekViewEvent(1, "", "", startTime, endTime);
                    event.setColor(event_color_05);
                    addEventToList(event);
                }
            }
        }

    }

    public void holidays(){
        for(int y = 2015; y < 2018; y++)
        {
            //New Year’s Day
            Calendar NewYearDay = Calendar.getInstance();
            NewYearDay.set(Calendar.HOUR_OF_DAY, 0);
            NewYearDay.set(Calendar.MINUTE, 0);
            NewYearDay.set(Calendar.DAY_OF_MONTH, 1);
            NewYearDay.set(Calendar.MONTH, 0);
            NewYearDay.set(Calendar.YEAR, y);

            Calendar endNewYearDay = Calendar.getInstance();
            endNewYearDay.set(Calendar.HOUR_OF_DAY, 23);
            endNewYearDay.set(Calendar.MINUTE, 59);
            endNewYearDay.set(Calendar.DAY_OF_MONTH, 1);
            endNewYearDay.set(Calendar.MONTH, 0);
            endNewYearDay.set(Calendar.YEAR, y);

            WeekViewEvent eventNewYearDay = new WeekViewEvent(1, "New Year’s Day", "", NewYearDay, endNewYearDay);
            eventNewYearDay.setColor(event_color_06);
            addEventToList(eventNewYearDay);

            //Birthday of Martin Luther King, Jr.
            Calendar MLK = Calendar.getInstance();
            MLK.set(Calendar.HOUR_OF_DAY, 0);
            MLK.set(Calendar.MINUTE, 0);
            MLK.set(Calendar.DAY_OF_MONTH, 19);
            MLK.set(Calendar.MONTH, 0);
            MLK.set(Calendar.YEAR, y);

            Calendar endMLK = Calendar.getInstance();
            endMLK.set(Calendar.HOUR_OF_DAY, 23);
            endMLK.set(Calendar.MINUTE, 59);
            endMLK.set(Calendar.DAY_OF_MONTH, 19);
            endMLK.set(Calendar.MONTH, 0);
            endMLK.set(Calendar.YEAR, y);

            WeekViewEvent eventMLK = new WeekViewEvent(1, "Martin Luther King's day", "", MLK, endMLK);
            eventMLK.setColor(event_color_06);
            addEventToList(eventMLK);

            //Washington’s Birthday
            Calendar Washington = Calendar.getInstance();
            Washington.set(Calendar.HOUR_OF_DAY, 0);
            Washington.set(Calendar.MINUTE, 0);
            Washington.set(Calendar.DAY_OF_MONTH, 16);
            Washington.set(Calendar.MONTH, 1);
            Washington.set(Calendar.YEAR, y);

            Calendar endWashington = Calendar.getInstance();
            endWashington.set(Calendar.HOUR_OF_DAY, 23);
            endWashington.set(Calendar.MINUTE, 59);
            endWashington.set(Calendar.DAY_OF_MONTH, 16);
            endWashington.set(Calendar.MONTH, 1);
            endWashington.set(Calendar.YEAR, y);

            WeekViewEvent eventWashington = new WeekViewEvent(1, "Martin Luther King's day", "", Washington, endWashington);
            eventWashington.setColor(event_color_06);
            addEventToList(eventWashington);

            //Memorial Day
            Calendar Memorial = Calendar.getInstance();
            Memorial.set(Calendar.HOUR_OF_DAY, 0);
            Memorial.set(Calendar.MINUTE, 0);
            Memorial.set(Calendar.DAY_OF_MONTH, 25);
            Memorial.set(Calendar.MONTH, 4);
            Memorial.set(Calendar.YEAR, y);

            Calendar endMemorial = Calendar.getInstance();
            endMemorial.set(Calendar.HOUR_OF_DAY, 23);
            endMemorial.set(Calendar.MINUTE, 59);
            endMemorial.set(Calendar.DAY_OF_MONTH, 25);
            endMemorial.set(Calendar.MONTH, 4);
            endMemorial.set(Calendar.YEAR, y);

            WeekViewEvent eventMemorial = new WeekViewEvent(1, "Memorial Day", "", Memorial, endMemorial);
            eventMemorial.setColor(event_color_06);
            addEventToList(eventMemorial);

            //Independence Day
            Calendar Independence = Calendar.getInstance();
            Independence.set(Calendar.HOUR_OF_DAY, 0);
            Independence.set(Calendar.MINUTE, 0);
            Independence.set(Calendar.DAY_OF_MONTH, 4);
            Independence.set(Calendar.MONTH, 5);
            Independence.set(Calendar.YEAR, y);

            Calendar endIndependence = Calendar.getInstance();
            endIndependence.set(Calendar.HOUR_OF_DAY, 23);
            endIndependence.set(Calendar.MINUTE, 59);
            endIndependence.set(Calendar.DAY_OF_MONTH, 4);
            endIndependence.set(Calendar.MONTH, 5);
            endIndependence.set(Calendar.YEAR, y);

            WeekViewEvent eventIndependence = new WeekViewEvent(1, "Independence Day", "", Independence, endIndependence);
            eventIndependence.setColor(event_color_06);
            addEventToList(eventIndependence);

            //Labor Day
            Calendar Labor = Calendar.getInstance();
            Labor.set(Calendar.HOUR_OF_DAY, 0);
            Labor.set(Calendar.MINUTE, 0);
            Labor.set(Calendar.DAY_OF_MONTH, 7);
            Labor.set(Calendar.MONTH, 8);
            Labor.set(Calendar.YEAR, y);

            Calendar endLabor = Calendar.getInstance();
            endLabor.set(Calendar.HOUR_OF_DAY, 23);
            endLabor.set(Calendar.MINUTE, 59);
            endLabor.set(Calendar.DAY_OF_MONTH, 7);
            endLabor.set(Calendar.MONTH, 8);
            endLabor.set(Calendar.YEAR, y);

            WeekViewEvent eventLabor = new WeekViewEvent(1, "Labor Day", "", Labor, endLabor);
            eventLabor.setColor(event_color_06);
            addEventToList(eventLabor);

            //Columbus Day
            Calendar Columbus = Calendar.getInstance();
            Columbus.set(Calendar.HOUR_OF_DAY, 0);
            Columbus.set(Calendar.MINUTE, 0);
            Columbus.set(Calendar.DAY_OF_MONTH, 12);
            Columbus.set(Calendar.MONTH, 9);
            Columbus.set(Calendar.YEAR, y);

            Calendar endColumbus = Calendar.getInstance();
            endColumbus.set(Calendar.HOUR_OF_DAY, 23);
            endColumbus.set(Calendar.MINUTE, 59);
            endColumbus.set(Calendar.DAY_OF_MONTH, 12);
            endColumbus.set(Calendar.MONTH, 9);
            endColumbus.set(Calendar.YEAR, y);

            WeekViewEvent eventColumbus = new WeekViewEvent(1, "Columbus Day", "", Columbus, endColumbus);
            eventColumbus.setColor(event_color_06);
            addEventToList(eventColumbus);

            //Veterans Day
            Calendar Veterans = Calendar.getInstance();
            Veterans.set(Calendar.HOUR_OF_DAY, 0);
            Veterans.set(Calendar.MINUTE, 0);
            Veterans.set(Calendar.DAY_OF_MONTH, 11);
            Veterans.set(Calendar.MONTH, 10);
            Veterans.set(Calendar.YEAR, y);

            Calendar endVeterans = Calendar.getInstance();
            endVeterans.set(Calendar.HOUR_OF_DAY, 23);
            endVeterans.set(Calendar.MINUTE, 59);
            endVeterans.set(Calendar.DAY_OF_MONTH, 11);
            endVeterans.set(Calendar.MONTH, 10);
            endVeterans.set(Calendar.YEAR, y);

            WeekViewEvent eventVeterans = new WeekViewEvent(1, "Veterans Day", "", Veterans, endVeterans);
            eventVeterans.setColor(event_color_06);
            addEventToList(eventVeterans);

            //Thanksgiving Day
            Calendar thanksgiving = Calendar.getInstance();
            thanksgiving.set(Calendar.HOUR_OF_DAY, 0);
            thanksgiving.set(Calendar.MINUTE, 0);
            thanksgiving.set(Calendar.DAY_OF_MONTH, 26);
            thanksgiving.set(Calendar.MONTH, 10);
            thanksgiving.set(Calendar.YEAR, y);

            Calendar endthanksgiving = Calendar.getInstance();
            endthanksgiving.set(Calendar.HOUR_OF_DAY, 23);
            endthanksgiving.set(Calendar.MINUTE, 59);
            endthanksgiving.set(Calendar.DAY_OF_MONTH, 26);
            endthanksgiving.set(Calendar.MONTH, 10);
            endthanksgiving.set(Calendar.YEAR, y);

            WeekViewEvent eventthanksgiving = new WeekViewEvent(1, "Thanksgiving Day", "", thanksgiving, endthanksgiving);
            eventthanksgiving.setColor(event_color_06);
            addEventToList(eventthanksgiving);

            //Christmas
            Calendar Christmas = Calendar.getInstance();
            Christmas.set(Calendar.HOUR_OF_DAY, 0);
            Christmas.set(Calendar.MINUTE, 0);
            Christmas.set(Calendar.DAY_OF_MONTH, 25);
            Christmas.set(Calendar.MONTH, 11);
            Christmas.set(Calendar.YEAR, y);

            Calendar endChristmas = Calendar.getInstance();
            endChristmas.set(Calendar.HOUR_OF_DAY, 23);
            endChristmas.set(Calendar.MINUTE, 59);
            endChristmas.set(Calendar.DAY_OF_MONTH, 25);
            endChristmas.set(Calendar.MONTH, 11);
            endChristmas.set(Calendar.YEAR, y);

            WeekViewEvent eventChristmas = new WeekViewEvent(1, "Christmas", "", Christmas, endChristmas);
            eventChristmas.setColor(event_color_06);
            addEventToList(eventChristmas);

        }

    }

    public void clearAll(){
        mEventList.clear();
    }




}

