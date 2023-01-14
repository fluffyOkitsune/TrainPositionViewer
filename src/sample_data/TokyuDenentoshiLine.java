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

public class TokyuDenentoshiLine extends LineData {
    private Image imageIconLocal;
    private Image imageIconSemiExp;
    private Image imageIconExp;

    private static final Color COLOR_LOCAL = new Color(0, 0, 255);
    private static final Color COLOR_SEMI_EXP = new Color(0, 153, 0);
    private static final Color COLOR_EXP = new Color(238, 0, 17);

    public TokyuDenentoshiLine() {
        super();
        try {
            Image img = ImageIO.read(new File("icon/toq2020.png"));
            imageIconLocal = LineData.createEdgedImage(img, COLOR_LOCAL, 2);
            imageIconSemiExp = LineData.createEdgedImage(img, COLOR_SEMI_EXP, 2);
            imageIconExp = LineData.createEdgedImage(img, COLOR_EXP, 2);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static final Color LINE_COLOR = new Color(1, 141, 84);

    @Override
    public Color getLineColor() {
        return LINE_COLOR;
    }

    @Override
    public String getLineName() {
        return "[東急]田園都市線";
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
                // 渋谷 - 三軒茶屋
                LineSegmentPath.getInstance(getDistProportion(2),
                        new Point(origin.x - 0, origin.y + 0 + offset),
                        new Point(origin.x - 200, origin.y + 0 + offset)),
                // 三軒茶屋 - 二子玉川
                LineSegmentPath.getInstance(getDistProportion(6),
                        new Point(origin.x - 200, origin.y + 0 + offset),
                        new Point(origin.x - 500, origin.y + 0 + offset)),
                // 二子玉川 - 溝の口
                LineSegmentPath.getInstance(getDistProportion(9),
                        new Point(origin.x - 500, origin.y + 0 + offset),
                        new Point(origin.x - 650, origin.y + 0 + offset)),
                // 溝の口 - 長津田
                LineSegmentPath.getInstance(getDistProportion(21),
                        new Point(origin.x - 650, origin.y + 0 + offset),
                        new Point(origin.x - 1500, origin.y + 0 + offset)),
                // 長津田 - 中央林間
                LineSegmentPath.getInstance(getDistProportion(26),
                        new Point(origin.x - 1500, origin.y + 0 + offset),
                        new Point(origin.x - 1800, origin.y + 0 + offset)),
                LineSegmentPath.getInstance(Float.MAX_VALUE,
                        new Point(origin.x - 1800, origin.y + 0 + offset),
                        new Point(origin.x - 1800, origin.y + 0 + offset))
        };
        return generateEasyPathPoint(epp, dist);
    }

    @Override
    protected String getStationDataCsvPath() {
        return "time_table/tokyu_den-en-toshi_line_station.csv";
    }

    @Override
    protected String getTimeTableOutCsvPath() {
        return "time_table/tokyu_den-en-toshi_line_weekdays_out.csv";
    }

    @Override
    protected String getTimeTableInCsvPath() {
        return "time_table/tokyu_den-en-toshi_line_weekdays_in.csv";
    }

    @Override
    public Image getIconImg(TrainData trainData) {
        String trainType = trainData.getTimeTable().getTrainType();
        switch (trainType) {
            case "急行":
                return imageIconExp;
            case "準急":
                return imageIconSemiExp;
            default:
                return imageIconLocal;
        }
    }

    @Override
    public Color getTypeColor(TrainData trainData) {
        switch (trainData.getTimeTable().getTrainType()) {
            case "急行":
                return COLOR_EXP;
            case "準急":
                return COLOR_SEMI_EXP;
            default:
                return COLOR_LOCAL;
        }
    }
}