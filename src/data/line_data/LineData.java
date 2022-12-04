package data.line_data;

import java.awt.*;
import java.io.FileNotFoundException;
import java.util.Vector;

import data.Time;
import data.time_table.StationData;
import data.time_table.TimeTable;
import data.time_table.TimeTableReader;
import data.train_data.TrainData;

// 路線のデータ
public abstract class LineData {
    public abstract Color getLineColor();

    public abstract String getLineName();

    private StationData[] stationData;
    private TimeTable[] timeTableOut, timeTableIn;
    private TrainData[] trainData;

    public enum Direction {
        OUTBOUND, INBOUND;
    }

    // --------------------------------------------------------------------------------
    // データ作成
    // --------------------------------------------------------------------------------
    public final void importCSV() throws FileNotFoundException {
        stationData = StationData.createStationData(getStationDataCsvPath());
        timeTableOut = TimeTableReader.readTimeTable(this, Direction.OUTBOUND, getTimeTableOutCsvPath());
        timeTableIn = TimeTableReader.readTimeTable(this, Direction.INBOUND, getTimeTableInCsvPath());
    }

    protected abstract String getStationDataCsvPath();

    protected abstract String getTimeTableOutCsvPath();

    protected abstract String getTimeTableInCsvPath();

    // --------------------------------------------------------------------------------
    // 列車位置を計算する
    // --------------------------------------------------------------------------------
    public abstract Point calcPositionOnLinePath(float dist);

    public void update(Time currentTime) {
        Vector<TrainData> vTrainData = new Vector<>();

        addTrainData(vTrainData, Direction.OUTBOUND, currentTime);
        addTrainData(vTrainData, Direction.INBOUND, currentTime);

        this.trainData = vTrainData.toArray(new TrainData[0]);
    }

    private void addTrainData(Vector<TrainData> trainData, Direction direction, Time currentTime) {
        TimeTable[] timeTable = getTimeTable(direction);

        for (int i = 0; i < timeTable.length; i++) {
            TrainData td = timeTable[i].createCurrTrainData(direction, currentTime);
            if (td != null) {
                trainData.add(td);
            }
        }
    }

    public Point calcTrainPos(TrainData trainData, Time currentTime) {
        float pos = trainData.calcPos(this, currentTime);
        return calcPositionOnLinePath(pos);
    }

    // --------------------------------------------------------------------------------
    // 描画処理
    // --------------------------------------------------------------------------------
    public abstract void drawTrain(Graphics g, TrainData trainData, Time currentTime);

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
    public StationData[] getStationData() {
        return stationData;
    }

    public final int numStation() {
        return stationData.length;
    }

    public final TimeTable[] getTimeTable(Direction direction) {
        switch (direction) {
            case OUTBOUND:
                return timeTableOut;
            case INBOUND:
                return timeTableIn;
        }
        throw new IllegalArgumentException(direction.toString());
    }

    public TrainData[] getTrainData() {
        return trainData;
    }
}
