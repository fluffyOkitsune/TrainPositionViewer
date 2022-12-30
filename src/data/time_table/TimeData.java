package data.time_table;

import data.Time;
import data.line_data.LineData.Direction;

// 時刻表の時刻
public class TimeData {
    private int staID;

    public static final byte TYPE_BLANK = 0;
    public static final byte TYPE_STOP = 1;
    public static final byte TYPE_PASS = 2;
    public static final byte TYPE_DETOUR = 3;
    private byte type;

    public static final int TIME_NOT_DEFINED = -1;
    private Time arrivedTime;
    private Time departureTime;

    TimeData(int staID) {
        this.staID = staID;
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

    // 駅IDがStationDataの駅IDと一致するように修正する
    public TimeData fixStaID(int numStation, Direction direction) {
        // 上りの場合は駅IDをStationDataのIDに対応するように変換する
        if (direction == Direction.INBOUND) {
            staID = numStation - 1 - staID;
        }
        return this;
    }

    // --------------------------------------------------------------------------------
    @Override
    public String toString() {
        return "Time [type=" + type + ", arrived=" + arrivedTime + ", departure=" + departureTime + "]";
    }

    // --------------------------------------------------------------------------------
    public int getStaID() {
        return staID;
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

}