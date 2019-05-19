package com.example.scheduler;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class InsertionActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.insertion);

        Spinner yearSpinner = (Spinner)findViewById(R.id.insertion_year);
        Spinner monthSpinner = (Spinner)findViewById(R.id.insertion_month);
        Spinner daySpinner = (Spinner)findViewById(R.id.insertion_day);

        // List를 활용한 Spinner
        // https://farmerkyh.tistory.com/412?category=687218
        List<String> yearList = new ArrayList<>();
        yearList.add("년");
        for(int i=1970; i< 2038; i++) {
            yearList.add(Integer.toString(i));
        }

        // 스피너에 뿌려질 List형식의 Data를 담을 Adapter
        ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, yearList);

        // Spinner 클릭시 DropDown 모양을 설정
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // 스피너에 어답터를 연결
        yearSpinner.setAdapter(spinnerArrayAdapter);

        List<String> monthList = new ArrayList<>();
        monthList.add("월");
        for(int i=1; i<= 12; i++) {
            if (i < 10) {
                monthList.add("0" + Integer.toString(i));
            }
            else {
                monthList.add(Integer.toString(i));
            }
        }

        // 스피너에 뿌려질 List형식의 Data를 담을 Adapter
        spinnerArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, monthList);

        // Spinner 클릭시 DropDown 모양을 설정
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // 스피너에 어답터를 연결
        monthSpinner.setAdapter(spinnerArrayAdapter);

        // Spinner Event
        // https://mailmail.tistory.com/16
        yearSpinner.setOnItemSelectedListener(
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Spinner yearSpinner = (Spinner)findViewById(R.id.insertion_year);
                    Spinner daySpinner = (Spinner)findViewById(R.id.insertion_day);
                    String monthStr = (String)parent.getAdapter().getItem(position);

                    List dayList = new ArrayList<String>();
                    dayList.add("일");

                    if (!yearSpinner.getSelectedItem().toString().equals("년") && !monthStr.equals("월")) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Integer.parseInt(yearSpinner.getSelectedItem().toString()), Integer.parseInt(monthStr) - 1, 1);

                        for(int i=1; i<= calendar.getActualMaximum(Calendar.DATE); i++) {
                            if (i < 10) {
                                dayList.add("0" + i);
                            }
                            else {
                                dayList.add(Integer.toString(i));
                            }
                        }
                    }

                    // 스피너에 뿌려질 List형식의 Data를 담을 Adapter
                    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(InsertionActivity.this, android.R.layout.simple_spinner_item, dayList);

                    // Spinner 클릭시 DropDown 모양을 설정
                    spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    // 스피너에 어답터를 연결
                    daySpinner.setAdapter(spinnerArrayAdapter);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            }
        );

        monthSpinner.setOnItemSelectedListener(
            new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    Spinner yearSpinner = (Spinner)findViewById(R.id.insertion_year);
                    Spinner daySpinner = (Spinner)findViewById(R.id.insertion_day);
                    String monthStr = (String)parent.getAdapter().getItem(position);

                    List dayList = new ArrayList<String>();
                    dayList.add("일");

                    if (!yearSpinner.getSelectedItem().toString().equals("년") && !monthStr.equals("월")) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(Integer.parseInt(yearSpinner.getSelectedItem().toString()), Integer.parseInt(monthStr) - 1, 1);

                        for(int i=1; i<= calendar.getActualMaximum(Calendar.DATE); i++) {
                            if (i < 10) {
                                dayList.add("0" + i);
                            }
                            else {
                                dayList.add(Integer.toString(i));
                            }
                        }
                    }

                    // 스피너에 뿌려질 List형식의 Data를 담을 Adapter
                    ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(InsertionActivity.this, android.R.layout.simple_spinner_item, dayList);

                    // Spinner 클릭시 DropDown 모양을 설정
                    spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                    // 스피너에 어답터를 연결
                    daySpinner.setAdapter(spinnerArrayAdapter);
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            }
        );

        findViewById(R.id.InsertButton).setOnClickListener(
            new Button.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Spinner yearSpinner = (Spinner)findViewById(R.id.insertion_year);
                    Spinner monthSpinner = (Spinner)findViewById(R.id.insertion_month);
                    Spinner daySpinner = (Spinner)findViewById(R.id.insertion_day);

                    String yearStr = yearSpinner.getSelectedItem().toString();
                    String monthStr = monthSpinner.getSelectedItem().toString();
                    String dayStr = daySpinner.getSelectedItem().toString();

                    EditText title = (EditText)findViewById(R.id.insertion_title);
                    EditText contents = (EditText)findViewById(R.id.insertion_contents);
                    EditText tags = (EditText)findViewById(R.id.insertion_tag);

                    if (yearStr.equals("년") || monthStr.equals("월") || dayStr.equals("일")) {
                        Toast.makeText(InsertionActivity.this, "날짜는 반드시 입력하셔야합니다.", Toast.LENGTH_LONG).show();
                    }
                    else if (title.getText().toString().length() == 0) {
                        Toast.makeText(InsertionActivity.this, "제목은 반드시 입력하셔야합니다.", Toast.LENGTH_LONG).show();
                    }
                    else {
                        ManagedFile manager = new ManagedFile(getFilesDir().getAbsolutePath());
                        String[] data = new String[]{ title.getText().toString(), contents.getText().toString(), tags.getText().toString() };
                        String dateStr = yearStr + monthStr + dayStr;

                        manager.writeData(dateStr, data, true);

                        Intent intent = new Intent(InsertionActivity.this, CalendarActivity.class);

                        startActivity(intent);
                        finish();
                    }
                }
            }
        );
    }
}
