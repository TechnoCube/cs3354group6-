package com.alamkanak.weekview.sample;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.alamkanak.weekview.WeekViewEvent;

import java.util.ArrayList;
import java.util.List;



//not yet working need more work
public class agenda extends ActionBarActivity {
    public List<WeekViewEvent> nEventList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agenda);

        nEventList = MainActivity.getmEventList();
        ListView n = (ListView) findViewById(R.id.myList);
        ArrayAdapter<WeekViewEvent> myAdapter = new ArrayAdapter<>(this, R.layout.activity_agenda, nEventList);
        n.setAdapter(myAdapter);

    }
}
