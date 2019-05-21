package com.example.scheduler;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;

public class UpdateActivity extends Activity {
    TextView yearTextView;
    TextView monthTextView;
    TextView dayTextView;
    TextView indexTextView;

    EditText titleEditText;
    EditText contentsEditText;
    EditText tagEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.revise);

        yearTextView = (TextView)findViewById(R.id.scheduler_year);
        monthTextView = (TextView)findViewById(R.id.scheduler_month);
        dayTextView = (TextView)findViewById(R.id.scheduler_day);
        indexTextView = (TextView)findViewById(R.id.scheduler_index);
        titleEditText = (EditText)findViewById(R.id.revise_title);
        contentsEditText = (EditText)findViewById(R.id.revise_contents);
        tagEditText = (EditText)findViewById(R.id.revise_tag);

        Intent intent = getIntent();
        String dateStr = intent.getExtras().getString("date");
        String indexStr = intent.getExtras().getString("index");

        yearTextView.setText(dateStr.substring(0, 4)+"년");
        monthTextView.setText((Integer.parseInt(dateStr.substring(4, 6)))+"월");
        dayTextView.setText((Integer.parseInt(dateStr.substring(6, 8)))+"일");
        indexTextView.setText(indexStr);

        ManagedFile manager = new ManagedFile(getFilesDir().getAbsolutePath());
        String[] data = manager.readData(dateStr, Integer.parseInt(indexStr));

        titleEditText.setText(data[1]);
        contentsEditText.setText(data[2]);
        tagEditText.setText(data[3]);
    }
}
