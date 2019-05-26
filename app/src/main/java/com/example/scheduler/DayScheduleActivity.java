package com.example.scheduler;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class DayScheduleActivity extends Activity {
    TextView yearTextView;
    TextView monthTextView;
    TextView dayTextView;

    ListView scheduleListView;
    ListAdapter listAdapter;
    List<SearchListItem> searchItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.day_scheduler);

        scheduleListView = (ListView)findViewById(R.id.schedule_list);

        searchItemList = new ArrayList<>();

        yearTextView = (TextView)findViewById(R.id.scheduler_year);
        monthTextView = (TextView)findViewById(R.id.scheduler_month);
        dayTextView = (TextView)findViewById(R.id.scheduler_day);

        Intent intent = getIntent();

        yearTextView.setText(intent.getExtras().getString("year"));
        monthTextView.setText(intent.getExtras().getString("month"));
        dayTextView.setText(intent.getExtras().getString("day"));

        setSearchItemList(yearTextView.getText().toString() + monthTextView.getText().toString() + dayTextView.getText().toString());
    }

    private void setSearchItemList(String date) {
        searchItemList.clear();

        ManagedFile manager = new ManagedFile(getFilesDir().getAbsolutePath());

        ArrayList<String[]> datas = manager.readFile(date);
        for (String[] data : datas) {
            StringBuilder tag = new StringBuilder("");
            if (!data[3].equals("")) {
                String[] tags = data[3].split(",");
                for (String temp : tags) {
                    tag.append("#").append(temp).append(" ");
                }
            }


            SearchListItem tempItem = new SearchListItem(date, data[0], data[1], tag.toString());
            searchItemList.add(tempItem);
        }

        listAdapter = new ListAdapter(DayScheduleActivity.this, searchItemList);
        if (listAdapter == null) Log.d("Information", "setSearchItemList: ");
        scheduleListView.setAdapter(listAdapter);
    }

    private class ListAdapter extends BaseAdapter {
        Context context;
        List<SearchListItem> scheduleList;

        TextView yearTextview, monthTextview, dayTextview, indexTextview, titleTextview, tagsTextview;

        public ListAdapter(Context context, List<SearchListItem> scheduleList) {
            this.context = context;
            this.scheduleList = scheduleList;
        }

        @Override
        public int getCount() {
            return this.scheduleList.size();
        }

        @Override
        public SearchListItem getItem(int position) {
            return this.scheduleList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(context).inflate(R.layout.item_calendar_listview, null);
                yearTextview = (TextView)convertView.findViewById(R.id.search_list_year);
                monthTextview = (TextView)convertView.findViewById(R.id.search_list_month);
                dayTextview = (TextView)convertView.findViewById(R.id.search_list_day);
                indexTextview = (TextView)convertView.findViewById(R.id.search_list_index);
                titleTextview = (TextView)convertView.findViewById(R.id.search_list_title);
                tagsTextview = (TextView)convertView.findViewById(R.id.search_list_tags);
            }

            int month = Integer.parseInt(getItem(position).getDate().substring(4, 6));

            yearTextview.setText(getItem(position).getDate().substring(0, 4)+"년");
            monthTextview.setText(month+"월");
            dayTextview.setText(getItem(position).getDate().substring(6)+"일");
            indexTextview.setText(getItem(position).getIndex());
            titleTextview.setText(getItem(position).getTitle());
            tagsTextview.setText(getItem(position).getTags());

            final int index = position;
            final String dateStr = getItem(position).getDate();
            final String indexStr = getItem(position).getIndex();

            convertView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String[] dateAry = { "date", dateStr };
                        String[] indexAry = { "index", indexStr };
                        ManagedActivity.getInstance().moveActivity(DayScheduleActivity.this, UpdateActivity.class, dateAry, indexAry);
                    }
                }
            );

            Button deleteButton = (Button)convertView.findViewById(R.id.scheduleDeleteButton);
            deleteButton.setOnClickListener(
                    new Button.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            ManagedFile manager = new ManagedFile(getFilesDir().getAbsolutePath());
                            if (manager.deleteData(dateStr, Integer.parseInt(indexStr))) {
                                scheduleList.remove(index);
                                listAdapter.notifyDataSetChanged();
                                scheduleListView.setAdapter(listAdapter);
                            }
                        }
                    }
            );

            return convertView;
        }
    }
}
