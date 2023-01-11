package data.time_table;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;
import java.awt.*;

import data.Time;
import data.line_data.LineData;
import data.line_data.LineData.Direction;

// 列車の運行時刻データ
public class TimeTable {
    private Direction direction;

    private String trainID;
    private String trainType;
    private String trainName;
    private String trainNo;
    public String note = "";

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
    public void setArrived(LineData lineData, int staID, String strTime) {
        // IDの駅を列車が通らない場合は時刻データを追加しない
        if (strTime.equals("") || strTime.equals("||")) {
            return;
        }

        // 時間なし通過駅は時刻データを作成しない
        if (strTime.equals(" ﾚ") || strTime.equals("レ")) {
            return;
        }

        // 時間あり通過駅は停車扱いとする
        if (strTime.charAt(strTime.length() - 1) == '?') {
            strTime = strTime.substring(0, strTime.length() - 1);
        }

        // 時刻データオブジェクトが登録されていない場合は新規に作成する
        if (!mapTimeBuf.containsKey(staID)) {
            mapTimeBuf.put(staID, new TimeData(lineData.getStationData(fixStaID(staID, lineData, this.direction))));
        }

        mapTimeBuf.get(staID).setArrived(strTime);
    }

    // 発駅を追加する。
    public void setDeparture(LineData lineData, int staID, String strTime) {
        // IDの駅を列車が通らない場合は時刻データを追加しない
        if (strTime.equals("") || strTime.equals("||")) {
            return;
        }

        // 時間なし通過駅は時刻データを作成しない
        if (strTime.equals(" ﾚ") || strTime.equals("レ")) {
            return;
        }

        // 時間あり通過駅は停車扱いとする
        if (strTime.charAt(strTime.length() - 1) == '?') {
            strTime = strTime.substring(0, strTime.length() - 1);
        }

        // 時刻データオブジェクトが登録されていない場合は新規に作成する
        if (!mapTimeBuf.containsKey(staID)) {
            mapTimeBuf.put(staID, new TimeData(lineData.getStationData(fixStaID(staID, lineData, this.direction))));
        }

        mapTimeBuf.get(staID).setDeparture(strTime);
    }

    private int fixStaID(int staID, LineData lineData, Direction direction) {
        // 上り時刻表は、駅一覧と逆向きの順番に記載されているので、行のIDを駅IDと対応付けるため逆さにする必要がある。
        if (direction == Direction.INBOUND) {
            staID = lineData.numStation() - 1 - staID;
        }
        return staID;
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

        // バッファが不要になったのでクリアする
        mapTimeBuf.clear();

        return this;
    }

    // --------------------------------------------------------------------------------
    // 直通運転
    // --------------------------------------------------------------------------------
    public TimeTable combine(TimeTable timeTable) {
        List<TimeData> prev = new ArrayList<>(Arrays.asList(this.timeData));
        List<TimeData> next = Arrays.asList(timeTable.timeData);
        prev.addAll(next);

        TimeTable ret = new TimeTable(direction);
        ret.copyAttrs(this);
        ret.timeData = prev.toArray(new TimeData[0]);
        return ret;
    }

    private void copyAttrs(TimeTable src) {
        direction = src.direction;
        trainID = src.trainID;
        trainName = src.trainName;
        trainNo = src.trainNo;
        trainType = src.trainType;
        note = src.note;
    }

    // --------------------------------------------------------------------------------
    // 最小所要時間の適用
    // --------------------------------------------------------------------------------
    // 最小所要時間を適用する所要時間の閾値
    private static final Time APPLY_MIN_REQ_TIME_TH = new Time(0, 2, 0);

    public void applyMinReqTime(Map<Point, Time> mapMinReqTime) {
        for (int i = 0; i < getTimeDataSize() - 1; i++) {
            // 現在駅発時刻と次駅着時刻が指定されている場合は最小所要時間を適用しない。
            // getArrTimeは着時刻が指定されているかわからない（指定なしの場合発時刻が返る）のでここでは使えない。
            if (getTimeData(i).getDepartureTime() != null && getTimeData(i + 1).getArrivedTime() != null) {
                continue;
            }

            Time reqTime = getReqTime(i);
            Point key = new Point(timeData[i].getStationData().getStationID(),
                    timeData[i + 1].getStationData().getStationID());
            Time minReqTime = mapMinReqTime.get(key);
            if (reqTime.sub(minReqTime).compareTo(APPLY_MIN_REQ_TIME_TH) > 0) {
                // 次駅着時刻がなければ指定する
                if (timeData[i + 1].getArrivedTime() == null) {
                    timeData[i + 1].setArrivedTime(timeData[i].getDepTime().add(minReqTime));
                }
            }
        }
    }

    public Time getReqTime(int idx) {
        if (this.getDepTime(idx) != null) {
            if (this.getArrTime(idx + 1) != null) {
                // Dep -> Arr
                return this.getDepTime(idx + 1).sub(this.getArrTime(idx));

            } else if (this.getDepTime(idx + 1) != null) {
                // Dep -> Dep
                return this.getDepTime(idx + 1).sub(this.getDepTime(idx));

            } else {
                // 算出不能
                return null;
            }
        } else if (this.getArrTime(idx) != null) {
            if (this.getArrTime(idx + 1) != null) {
                // Arr -> Arr
                return this.getArrTime(idx + 1).sub(this.getArrTime(idx));

            } else if (this.getDepTime(idx + 1) != null) {
                // Arr -> Dep
                return this.getArrTime(idx + 1).sub(this.getDepTime(idx));

            } else {
                // 算出不能
                return null;
            }
        } else {
            // 算出不能
            return null;
        }
    }

    // --------------------------------------------------------------------------------
    // インタフェース
    // --------------------------------------------------------------------------------
    public Direction getDirection() {
        return direction;
    }

    public String getTrainID() {
        return trainID;
    }

    void setTrainID(String trainID) {
        this.trainID = trainID;
    }

    public String getTrainType() {
        return trainType;
    }

    void setTrainType(String trainType) {
        this.trainType = trainType;
    }

    public String getTrainName() {
        return trainName;
    }

    void setTrainName(String trainName) {
        this.trainName = trainName;
    }

    public String getTrainNo() {
        return trainNo;
    }

    void setTrainNo(String trainNo) {
        this.trainNo = trainNo;
    }

    public TimeData getTimeData(int stationID) {
        return timeData[stationID];
    }

    public int getTimeDataSize() {
        // 線内に２つ以上の停車駅がない場合（始発の次の停車駅がこの線の外の駅の場合など）はnullになる。
        if (timeData == null) {
            return 0;
        } else {
            return timeData.length;
        }
    }

    public Time getDepTime(int idx) {
        return timeData[idx].getDepTime();
    }

    public Time getArrTime(int idx) {
        return timeData[idx].getArrTime();
    }

    // 停車駅の駅データを返す
    public StationData[] getStopStations() {
        Stream<StationData> stream = Arrays.stream(this.timeData).map(e -> {
            return e.getStationData();
        });
        return stream.toArray(StationData[]::new);
    }

    // 始発駅のデータを返す
    public StationData getFirstStation() {
        if (timeData == null) {
            return null;
        } else {
            return timeData[0].getStationData();
        }
    }

    // 行先の駅データを返す
    public StationData getTerminalStation() {
        if (timeData == null) {
            return null;
        } else {
            return timeData[timeData.length - 1].getStationData();
        }
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