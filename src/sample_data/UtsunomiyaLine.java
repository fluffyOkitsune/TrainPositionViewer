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

public class UtsunomiyaLine extends LineData {
    // 宇都宮線
    private Image imageIconLocal;
    private Image imageIconRapid;
    private Image imageIconComRapid;

    // 上野東京ライン
    private Image imageIcon_JT_Local;
    private Image imageIcon_JT_Rapid;

    // 湘南新宿ライン
    private Image imageIcon_JS_Local;
    private Image imageIcon_JS_Rapid;

    private static final Color COLOR_LOCAL = new Color(24, 166, 41);
    private static final Color COLOR_RAPID = new Color(246, 139, 30);
    private static final Color COLOR_COMMUTER_RAPID = new Color(96, 24, 134);

    private static final Color COLOR_JT_LINE = new Color(128, 0, 128);
    private static final Color COLOR_JS_LINE = new Color(226, 31, 38);

    public UtsunomiyaLine() {
        super();
        try {
            Image img;
            // 宇都宮線（E233）
            img = ImageIO.read(new File("icon/e233sh.png"));
            imageIconLocal = LineData.createEdgedImage(img, COLOR_LOCAL, 2);
            imageIconRapid = LineData.createEdgedImage(img, COLOR_RAPID, 2);
            imageIconComRapid = LineData.createEdgedImage(img, COLOR_COMMUTER_RAPID, 2);

            // 上野東京ライン（E233）
            img = ImageIO.read(new File("icon/e233sh.png"));
            img = LineData.createEdgedImage(img, COLOR_JT_LINE, 2);
            imageIcon_JT_Local = LineData.createEdgedImage(img, COLOR_LOCAL, 2);
            img = ImageIO.read(new File("icon/e233sh.png"));
            img = LineData.createEdgedImage(img, COLOR_JT_LINE, 2);
            imageIcon_JT_Rapid = LineData.createEdgedImage(img, COLOR_RAPID, 2);

            // 湘南新宿ライン
            img = ImageIO.read(new File("icon/e231tdk.png"));
            img = LineData.createEdgedImage(img, COLOR_JS_LINE, 2);
            imageIcon_JS_Local = LineData.createEdgedImage(img, COLOR_LOCAL, 2);
            img = ImageIO.read(new File("icon/e231tdk.png"));
            img = LineData.createEdgedImage(img, COLOR_JS_LINE, 2);
            imageIcon_JS_Rapid = LineData.createEdgedImage(img, COLOR_RAPID, 2);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static final Color LINE_COLOR = COLOR_RAPID;

    @Override
    public Color getLineColor() {
        return LINE_COLOR;
    }

    @Override
    public String getLineName() {
        return "宇都宮線";
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
                // 上野 - 赤羽
                LineSegmentPath.getInstance(getDistProportion(2),
                        new Point(JRE_SubUrbanLine.ORIGIN.x + 1900, JRE_SubUrbanLine.ORIGIN.y + 200 + offset),
                        new Point(JRE_SubUrbanLine.ORIGIN.x + 2000, JRE_SubUrbanLine.ORIGIN.y + 200 + offset)),
                // 赤羽 - 浦和
                LineSegmentPath.getInstance(getDistProportion(3),
                        new Point(JRE_SubUrbanLine.ORIGIN.x + 2000, JRE_SubUrbanLine.ORIGIN.y + 200 + offset),
                        new Point(JRE_SubUrbanLine.ORIGIN.x + 2200, JRE_SubUrbanLine.ORIGIN.y + 200 + offset)),
                // 浦和 - 大宮
                LineSegmentPath.getInstance(getDistProportion(5),
                        new Point(JRE_SubUrbanLine.ORIGIN.x + 2200, JRE_SubUrbanLine.ORIGIN.y + 200 + offset),
                        new Point(JRE_SubUrbanLine.ORIGIN.x + 2300, JRE_SubUrbanLine.ORIGIN.y + 200 + offset)),
                // 大宮 - 宇都宮
                LineSegmentPath.getInstance(getDistProportion(22),
                        new Point(JRE_SubUrbanLine.ORIGIN.x + 2300, JRE_SubUrbanLine.ORIGIN.y + 200 + offset),
                        new Point(JRE_SubUrbanLine.ORIGIN.x + 3300, JRE_SubUrbanLine.ORIGIN.y + 200 + offset)),
                // 宇都宮 - 黒磯
                LineSegmentPath.getInstance(getDistProportion(32),
                        new Point(JRE_SubUrbanLine.ORIGIN.x + 3300, JRE_SubUrbanLine.ORIGIN.y + 200 + offset),
                        new Point(JRE_SubUrbanLine.ORIGIN.x + 3700, JRE_SubUrbanLine.ORIGIN.y + 200 + offset)),
        };
        return generateEasyPathPoint(epp, dist);
    }

    @Override
    protected String getStationDataCsvPath() {
        return "time_table/utsunomiya_line_station.csv";
    }

    @Override
    protected String getTimeTableOutCsvPath() {
        return "time_table/utsunomiya_line_weekdays_out.csv";
    }

    @Override
    protected String getTimeTableInCsvPath() {
        return "time_table/utsunomiya_line_weekdays_in.csv";
    }

    @Override
    public Image getIconImg(TrainData trainData) {
        String trainID = trainData.getTimeTable().getTrainID();
        String trainType = trainData.getTimeTable().getTrainType();
        char alphabet = trainID.charAt(trainID.length() - 1);
        switch (alphabet) {
            case 'E':
                // 上野東京ライン
                switch (trainType) {
                    case "快速":
                        return imageIcon_JT_Rapid;
                    default:
                        return imageIcon_JT_Local;
                }
            case 'Y':
                // 湘南新宿ライン
                switch (trainType) {
                    case "快速":
                        return imageIcon_JS_Rapid;
                    default:
                        return imageIcon_JS_Local;
                }
            default:
                break;
        }
        // 宇都宮線
        switch (trainType) {
            case "通快":
                return imageIconComRapid;
            case "快速":
                return imageIconRapid;
            default:
                return imageIconLocal;
        }
    }

    @Override
    public Color getTypeColor(TrainData trainData) {
        switch (trainData.getTimeTable().getTrainType()) {
            case "特急":
                return Color.RED;
            case "通快":
                return COLOR_COMMUTER_RAPID;
            case "快速":
                return COLOR_RAPID;
            default:
                return COLOR_LOCAL;
        }
    }
}