package com.example.scheduler;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

// Grid View를 이용한 달력 제작
// https://heum-story.tistory.com/6
public class CalendarActivity extends Activity {

    private TextView Month;
    private TextView Year;

    private GridAdapter gridAdapter;

    private ArrayList<String> dayList;

    private GridView gridView;

    private Calendar mCal;
    private int year, month;

    boolean[] bSchedule;
    ArrayList<String[]> weathers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.calendar);

        Month = (TextView)findViewById(R.id.Month);
        Year = (TextView)findViewById(R.id.Year);
        gridView = (GridView)findViewById(R.id.gridview);
        dayList = new ArrayList<>();

        // 오늘에 날짜를 세팅 해준다.
        Calendar cal = Calendar.getInstance();

        mCal = Calendar.getInstance();

        //이번달 1일 무슨요일인지 판단 mCal.set(Year,Month,Day)
        year = Integer.parseInt(new SimpleDateFormat("yyyy", Locale.KOREA).format(cal.getTime()));
        month = Integer.parseInt(new SimpleDateFormat("MM", Locale.KOREA).format(cal.getTime())) - 1;
        mCal.set(year, month, Integer.parseInt(new SimpleDateFormat("dd", Locale.KOREA).format(cal.getTime())));

        resetCalendar();

        findViewById(R.id.ScheduleSearchButton).setOnClickListener(
            new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ManagedActivity.getInstance().moveActivity(CalendarActivity.this, SearchActivity.class);
                }
            }
        );

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

        findViewById(R.id.add_button).setOnClickListener(
            new Button.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ManagedActivity.getInstance().moveActivity(CalendarActivity.this, InsertionActivity.class);
                }
            }
        );
    }

    private void resetCalendar() {
        setWeather();
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
        checkMonthSchedule();

        gridAdapter = new GridAdapter(getApplicationContext(), dayList);
        gridView.setAdapter(gridAdapter);
    }

    /**
     * 해당 월에 표시할 일 수 구함
     *
     */
    private void setCalendarDate() {
        for (int i = 0; i < mCal.getActualMaximum(Calendar.DAY_OF_MONTH); i++) {
            dayList.add("" + (i + 1));
        }
    }

    private void checkMonthSchedule() {
        bSchedule = new boolean[mCal.getActualMaximum(Calendar.DAY_OF_MONTH)];

        ManagedFile manager = new ManagedFile(getFilesDir().getAbsolutePath());
        ArrayList<String[]> allScheduleList = manager.allReadFile();
        for (String[] data : allScheduleList) {
            int month = Integer.parseInt(data[0].substring(4, 6));
            int day = Integer.parseInt(data[0].substring(6, 8));
            if (!bSchedule[day - 1] &&  month == mCal.get(Calendar.MONTH) + 1) {
                bSchedule[day - 1] = true;
            }
        }
    }

    private void setWeather() {
        weathers = new ArrayList<String[]>();

        WeatherConnection weatherConnection = new WeatherConnection();
        AsyncTask<String, String, String> weathers = weatherConnection.execute("","");

        try {
            String weathersStr = weathers.get();
            String[] weathersStrAry = weathersStr.split("\r");
            Calendar today = Calendar.getInstance();
            for (int i = 0; i < weathersStrAry.length; i++) {
                int year, month, day;
                String[] tempAry = new String[2];
                year = today.get(Calendar.YEAR);
                month = today.get(Calendar.MONTH) + 1;
                day = today.get(Calendar.DATE);
                tempAry[0] = year + (month < 10 ? "0" + month : Integer.toString(month)) + (day < 10 ? "0" + day : Integer.toString(day));
                tempAry[1]  = weathersStrAry[i];

                this.weathers.add(tempAry);
                today.set(Calendar.DATE, day + 1);
            }
        }
        catch (Exception e) {

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
            Calendar todayCal = Calendar.getInstance();
            todayCal.setTimeInMillis(System.currentTimeMillis());
            Integer today = todayCal.get(Calendar.DAY_OF_MONTH);
            String sToday = String.valueOf(today);
            if (weathers.size() != 0) {
                String dateStr = weathers.get(0)[0];
                if (year == Integer.parseInt(dateStr.substring(0, 4)) &&
                    month == Integer.parseInt(dateStr.substring(4, 6)) - 1 &&
                    getItem(position).equals(Integer.toString(Integer.parseInt(dateStr.substring(6, 8))))) {
                    holder.tvItemGridView.setText(Html.fromHtml("<font size=1>" + holder.tvItemGridView.getText() + "\n" + weathers.remove(0)[1]) + "</font>");
                }
            }

            if (todayCal.get(Calendar.YEAR) == mCal.get(Calendar.YEAR) &&  todayCal.get(Calendar.MONTH) == mCal.get(Calendar.MONTH) &&
                sToday.equals(getItem(position))) { //오늘 day 텍스트 컬러 변경
                holder.tvItemGridView.setTextColor(getResources().getColor(R.color.black));
                holder.tvItemGridView.setText(Html.fromHtml("<b>" + holder.tvItemGridView.getText() + "</b>"));
            }

            switch (position % 7) {
                case 0: holder.tvItemGridView.setTextColor(getResources().getColor(R.color.sunday)); break;
                case 6:  holder.tvItemGridView.setTextColor(getResources().getColor(R.color.saturday)); break;
                default: holder.tvItemGridView.setTextColor(getResources().getColor(R.color.weekday));
            }

            final int day = getItem(position) == "" ? 0 : Integer.parseInt(getItem(position));
            if (0 < day && day<= mCal.getActualMaximum(Calendar.DAY_OF_MONTH) && bSchedule[day - 1]) {
                holder.tvItemGridView.setBackgroundColor(getResources().getColor(R.color.schedule));

                convertView.findViewById(R.id.tv_item_gridview).setOnClickListener(
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String[] yearAry = { "year", Integer.toString(year) };
                            String[] monthAry = { "month", month + 1 < 10 ? "0" + (month + 1) : Integer.toString(month + 1) };
                            String[] dayAry = { "day", day < 10 ? "0" + day : Integer.toString(day) };
                            ManagedActivity.getInstance().moveActivity(CalendarActivity.this, DayScheduleActivity.class, yearAry, monthAry, dayAry);
                        }
                    }
                );
            }

            return convertView;
        }
    }

    private class ViewHolder {
        TextView tvItemGridView;
    }

    // 네이버 날씨
    // https://github.com/xxria17/android
    // 네트워크 작업은 AsyncTask 를 사용해야 한다
    public class WeatherConnection extends AsyncTask<String, String, String> {
        // 백그라운드에서 작업하게 한다
        @Override
        protected String doInBackground(String... params) {
            // Jsoup을 이용한 날씨데이터 Pasing하기.
            try {
                //제주특별자치도 제주시 아라동 기준
                String path = "https://weather.naver.com/rgn/townWetr.nhn?naverRgnCd=14110630";
                Document document = Jsoup.connect(path).get();
                //Elements elements = document.select(".nm");
                Elements temperatures = document.select(".nm");
                Elements weathers = document.select(".info");

                ArrayList<String> temperaturesStr = new ArrayList<>(temperatures.eachText());
                ArrayList<String> weathersStr = new ArrayList<>(weathers.eachText());
                String result = "";
                for (int i = 0; i < weathers.size(); i += 2) {
                    result += temperaturesStr.get(i).substring(2, 7) + "\n" + weathersStr.get(i).split("\n")[0] + "\r";
                }

                return result;
            }
            catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }
}