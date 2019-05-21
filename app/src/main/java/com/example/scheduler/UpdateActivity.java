package com.example.scheduler;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

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
        final String dateStr = intent.getExtras().getString("date");
        final String indexStr = intent.getExtras().getString("index");

        yearTextView.setText(dateStr.substring(0, 4)+"년");
        monthTextView.setText((Integer.parseInt(dateStr.substring(4, 6)))+"월");
        dayTextView.setText((Integer.parseInt(dateStr.substring(6, 8)))+"일");
        indexTextView.setText(indexStr);

        ManagedFile manager = new ManagedFile(getFilesDir().getAbsolutePath());
        String[] data = manager.readData(dateStr, Integer.parseInt(indexStr));

        titleEditText.setText(data[1]);
        contentsEditText.setText(data[2]);
        tagEditText.setText(data[3]);

        findViewById(R.id.UpdateButton).setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (titleEditText.getText().toString().length() == 0) {
                        Toast.makeText(getApplicationContext(), "제목은 반드시 입력하셔야합니다.", Toast.LENGTH_LONG);
                    }
                    else {
                        ManagedFile manager = new ManagedFile(getFilesDir().getAbsolutePath());
                        String[] data = { titleEditText.getText().toString(), contentsEditText.getText().toString(), tagEditText.getText().toString() };
                        manager.updateData(dateStr, data, Integer.parseInt(indexStr));

                        ManagedActivity.getInstance().moveActivity(UpdateActivity.this, CalendarActivity.class);
                        ManagedActivity.getInstance().allActivityFinish();
                    }
                }
            }
        );
    }
}
