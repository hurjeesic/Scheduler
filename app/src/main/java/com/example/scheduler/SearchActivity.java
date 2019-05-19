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
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends Activity {
    ListView scheduleList;
    ListAdapter listAdapter;
    List<SearchListItem> searchItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.search);

        scheduleList = (ListView)findViewById(R.id.schedule_list);

        searchItemList = new ArrayList<>();

        ManagedFile manager = new ManagedFile(getFilesDir().getAbsolutePath());
        ArrayList<String[]> datas = manager.allReadFile();
        for (String[] data : datas) {
            String[] tags = data[3].split(",");
            StringBuilder tag = new StringBuilder();
            for (String temp : tags) {
                tag.append("#").append(temp).append(" ");
            }

            SearchListItem tempItem = new SearchListItem(data[0], data[1], tag.toString());
            searchItemList.add(tempItem);
        }

        listAdapter = new ListAdapter(SearchActivity.this, searchItemList);
        scheduleList.setAdapter(listAdapter);

        findViewById(R.id.ScheduleCalendarButton).setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        SearchActivity.super.onBackPressed();
                    }
                }
        );
    }

    class SearchListItem {
        private String date;
        private String title;
        private String tags;

        public SearchListItem(String date, String title, String tags) {
            this.date = date;
            this.title = title;
            this.tags = tags;
        }

        public String getDate() {
            return date;
        }

        public void setDate(String date) {
            this.date = date;
        }

        public String getTitle() {
            return title;
        }

        public void setTitle(String title) {
            this.title = title;
        }

        public String getTags() {
            return tags;
        }

        public void setTags(String tags) {
            this.tags = tags;
        }
    }

    private class ListAdapter extends BaseAdapter {
        Context context;
        List<SearchListItem> scheduleList;

        TextView yearTextview, monthTextview, titleTextview, tagsTextview, dayTextview;

        public ListAdapter(Context context, List<SearchListItem> scheduleList) {
            this.context = context;
            this.scheduleList = scheduleList;
        }

        @Override
        public int getCount() {
            return this.scheduleList.size();
        }

        @Override
        public Object getItem(int position) {
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
                titleTextview = (TextView)convertView.findViewById(R.id.search_list_title);
                tagsTextview = (TextView)convertView.findViewById(R.id.search_list_tags);
            }

            yearTextview.setText(scheduleList.get(position).getDate().substring(0, 4)+"년");
            monthTextview.setText(scheduleList.get(position).getDate().substring(4, 6)+"월");
            dayTextview.setText(scheduleList.get(position).getDate().substring(6)+"일");
            titleTextview.setText(scheduleList.get(position).getTitle());
            tagsTextview.setText(scheduleList.get(position).getTags());

            return convertView;
        }
    }
}
