package data.train_data;

import data.Time;
import data.line_data.LineData;
import data.line_data.LineData.Direction;
import data.time_table.StationData;
import data.time_table.TimeData;
import data.time_table.TimeTable;

public class TrainData {
    public static int DIRECTION_OUTBOUND = 0;
    public static int DIRECTION_INBOUND = 1;

    public String trainID;
    public String trainType;
    public String trainName;
    public String trainNo;
    public Time requiredTime;

    private TimeData departed, destination;
    Direction direction;

    public static final int NONE = -1;

    public TrainData(Direction direction, TimeData departed, TimeData destination, Time requiredTime, TimeTable timeTable) {
        this.direction = direction;
        this.departed = departed;
        this.destination = destination;
        this.requiredTime = requiredTime;

        // 列車属性を時刻表からコピーする
        this.trainID = timeTable.trainID;
        this.trainType = timeTable.trainType;
        this.trainName = timeTable.trainName;
        this.trainNo = timeTable.trainNo;
    }

    // --------------------------------------------------------------------------------
    // 現在時刻の列車位置
    // --------------------------------------------------------------------------------
    public float calcPos(LineData lineData, Time currentTime) {
        int depStaID = getDepartedStaID();
        int dstStaID = getDitinationStaID();

        StationData[] stationData = lineData.getStationData();
        float trainPosCurr;

        if (departed.getArrTime() == destination.getDepTime()) {
            // 駅に停車中は出発駅と同じ位置
            trainPosCurr = stationData[depStaID].getDistProportion();
        } else {
            int secTimeElapsed = currentTime.sub(departed.getDepTime()).convertToSec();
            int secRequiredTime = this.requiredTime.convertToSec();

            float trainPosDep = stationData[depStaID].getDistProportion();
            float trainPosDst = stationData[dstStaID].getDistProportion();

            // 秒による駅間位置の補正を加えた列車位置を計算する
            if (secRequiredTime > 0) {
                trainPosCurr = trainPosDep;
                switch (direction) {
                    case OUTBOUND:
                         // 下り列車は向きはそのまま
                        trainPosCurr += (secTimeElapsed * (trainPosDst - trainPosDep) / (secRequiredTime));
                        break;
                    case INBOUND:
                        // 上り列車は向きを反転する
                        trainPosCurr -= (secTimeElapsed * (trainPosDep - trainPosDst) / (secRequiredTime));
                        break;
                }
            } else {
                trainPosCurr = trainPosDep;
            }
        }
        return trainPosCurr;
    }

    private int calcTimeSub(int before, int after) {
        int beforeH = before / 100;
        int beforeM = before % 100;
        int afterH = after / 100;
        int afterM = after % 100;
        int resH, resM;

        if (beforeM > afterM) {
            // 繰り下がり
            afterH--;
            resM = afterM + 60 - beforeM;
        } else {
            resM = afterM - beforeM;
        }
        resH = afterH - beforeH;
        return (60 * resH) + resM;
    }

    // --------------------------------------------------------------------------------
    public int getDepartedStaID() {
        return departed.getStaID();
    }

    public int getDitinationStaID() {
        return destination.getStaID();
    }

    public Direction getDirection() {
        return direction;
    }

    // --------------------------------------------------------------------------------
    @Override
    public String toString() {
        return "TrainData [trainID=" + trainID + ", trainType=" + trainType + ", trainName=" + trainName + ", trainNo="
                + trainNo + ", requiredTime=" + requiredTime + ", departed=" + departed + ", destination=" + destination
                + ", direction=" + direction + "]";
    }

}
