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

public class TakasakiLine extends LineData {
    // 高崎線
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

    // 特急
    private Image imageIconLtdAkagi;

    private static final Color COLOR_LOCAL = new Color(24, 166, 41);
    private static final Color COLOR_RAPID = new Color(246, 139, 30);
    private static final Color COLOR_COMMUTER_RAPID = new Color(96, 24, 134);
    private static final Color COLOR_SPECIAL_RAPID = new Color(51, 204, 255);

    private static final Color COLOR_JT_LINE = new Color(128, 0, 128);
    private static final Color COLOR_JS_LINE = new Color(226, 31, 38);

    public TakasakiLine() {
        super();
        try {
            Image img;
            // 高崎線（E233）
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

            imageIconLtdAkagi = ImageIO.read(new File("icon/e651f.png"));
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
        return "高崎線";
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
                // 大宮 - 上尾
                LineSegmentPath.getInstance(getDistProportion(7),
                        new Point(JRE_SubUrbanLine.ORIGIN.x + 2300, JRE_SubUrbanLine.ORIGIN.y + 200 + offset),
                        new Point(JRE_SubUrbanLine.ORIGIN.x + 2400, JRE_SubUrbanLine.ORIGIN.y + 100 + offset)),
                // 上尾 - 高崎
                LineSegmentPath.getInstance(getDistProportion(23),
                        new Point(JRE_SubUrbanLine.ORIGIN.x + 2400, JRE_SubUrbanLine.ORIGIN.y + 100 + offset),
                        new Point(JRE_SubUrbanLine.ORIGIN.x + 3300, JRE_SubUrbanLine.ORIGIN.y + 100 + offset)),
        };
        return generateEasyPathPoint(epp, dist);
    }

    @Override
    protected String getStationDataCsvPath() {
        return "time_table/takasaki_line_station.csv";
    }

    @Override
    protected String getTimeTableOutCsvPath() {
        return "time_table/takasaki_line_weekdays_out.csv";
    }

    @Override
    protected String getTimeTableInCsvPath() {
        return "time_table/takasaki_line_weekdays_in.csv";
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
                    case "特快":
                        return imageIcon_JS_SpRapid;
                    case "快速":
                        return imageIcon_JS_Rapid;
                    default:
                        return imageIcon_JS_Local;
                }
            default:
                break;
        }
        // 東海道線
        switch (trainType) {
            case "特急":
                return imageIconLtdAkagi;
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