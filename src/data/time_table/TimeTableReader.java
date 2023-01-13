package data.time_table;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.Vector;

import data.line_data.LineData;
import data.line_data.LineData.Direction;

public class TimeTableReader {
    Scanner csvScanner;
    ArrayList<TimeTable> lTimeTables;

    private static final int NUM_HEADER_LINE = 4;
    private static final int NUM_EMPTY_LINE = 1;

    public static TimeTable[] readTimeTable(LineData lineData, Direction direction, String timeTableCSVPath)
            throws FileNotFoundException {
        TimeTableReader timeTableReader = new TimeTableReader(timeTableCSVPath);

        timeTableReader.skipLines(NUM_HEADER_LINE);

        timeTableReader.readTrainID(lineData, direction);
        timeTableReader.readTrainType(lineData);
        timeTableReader.readTrainName(lineData);
        timeTableReader.readTrainNo(lineData);

        timeTableReader.skipLines(NUM_EMPTY_LINE);

        timeTableReader.readTrainTime(lineData, direction);

        timeTableReader.close();

        Vector<TimeTable> buf = new Vector<>();
        for (TimeTable timeTable : timeTableReader.lTimeTables) {
            timeTable.packData();
            for (TimeTable newTimeTable : timeTable.separateDetour()) {
                buf.add(newTimeTable);
            }
        }
        timeTableReader.lTimeTables.clear();

        return buf.toArray(new TimeTable[0]);
    }

    public TimeTableReader(String timeTableCSVPath) throws FileNotFoundException {
        csvScanner = new Scanner(new File(timeTableCSVPath), "UTF-8");
        lTimeTables = new ArrayList<>();
    }

    public void close() {
        csvScanner.close();
    }

    // --------------------------------------------------------------------------------
    // Readする項目
    // --------------------------------------------------------------------------------
    private static final int NUM_HEADER_COL = 2;

    private void skipLines(int numLineSkip) {
        // ヘッダー部分は読み飛ばすだけ
        for (int i = 0; i < numLineSkip; i++) {
            if (!csvScanner.hasNext()) {
                throw new RuntimeException("行数が不足しています。");
            }
            csvScanner.nextLine();
        }
    }

    // 列車番号
    private void readTrainID(LineData lineData, Direction direction) {
        if (!csvScanner.hasNext()) {
            throw new RuntimeException("行数が不足しています。");
        }

        final String line = csvScanner.nextLine();
        String[] items = line.split(",", -1);

        if (!items[0].equals("列車番号")) {
            throw new RuntimeException("列車番号でない行 : " + items[0]);
        }

        // 駅名と発着を除去する
        items = Arrays.copyOfRange(items, NUM_HEADER_COL, items.length);

        for (String item : items) {
            TimeTable timeTable = new TimeTable(direction);
            timeTable.setTrainID(item);
            lTimeTables.add(timeTable);
        }
    }

    // 列車種別
    private void readTrainType(LineData lineData) {
        if (!csvScanner.hasNext()) {
            throw new RuntimeException("行数が不足しています。");
        }

        final String line = csvScanner.nextLine();
        String[] items = line.split(",", -1);

        if (!items[0].equals("列車種別")) {
            throw new RuntimeException("列車種別でない行 : " + items[0]);
        }

        // 駅名と発着を除去する
        items = Arrays.copyOfRange(items, NUM_HEADER_COL, items.length);

        int idx = 0;
        for (TimeTable timeTable : lTimeTables) {
            timeTable.setTrainType(items[idx]);
            idx++;
        }
    }

    // 列車名
    private void readTrainName(LineData lineData) {
        if (!csvScanner.hasNext()) {
            throw new RuntimeException("行数が不足しています。");
        }

        final String line = csvScanner.nextLine();
        String[] items = line.split(",", -1);

        if (!items[0].equals("列車名"))
            throw new RuntimeException("列車名でない行 : " + items[0]);

        // 駅名と発着を除去する
        items = Arrays.copyOfRange(items, NUM_HEADER_COL, items.length);

        int idx = 0;
        for (TimeTable timeTable : lTimeTables) {
            timeTable.setTrainName(items[idx]);
            idx++;
        }
    }

    // 号数
    private void readTrainNo(LineData lineData) {
        if (!csvScanner.hasNext()) {
            throw new RuntimeException("行数が不足しています。");
        }

        final String line = csvScanner.nextLine();
        String[] items = line.split(",", -1);

        if (!items[0].equals("号数"))
            throw new RuntimeException("号数でない行 : " + items[0]);

        // 駅名と発着を除去する
        items = Arrays.copyOfRange(items, NUM_HEADER_COL, items.length);

        int idx = 0;
        for (TimeTable timeTable : lTimeTables) {
            timeTable.setTrainNo(items[idx]);
            idx++;
        }
    }

    // --------------------------------------------------------------------------------
    // 時刻
    // --------------------------------------------------------------------------------
    private void readTrainTime(LineData lineData, Direction direction) {
        int staID = 0;
        String prevStaName = "";

        while (csvScanner.hasNext()) {
            String line = csvScanner.nextLine();
            String[] items = line.split(",", -1);
            
            // 駅名を記憶する
            String staName = items[0];
            // 発着
            String depArr = items[1];

            // 駅名と発着を除去する
            items = Arrays.copyOfRange(items, NUM_HEADER_COL, items.length);

            int idx = 0;
            switch (depArr) {
                case "着":
                    // 駅IDをインクリメント
                    if (!prevStaName.isEmpty()) {
                        staID++;
                    }
                    // 着時刻を格納する
                    for (TimeTable timeTable : lTimeTables) {
                        timeTable.setArrived(lineData, staID, items[idx]);
                        idx++;
                    }
                    break;

                case "発":
                    // 前の駅と違う場合は駅IDをインクリメント
                    if (!prevStaName.isEmpty() && !staName.equals(prevStaName)) {
                        staID++;
                    }
                    // "発時刻を格納する
                    for (TimeTable timeTable : lTimeTables) {
                        timeTable.setDeparture(lineData, staID, items[idx]);
                        idx++;
                    }
                    break;

                default:
                    // 着でも発でもない場合は備考欄
                    for (TimeTable timeTable : lTimeTables) {
                        timeTable.setNote(items[idx]);
                        idx++;
                    }
                    break;
            }
            prevStaName = staName;
        }
    }
}