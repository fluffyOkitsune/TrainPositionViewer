package data.time_table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import data.Time;
import data.line_data.LineData.Direction;
import data.train_data.TrainData;

// 列車の運行時刻データ
public class TimeTable {
    public String trainID;
    public String trainType;
    public String trainName;
    public String trainNo;

    private TimeData[] timeData;

    // 駅の着発時刻バッファ
    private Map<Integer, TimeData> mapTimeBuf;

    public TimeTable() {
        mapTimeBuf = new HashMap<>();
    }

    // --------------------------------------------------------------------------------
    // データ作成
    // --------------------------------------------------------------------------------
    // 着駅を追加する。
    public void setArrived(int staID, String strTime) {
        // IDの駅を列車が通らない場合は時刻データを追加しない
        if (strTime.equals("") || strTime.equals("||")) {
            return;
        }

        // 時間なし通過駅は時刻データを作成しない
        if (strTime.equals(" ﾚ") || strTime.equals("レ")) {
            return;
        }
        
        // 時刻データオブジェクトが登録されていない場合は新規に作成する
        if (!mapTimeBuf.containsKey(staID)) {
            mapTimeBuf.put(staID, new TimeData(staID));
        }

        mapTimeBuf.get(staID).setArrived(strTime);
    }

    // 発駅を追加する。
    public void setDeparture(int staID, String strTime) {
        // IDの駅を列車が通らない場合は時刻データを追加しない
        if (strTime.equals("") || strTime.equals("||")) {
            return;
        }

        // 時間なし通過駅は時刻データを作成しない
        if (strTime.equals(" ﾚ") || strTime.equals("レ")) {
            return;
        }

        // 時刻データオブジェクトが登録されていない場合は新規に作成する
        if (!mapTimeBuf.containsKey(staID)) {
            mapTimeBuf.put(staID, new TimeData(staID));
        }

        mapTimeBuf.get(staID).setDeparture(strTime);
    }

    // --------------------------------------------------------------------------------
    // データ整理
    // --------------------------------------------------------------------------------
    // バッファの一時データをインスタンスに書き込む
    public TimeTable packData(int numStation, Direction direction) {
        // 空の場合はすでに書き込み済み
        if (mapTimeBuf.isEmpty()) {
            return this;
        }

        // キー（駅ID）をソートする
        List<Integer> listStaIDs = new ArrayList<>(mapTimeBuf.keySet());
        listStaIDs.sort((e1, e2) -> {
            return Integer.compare(e1, e2);
        });

        // 時刻データを駅ID順に並べた配列にする
        timeData = new TimeData[listStaIDs.size()];
        for (int i = 0; i < timeData.length; i++) {
            timeData[i] = mapTimeBuf.get(listStaIDs.get(i));
        }

        for (TimeData t : timeData) {
            t.fixStaID(numStation, direction);
        }

        // バッファが不要になったのでクリアする
        mapTimeBuf.clear();

        return this;
    }

    // --------------------------------------------------------------------------------
    // 列車位置検索
    // --------------------------------------------------------------------------------
    // 現在時刻のとき、どの駅間を列車が走行しているか？
    // 列車は着時刻になった瞬間に次の駅に到着したとみなす。
    // 列車は発時刻になった瞬間に次の駅に向けて発車したとみなす。
    public TrainData createCurrTrainData(Direction direction, Time currentTime) {
        // 時刻が設定されていない
        if(timeData == null){
            return null;
        }

        final Time waitTime = new Time(0,1,0);
        // 始発駅に停車している場合
        // FIXME: とりあえず始発駅発車1分前から停車していることにする
        final int firstStaID = 0;
        if (currentTime.compareTo(timeData[firstStaID].getDepTime().sub(waitTime)) >= 0 && currentTime.compareTo(timeData[firstStaID].getDepTime()) < 0) {
            return new TrainData(direction, timeData[firstStaID], timeData[firstStaID], Time.ZERO, this);
        }

        // 終着駅に停車している場合
        // FIXME: とりあえず終着駅到着1分後まで停車していることにする)
        final int lastStaID = timeData.length - 1;
        if (currentTime.compareTo(timeData[lastStaID].getArrTime()) >= 0 && currentTime.compareTo(timeData[lastStaID].getArrTime().add(waitTime)) <= 0) {
            return new TrainData(direction, timeData[lastStaID], timeData[lastStaID], Time.ZERO, this);
        }

        for (int i = 0; i < lastStaID; i++) {
            if (currentTime.compareTo(timeData[i].getArrTime()) >= 0 && currentTime.compareTo(timeData[i].getDepTime()) < 0) {
                // 駅に停車している場合
                return new TrainData(direction, timeData[i], timeData[i], Time.ZERO, this);
            }
            if (currentTime.compareTo(timeData[i].getDepTime()) >= 0 && currentTime.compareTo(timeData[i + 1].getArrTime()) < 0) {
                // 駅間を走行している場合
                Time requiredTime = timeData[i + 1].getArrTime().sub(timeData[i].getDepTime());
                return new TrainData(direction, timeData[i], timeData[i + 1], requiredTime, this);
            }
        }

        // この列車は現在時刻では運行していない
        return null;
    }

    // --------------------------------------------------------------------------------
    @Override
    public String toString() {
        return "TimeTable [trainID=" + trainID + ", trainType=" + trainType + ", trainName=" + trainName + ", trainNo="
                + trainNo + ", timeTable=" + mapTimeBuf + "]";
    }

    public TimeData getTimeData(int staID) {
        return mapTimeBuf.get(staID);
    }

}