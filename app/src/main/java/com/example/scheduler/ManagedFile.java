package com.example.scheduler;

import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class ManagedFile {

    private final String FILE_PATH = ManagedFile.class.getResource("").getPath() + "Data";
    private final String delimiter = "-- This is delimiter --";

    private static ManagedFile instance = new ManagedFile();

    public static ManagedFile getInstance() {
        return instance;
    }

    private ManagedFile() {

    }

    //데이터 형식 : 제목, 내용, 태그
    //일정 목록을 보여줄 때 사용
    public ArrayList<String[]> readFile(String date) {
        File f = null;
        FileReader fr = null;
        BufferedReader br = null;
        ArrayList<String[]> result = null;

        try {
            f = new File(FILE_PATH, date);

            if (f.isFile()) {
                fr = new FileReader(f);
                br = new BufferedReader(fr);

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
            }
            else {
                return null;
            }
        }
        catch (IOException ie) {
            Log.e("예외", "입출력 예외");
        }
        catch (Exception e) {
            Log.e("예외", "알 수 없는 예외");
        }
        finally {
            if (br != null) {
                try {
                    br.close();
                }
                catch (IOException ie) {
                    Log.e("예외", "BufferedReader 종료 예외");
                }
            }

            if (fr != null) {
                try {
                    fr.close();
                }
                catch (IOException ie) {
                    Log.e("예외", "FileReader 종료 예외");
                }
            }
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
    public boolean writeData(String date, String[] data) {
        boolean result = true;

        File f = null;
        FileWriter fw = null;
        BufferedWriter bw = null;

        try {
            f = new File(FILE_PATH, date);

            if (!f.exists()) {
                f.createNewFile();
            }

            fw = new FileWriter(f, true);
            bw = new BufferedWriter(fw);

            for (int i = 0; i < 3; i++) {
                bw.write(data[i]);
                bw.newLine();
            }

            bw.write(delimiter);
            bw.newLine();

            bw.flush();
        }
        catch (IOException ie) {
            Log.e("예외", "입출력 예외");

            result = false;
        }
        catch (Exception e) {
            Log.e("예외", "알 수 없는 예외");

            result = false;
        }
        finally
        {
            if (bw != null) {
                try {
                    bw.close();
                }
                catch (IOException ie) {
                    Log.e("예외", "BufferedReader 종료 예외");
                }
            }

            if (fw != null) {
                try {
                    fw.close();
                }
                catch (IOException ie) {
                    Log.e("예외", "FileReader 종료 예외");
                }
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

            result = writeData(date, temp);
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