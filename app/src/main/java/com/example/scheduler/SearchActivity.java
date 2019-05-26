package com.example.scheduler;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.AbstractList;
import java.util.ArrayList;
import java.util.List;

// ListView 사용하기
// https://medium.com/@henen/%EB%B9%A0%EB%A5%B4%EA%B2%8C-%EB%B0%B0%EC%9A%B0%EB%8A%94-%EC%95%88%EB%93%9C%EB%A1%9C%EC%9D%B4%EB%93%9C-%EB%A6%AC%EC%8A%A4%ED%8A%B8%EB%B7%B0-listview-4-%ED%81%B4%EB%A6%AD%EC%9D%B4%EB%B2%A4%ED%8A%B8-onitemclicklistener-toast-4432e650cb

// ListView 데이터 중복 현상 해결
// https://sharp57dev.tistory.com/9
public class SearchActivity extends Activity implements AbsListView.OnScrollListener {
    private ManagedFile manager;

    private ListView scheduleListView;// 리스트뷰
    private boolean lastItemVisibleFlag = false;// 리스트 스크롤이 마지막 셀(맨 바닥)로 이동했는지 체크할 변수
    private List<SearchListItem> searchItemListAll; // 모든 데이터를 담고있는 리스트
    private List<SearchListItem> searchItemList; // 일부 데이터를 담고 있는 리스트
    private ListAdapter listAdapter; // 리스트뷰의 아답터
    private int page = 0; // 페이징변수. 초기 값은 0 이다.
    private final int OFFSET = 5; // 한 페이지마다 로드할 데이터 갯수.
    private ProgressBar searchProgressBar; // 데이터 로딩중을 표시할 프로그레스바
    private boolean mLockListView = false; // 데이터 불러올때 중복안되게 하기위한 변수

