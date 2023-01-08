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

public class TokyuOimachiLine extends LineData {
    private Image imageIconLocal;
    private Image imageIconExp;

    private static final Color COLOR_DT_LOCAL = new Color(0, 0, 255);
    private static final Color COLOR_LOCAL = new Color(0, 128, 0);
    private static final Color COLOR_EXP = new Color(238, 0, 17);

    public TokyuOimachiLine() {
        super();
        try {
            Image img = ImageIO.read(new File("icon/toq9001or.png"));
            imageIconLocal = LineData.createEdgedImage(img, COLOR_LOCAL, 2);
            img = ImageIO.read(new File("icon/toq6001.png"));
            imageIconExp = LineData.createEdgedImage(img, COLOR_EXP, 2);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static final Color LINE_COLOR = new Color(241, 140, 67);

    @Override
    public Color getLineColor() {
        return LINE_COLOR;
    }

    @Override
    public String getLineName() {
        return "[東急]大井町線";
    }

    private Point origin = new Point(2000, 200);

    @Override
    public Point calcPosOnLinePath(float dist, Direction direction) {
        int offset, offsetInnerTrack;

        if (direction == Direction.OUTBOUND) {
            offset = -20;
            offsetInnerTrack = -15;
        } else {
            offset = +20;
            offsetInnerTrack = +15;
        }

        EasyPathPoint[] epp = {
                // 大井町 - 旗の台
                LineSegmentPath.getInstance(getDistProportion(5),
                        new Point(origin.x - 500 + offset, origin.y + 1200),
                        new Point(origin.x - 500 + offset, origin.y + 900)),
                // 旗の台 - 大岡山
                LineSegmentPath.getInstance(getDistProportion(7),
                        new Point(origin.x - 500 + offset, origin.y + 900),
                        new Point(origin.x - 500 + offset, origin.y + 600)),
                // 大岡山- 自由が丘
                LineSegmentPath.getInstance(getDistProportion(9),
                        new Point(origin.x - 500 + offset, origin.y + 600),
                        new Point(origin.x - 500 + offset, origin.y + 500)),
                // 自由が丘 - 二子玉川
                LineSegmentPath.getInstance(getDistProportion(14),
                        new Point(origin.x - 500 + offset, origin.y + 500),
                        new Point(origin.x - 500 + offset, origin.y + 0 - offsetInnerTrack)),
                // 二子玉川 - 溝の口
                LineSegmentPath.getInstance(getDistProportion(17),
                        new Point(origin.x - 500 + offset, origin.y + 0 - offsetInnerTrack),
                        new Point(origin.x - 650, origin.y + 0 - offsetInnerTrack)),
                LineSegmentPath.getInstance(Float.MAX_VALUE,
                        new Point(origin.x - 650, origin.y + 0 - offsetInnerTrack),
                        new Point(origin.x - 650, origin.y + 0 - offsetInnerTrack))
        };
        return generateEasyPathPoint(epp, dist);
    }

    @Override
    protected String getStationDataCsvPath() {
        return "time_table/tokyu_oimachi_line_station.csv";
    }

    @Override
    protected String getTimeTableOutCsvPath() {
        return "time_table/tokyu_oimachi_line_weekdays_out.csv";
    }

    @Override
    protected String getTimeTableInCsvPath() {
        return "time_table/tokyu_oimachi_line_weekdays_in.csv";
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