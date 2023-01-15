package draw;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
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
    private boolean stoppingAtStation(Time currentTime, int stopID) {
        if (stopID < 0) {
            return false;
        }
        if (stopID >= trainData.getTimeTable().getTimeDataSize()) {
            return false;
        }

        if (currentTime.compareTo(trainData.getTimeTable().getArrTime(stopID)) >= 0
                && currentTime.compareTo(trainData.getTimeTable().getDepTime(stopID)) < 0) {
            departed = trainData.getTimeTable().getTimeData(stopID);
            destination = trainData.getTimeTable().getTimeData(stopID);
            requiredTime = Time.ZERO;
            onDuty = true;

            return true;
        } else {
            return false;
        }
    }

    // 駅間を走行している場合
    private boolean locomotingBetweenStations(Time currentTime, int stopID) {
        if (stopID < 0) {
            return false;
        }
        if (stopID >= trainData.getTimeTable().getTimeDataSize() - 1) {
            return false;
        }

        Time departure = trainData.getTimeTable().getDepTime(stopID);
        Time arivered = trainData.getTimeTable().getArrTime(stopID + 1);
        if (currentTime.compareTo(departure) >= 0 && currentTime.compareTo(arivered) < 0) {
            this.departed = trainData.getTimeTable().getTimeData(stopID);
            this.destination = trainData.getTimeTable().getTimeData(stopID + 1);

            this.requiredTime = trainData.getTimeTable().getArrTime(stopID + 1)
                    .sub(trainData.getTimeTable().getDepTime(stopID));

            this.onDuty = true;

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
        float trainPosCurr;

        if (departed.getArrTime() == destination.getDepTime()) {
            // 駅に停車中は出発駅と同じ位置
            trainPosCurr = getDepartedStation().getDistProportion();

        } else {
            int secTimeElapsed = currentTime.sub(departed.getDepTime()).convertToSec();
            int secRequiredTime = this.requiredTime.convertToSec();

            float trainPosDep = getDepartedStation().getDistProportion();
            float trainPosDst = getDestinationStation().getDistProportion();

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

    // 列車が駅をすでに過ぎた後か？
    public boolean hasPassedStation(StationData stationData) {
        // 上りの場合はstaIDが小さくなる方向に列車が進むことになる。
        if (this.getDirection() == Direction.OUTBOUND) {
            return stationData.getStationID() > this.getDepartedStation().getStationID();
        } else {
            return stationData.getStationID() < this.getDepartedStation().getStationID();
        }
    }

    // --------------------------------------------------------------------------------
    public StationData getDepartedStation() {
        if (departed == null) {
            return null;
        } else {
            return departed.getStationData();
        }
    }

    public StationData getDestinationStation() {
        if (destination == null) {
            return null;
        } else {
            return destination.getStationData();
        }
    }

    public Direction getDirection() {
        return trainData.getTimeTable().getDirection();
    }

    public StationData getFirstStation() {
        return trainData.getTimeTable().getFirstStation();
    }

    public StationData getTerminalStation() {
        return trainData.getTimeTable().getTerminalStation();
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
        return getDepartedStation().getLineData().calcPosOnLinePath(pos, this.getDirection());
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

    private final AlphaComposite ALHA_COMP_NONE = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f);
    private final AlphaComposite ALHA_COMP_HARF = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f);

    private static final Color COLOR_TRAINSPARENT = new Color(0, 0, 0, 0);

    public void draw(Graphics2D g, int operationDateMask) {
        if (onDuty) {
            if ((trainData.getOperationDate() & operationDateMask) > 0) {
                // 運転日の場合の表示
                final boolean isExtra = trainData.getOperationDate() == LineData.OPR_DATE_EXTRA;
                if (isExtra) {
                    g.setComposite(ALHA_COMP_HARF);
                } else {
                    g.setComposite(ALHA_COMP_NONE);
                }
                g.drawImage(image, rect.getLocation().x, rect.getLocation().y, null);

                // 描画が終わったら元の設定に戻す
                g.setComposite(ALHA_COMP_NONE);
            } else {
                // 運転日以外の場合の表示
                Image img = getEdgeOfIcon(image, Color.GRAY);
                g.drawImage(img, rect.getLocation().x, rect.getLocation().y, null);
                img.flush();
            }
        }
    }

    private static Image getEdgeOfIcon(Image image, Color edgeColor) {
        // 外側部分を取得
        BufferedImage imgOutside = new BufferedImage(image.getWidth(null), image.getHeight(null),
                BufferedImage.TYPE_4BYTE_ABGR);
        Graphics imgOutsideG = imgOutside.getGraphics();
        imgOutsideG.drawImage(image, 0, 0, null);
        imgOutsideG.dispose();

        // 内側部分を取得
        BufferedImage imgInside = new BufferedImage(image.getWidth(null), image.getHeight(null),
                BufferedImage.TYPE_4BYTE_ABGR);
        int edgeSize = 2;

        Graphics imgInsideG = imgInside.getGraphics();
        imgInsideG.drawImage(image, edgeSize, edgeSize, image.getWidth(null) - 2 * edgeSize,
                image.getHeight(null) - 2 * edgeSize, null);
        imgInsideG.dispose();

        // 描画
        for (int x = 0; x < imgOutside.getTileWidth(); x++) {
            for (int y = 0; y < imgOutside.getTileHeight(); y++) {
                if (imgOutside.getRGB(x, y) != 0) {
                    if (imgInside.getRGB(x, y) != 0) {
                        imgOutside.setRGB(x, y, COLOR_TRAINSPARENT.getRGB());
                    } else {
                        imgOutside.setRGB(x, y, edgeColor.getRGB());
                    }
                }
            }
        }
        imgInside.flush();
        return imgOutside;
    }

    public Color getTypeColor() {
        return lineData.getTypeColor(this.trainData);
    }

    public void combine(Train nextTrain) {
        this.trainData = this.trainData.combine(nextTrain.trainData);
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
    // インタフェース
    // --------------------------------------------------------------------------------
    public LineData getLineData() {
        return this.lineData;
    }

    // --------------------------------------------------------------------------------
    @Override
    public String toString() {
        return "列車番号: " + trainData.getTimeTable().getTrainID();
    }

}
