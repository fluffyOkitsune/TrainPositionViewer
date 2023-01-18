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

public class JobanLine extends LineData {
    // 常磐線
    private Image imageIcon_JJ_Local;
    private Image imageIcon_JJ_SpLapid;

    // 特急
    private Image imageIconLtdHitachi;

    private static final Color COLOR_LOCAL = new Color(0, 63, 108);
    private static final Color COLOR_SPECIAL_RAPID = new Color(51, 204, 255);

    public JobanLine() {
        super();
        try {
            Image img;
            // 常磐線
            img = ImageIO.read(new File("icon/e531s.png"));
            imageIcon_JJ_Local = LineData.createEdgedImage(img, COLOR_LOCAL, 2);
            imageIcon_JJ_SpLapid = LineData.createEdgedImage(img, COLOR_SPECIAL_RAPID, 2);

            imageIconLtdHitachi = ImageIO.read(new File("icon/e657.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static final Color LINE_COLOR = COLOR_LOCAL;

    @Override
    public Color getLineColor() {
        return LINE_COLOR;
    }

    @Override
    public String getLineName() {
        return "常磐線";
    }

    @Override
    public Point calcPosOnLinePath(float dist, Direction direction) {
        int offset;

        if (direction == Direction.OUTBOUND) {
            offset = -20;
        } else {
            offset = +20;
        }

        EasyPathPoint[] epp = {
                // 上野 - 南千住
                LineSegmentPath.getInstance(getDistProportion(3),
                        new Point(JRE_SubUrbanLine.ORIGIN.x + 1900, JRE_SubUrbanLine.ORIGIN.y + 200 + offset),
                        new Point(JRE_SubUrbanLine.ORIGIN.x + 2000, JRE_SubUrbanLine.ORIGIN.y + 300 + offset)),
                // 南千住 - 我孫子
                LineSegmentPath.getInstance(getDistProportion(7),
                        new Point(JRE_SubUrbanLine.ORIGIN.x + 2000, JRE_SubUrbanLine.ORIGIN.y + 300 + offset),
                        new Point(JRE_SubUrbanLine.ORIGIN.x + 2300, JRE_SubUrbanLine.ORIGIN.y + 300 + offset)),
                // 我孫子 - 取手
                LineSegmentPath.getInstance(getDistProportion(9),
                        new Point(JRE_SubUrbanLine.ORIGIN.x + 2300, JRE_SubUrbanLine.ORIGIN.y + 300 + offset),
                        new Point(JRE_SubUrbanLine.ORIGIN.x + 2400, JRE_SubUrbanLine.ORIGIN.y + 300 + offset)),
                // 取手 - いわき
                LineSegmentPath.getInstance(getDistProportion(42),
                        new Point(JRE_SubUrbanLine.ORIGIN.x + 2400, JRE_SubUrbanLine.ORIGIN.y + 300 + offset),
                        new Point(JRE_SubUrbanLine.ORIGIN.x + 3900, JRE_SubUrbanLine.ORIGIN.y + 300 + offset)),
        };
        return generateEasyPathPoint(epp, dist);
    }

    @Override
    protected String getStationDataCsvPath() {
        return "time_table/joban_line_station.csv";
    }

    @Override
    protected String getTimeTableOutCsvPath() {
        return "time_table/joban_line_weekdays_out.csv";
    }

    @Override
    protected String getTimeTableInCsvPath() {
        return "time_table/joban_line_weekdays_in.csv";
    }

    @Override
    public Image getIconImg(TrainData trainData) {
        switch (trainData.getTimeTable().getTrainType()) {
            case "特急":
                return imageIconLtdHitachi;
            case "特快":
                return imageIcon_JJ_SpLapid;
            default:
                return imageIcon_JJ_Local;
        }
    }

    @Override
    public Color getTypeColor(TrainData trainData) {
        switch (trainData.getTimeTable().getTrainType()) {
            case "特急":
                return Color.RED;
            case "特快":
                return COLOR_SPECIAL_RAPID;
            default:
                return COLOR_LOCAL;
        }
    }
}