package sample_data;

import java.awt.Color;
import java.awt.Image;
import java.awt.Point;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import data.line_data.EasyPathPoint;
import data.line_data.LineData;
import data.line_data.LineSegmentPath;
import data.train_data.TrainData;

public class TokyuMeguroLine extends LineData {
    // 東急
    private Image imageIconLocal;
    private Image imageIconExp;

    private static final Color COLOR_LOCAL = new Color(0, 0, 255);
    private static final Color COLOR_EXP = new Color(255, 0, 0);

    public TokyuMeguroLine() {
        super();
        try {
            Image img = ImageIO.read(new File("icon/toq3000.png"));
            imageIconLocal = LineData.createEdgedImage(img, COLOR_LOCAL, 2);
            imageIconExp = LineData.createEdgedImage(img, COLOR_EXP, 2);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static final Color LINE_COLOR = new Color(44, 148, 182);

    @Override
    public Color getLineColor() {
        return LINE_COLOR;
    }

    @Override
    public String getLineName() {
        return "[東急]目黒線";
    }

    private Point origin = new Point(2000, 200);

    @Override
    public Point calcPosOnLinePath(float dist, Direction direction) {
        int offset;

        if (direction == Direction.OUTBOUND) {
            offset = +15;
        } else {
            offset = -15;
        }

        EasyPathPoint[] epp = {
                // 目黒 - 大岡山
                LineSegmentPath.getInstance(getDistProportion(5),
                        new Point(origin.x + 0 + offset, origin.y + 600 + offset),
                        new Point(origin.x - 500 + offset, origin.y + 600 + offset)),
                // 大岡山 - 田園調布
                LineSegmentPath.getInstance(getDistProportion(7),
                        new Point(origin.x - 500 + offset, origin.y + 600 + offset),
                        new Point(origin.x - 600 + offset, origin.y + 600 + offset)),
                // 田園調布 - 多摩川
                LineSegmentPath.getInstance(getDistProportion(8),
                        new Point(origin.x - 600 + offset, origin.y + 600 + offset),
                        new Point(origin.x - 700 + offset, origin.y + 600 + offset)),
                // 多摩川 - 日吉
                LineSegmentPath.getInstance(getDistProportion(12),
                        new Point(origin.x - 600 + offset, origin.y + 600 + offset),
                        new Point(origin.x - 1000 + offset, origin.y + 600 + offset)),
                LineSegmentPath.getInstance(Float.MAX_VALUE,
                        new Point(origin.x - 1000 + offset, origin.y + 600 + offset),
                        new Point(origin.x - 1000 + offset, origin.y + 600 + offset))
        };
        return generateEasyPathPoint(epp, dist);
    }

    @Override
    protected String getStationDataCsvPath() {
        return "time_table/tokyu_meguro_line_station.csv";
    }

    @Override
    protected String getTimeTableOutCsvPath() {
        return "time_table/tokyu_meguro_line_weekdays_out.csv";
    }

    @Override
    protected String getTimeTableInCsvPath() {
        return "time_table/tokyu_meguro_line_weekdays_in.csv";
    }

    @Override
    public Image getIconImg(TrainData trainData) {
        String trainType = trainData.getTimeTable().trainType;
        switch (trainType) {
            case "急行":
                return imageIconExp;
            default:
                return imageIconLocal;
        }
    }

    @Override
    public Color getTypeColor(TrainData trainData) {
        switch (trainData.getTimeTable().trainType) {
            case "急行":
                return COLOR_EXP;
            default:
                return COLOR_LOCAL;
        }
    }
}