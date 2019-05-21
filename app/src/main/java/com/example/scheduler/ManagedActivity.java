package com.example.scheduler;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;

public class ManagedActivity {
    private ArrayList<Activity> activityList;
    private static ManagedActivity instance = new ManagedActivity();

    public static ManagedActivity getInstance() {
        return instance;
    }

    private ManagedActivity() {
        activityList = new ArrayList<>();
    }

    public void allActivityFinish() {
        for (int i = 0; i < activityList.size(); i++) {
            activityList.get(i).finish();
        }

        activityList.clear();
    }

    public void moveActivity(Activity original, Class move, String[]... datas) {
        Intent intent = new Intent(original.getApplicationContext(), move);
        activityList.add(original);

        for (String[] data : datas) {
            intent.putExtra(data[0], data[1]);
        }

        original.startActivity(intent);
    }
}
