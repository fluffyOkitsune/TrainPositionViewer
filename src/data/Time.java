package data;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

// イミュータブルな時間乗法コンテナ
public class Time implements Comparable<Time> {
    public static final Time ZERO = new Time(0, 0, 0);
    public static final Time MAX_VALUE = new Time(23, 59, 59);
    
    public final int hour, min, sec;

    public Time(int hour, int min, int sec) {
        if (!(0 <= hour && hour <= 23)) {
            throw new IllegalArgumentException("hour: " + Integer.valueOf(hour).toString());
        }
        this.hour = hour;

        if (!(0 <= min && min <= 59)) {
            throw new IllegalArgumentException("min: " + Integer.valueOf(min).toString());
        }
        this.min = min;

        if (!(0 <= sec && sec <= 59)) {
            throw new IllegalArgumentException("min: " + Integer.valueOf(min).toString());
        }
        this.sec = sec;
    }

    // --------------------------------------------------------------------------------
    // データ作成
    // --------------------------------------------------------------------------------
    private static Pattern ptnHHMMSS = Pattern.compile("([0-2][0-9]):([0-5][0-9]):([0-5][0-9])");
    private static Pattern ptnHHMM = Pattern.compile("([0-2]?[0-9])([0-5][0-9])");

    public static Time parseTime(String str) {
        // HH:MM:SS 形式
        Matcher mtrHHMMSS = ptnHHMMSS.matcher(str.trim());
        if (mtrHHMMSS.matches()) {
            return new Time(Integer.parseInt(mtrHHMMSS.group(1)), Integer.parseInt(mtrHHMMSS.group(2)),
                    Integer.parseInt(mtrHHMMSS.group(3)));
        }

        // HHMM 形式
        Matcher mtrHHMM = ptnHHMM.matcher(str.trim());
        if (mtrHHMM.matches()) {
            return new Time(Integer.parseInt(mtrHHMM.group(1)), Integer.parseInt(mtrHHMM.group(2)), 0);
        }

        throw new IllegalArgumentException(str);
    }

    // --------------------------------------------------------------------------------
    // 計算
    // --------------------------------------------------------------------------------
    public Time add(Time t) {
        int carry;

        // 秒の計算
        int sec = (this.sec + t.sec);
        carry = (sec >= 60) ? 1 : 0;
        sec = (sec >= 60) ? sec - 60 : sec;

        // 分の計算
        int min = (this.min + t.min) + carry;
        carry = (min >= 60) ? 1 : 0;
        min = (min >= 60) ? min - 60 : min;

        // 時の計算
        int hour = (this.hour + t.hour) + carry;
        hour = (hour >= 24) ? hour - 24 : hour;

        return new Time(hour, min, sec);
    }

    public Time sub(Time t) {
        int borrow;

        // 秒の計算
        int sec = (this.sec - t.sec);
        borrow = (sec < 0) ? 1 : 0;
        sec = (sec < 0) ? sec + 60 : sec;

        // 分の計算
        int min = (this.min - t.min) - borrow;
        borrow = (min < 0) ? 1 : 0;
        min = (min < 0) ? min + 60 : min;

        // 時の計算
        int hour = (this.hour - t.hour) - borrow;
        hour = (hour < 0) ? hour + 24 : hour;

        return new Time(hour, min, sec);
    }

    public int calcElapsedMin(Time before, Time after) {
        return after.sub(before).convertToMin();
    }

    // --------------------------------------------------------------------------------
    @Override
    public int compareTo(Time t) {
        if (this.equals(t)) {
            return 0;
        }

        if (this.hour == t.hour) {

            if (this.min == t.min) {
                return Integer.valueOf(this.sec).compareTo(t.sec);
            } else {
                return Integer.valueOf(this.min).compareTo(t.min);
            }
        } else {
            return Integer.valueOf(this.hour).compareTo(t.hour);
        }
    }

    // --------------------------------------------------------------------------------
    public int convertToMin() {
        return this.min + this.hour * 60;
    }

    public int convertToSec() {
        return this.sec + this.min * 60 + this.hour * 3600;
    }

    // --------------------------------------------------------------------------------
    @Override
    public String toString() {
        return String.format("%02d:%02d:%02d", hour, min, sec);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + hour;
        result = prime * result + min;
        result = prime * result + sec;
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Time other = (Time) obj;
        if (hour != other.hour)
            return false;
        if (min != other.min)
            return false;
        if (sec != other.sec)
            return false;
        return true;
    }
}