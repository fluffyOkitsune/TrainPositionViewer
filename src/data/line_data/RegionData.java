package data.line_data;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import data.Time;
import data.line_data.LineData.Direction;
import data.time_table.StationData;
import draw.Train;

public abstract class RegionData {
    private Train[] train;
    LineData[] lineData;
    private List<TransferData> transferData;

    // 独立した路線用
    public RegionData(LineData lineData) {
        this.lineData = new LineData[1];
        this.lineData[0] = lineData;
    }

    protected RegionData() {
        transferData = new Vector<>();
    }

    public abstract void defineThroughService();

    protected void setLineData(LineData[] lineData) {
        this.lineData = lineData;
    }

    public void init() {
        try {
            Vector<Train> vTrain = new Vector<>();

            for (LineData ld : lineData) {
                ld.importCSV();
                ld.compilePosOnLinePath();

                for (Train t : ld.getTrain()) {
                    vTrain.add(t);
                }
            }
            train = vTrain.toArray(new Train[0]);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void update(Time currentTime) {
        for (LineData ld : lineData) {
            ld.update(currentTime);
        }
    }

    // --------------------------------------------------------------------------------
    // データ作成（直通データ）
    // --------------------------------------------------------------------------------
    protected void addTrainsfer(StationData from, StationData to) {
        TransferData transferData;

        // 下り方向
        transferData = new TransferData();
        transferData.stationFrom = from;
        transferData.stationTo = to;
        this.transferData.add(transferData);

        // 上り方向
        transferData = new TransferData();
        transferData.stationFrom = to;
        transferData.stationTo = from;
        this.transferData.add(transferData);
    }

    public void combineTrainData() {
        List<Train> lTrain = new Vector<>(Arrays.asList(train));
        for (Train tFrom : train) {
            for (TransferData td : transferData) {
                // 直通運転の境界駅が終着の列車を検索する
                if (tFrom.getTerminalStation() == td.stationFrom) {
                    for (Train tTo : train) {
                        // 直通運転の境界駅が始発の列車を検索する
                        if (tTo.getFirstStation() == td.stationTo) {
                            if (tFrom.trainData.getTimeTable().getTrainID()
                                    .equals(tTo.trainData.getTimeTable().getTrainID())) {
                                tFrom.combine(tTo);
                                lTrain.remove(tTo);
                            }
                        }
                    }
                }
            }
        }
        train = lTrain.toArray(new Train[0]);
    }

    // --------------------------------------------------------------------------------
    // 描画処理（列車）
    // --------------------------------------------------------------------------------
    // 列車アイコンを描画する
    public void drawTrain(Graphics g) {
        for (Train t : train) {
            t.draw(g);
        }
    }

    // 列車番号を描画する
    public void drawTrainID(Graphics g) {
        for (Train t : train) {
            if (t.onDuty) {
                String trainID = t.trainData.getTimeTable().getTrainID();
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

    // --------------------------------------------------------------------------------
    // 描画処理（路線）
    // --------------------------------------------------------------------------------
    public void drawRailLine(Graphics2D g) {
        for (LineData ld : lineData) {
            drawRailLine(g, ld);
        }
    }

    private static final Stroke STROKE_LINE = new BasicStroke(5.0f);

    private void drawRailLine(Graphics g, LineData lineData) {
        g.setColor(lineData.getLineColor());
        ((Graphics2D) g).setStroke(STROKE_LINE);
        lineData.drawLinePath(g);
    }

    // --------------------------------------------------------------------------------
    // 描画処理（駅）
    // --------------------------------------------------------------------------------
    public void drawStation(Graphics2D g) {
        for (LineData ld : lineData) {
            drawStation(g, ld);
        }
    }

    private void drawStation(Graphics g, LineData lineData) {
        // 駅を書く
        final int radiusOut = 20;
        final int radiusIn = 15;
        for (StationData sd : lineData.getStationData()) {
            Point posO = lineData.calcPosOnLinePath(sd.getDistProportion(), Direction.OUTBOUND);
            Point posI = lineData.calcPosOnLinePath(sd.getDistProportion(), Direction.INBOUND);
            Point pos = new Point((posO.x + posI.x) / 2, (posO.y + posI.y) / 2);

            // 駅の位置を描画する
            g.setColor(lineData.getLineColor());
            g.fillOval(pos.x - radiusOut / 2, pos.y - radiusOut / 2, radiusOut, radiusOut);
            g.setColor(Color.WHITE);
            g.fillOval(pos.x - radiusIn / 2, pos.y - radiusIn / 2, radiusIn, radiusIn);
        }
    }

    private int drawStopsAnimCnt = 0;
    private int drawStopsAnimCntTh = 50;

    // --------------------------------------------------------------------------------
    public static final StationData[] STOPS_NOTHING = new StationData[0];

    public void drawStops(Graphics2D g, Train train, StationData[] stopStations) {
        if (train == null || stopStations.length == 0) {
            return;
        }
        if (!train.onDuty) {
            return;
        }

        final int radiusOut = 30;
        final int radiusMid = 25;
        final int radiusIn = 20;

        Color baseColor = train.getTypeColor();

        boolean isPassedStation = true;
        for (StationData station : stopStations) {
            Point posO = station.getLineData().calcPosOnLinePath(station.getDistProportion(), Direction.OUTBOUND);
            Point posI = station.getLineData().calcPosOnLinePath(station.getDistProportion(), Direction.INBOUND);
            Point pos = new Point((posO.x + posI.x) / 2, (posO.y + posI.y) / 2);

            // これから停車する駅は目立つようにして、すでに過ぎた駅は半透明にする。
            if (isPassedStation) {
                baseColor = new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), 0x40);
            } else {
                baseColor = new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(),
                        getStopsColorAlpha());
            }

            if (station == train.getDepartedStation()) {
                isPassedStation = false;
            }

            g.setColor(baseColor);
            g.fillOval(pos.x - radiusOut / 2, pos.y - radiusOut / 2, radiusOut, radiusOut);
            g.setColor(Color.WHITE);
            g.fillOval(pos.x - radiusMid / 2, pos.y - radiusMid / 2, radiusMid, radiusMid);
            g.setColor(baseColor);
            g.fillOval(pos.x - radiusIn / 2, pos.y - radiusIn / 2, radiusIn, radiusIn);
        }
        if (drawStopsAnimCnt < drawStopsAnimCntTh) {
            drawStopsAnimCnt++;
        } else {
            drawStopsAnimCnt = 0;
        }
    }

    private int getStopsColorAlpha() {
        int harfOfdrawStopsAnimCntTh = drawStopsAnimCntTh / 2;
        if (drawStopsAnimCnt < harfOfdrawStopsAnimCntTh) {
            return 255 * drawStopsAnimCnt / harfOfdrawStopsAnimCntTh;
        } else {
            return 255 * (drawStopsAnimCntTh - drawStopsAnimCnt) / harfOfdrawStopsAnimCntTh;
        }
    }

    // --------------------------------------------------------------------------------
    public void drawLinePath(Graphics g) {
        for (LineData ld : lineData) {
            ld.drawLinePath(g);
        }
    }

    // --------------------------------------------------------------------------------
    public void drawStaName(Graphics g) {
        for (LineData ld : lineData) {
            drawStaName(g, ld);
        }
    }

    private static final Font FONT_STA_NAME = new Font(null, Font.PLAIN, 10);

    private void drawStaName(Graphics g, LineData lineData) {
        for (StationData stationData : lineData.getStationData()) {
            Point pos = stationData.calcStationPos();
            String staName = stationData.getName();

            // TODO: 縁取り(暫定)
            g.setFont(FONT_STA_NAME);
            g.setColor(Color.WHITE);
            for (int i = 0; i < 9; i++) {
                Point offsetPos = new Point(pos.x + i / 3 - 1, pos.y + i % 3 - 1);
                LineData.drawString(g, staName, offsetPos);
            }

            g.setColor(lineData.getLineColor());
            LineData.drawString(g, staName, pos);
        }
    }

    // --------------------------------------------------------------------------------
    // マウスイベント
    // --------------------------------------------------------------------------------
    // クリックした列車アイコンに対応する列車データを探索する
    public Train seekClickedTrain(MouseEvent e) {
        Train train = null;
        for (LineData ld : lineData) {
            for (Train t : ld.getTrain()) {
                if (t.getOnMouse(e)) {
                    train = t;
                }
            }
        }
        return train;
    }
}
