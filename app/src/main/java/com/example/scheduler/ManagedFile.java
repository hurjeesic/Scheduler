package com.example.scheduler;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

// 안드로이드 파일 관리
// https://berabue.tistory.com/24
public class ManagedFile {
    private String FILE_PATH;
    private final String delimiter = "-- This is delimiter --";

    public ManagedFile(String path) {
        FILE_PATH = path + File.separator + "Data";

        //폴더가 존재하지 않으면 생성
        File file = new File(FILE_PATH);
        if (!file.exists()) {
            file.mkdirs();
        }
    }

    private boolean checkDate(String date) {
        if (date == null || date.length() != 8) {
            return false;
        }
        else {
            try {
                int year = Integer.parseInt(date.substring(0, 4)), month = Integer.parseInt(date.substring(4, 6)), day = Integer.parseInt(date.substring(6, 8));
                Calendar cal = Calendar.getInstance();
                cal.set(year, month - 1, 1);
                if (month < 1 || month > 12 || day < 1 || day > cal.getActualMaximum(Calendar.DATE)) {
                    Log.d("Information", "Invalid date range");
                    return false;
                }
            }
            catch (NumberFormatException ne) {
                Log.d("Information", "Invalid date format");

                return false;
            }
            catch (Exception e) {
                Log.d("Information", "Unknown Exception");

                return false;
            }
        }

        return true;
    }

    public ArrayList<String[]> allReadFile() {
        ArrayList<String[]> result = null;

        try {
            File[] scheduleFiles = new File(FILE_PATH).listFiles();
            if (scheduleFiles.length > 0) {
                result = new ArrayList<>();
                for (File scheduleFile : scheduleFiles) {
                    String fileName, loadPath;

                    fileName = scheduleFile.getName();
                    fileName = fileName.substring(0, fileName.length() - 4);

                    ArrayList<String[]> tempList = readFile(fileName);
                    for (String[] scheduleItem : tempList) {
                        String[] temp = { fileName, scheduleItem[0], scheduleItem[1], scheduleItem[2] };
                        result.add(temp);
                    }
                }
            }
        }
        catch (Exception e) {

        }

        return result;
    }

    //데이터 형식 : 제목, 내용, 태그
    //일정 목록을 보여줄 때 사용
    public ArrayList<String[]> readFile(String date) {
        ArrayList<String[]> result = null;

        try {
            if (checkDate(date)) {
                File f = new File(FILE_PATH + File.separator + date + ".txt");

                if (f.isFile()) {
                    FileInputStream fis = new FileInputStream(f);
                    BufferedReader br = new BufferedReader(new InputStreamReader(fis));

                    result = new ArrayList<>();
                    String data;

                    String[] tmpResult = new String[3];
                    ArrayList<String> midContent = new ArrayList<>();
                    boolean bTitle = true;
                    while ((data = br.readLine()) != null) {
                        if (bTitle) {
                            bTitle = false;
                            tmpResult[0] = data; // 제목 추가
                        }
                        else {
                            midContent.add(data);
                        }

                        if (data.equals(delimiter)) {
                            bTitle = true;

                            midContent.remove(midContent.size() - 1); //내용 중 맨 뒷줄은 구분자이므로 제거

                            tmpResult[2] = midContent.get(midContent.size() - 1);

                            midContent.remove(midContent.size() - 1); //구분자 다음은 태그이므로 제거

                            StringBuilder mid = new StringBuilder();
                            for (int i = 0; i < midContent.size(); i++) {
                                mid.append(midContent.get(i));
                                if (i != midContent.size() - 1) {
                                    mid.append("\n");
                                }
                            }

                            tmpResult[1] = mid.toString(); // 내용 부분 추가

                            result.add(tmpResult);
                            tmpResult = new String[3];
                            midContent.clear();
                        }
                    }

                    br.close();
                    fis.close();
                }
            }
        }
        catch (IOException ie) {
            Log.e("예외", "입출력 예외");
        }
        catch (Exception e) {
            Log.e("예외", "알 수 없는 예외");
        }

        return result;
    }

    public void deleteFile(String date) {
        File f = null;

        try {
            f = new File(FILE_PATH, date);

            if (f.exists()) {
                f.delete();
            }
        }
        catch (Exception e) {
            Log.e("예외", "알 수 없는 예외");
        }
    }

    //파일에 내용을 추가하는 함수
    public boolean writeData(String date, String[] data, boolean append) {
        boolean result = false;

        if (checkDate(date)) {
            try {
                //쓰기 위한 객체 생성
                File f = new File(FILE_PATH + File.separator + date + ".txt"); //실제 쓸거
                FileOutputStream fos = new FileOutputStream(f, append);

                //실제 쓰기 - byte 형식
                for (int i = 0; i < 3; i++) {
                    data[i] += '\n';
                    fos.write(data[i].getBytes());
                }

                //Schedule 마다 구분자를 표시
                String temp = delimiter + '\n';
                fos.write(temp.getBytes());

                fos.close();

                result = true;
            }
            catch (IOException ie) {
                Log.e("예외", "입출력 예외");
            }
            catch (Exception e) {
                Log.e("예외", "알 수 없는 예외");
            }
        }

        return result;
    }

    //파일 업데이트 시에 사용되는 함수
    private boolean writeData(String date, ArrayList<String[]> data) {
        boolean result = true;
        String[] temp;

        deleteFile(date);
        for (int i = 0; result && i < data.size(); i++) {
            temp = new String[3];
            for (int j = 0; j < 3; j++) {
                temp[j] = data.get(i)[j];
            }

            result = writeData(date, temp, false);
        }

        return result;
    }

    public boolean updateData(String date, String[] data, int index) {
        boolean result;
        ArrayList<String[]> totalData = new ArrayList<>(readFile(date));

        if (index >= totalData.size()) {
            result = false;
        }
        else {
            totalData.set(index, data);
            result = writeData(date, totalData);
        }

        return result;
    }
}