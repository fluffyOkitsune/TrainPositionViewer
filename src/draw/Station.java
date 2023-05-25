package draw;

import java.awt.*;
import java.util.Arrays;

import data.line_data.LineData;
import data.line_data.LineData.Direction;
import data.time_table.StationData;

public class Station {
    private StationData stationData;
    private Point pos;

    // --------------------------------------------------------------------------------
    // データ作成
    // --------------------------------------------------------------------------------
    public static Station[] convert(StationData[] stationData) {
        return Arrays.stream(stationData).map(
                e -> {
                    return new Station(e);
                }).toArray(Station[]::new);
    }

    public Station(StationData stationData) {
        this.stationData = stationData;
    }

    public void calcPos() {
        Point posO = calcPosOnLinePath(getDistProportion(), Direction.OUTBOUND);
        Point posI = calcPosOnLinePath(getDistProportion(), Direction.INBOUND);
        pos = new Point((posO.x + posI.x) / 2, (posO.y + posI.y) / 2);
    }

    // --------------------------------------------------------------------------------
    // 描画処理
    // --------------------------------------------------------------------------------
    private static final Font FONT_STA_NAME = new Font(null, Font.PLAIN, 10);

    public void draw(Graphics g) {
        final int radiusOut = 20;
        final int radiusIn = 15;

        // 駅の位置を描画する
        g.setColor(stationData.getLineData().getLineColor());
        g.fillOval(pos.x - radiusOut / 2, pos.y - radiusOut / 2, radiusOut, radiusOut);
        g.setColor(Color.WHITE);
        g.fillOval(pos.x - radiusIn / 2, pos.y - radiusIn / 2, radiusIn, radiusIn);
    }

    public void drawStaName(Graphics g) {
        Point pos = calcStationPos();
        String staName = getName();

        // TODO: 縁取り(暫定)
        g.setFont(FONT_STA_NAME);
        g.setColor(Color.WHITE);
        for (int i = 0; i < 9; i++) {
            Point offsetPos = new Point(pos.x + i / 3 - 1, pos.y + i % 3 - 1);
            LineData.drawString(g, staName, offsetPos);
        }

        g.setColor(getLineData().getLineColor());
        LineData.drawString(g, staName, pos);
    }

    private Point calcPosOnLinePath(float dist, Direction direction) {
        return stationData.getLineData().calcPosOnLinePath(dist, direction);
    }

    public Point calcStationPos() {
        return stationData.calcStationPos();
    }

    // --------------------------------------------------------------------------------
    // インタフェース
    // --------------------------------------------------------------------------------
    public float getDistance() {
        return stationData.getDistance();
    }

    public float getDistProportion() {
        return stationData.getDistProportion();
    }

    public String getLineName() {
        return stationData.getLineData().getLineName();
    }

    public LineData getLineData() {
        return stationData.getLineData();
    }

    public String getName() {
        return stationData.getName();
    }

    public Point getPos() {
        return pos;
    }

    public StationData getStationData() {
        return stationData;
    }

    // --------------------------------------------------------------------------------
    @Override
    public String toString() {
        return String.format("%s : %s (%f km)", getName(), getDistance(), getLineName());
    }
}
