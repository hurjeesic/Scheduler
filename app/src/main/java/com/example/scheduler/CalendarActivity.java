package com.example.scheduler;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

// Grid View를 이용한 달력 제작
// https://heum-story.tistory.com/6
public class CalendarActivity extends Activity {
    /**
     * 연/월 텍스트뷰
     */
    private TextView Month;
    private TextView Year;
    /**
     * 그리드뷰 어댑터
     */
    private GridAdapter gridAdapter;
    /**
     * 일 저장 할 리스트
     */
    private ArrayList<String> dayList;
    /**
     * 그리드뷰
     */
    private GridView gridView;
    /**
     * 캘린더 변수
     */
    private Calendar mCal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.calendar);
        setContentView(R.layout.calendar);

        Month = (TextView)findViewById(R.id.Month);
        Year = (TextView)findViewById(R.id.Year);
        gridView = (GridView)findViewById(R.id.gridview);
        dayList = new ArrayList<>();

        // 오늘에 날짜를 세팅 해준다.
        long now = System.currentTimeMillis();
        Date date = new Date(now);

        //연,월,일을 따로 저장
        final SimpleDateFormat curYearFormat = new SimpleDateFormat("yyyy", Locale.KOREA);
        final SimpleDateFormat curMonthFormat = new SimpleDateFormat("MM", Locale.KOREA);
        final SimpleDateFormat curDayFormat = new SimpleDateFormat("dd", Locale.KOREA);

        mCal = Calendar.getInstance();
        //이번달 1일 무슨요일인지 판단 mCal.set(Year,Month,Day)
        mCal.set(Integer.parseInt(curYearFormat.format(date)), Integer.parseInt(curMonthFormat.format(date)) - 1, 1);

        resetCalendar();
    }

    private void addMonth() {
        mCal.set(Calendar.MONTH, mCal.get(Calendar.MONTH) + 1);
        resetCalendar();
    }

    private void divideMonth() {
        mCal.set(Calendar.MONTH, mCal.get(Calendar.MONTH) - 1);
        resetCalendar();
    }

    private void resetCalendar() {
        dayList.clear();
//        dayList.add("일");
//        dayList.add("월");
//        dayList.add("화");
//        dayList.add("수");
//        dayList.add("목");
//        dayList.add("금");
//        dayList.add("토");

        //현재 날짜 텍스트뷰에 뿌려줌
        Month.setText(Integer.toString(mCal.get(Calendar.MONTH) + 1));
        Year.setText(Integer.toString(mCal.get(Calendar.YEAR)));

        int dayNum = mCal.get(Calendar.DAY_OF_WEEK);
        //1일 - 요일 매칭 시키기 위해 공백 add
        for (int i = 1; i < dayNum; i++) {
            dayList.add("");
        }

        setCalendarDate(mCal.get(Calendar.MONTH) + 1);

        gridAdapter = new GridAdapter(getApplicationContext(), dayList);
        gridView.setAdapter(gridAdapter);
    }

    /**
     * 해당 월에 표시할 일 수 구함
     *
     * @param month
     */
    private void setCalendarDate(int month) {
        mCal.set(Calendar.MONTH, month - 1);
        for (int i = 0; i < mCal.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
            dayList.add("" + (i + 1));
        }
    }
    /**
     * 그리드뷰 어댑터
     *
     */
    private class GridAdapter extends BaseAdapter {
        private final List<String> list;
        private final LayoutInflater inflater;
        /**
         * 생성자
         *
         * @param context
         * @param list
         */
        public GridAdapter(Context context, List<String> list) {
            this.list = list;
            this.inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }
        @Override
        public int getCount() {
            return list.size();
        }
        @Override
        public String getItem(int position) {
            return list.get(position);
        }
        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.item_calendar_gridview, parent, false);
                holder = new ViewHolder();
                holder.tvItemGridView = (TextView)convertView.findViewById(R.id.tv_item_gridview);
                convertView.setTag(holder);
            }
            else {
                holder = (ViewHolder)convertView.getTag();
            }
            holder.tvItemGridView.setText("" + getItem(position));
            //해당 날짜 텍스트 컬러,배경 변경
            mCal = Calendar.getInstance();
            //오늘 day 가져옴
            Integer today = mCal.get(Calendar.DAY_OF_MONTH);
            String sToday = String.valueOf(today);
            if (System.currentTimeMillis() == mCal.getTimeInMillis() && sToday.equals(getItem(position))) { //오늘 day 텍스트 컬러 변경
                holder.tvItemGridView.setTextColor(getResources().getColor(R.color.black));
            }

            if (position % 7 == 0) {
                holder.tvItemGridView.setTextColor(getResources().getColor(R.color.sunday));
            }
            else if (position % 7 == 6) {
                holder.tvItemGridView.setTextColor(getResources().getColor(R.color.saturday));
            }
            else{
                holder.tvItemGridView.setTextColor(getResources().getColor(R.color.weekday));
            }
            return convertView;
        }
    }

    private class ViewHolder {
        TextView tvItemGridView;
    }
}
