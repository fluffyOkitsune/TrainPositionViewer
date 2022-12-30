package draw;

import java.awt.*;
import java.awt.event.*;
import java.util.Map;

import data.Time;
import data.line_data.LineData;
import data.line_data.LineData.Direction;
import data.time_table.StationData;
import data.time_table.TimeData;
import data.time_table.TimeTable;
import data.train_data.TrainData;

public class Train {
    public static final int NONE = -1;

    public TrainData trainData;
    private LineData lineData;

    // 列車の状態
    private TimeData departed, destination;
    private Time requiredTime;

    public Train(LineData lineData, TrainData trainData) {
        this.trainData = trainData;
        this.lineData = lineData;

        this.image = lineData.getIconImg(trainData);
    }

    public void update(Time currentTime) {
        // 位置更新
        updateLocation(currentTime);

        if (onDuty) {
            // 描画領域
            rect = updateIconRect(calcTrainPos(currentTime));
        } else {
            rect = null;
        }
    }

    // --------------------------------------------------------------------------------
    // 列車位置検索
    // --------------------------------------------------------------------------------
    // 現在時刻のとき、どの駅間を列車が走行しているか？
    // 列車は着時刻になった瞬間に次の駅に到着したとみなす。
    // 列車は発時刻になった瞬間に次の駅に向けて発車したとみなす。

    // 現在時刻で、この列車はどこを走行しているかを計算する
    public void updateLocation(Time currentTime) {
        // データがない場合は計算不能のため無視する
        if (this.trainData.getTimeTable().getTimeDataSize() == 0) {
            onDuty = false;
            return;
        }

        if (waintingForDeparture(currentTime)) {
            return;
        }
        if (arrivedTerminal(currentTime)) {
            return;
        }
        for (int i = 0; i < trainData.getTimeTable().getTimeDataSize(); i++) {
            if (stoppingAtStation(currentTime, i)) {
                return;
            }
            if (locomotingBetweenStations(currentTime, i)) {
                return;
            }
        }

        // この列車は現在時刻では運行していない
        onDuty = false;
    }

    // 始発駅で停車し、出発を待っている場合
    private boolean waintingForDeparture(Time currentTime) {
        // FIXME: とりあえず始発駅発車1分前から停車していることにする
        final Time waitTime = new Time(0, 1, 0);

        final int firstStaID = 0;
        if (currentTime.compareTo(trainData.getTimeTable().getDepTime(firstStaID).sub(waitTime)) >= 0
                && currentTime.compareTo(trainData.getTimeTable().getDepTime(firstStaID)) < 0) {
            departed = trainData.getTimeTable().getTimeData(firstStaID);
            destination = trainData.getTimeTable().getTimeData(firstStaID);
            requiredTime = Time.ZERO;
            onDuty = true;

            return true;
        } else {
            return false;
        }
    }

    // 終着駅に到着し、停車している場合
    private boolean arrivedTerminal(Time currentTime) {
        // FIXME: とりあえず終着駅到着1分後まで停車していることにする)
        final Time waitTime = new Time(0, 1, 0);

        final int lastStaID = trainData.getTimeTable().getTimeDataSize() - 1;
        if (currentTime.compareTo(trainData.getTimeTable().getArrTime(lastStaID)) >= 0
                && currentTime.compareTo(trainData.getTimeTable().getArrTime(lastStaID).add(waitTime)) <= 0) {
            departed = trainData.getTimeTable().getTimeData(lastStaID);
            destination = trainData.getTimeTable().getTimeData(lastStaID);
            requiredTime = Time.ZERO;
            onDuty = true;

            return true;
        } else {
            return false;
        }
    }

