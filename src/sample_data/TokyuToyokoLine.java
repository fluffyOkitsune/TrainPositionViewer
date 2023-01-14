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

public class TokyuToyokoLine extends LineData {
    // 東横線
    private Image imageIconLocal;
    private Image imageIconExp;
    private Image imageIconComLtd;
    private Image imageIconLtd;

    private static final Color COLOR_LOCAL = new Color(0, 0, 255);
    private static final Color COLOR_EXP = new Color(255, 0, 0);
    private static final Color COLOR_COM_LTD = new Color(255, 165, 0);
    private static final Color COLOR_LTD = new Color(255, 165, 0);

    public TokyuToyokoLine() {
        super();
        try {
            Image img = ImageIO.read(new File("icon/toq5050.png"));
            imageIconLocal = LineData.createEdgedImage(img, COLOR_LOCAL, 2);
            imageIconExp = LineData.createEdgedImage(img, COLOR_EXP, 2);
            imageIconLtd = LineData.createEdgedImage(img, COLOR_LTD, 2);

            img = LineData.createEdgedImage(img, Color.WHITE, 1);
            imageIconComLtd = LineData.createEdgedImage(img, COLOR_LTD, 1);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static final Color LINE_COLOR = new Color(218, 4, 66);

    @Override
    public Color getLineColor() {
        return LINE_COLOR;
    }

    @Override
    public String getLineName() {
        return "[東急]東横線";
    }

    private Point origin = new Point(2000, 200);

    @Override
    public Point calcPosOnLinePath(float dist, Direction direction) {
        int offset;

        if (direction == Direction.OUTBOUND) {
            offset = +20;
        } else {
            offset = -20;
        }

        EasyPathPoint[] epp = {
                // 渋谷 - 自由が丘
                LineSegmentPath.getInstance(getDistProportion(6),
                        new Point(origin.x + 0 + offset, origin.y + 0 + offset),
                        new Point(origin.x - 500 + offset, origin.y + 500 + offset)),
                // 自由が丘 - 田園調布
                LineSegmentPath.getInstance(getDistProportion(7),
                        new Point(origin.x - 500 + offset, origin.y + 500 + offset),
                        new Point(origin.x - 600 + offset, origin.y + 600 + offset)),
                // 田園調布 - 多摩川
                LineSegmentPath.getInstance(getDistProportion(8),
                        new Point(origin.x - 600 + offset, origin.y + 600 + offset),
                        new Point(origin.x - 700 + offset, origin.y + 600 + offset)),
                // 多摩川 - 日吉
                LineSegmentPath.getInstance(getDistProportion(12),
                        new Point(origin.x - 700 + offset, origin.y + 600 + offset),
                        new Point(origin.x - 1000 + offset, origin.y + 600 + offset)),
                // 日吉 - 横浜
                LineSegmentPath.getInstance(getDistProportion(20),
                        new Point(origin.x - 1000 + offset, origin.y + 600 + offset),
                        new Point(origin.x - 1500 + offset, origin.y + 1100 + offset)),
                LineSegmentPath.getInstance(Float.MAX_VALUE,
                        new Point(origin.x - 1500 + offset, origin.y + 1100 + offset),
                        new Point(origin.x - 1500 + offset, origin.y + 1100 + offset))
        };
        return generateEasyPathPoint(epp, dist);
    }

    @Override
    protected String getStationDataCsvPath() {
        return "time_table/tokyu_toyoko_line_station.csv";
    }

    @Override
    protected String getTimeTableOutCsvPath() {
        return "time_table/tokyu_toyoko_line_weekdays_out.csv";
    }

    @Override
    protected String getTimeTableInCsvPath() {
        return "time_table/tokyu_toyoko_line_weekdays_in.csv";
    }

    @Override
    public Image getIconImg(TrainData trainData) {
        String trainType = trainData.getTimeTable().getTrainType();
        switch (trainType) {
            case "特急":
                return imageIconLtd;
            case "通特":
                return imageIconComLtd;
            case "急行":
                return imageIconExp;
            default:
                return imageIconLocal;
        }
    }

    @Override
    public Color getTypeColor(TrainData trainData) {
        switch (trainData.getTimeTable().getTrainType()) {
            case "特急":
                return COLOR_LTD;
            case "通特":
                return COLOR_COM_LTD;
            case "急行":
                return COLOR_EXP;
            default:
                return COLOR_LOCAL;
        }
    }
}