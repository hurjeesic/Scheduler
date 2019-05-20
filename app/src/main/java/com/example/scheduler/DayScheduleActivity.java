package com.example.scheduler;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class DayScheduleActivity extends Activity {
    TextView yearTextView;
    TextView monthTextView;
    TextView dayTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.day_scheduler);

        yearTextView = (TextView)findViewById(R.id.scheduler_year);
        monthTextView = (TextView)findViewById(R.id.scheduler_month);
        dayTextView = (TextView)findViewById(R.id.scheduler_day);

        Intent intent = getIntent();

        yearTextView.setText(Integer.toString(intent.getExtras().getInt("year")));
        monthTextView.setText(Integer.toString(intent.getExtras().getInt("month")));
        dayTextView.setText(Integer.toString(intent.getExtras().getInt("day")));
    }
}
