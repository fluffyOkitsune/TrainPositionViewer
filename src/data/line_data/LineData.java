package data.line_data;

import java.awt.*;
import java.io.FileNotFoundException;
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

        setTrain(vTrain.toArray(new Train[0]));
    }

    private void generateTrainData(Vector<Train> vTrain, TimeTable[] timeTables){
        for(TimeTable timeTable : timeTables){
            vTrain.add(new Train(this, new TrainData(timeTable)));
        }
    }

    // --------------------------------------------------------------------------------
    // 列車位置を計算する
    // --------------------------------------------------------------------------------
    public abstract Point calcPositionOnLinePath(float dist);

    public void update(Time currentTime) {
        for (Train t : train) {
            t.update(currentTime);
        }
    }

    // --------------------------------------------------------------------------------
    // 描画処理
    // --------------------------------------------------------------------------------
    public abstract Image getIconImg(TrainData trainData);

    public abstract Color getTypeColor(TrainData trainData);

    public void drawTrain(Graphics g){
        for(Train t : train){
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
