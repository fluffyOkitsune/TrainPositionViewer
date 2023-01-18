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

public class ShonanShinjukuLine extends LineData {
    // 湘南新宿ライン
    private Image imageIcon_JS_Local;
    private Image imageIcon_JS_Rapid;
    private Image imageIcon_JS_SpRapid;

    // 特急
    private Image imageIconLtdOdoriko;
    private Image imageIconLtdSVO;

    private static final Color COLOR_LOCAL = new Color(24, 166, 41);
    private static final Color COLOR_RAPID = new Color(246, 139, 30);
    private static final Color COLOR_COMMUTER_RAPID = new Color(96, 24, 134);
    private static final Color COLOR_SPECIAL_RAPID = new Color(51, 204, 255);

    private static final Color COLOR_JS_LINE = new Color(226, 31, 38);

    public ShonanShinjukuLine() {
        super();
        try {
            Image img;

            // 湘南新宿ライン
            img = ImageIO.read(new File("icon/e231tdk.png"));
            img = LineData.createEdgedImage(img, COLOR_JS_LINE, 2);
            imageIcon_JS_Local = LineData.createEdgedImage(img, COLOR_LOCAL, 2);
            img = ImageIO.read(new File("icon/e231tdk.png"));
            img = LineData.createEdgedImage(img, COLOR_JS_LINE, 2);
            imageIcon_JS_Rapid = LineData.createEdgedImage(img, COLOR_RAPID, 2);
            img = ImageIO.read(new File("icon/e231tdk.png"));
            img = LineData.createEdgedImage(img, COLOR_JS_LINE, 2);
            imageIcon_JS_SpRapid = LineData.createEdgedImage(img, COLOR_SPECIAL_RAPID, 2);

            imageIconLtdOdoriko = ImageIO.read(new File("icon/e185odj.png"));
            imageIconLtdSVO = ImageIO.read(new File("icon/e261.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static final Color LINE_COLOR = new Color(226, 31, 38);

    @Override
    public Color getLineColor() {
        return LINE_COLOR;
    }

    @Override
    public String getLineName() {
        return "湘南新宿ライン";
    }

    @Override
    public Point calcPosOnLinePath(float dist, Direction direction) {
        int offset;

        if (direction == Direction.OUTBOUND) {
            offset = +24;
        } else {
            offset = -24;
        }

        EasyPathPoint[] epp = {
                // 大宮 - 浦和
                LineSegmentPath.getInstance(getDistProportion(1),
                        new Point(JRE_SubUrbanLine.ORIGIN.x + 2300, JRE_SubUrbanLine.ORIGIN.y + 200 + offset),
                        new Point(JRE_SubUrbanLine.ORIGIN.x + 2200, JRE_SubUrbanLine.ORIGIN.y + 100 + offset)),
                // 浦和 - 赤羽
                LineSegmentPath.getInstance(getDistProportion(2),
                        new Point(JRE_SubUrbanLine.ORIGIN.x + 2200, JRE_SubUrbanLine.ORIGIN.y + 100 + offset),
                        new Point(JRE_SubUrbanLine.ORIGIN.x + 2000, JRE_SubUrbanLine.ORIGIN.y + 100 + offset)),
                // 赤羽 - 池袋
                LineSegmentPath.getInstance(getDistProportion(3),
                        new Point(JRE_SubUrbanLine.ORIGIN.x + 2000, JRE_SubUrbanLine.ORIGIN.y + 100 + offset),
                        new Point(JRE_SubUrbanLine.ORIGIN.x + 1900, JRE_SubUrbanLine.ORIGIN.y + 100 + offset)),
                // 池袋 - 新宿
                LineSegmentPath.getInstance(getDistProportion(4),
                        new Point(JRE_SubUrbanLine.ORIGIN.x + 1900, JRE_SubUrbanLine.ORIGIN.y + 100 + offset),
                        new Point(JRE_SubUrbanLine.ORIGIN.x + 1800, JRE_SubUrbanLine.ORIGIN.y + 100 + offset)),
                // 新宿 - 大崎
                LineSegmentPath.getInstance(getDistProportion(7),
                        new Point(JRE_SubUrbanLine.ORIGIN.x + 1800, JRE_SubUrbanLine.ORIGIN.y + 100 + offset),
                        new Point(JRE_SubUrbanLine.ORIGIN.x + 1600, JRE_SubUrbanLine.ORIGIN.y + 100 + offset)),
                // 大崎 - 西大井
                LineSegmentPath.getInstance(getDistProportion(8),
                        new Point(JRE_SubUrbanLine.ORIGIN.x + 1600, JRE_SubUrbanLine.ORIGIN.y + 100 + offset),
                        new Point(JRE_SubUrbanLine.ORIGIN.x + 1400, JRE_SubUrbanLine.ORIGIN.y + 100 + offset)),
                // 西大井 - 横浜
                LineSegmentPath.getInstance(getDistProportion(11),
                        new Point(JRE_SubUrbanLine.ORIGIN.x + 1400, JRE_SubUrbanLine.ORIGIN.y + 100 + offset),
                        new Point(JRE_SubUrbanLine.ORIGIN.x + 1200, JRE_SubUrbanLine.ORIGIN.y + 100 + offset)),
                // 横浜 - 戸塚
                LineSegmentPath.getInstance(getDistProportion(14),
                        new Point(JRE_SubUrbanLine.ORIGIN.x + 1200, JRE_SubUrbanLine.ORIGIN.y + 100 + offset),
                        new Point(JRE_SubUrbanLine.ORIGIN.x + 1000, JRE_SubUrbanLine.ORIGIN.y + 100 + offset)),
                // 戸塚 - 大船
                LineSegmentPath.getInstance(getDistProportion(15),
                        new Point(JRE_SubUrbanLine.ORIGIN.x + 1000, JRE_SubUrbanLine.ORIGIN.y + 100 + offset),
                        new Point(JRE_SubUrbanLine.ORIGIN.x + 900, JRE_SubUrbanLine.ORIGIN.y + 200 + offset)),
        };
        return generateEasyPathPoint(epp, dist);
    }

    @Override
    protected String getStationDataCsvPath() {
        return "time_table/shounan-shinjuku_line_station.csv";
    }

    @Override
    protected String getTimeTableOutCsvPath() {
        return "time_table/shounan-shinjuku_line_weekdays_out.csv";
    }

    @Override
    protected String getTimeTableInCsvPath() {
        return "time_table/shounan-shinjuku_line_weekdays_in.csv";
    }

    @Override
    public Image getIconImg(TrainData trainData) {
        if (trainData.getTimeTable().getTrainType().equals("特急")) {
            String trainName = trainData.getTimeTable().getTrainName();
            switch (trainName) {
                case "スーパービュー踊り子":
                    return imageIconLtdSVO;
                default:
                    return imageIconLtdOdoriko;
            }
        }

        String trainType = trainData.getTimeTable().getTrainType();
        // 湘南新宿ライン
        switch (trainType) {
            case "特快":
                return imageIcon_JS_SpRapid;
            case "快速":
                return imageIcon_JS_Rapid;
            default:
                return imageIcon_JS_Local;
        }
    }

    @Override
    public Color getTypeColor(TrainData trainData) {
        switch (trainData.getTimeTable().getTrainType()) {
            case "特急":
                return Color.RED;
            case "特快":
                return COLOR_SPECIAL_RAPID;
            case "通快":
                return COLOR_COMMUTER_RAPID;
            case "快速":
                return COLOR_RAPID;
            default:
                return COLOR_LOCAL;
        }
    }
}