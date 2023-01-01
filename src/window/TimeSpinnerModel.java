package window;
import javax.swing.AbstractSpinnerModel;

import data.Time;

// --------------------------------------------------------------------------------
// Time指定用スピナーモデル
// --------------------------------------------------------------------------------
public class TimeSpinnerModel extends AbstractSpinnerModel {
    public enum AddUnit {
        HOUR, MIN, SEC;
    }

    private Time value;
    private AddUnit addUnit = AddUnit.MIN;

    public TimeSpinnerModel() {
        this.value = new Time(0, 0, 0);
    }

    public void setAddUnit(AddUnit addUnit) {
        this.addUnit = addUnit;
    }

    @Override
    public Object getValue() {
        return this.value;
    }

    @Override
    /**
     * @exception IllegalArgumentException : Time型以外をセットしようとした場合
     */
    public void setValue(Object value) {
        if (!(value instanceof Time time)) {
            throw new IllegalArgumentException("illegal value");
        }
        if (!time.equals(this.value)) {
            this.value = time;
            fireStateChanged();
        }
    }

    @Override
    public Object getNextValue() {
        int hour = value.hour;
        int min = value.min;
        int sec = value.sec;

        switch (addUnit) {
            case HOUR:
                hour++;
                break;
            case MIN:
                min++;
                break;
            case SEC:
                sec++;
                break;
            default:
                break;
        }

        if (sec == 60) {
            sec = 0;
            min++;
        }
        if (min == 60) {
            min = 0;
            hour++;
        }
        if (hour == 24) {
            hour = 0;
        }
        return new Time(hour, min, sec);
    }

    @Override
    public Object getPreviousValue() {
        int hour = value.hour;
        int min = value.min;
        int sec = value.sec;

        switch (addUnit) {
            case HOUR:
                hour--;
                break;
            case MIN:
                min--;
                break;
            case SEC:
                sec--;
                break;
            default:
                break;
        }

        if (sec == -1) {
            sec = 59;
            min--;
        }
        if (min == -1) {
            min = 59;
            hour--;
        }
        if (hour == -1) {
            hour = 23;
        }
        return new Time(hour, min, sec);
    }
}
