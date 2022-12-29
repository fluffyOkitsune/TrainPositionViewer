package data.line_data;

import java.awt.*;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import data.Time;
import data.time_table.StationData;
import data.time_table.TimeTable;
import data.time_table.TimeTableReader;
import data.train_data.TrainData;
import draw.Train;

// 路線のデータ
public abstract class LineData {
    public abstract Color getLineColor();

    public abstract String getLineName();

    private StationData[] stationData;
    private Train[] train;
    private Map<Point, Time> minReqTime;

    public enum Direction {
        OUTBOUND, INBOUND;
    }

    // --------------------------------------------------------------------------------
    // データ作成
    // --------------------------------------------------------------------------------
    protected abstract String getStationDataCsvPath();

    protected abstract String getTimeTableOutCsvPath();

    protected abstract String getTimeTableInCsvPath();

    public final void importCSV() throws FileNotFoundException {
        // 駅データの入力
        setStationData(StationData.createStationData(getStationDataCsvPath()));

        // 列車運行データの入力
        TimeTable[] trainData;
        Vector<Train> vTrain = new Vector<>();

        trainData = TimeTableReader.readTimeTable(this, Direction.OUTBOUND, getTimeTableOutCsvPath());
        generateTrainData(vTrain, trainData);

        trainData = TimeTableReader.readTimeTable(this, Direction.INBOUND, getTimeTableInCsvPath());
        generateTrainData(vTrain, trainData);

        Train[] train = vTrain.toArray(new Train[0]);
        setTrainData(train);
    }

    private void setTrainData(Train[] train) {
        setTrain(train);

        minReqTime = new HashMap<>();
        for (Train t : train) {
            calcMinRequiedTime(t.trainData.getTimeTable());
        }

        for (Train t : train) {
            t.applyMinReqTime(minReqTime);
        }
    }

    private void generateTrainData(Vector<Train> vTrain, TimeTable[] timeTables) {
        for (TimeTable timeTable : timeTables) {
            Train train = new Train(this, new TrainData(timeTable));
            vTrain.add(train);
        }
    }

    // --------------------------------------------------------------------------------
    // 次の駅への最小の所要時間を計算する
    // --------------------------------------------------------------------------------
    private void calcMinRequiedTime(TimeTable timeTable) {
        for (int idx = 0; idx < timeTable.getTimeDataSize() - 1; idx++) {
            int depStaID = timeTable.getTimeData(idx).getStaID();
            int destStaID = timeTable.getTimeData(idx + 1).getStaID();
            Time reqTime = timeTable.getReqTime(idx);
            setMinReqTime(depStaID, destStaID, reqTime);
        }
    }

    private void setMinReqTime(int depStaID, int destStaID, Time reqTime) {
        if (reqTime == null) {
            return;
        }

        Point key = new Point(depStaID, destStaID);
        if (minReqTime.containsKey(key)) {
            Time currentMinReqTime = minReqTime.get(key);
            if (reqTime.compareTo(currentMinReqTime) < 0) {
                minReqTime.put(key, reqTime);
            }

        } else {
            minReqTime.put(key, reqTime);
        }
    }

    // --------------------------------------------------------------------------------
    // 列車位置を計算する
    // --------------------------------------------------------------------------------
    public abstract Point calcPositionOnLinePath(float dist, Direction direction);

    public void update(Time currentTime) {
        for (Train t : train) {
            t.update(currentTime);
        }
    }

    // --------------------------------------------------------------------------------
    // パスを設定する
    // --------------------------------------------------------------------------------
    protected Point generateEasyPathPoint(EasyPathPoint[] epp, float dist) {
        for (int i = 0; i < epp.length; i++) {
            if (dist < epp[i].getEndPointDist()) {
                float distBwPoints;

                if (i > 0) {
                    distBwPoints = epp[i].getEndPointDist() - epp[i - 1].getEndPointDist();
                    dist -= epp[i - 1].getEndPointDist();
                } else {
                    distBwPoints = epp[i].getEndPointDist();
                }
                return epp[i].calcPositionOnLinePath(dist / distBwPoints);
            }
        }

        return new Point(0, 0);
    }

    // --------------------------------------------------------------------------------
    // 描画処理
    // --------------------------------------------------------------------------------
    public abstract Image getIconImg(TrainData trainData);

    public abstract Color getTypeColor(TrainData trainData);

    public void drawTrain(Graphics g) {
        for (Train t : train) {
            t.draw(g);
        }
    }

    public static void drawImage(Graphics g, Image img, Point pos) {
        g.drawImage(img, pos.x - img.getWidth(null) / 2,
                pos.y - img.getHeight(null) / 2, null);
    }

    public static void drawString(Graphics g, String str, Point pos) {
        Rectangle rectText = g.getFontMetrics().getStringBounds(str, g).getBounds();
        g.drawString(str, pos.x - rectText.width / 2, pos.y + rectText.height / 2);
    }

    // --------------------------------------------------------------------------------
    // インタフェース
    // --------------------------------------------------------------------------------
    public void setStationData(StationData[] stationData) {
        this.stationData = stationData;
    }

    public StationData[] getStationData() {
        return stationData;
    }

    public StationData getStationData(int staID) {
        return stationData[staID];
    }

    public float getDistProportion(int staID) {
        return stationData[staID].getDistProportion();
    }

    public String getStationName(int staID) {
        return stationData[staID].getName();
    }

    public final int numStation() {
        return stationData.length;
    }

    public void setTrain(Train[] train) {
        this.train = train;
    }

    public Train[] getTrain() {
        return train;
    }
}