    // 駅に停車している場合
    private boolean stoppingAtStation(Time currentTime, int staID) {
        if (staID < 0) {
            return false;
        }
        if (staID >= trainData.getTimeTable().getTimeDataSize()) {
            return false;
        }

        if (currentTime.compareTo(trainData.getTimeTable().getArrTime(staID)) >= 0
                && currentTime.compareTo(trainData.getTimeTable().getDepTime(staID)) < 0) {
            departed = trainData.getTimeTable().getTimeData(staID);
            destination = trainData.getTimeTable().getTimeData(staID);
            requiredTime = Time.ZERO;
            onDuty = true;

            return true;
        } else {
            return false;
        }
    }

    // 駅間を走行している場合
    private boolean locomotingBetweenStations(Time currentTime, int staID) {
        if (staID < 0) {
            return false;
        }
        if (staID >= trainData.getTimeTable().getTimeDataSize() - 1) {
            return false;
        }

        if (currentTime.compareTo(trainData.getTimeTable().getDepTime(staID)) >= 0
                && currentTime.compareTo(trainData.getTimeTable().getArrTime(staID + 1)) < 0) {
            departed = trainData.getTimeTable().getTimeData(staID);
            destination = trainData.getTimeTable().getTimeData(staID + 1);
            requiredTime = trainData.getTimeTable().getArrTime(staID + 1)
                    .sub(trainData.getTimeTable().getDepTime(staID));
            onDuty = true;

            return true;
        } else {
            return false;
        }
    }

    // --------------------------------------------------------------------------------
    // 最小所要時間の適用
    // --------------------------------------------------------------------------------
    public void applyMinReqTime(Map<Point, Time> minReqTime) {
        TimeTable timeTable = trainData.getTimeTable();
        timeTable.applyMinReqTime(minReqTime);
    }

    // --------------------------------------------------------------------------------
    // 現在時刻の列車位置
    // --------------------------------------------------------------------------------
    private float calcPos(Time currentTime) {
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
                switch (getDirection()) {
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

    // --------------------------------------------------------------------------------
    public int getDepartedStaID() {
        if (departed == null) {
            return -1;
        } else {
            return departed.getStaID();
        }
    }

    public int getDitinationStaID() {
        if (destination == null) {
            return -1;
        } else {
            return destination.getStaID();
        }
    }

    public Direction getDirection() {
        return trainData.getTimeTable().direction;
    }

    public String getTerminalName() {
        if (!onDuty) {
            return "";
        } else {
            int terminalStaID = trainData.getTimeTable().getTerminalStaID();
            return lineData.getStationName(terminalStaID);
        }
    }

    // --------------------------------------------------------------------------------
    // 列車の描画
    // --------------------------------------------------------------------------------
    // 描画関係
    public boolean onDuty;
    private Rectangle rect;
    private Image image;

    // 描画する列車の位置を計算する
    private Point calcTrainPos(Time currentTime) {
        final float pos = calcPos(currentTime);
        return lineData.calcPositionOnLinePath(pos, this.getDirection());
    }

    // 描画する列車の領域を計算する
    private Rectangle updateIconRect(Point pos) {
        if (image == null) {
            return new Rectangle(pos.x, pos.y, 0, 0);
        } else {
            final int width = image.getWidth(null);
            final int height = image.getHeight(null);
            return new Rectangle(pos.x - width / 2, pos.y - height / 2, width, height);
        }
    }

    public void draw(Graphics g) {
        if (onDuty) {
            g.drawImage(image, rect.getLocation().x, rect.getLocation().y, null);
        }
    }

    public Color getTypeColor() {
        return lineData.getTypeColor(this.trainData);
    }

    // --------------------------------------------------------------------------------
    // マウスイベント
    // --------------------------------------------------------------------------------
    public boolean getOnMouse(MouseEvent e) {
        if (rect == null) {
            return false;
        } else {
            return rect.contains(new Point(e.getX(), e.getY()));
        }
    }

    public Rectangle getRect() {
        return rect;
    }

    // --------------------------------------------------------------------------------
    @Override
    public String toString() {
        return "列車番号: " + trainData.getTimeTable().trainID;
    }

}
