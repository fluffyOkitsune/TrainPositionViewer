package data.time_table;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import data.Time;
import data.line_data.LineData.Direction;

// 列車の運行時刻データ
public class TimeTable {
    public Direction direction;

    public String trainID;
    public String trainType;
    public String trainName;
    public String trainNo;

    private TimeData[] timeData;

    // 駅の着発時刻バッファ
    private Map<Integer, TimeData> mapTimeBuf;

    public TimeTable(Direction direction) {
        this.direction = direction;
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
    public TimeData getTimeData(int stationID){
        return timeData[stationID];
    }

    public int getTimeDataSize(){
        return timeData.length;
    }

    public Time getDepTime(int stationID){
        return timeData[stationID].getDepTime();
    }

    public Time getArrTime(int stationID){
        return timeData[stationID].getArrTime();
    }

    // --------------------------------------------------------------------------------
    @Override
    public String toString() {
        return "TimeTable [trainID=" + trainID + ", trainType=" + trainType + ", trainName=" + trainName + ", trainNo="
                + trainNo + ", timeTable=" + mapTimeBuf + "]";
    }

    public TimeData getMapTimeData(int staID) {
        return mapTimeBuf.get(staID);
    }

}