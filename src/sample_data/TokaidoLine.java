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

public class TokaidoLine extends LineData {
    // 東海道線
    private Image imageIconLocal;
    private Image imageIconRapid;
    private Image imageIconComRapid;

    // 上野東京ライン
    private Image imageIcon_JT_Local;
    private Image imageIcon_JT_Rapid;

    // 湘南新宿ライン
    private Image imageIcon_JS_Local;
    private Image imageIcon_JS_Rapid;
    private Image imageIcon_JS_SpRapid;

    // 常磐線
    private Image imageIcon_JJ_Local;
    private Image imageIcon_JJ_Rapid;
    private Image imageIcon_JJ_SpLapid;

    // 特急
    private Image imageIconLtdOdoriko;
    private Image imageIconLtdSVO;
    private Image imageIconLtdHitachi;
    private Image imageIconSleepingLtd;

    private static final Color COLOR_LOCAL = new Color(24, 166, 41);
    private static final Color COLOR_RAPID = new Color(246, 139, 30);
    private static final Color COLOR_COMMUTER_RAPID = new Color(96, 24, 134);
    private static final Color COLOR_SPECIAL_RAPID = new Color(51, 204, 255);

    private static final Color COLOR_JJ_LOCAL = new Color(0, 208, 104);
    private static final Color COLOR_JJ_RAPID = new Color(0, 63, 108);

    private static final Color COLOR_JT_LINE = new Color(128, 0, 128);
    private static final Color COLOR_JS_LINE = new Color(226, 31, 38);

    public TokaidoLine() {
        super();
        try {
            Image img;
            // 東海道線（E233）
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
            img = ImageIO.read(new File("icon/e231tdk.png"));
            img = LineData.createEdgedImage(img, COLOR_JS_LINE, 2);
            imageIcon_JS_SpRapid = LineData.createEdgedImage(img, COLOR_SPECIAL_RAPID, 2);

            // 常磐線
            img = ImageIO.read(new File("icon/e531s.png"));
            imageIcon_JJ_Local = LineData.createEdgedImage(img, COLOR_JJ_LOCAL, 2);
            imageIcon_JJ_SpLapid = LineData.createEdgedImage(img, COLOR_SPECIAL_RAPID, 2);

            img = ImageIO.read(new File("icon/e231jy1.png"));
            imageIcon_JJ_Rapid = LineData.createEdgedImage(img, COLOR_JJ_RAPID, 2);

            imageIconLtdOdoriko = ImageIO.read(new File("icon/e185odj.png"));
            imageIconLtdSVO = ImageIO.read(new File("icon/e261.png"));
            imageIconLtdHitachi = ImageIO.read(new File("icon/e657.png"));
            imageIconSleepingLtd = ImageIO.read(new File("icon/w285.png"));
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
        return "東海道線";
    }

    @Override
    public Point calcPosOnLinePath(float dist, Direction direction) {
        int offset;

        if (direction == Direction.OUTBOUND) {
            offset = +20;
        } else {
            offset = -20;
        }

        EasyPathPoint[] epp = {
                // 東京 - 品川
                LineSegmentPath.getInstance(getDistProportion(2),
                        new Point(JRE_SubUrbanLine.ORIGIN.x + 1800, JRE_SubUrbanLine.ORIGIN.y + 200 + offset),
                        new Point(JRE_SubUrbanLine.ORIGIN.x + 1600, JRE_SubUrbanLine.ORIGIN.y + 200 + offset)),
                // 品川 - 横浜
                LineSegmentPath.getInstance(getDistProportion(4),
                        new Point(JRE_SubUrbanLine.ORIGIN.x + 1600, JRE_SubUrbanLine.ORIGIN.y + 200 + offset),
                        new Point(JRE_SubUrbanLine.ORIGIN.x + 1200, JRE_SubUrbanLine.ORIGIN.y + 200 + offset)),
                // 横浜 - 戸塚
                LineSegmentPath.getInstance(getDistProportion(5),
                        new Point(JRE_SubUrbanLine.ORIGIN.x + 1200, JRE_SubUrbanLine.ORIGIN.y + 200 + offset),
                        new Point(JRE_SubUrbanLine.ORIGIN.x + 1000, JRE_SubUrbanLine.ORIGIN.y + 200 + offset)),
                // 戸塚 - 大船
                LineSegmentPath.getInstance(getDistProportion(6),
                        new Point(JRE_SubUrbanLine.ORIGIN.x + 1000, JRE_SubUrbanLine.ORIGIN.y + 200 + offset),
                        new Point(JRE_SubUrbanLine.ORIGIN.x + 900, JRE_SubUrbanLine.ORIGIN.y + 200 + offset)),
                // 大船 - 熱海
                LineSegmentPath.getInstance(getDistProportion(20),
                        new Point(JRE_SubUrbanLine.ORIGIN.x + 900, JRE_SubUrbanLine.ORIGIN.y + 200 + offset),
                        new Point(JRE_SubUrbanLine.ORIGIN.x + 0, JRE_SubUrbanLine.ORIGIN.y + 200 + offset)),
        };
        return generateEasyPathPoint(epp, dist);
    }

    @Override
    protected String getStationDataCsvPath() {
        return "time_table/jre_tokaido_line_station.csv";
    }

    @Override
    protected String getTimeTableOutCsvPath() {
        return "time_table/jre_tokaido_line_weekdays_out.csv";
    }

    @Override
    protected String getTimeTableInCsvPath() {
        return "time_table/jre_tokaido_line_weekdays_in.csv";
    }

    @Override
    public Image getIconImg(TrainData trainData) {
        // 寝台特急
        if (trainData.getTimeTable().getTrainType().equals("★彡")) {
            return imageIconSleepingLtd;
        }
        if (trainData.getTimeTable().getTrainType().equals("特急")) {
            String trainName = trainData.getTimeTable().getTrainName();
            switch (trainName) {
                case "スーパービュー踊り子":
                    return imageIconLtdSVO;
                case "ひたち":
                case "ときわ":
                    return imageIconLtdHitachi;
                default:
                    return imageIconLtdOdoriko;
            }
        }

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
                    case "特快":
                        return imageIcon_JS_SpRapid;
                    case "快速":
                        return imageIcon_JS_Rapid;
                    default:
                        return imageIcon_JS_Local;
                }
            case 'H':
                // 常磐線快速
                return imageIcon_JJ_Rapid;
            case 'M':
                if (trainType.equals("特快") && trainID.length() == 5 && trainID.charAt(0) == '3') {
                    // 3***M の特快は常磐線
                    return imageIcon_JJ_SpLapid;
                } else if (trainID.length() == 5 && trainID.charAt(0) == '1') {
                    // 1***M の特快は常磐線
                    return imageIcon_JJ_Local;
                }
                break;
            default:
                break;
        }
        // 東海道線
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
            case "★彡": // 寝台特急
                return Color.RED;
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