    boolean bAdd = true; // 리스트뷰에 데이터가 추가되도 되는지에 대한 여부
    private String find = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.search);

        manager = new ManagedFile(getFilesDir().getAbsolutePath());

        scheduleListView = (ListView)findViewById(R.id.schedule_list);
        searchProgressBar = (ProgressBar)findViewById(R.id.search_progressbar);

        searchItemList = new ArrayList<>();
        searchItemListAll = new ArrayList<>();
        listAdapter = new ListAdapter(SearchActivity.this, searchItemList);
        scheduleListView.setAdapter(listAdapter);

        searchProgressBar.setVisibility(View.GONE);

        scheduleListView.setOnScrollListener(this);

        setSearchItemList(find);
        getItem();

        findViewById(R.id.ScheduleCalendarButton).setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ManagedActivity.getInstance().moveActivity(SearchActivity.this, CalendarActivity.class);
                    ManagedActivity.getInstance().allActivityFinish();
                }
            }
        );

        findViewById(R.id.searchButton).setOnClickListener(
            new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    TextView findText = (TextView)findViewById(R.id.searchText);
                    find = findText.getText().toString().toUpperCase();
                    setSearchItemList(find);
                    getItem();
                }
            }
        );
    }

    private void setSearchItemList(String find) {
        searchItemList.clear();
        searchItemListAll.clear();

        page = 0;
        bAdd = true;
        ArrayList<String[]> datas = manager.allReadFile();
        for (String[] data : datas) {
            StringBuilder tag = new StringBuilder("");
            if (!data[4].equals("")) {
                String[] tags = data[4].split(",");
                for (String temp : tags) {
                    tag.append("#").append(temp).append(" ");
                }
            }

            if (data[2].toUpperCase().contains(find) || data[3].toUpperCase().contains(find) || tag.toString().toUpperCase().contains(find)) {
                SearchListItem tempItem = new SearchListItem(data[0], data[1], data[2], tag.toString());
                searchItemListAll.add(tempItem);
            }
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView absListView, int scrollState) {
        // 1. OnScrollListener.SCROLL_STATE_IDLE : 스크롤이 이동하지 않을때의 이벤트(즉 스크롤이 멈추었을때).
        // 2. lastItemVisibleFlag : 리스트뷰의 마지막 셀의 끝에 스크롤이 이동했을때.
        // 3. mLockListView == false : 데이터 리스트에 다음 데이터를 불러오는 작업이 끝났을때.
        // 1, 2, 3 모두가 true일때 다음 데이터를 불러온다.
        if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE && lastItemVisibleFlag && !mLockListView && bAdd) {
            // 화면이 바닦에 닿을때 처리
            // 로딩중을 알리는 프로그레스바를 보인다.
            searchProgressBar.setVisibility(View.VISIBLE);

            // 다음 데이터를 불러온다.
            getItem();
        }
    }

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        // firstVisibleItem : 화면에 보이는 첫번째 리스트의 아이템 번호.
        // visibleItemCount : 화면에 보이는 리스트 아이템의 갯수
        // totalItemCount : 리스트 전체의 총 갯수
        // 리스트의 갯수가 0개 이상이고, 화면에 보이는 맨 하단까지의 아이템 갯수가 총 갯수보다 크거나 같을때.. 즉 리스트의 끝일때. true
        lastItemVisibleFlag = (totalItemCount > 0) && (firstVisibleItem + visibleItemCount >= totalItemCount);
    }

    private void getItem(){
        // 리스트에 다음 데이터를 입력할 동안에 이 메소드가 또 호출되지 않도록 mLockListView 를 true로 설정한다.
        mLockListView = true;

        if (!bAdd)
        {
            return;
        }

        // 다음 데이터를 불러와서 리스트에 저장한다.
        for(int i = 0; i < OFFSET; i++){
            int index = page * OFFSET + i;
            if (index >= searchItemListAll.size())
            {
                bAdd = false;
                break;
            }

            searchItemList.add(searchItemListAll.get(index));
        }

        // 1초 뒤 프로그레스바를 감추고 데이터를 갱신하고, 중복 로딩 체크하는 Lock을 했던 mLockListView변수를 풀어준다.
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                page++;
                listAdapter.notifyDataSetChanged();
                searchProgressBar.setVisibility(View.GONE);
                mLockListView = false;
            }
        },1000);
    }

    private class ListAdapter extends BaseAdapter {
        LayoutInflater inflater;
        ViewHolder viewHolder;
        List<SearchListItem> scheduleList;


        public ListAdapter(Context context, List<SearchListItem> scheduleList) {
            this.inflater = LayoutInflater.from(context);
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
                convertView = inflater.inflate(R.layout.item_calendar_listview, null);
                viewHolder = new ViewHolder();
                viewHolder.yearTextview = (TextView)convertView.findViewById(R.id.search_list_year);
                viewHolder.monthTextview = (TextView)convertView.findViewById(R.id.search_list_month);
                viewHolder.dayTextview = (TextView)convertView.findViewById(R.id.search_list_day);
                viewHolder.indexTextview = (TextView)convertView.findViewById(R.id.search_list_index);
                viewHolder.titleTextview = (TextView)convertView.findViewById(R.id.search_list_title);
                viewHolder.tagsTextview = (TextView)convertView.findViewById(R.id.search_list_tags);

                convertView.setTag(viewHolder);
            }
            else
            {
                viewHolder = (ViewHolder)convertView.getTag();
            }

            viewHolder.yearTextview.setText(getItem(position).getDate().substring(0, 4)+"년");
            viewHolder.monthTextview.setText(getItem(position).getDate().substring(4, 6)+"월");
            viewHolder.dayTextview.setText(getItem(position).getDate().substring(6)+"일");
            viewHolder.indexTextview.setText(getItem(position).getIndex());
            viewHolder.titleTextview.setText(getItem(position).getTitle());
            viewHolder.tagsTextview.setText(getItem(position).getTags());

            final int index = position;
            final String dateStr = getItem(position).getDate();
            final String indexStr = getItem(position).getIndex();

            Button deleteButton = (Button)convertView.findViewById(R.id.scheduleDeleteButton);
            deleteButton.setOnClickListener(
                new Button.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ManagedFile manager = new ManagedFile(getFilesDir().getAbsolutePath());
                        if (manager.deleteData(dateStr, Integer.parseInt(indexStr))) {
                            scheduleList.remove(index);
                            listAdapter.notifyDataSetChanged();
                        }
                    }
                }
            );

            convertView.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String[] dateAry = { "date", dateStr };
                        String[] indexAry = { "index", indexStr };
                        ManagedActivity.getInstance().moveActivity(SearchActivity.this, UpdateActivity.class, dateAry, indexAry);
                    }
                }
            );

            return convertView;
        }

        class ViewHolder {
            public TextView yearTextview, monthTextview, dayTextview, indexTextview, titleTextview, tagsTextview;
        }
    }
}
