package com.example.scheduler;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
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
    private int year, month;

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
        year = Integer.parseInt(curYearFormat.format(date));
        month = Integer.parseInt(curMonthFormat.format(date)) - 1;
        mCal.set(year, month, Integer.parseInt(curDayFormat.format(date)));

        resetCalendar();

        findViewById(R.id.PreviousMonthButton).setOnClickListener(
            new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    month--;
                    resetCalendar();
                }
            }
        );

        findViewById(R.id.NextMonthButton).setOnClickListener(
            new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    month++;
                    resetCalendar();
                }
            }
        );
    }

    private void resetCalendar() {
        dayList.clear();

        if (month == 12) {
            year++;
            month = 0;
        }
        if (month == -1) {
            year--;
            month = 11;
        }

        mCal.set(year, month, 1);

        //현재 날짜 텍스트뷰에 뿌려줌
        Month.setText(Integer.toString(month + 1));
        Year.setText(Integer.toString(year));

        int dayNum = mCal.get(Calendar.DAY_OF_WEEK);
        //1일 - 요일 매칭 시키기 위해 공백 add
        for (int i = 1; i < dayNum; i++) {
            dayList.add("");
        }

        setCalendarDate();

        gridAdapter = new GridAdapter(getApplicationContext(), dayList);
        gridView.setAdapter(gridAdapter);
    }

    /**
     * 해당 월에 표시할 일 수 구함
     *
     *
     */
    private void setCalendarDate() {
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

            //오늘 day 가져옴
            Calendar mCalTmp = Calendar.getInstance();
            Integer today = mCalTmp.get(Calendar.DAY_OF_MONTH);
            String sToday = String.valueOf(today);
            Date nowDate = new Date(System.currentTimeMillis());
            final SimpleDateFormat curYearFormat = new SimpleDateFormat("yyyy", Locale.KOREA);
            final SimpleDateFormat curMonthFormat = new SimpleDateFormat("MM", Locale.KOREA);

            if (Integer.parseInt(curYearFormat.format(nowDate)) == mCal.get(Calendar.YEAR) &&  Integer.parseInt(curMonthFormat.format(nowDate)) == mCal.get(Calendar.MONTH) + 1 &&
                    sToday.equals(getItem(position))) { //오늘 day 텍스트 컬러 변경
                holder.tvItemGridView.setTextColor(getResources().getColor(R.color.black));
                holder.tvItemGridView.setText(Html.fromHtml("<b>" + holder.tvItemGridView.getText() + "</b>"));
            }

            switch (position % 7) {
                case 0: holder.tvItemGridView.setTextColor(getResources().getColor(R.color.sunday)); break;
                case 6:  holder.tvItemGridView.setTextColor(getResources().getColor(R.color.saturday)); break;
                default: holder.tvItemGridView.setTextColor(getResources().getColor(R.color.weekday));
            }

            return convertView;
        }
    }

    private class ViewHolder {
        TextView tvItemGridView;
    }
}
