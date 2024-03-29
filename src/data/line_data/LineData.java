package data.line_data;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import data.Time;
import data.time_table.StationData;
import data.time_table.TimeTable;
import data.time_table.TimeTableReader;
import data.train_data.TrainData;
import draw.Station;
import draw.Train;

// 路線のデータ
public abstract class LineData {
    public abstract Color getLineColor();

    public abstract String getLineName();

    private RegionData regionData;
    private Station[] station;
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
        setStation(Station.convert(StationData.createStationData(this, getStationDataCsvPath())));

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
            calcMinRequiedTime(t.getTimeTable());
        }

        for (Train t : train) {
            t.applyMinReqTime(minReqTime);
        }
    }

    private void generateTrainData(Vector<Train> vTrain, TimeTable[] timeTables) {
        for (TimeTable timeTable : timeTables) {
            Train train = new Train(this, new TrainData(timeTable, this));
            vTrain.add(train);
        }
    }

    public void calcStationPos() {
        for(Station sta : station){
            sta.calcPos();
        }
    }
    
    // --------------------------------------------------------------------------------
    // 次の駅への最小の所要時間を計算する
    // --------------------------------------------------------------------------------
    private void calcMinRequiedTime(TimeTable timeTable) {
        for (int idx = 0; idx < timeTable.getTimeDataSize() - 1; idx++) {
            int depStaID = timeTable.getTimeData(idx).getStationData().getStationID();
            int destStaID = timeTable.getTimeData(idx + 1).getStationData().getStationID();
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
    // 路線ごとに異なる経路
    public abstract Point calcPosOnLinePath(float dist, Direction direction);

    private short[][] linePath;

    private final static int IDX_OUT_X = 0;
    private final static int IDX_OUT_Y = 1;
    private final static int IDX_IN_X = 2;
    private final static int IDX_IN_Y = 3;

    private final static int NUM_SEPARATE = 1000;

    // あらかじめ経路の座標を保持しておくことで計算量を削減する
    public void compilePosOnLinePath() {
        linePath = new short[4][NUM_SEPARATE + 1];
        Point pos;
        for (int i = 0; i < NUM_SEPARATE + 1; i++) {
            // 下り線
            pos = calcPosOnLinePath((float) i / NUM_SEPARATE, Direction.OUTBOUND);
            linePath[IDX_OUT_X][i] = (short) pos.x;
            linePath[IDX_OUT_Y][i] = (short) pos.y;

            // 上り線
            pos = calcPosOnLinePath((float) i / NUM_SEPARATE, Direction.INBOUND);
            linePath[IDX_IN_X][i] = (short) pos.x;
            linePath[IDX_IN_Y][i] = (short) pos.y;
        }
    }

    public Point getPositionOnLinePath(float dist, Direction direction) {
        int idx = (int) (dist * NUM_SEPARATE);

        idx = Integer.max(idx, NUM_SEPARATE + 1);
        idx = Integer.min(idx, 0);

        return new Point(linePath[IDX_OUT_X][idx], linePath[IDX_OUT_Y][idx]);
    }

    // --------------------------------------------------------------------------------
    // パスを設定する
    // --------------------------------------------------------------------------------
    protected Point generateEasyPathPoint(EasyPathPoint[] epp, float dist) {
        for (int i = 0; i < epp.length; i++) {
            if (dist < epp[i].getEndPointDist()) {
                float distBwPoints;

                if (i == 0) {
                    // 最初の駅が0kmとは限らない
                    distBwPoints = epp[0].getEndPointDist();
                    return epp[i].calcPositionOnLinePath(dist / distBwPoints);
                } else if (i < epp.length) {
                    distBwPoints = epp[i].getEndPointDist() - epp[i - 1].getEndPointDist();
                    dist -= epp[i - 1].getEndPointDist();
                    return epp[i].calcPositionOnLinePath(dist / distBwPoints);
                }
            }
        }
        return epp[epp.length - 1].calcPositionOnLinePath(1.0f);
    }

    // --------------------------------------------------------------------------------
    // 備考欄の列車の詳細情報
    // --------------------------------------------------------------------------------
    // マスク（平日運転）
    public static final int OPR_DATE_MASK_WEEKDAYS = 0x1;
    // マスク（土曜運転）
    public static final int OPR_DATE_MASK_SATURDAY = 0x2;
    // マスク（休日運転）
    public static final int OPR_DATE_MASK_HOLIDAY = 0x4;
    // マスク（運転日注意）
    public static final int OPR_DATE_MASK_EXTRA = 0x8;

    // （毎日運転）
    public static final int OPR_DATE_EVERYDAY = 0xF;
    // 【休日運休】
    public static final int OPR_DATE_NOT_HOLYDAY = 0x3;
    // 【土休日運休】
    public static final int OPR_DATE_NOT_WEEKEND = 0x1;
    // ◆（土曜運転）
    public static final int OPR_DATE_ONLY_SATURDAY = 0x2;
    // ◆（休日運転）
    public static final int OPR_DATE_ONLY_HOLYDAY = 0x4;
    // ◆（土休日運転）
    public static final int OPR_DATE_ONLY_WEEKEND = 0x6;
    // ◆《運転日注意》
    public static final int OPR_DATE_EXTRA = 0x8;

    public int getOperationDate(TrainData trainData) {
        String note = trainData.getTimeTable().getNote();
        if (note.contains("◆《運転日注意》")) {
            return OPR_DATE_EXTRA;
        } else if (note.contains("【休日運休】")) {
            return OPR_DATE_NOT_HOLYDAY;
        } else if (note.contains("【土休日運休】")) {
            return OPR_DATE_NOT_WEEKEND;
        } else if (note.contains("◆（土曜運転）")) {
            return OPR_DATE_ONLY_SATURDAY;
        } else if (note.contains("◆（休日運転）")) {
            return OPR_DATE_ONLY_HOLYDAY;
        } else if (note.contains("◆（土休日運転）")) {
            return OPR_DATE_ONLY_WEEKEND;
        } else {
            return OPR_DATE_EVERYDAY;
        }
    }

    // --------------------------------------------------------------------------------
    // 描画処理
    // --------------------------------------------------------------------------------
    public static void drawImage(Graphics g, Image img, Point pos) {
        g.drawImage(img, pos.x - img.getWidth(null) / 2,
                pos.y - img.getHeight(null) / 2, null);
    }

    public static void drawString(Graphics g, String str, Point pos) {
        Rectangle rectText = g.getFontMetrics().getStringBounds(str, g).getBounds();
        g.drawString(str, pos.x - rectText.width / 2, pos.y + rectText.height / 2);
    }

    // 種別色で囲ったアイコン
    public static Image createEdgedImage(Image img, Color color, int edgeSize) {
        BufferedImage bimg = new BufferedImage(img.getWidth(null) + 2 * edgeSize, img.getHeight(null) + 2 * edgeSize,
                BufferedImage.TYPE_INT_ARGB);

        Graphics g = bimg.getGraphics();
        g.drawImage(img, 0, 0, img.getWidth(null) + 2 * edgeSize, img.getHeight(null) + 2 * edgeSize, null);

        for (int x = 0; x < bimg.getTileWidth(); x++) {
            for (int y = 0; y < bimg.getTileHeight(); y++) {
                if (bimg.getRGB(x, y) != 0) {
                    bimg.setRGB(x, y, color.getRGB());
                }
            }
        }

        g.drawImage(img, edgeSize, edgeSize, null);
        g.dispose();
        return bimg;
    }

    public abstract Image getIconImg(TrainData trainData);

    public abstract Color getTypeColor(TrainData trainData);

    // 列車番号を描画する
    public void drawTrainID(Graphics g) {
        for (Train t : train) {
            if (t.onDuty) {
                String trainID = t.getTimeTable().getTrainID();
                Rectangle rect = t.getRect();
                Point pos = new Point(rect.getLocation().x + rect.width / 2, rect.getLocation().y + rect.height / 2);

                // TODO: 縁取り(暫定)
                g.setColor(Color.WHITE);
                for (int i = 0; i < 9; i++) {
                    Point offsetPos = new Point(pos.x + i / 3 - 1, pos.y + i % 3 - 1);
                    LineData.drawString(g, trainID, offsetPos);
                }

                g.setColor(t.getTypeColor());
                LineData.drawString(g, trainID, pos);
            }
        }
    }

    public void drawLinePath(Graphics g) {
        for (int i = 0; i < NUM_SEPARATE; i++) {
            // 下り線
            g.drawLine(linePath[IDX_OUT_X][i], linePath[IDX_OUT_Y][i],
                    linePath[IDX_OUT_X][i + 1], linePath[IDX_OUT_Y][i + 1]);

            // 上り線
            g.drawLine(linePath[IDX_IN_X][i], linePath[IDX_IN_Y][i],
                    linePath[IDX_IN_X][i + 1], linePath[IDX_IN_Y][i + 1]);
        }
    }

    // --------------------------------------------------------------------------------
    // インタフェース
    // --------------------------------------------------------------------------------
    public void update(Time currentTime) {
        for (Train t : train) {
            t.update(currentTime);
        }
    }

    public void setStation(Station[] station) {
        this.station = station;
    }

    public Station[] getStation() {
        return station;
    }

    public StationData getStationData(int staID) {
        return station[staID].getStationData();
    }

    public StationData getStationData(String name) {
        for (Station sta : station) {
            if (name.equals(sta.getName())) {
                return sta.getStationData();
            }
        }
        return null;
    }

    public float getDistProportion(int staID) {
        return station[staID].getDistProportion();
    }

    public final int numStation() {
        return station.length;
    }

    public RegionData getRegionData() {
        return regionData;
    }

    public void setRegionData(RegionData regionData) {
        this.regionData = regionData;
    }

    public void setTrain(Train[] train) {
        this.train = train;
    }

    public Train[] getTrain() {
        return train;
    }

    @Override
    public String toString() {
        return "LineData [" + getLineName() + "]";
    }
}