package data.time_table;

import data.Time;
import data.line_data.LineData.Direction;

// 時刻表の時刻
public class TimeData {
    private StationData stationData;

    public static final byte TYPE_BLANK = 0;
    public static final byte TYPE_STOP = 1;
    public static final byte TYPE_PASS = 2;
    public static final byte TYPE_DETOUR = 3;
    private byte type;

    private Time arrivedTime;
    private Time departureTime;
    private Direction direction;

    TimeData(StationData stationData, Direction direction) {
        this.stationData = stationData;
        this.direction = direction;

        this.type = TYPE_BLANK;
        setArrivedTime(null);
        setDepartureTime(null);
    }

    // --------------------------------------------------------------------------------
    // データ作成
    // --------------------------------------------------------------------------------
    public void setArrived(String strArrived) {
        if (this.type == TYPE_BLANK) {
            switch (strArrived) {
                case "": // 空欄
                    break;

                case "||":
                    this.type = TYPE_DETOUR;
                    break;

                case " ﾚ":
                    this.type = TYPE_PASS;
                    break;

                default: // 時刻
                    this.type = TYPE_STOP;
                    setArrivedTime(Time.parseTime(strArrived));
                    break;
            }
        }
    }

    public void setDeparture(String strDeparture) {
        if (this.type == TYPE_BLANK) {
            switch (strDeparture) {
                case "":
                    break;

                case "||":
                    this.type = TYPE_DETOUR;
                    break;

                case " ﾚ":
                    this.type = TYPE_PASS;
                    break;

                default: // 時刻
                    this.type = TYPE_STOP;
                    setDepartureTime(Time.parseTime(strDeparture));
                    break;
            }
        } else if (this.type == TYPE_STOP) {
            // すでに着時刻が指定されている場合
            setDepartureTime(Time.parseTime(strDeparture));
        }
    }

    // --------------------------------------------------------------------------------
    public StationData getStationData() {
        return stationData;
    }

    public Direction getDirection() {
        return direction;
    }

    public byte getType() {
        return type;
    }

    public Time getArrTime() {
        // 着時刻が定義されていない場合（主要でない折り返し駅など）
        if (arrivedTime == null) {
            return departureTime;
        }
        return arrivedTime;
    }

    public Time getDepTime() {
        // 発時刻が定義されていない場合（都心よりの降車客が多い駅など）
        if (departureTime == null) {
            return arrivedTime;
        }
        return departureTime;
    }

    // --------------------------------------------------------------------------------
    // Package Private
    Time getArrivedTime() {
        return arrivedTime;
    }

    void setArrivedTime(Time arrivedTime) {
        this.arrivedTime = arrivedTime;
    }

    Time getDepartureTime() {
        return departureTime;
    }

    void setDepartureTime(Time departureTime) {
        this.departureTime = departureTime;
    }

    // --------------------------------------------------------------------------------
    @Override
    public String toString() {
        if (type == TYPE_DETOUR) {
            return "経由なし";
        } else if (type == TYPE_PASS) {
            return "通過";
        } else {
            return stationData.getName() + " : " + arrivedTime + "着 / " + departureTime + "発";
        }
    }
}