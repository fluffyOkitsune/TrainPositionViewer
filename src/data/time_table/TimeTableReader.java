package data.time_table;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import data.line_data.LineData;
import data.line_data.LineData.Direction;

public class TimeTableReader {
    Scanner csvScanner;
    TimeTable[] timeTables = null;

    private static final int NUM_HEADER_LINE = 4;
    private static final int NUM_EMPTY_LINE = 1;

    public static TimeTable[] readTimeTable(LineData lineData, Direction direction, String timeTableCSVPath) throws FileNotFoundException {
        TimeTableReader timeTableReader = new TimeTableReader(timeTableCSVPath);

        timeTableReader.skipLines(NUM_HEADER_LINE);

        timeTableReader.readTrainID(lineData, direction);
        timeTableReader.readTrainType(lineData);
        timeTableReader.readTrainName(lineData);
        timeTableReader.readTrainNo(lineData);

        timeTableReader.skipLines(NUM_EMPTY_LINE);
        
        timeTableReader.readTrainTime(lineData, direction);

        timeTableReader.close();

        return timeTableReader.timeTables;
    }

    public TimeTableReader(String timeTableCSVPath) throws FileNotFoundException {
        csvScanner = new Scanner(new File(timeTableCSVPath), "UTF-8");
    }

    public void close() {
        csvScanner.close();
    }

    // --------------------------------------------------------------------------------
    // Readする項目
    // --------------------------------------------------------------------------------
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

        int numOperations = line.split(",", -1).length - 2;
        timeTables = new TimeTable[numOperations];

        // 列車番号
        if (!items[0].equals("列車番号")) {
            throw new RuntimeException("列車番号でない行 : " + items[0]);
        }

        for (int idx = 0; idx < timeTables.length; idx++) {
            // 最初の2要素は行ラベルなので除く。
            timeTables[idx] = new TimeTable(direction);
            timeTables[idx].trainID = items[idx + 2];
        }
    }

    // 列車種別
    private void readTrainType(LineData lineData) {
        if (!csvScanner.hasNext()) {
            throw new RuntimeException("行数が不足しています。");
        }

        final String line = csvScanner.nextLine();
        String[] items = line.split(",", -1);

        if (!items[0].equals("列車種別"))
            throw new RuntimeException("列車種別でない行 : " + items[0]);

        for (int idx = 0; idx < timeTables.length; idx++) {
            // 最初の2要素は行ラベルなので除く。
            timeTables[idx].trainType = items[idx + 2];
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

        for (int idx = 0; idx < timeTables.length; idx++) {
            // 最初の2要素は行ラベルなので除く。
            timeTables[idx].trainName = items[idx + 2];
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

        for (int idx = 0; idx < timeTables.length; idx++) {
            // 最初の2要素は行ラベルなので除く。
            timeTables[idx].trainNo = items[idx + 2];
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
            String staName = items[0];
            
            switch (items[1]) {
                case "着":
                    // 駅IDをインクリメント
                    if(!prevStaName.isEmpty()){
                        staID++;
                    }
                    // 着時刻を格納する
                    for (int idx = 0; idx < timeTables.length; idx++) {
                        timeTables[idx].setArrived(staID, items[idx + 2]);
                    }
                    break;

                case "発":
                    // 前の駅と違う場合は駅IDをインクリメント
                    if(!prevStaName.isEmpty() && !staName.equals(prevStaName)){
                        staID++;
                    }
                    // "発時刻を格納する
                    for (int idx = 0; idx < timeTables.length; idx++) {
                        timeTables[idx].setDeparture(staID, items[idx + 2]);
                    }
                    break;

                default:
                    break;
            }
            prevStaName = staName;
        }
        
        for (TimeTable tt : timeTables) {
            tt.packData(lineData.numStation(), direction);
        }
    }
}