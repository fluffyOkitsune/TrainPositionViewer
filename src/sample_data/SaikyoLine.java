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
import data.line_data.SingleTrackLinePath;
import data.train_data.TrainData;

public class SaikyoLine extends LineData {
    // 埼京線
    private Image imageIcon_K_Local;
    private Image imageIcon_K_Rapid;
    private Image imageIcon_K_ComRapid;
    // 埼京線
    private Image imageIcon_T_Local;
    private Image imageIcon_T_Rapid;
    private Image imageIcon_T_ComRapid;

    private static final Color COLOR_LOCAL = new Color(46, 139, 87);
    private static final Color COLOR_RAPID = new Color(0, 153, 255);
    private static final Color COLOR_COMMUTER_RAPID = new Color(255, 0, 102);

    public SaikyoLine() {
        super();
        try {
            Image img;

            // JR
            img = ImageIO.read(new File("icon/e233sk.png"));
            imageIcon_K_Local = LineData.createEdgedImage(img, COLOR_LOCAL, 2);
            imageIcon_K_Rapid = LineData.createEdgedImage(img, COLOR_RAPID, 2);
            imageIcon_K_ComRapid = LineData.createEdgedImage(img, COLOR_COMMUTER_RAPID, 2);

            // TWR
            img = ImageIO.read(new File("icon/twr70000.png"));
            imageIcon_T_Local = LineData.createEdgedImage(img, COLOR_LOCAL, 2);
            imageIcon_T_Rapid = LineData.createEdgedImage(img, COLOR_RAPID, 2);
            imageIcon_T_ComRapid = LineData.createEdgedImage(img, COLOR_COMMUTER_RAPID, 2);

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
        return "埼京線";
    }

    @Override
    public Point calcPosOnLinePath(float dist, Direction direction) {
        int offset;

        if (direction == Direction.OUTBOUND) {
            offset = -20;
        } else {
            offset = +20;
        }

        // 駅の長さ割合
        float staLen = 0.2f;

        EasyPathPoint[] epp = {
                // 大崎 - 新宿
                LineSegmentPath.getInstance(getDistProportion(3),
                        new Point(JRE_SubUrbanLine.ORIGIN.x + 1600, JRE_SubUrbanLine.ORIGIN.y + 100 + offset),
                        new Point(JRE_SubUrbanLine.ORIGIN.x + 1800, JRE_SubUrbanLine.ORIGIN.y + 100 + offset)),
                // 新宿 - 池袋
                LineSegmentPath.getInstance(getDistProportion(4),
                        new Point(JRE_SubUrbanLine.ORIGIN.x + 1800, JRE_SubUrbanLine.ORIGIN.y + 100 + offset),
                        new Point(JRE_SubUrbanLine.ORIGIN.x + 1900, JRE_SubUrbanLine.ORIGIN.y + 100 + offset)),
                // 池袋 - 赤羽
                LineSegmentPath.getInstance(getDistProportion(7),
                        new Point(JRE_SubUrbanLine.ORIGIN.x + 1900, JRE_SubUrbanLine.ORIGIN.y + 100 + offset),
                        new Point(JRE_SubUrbanLine.ORIGIN.x + 2000, JRE_SubUrbanLine.ORIGIN.y + 0 + offset)),
                // 池袋 - 大宮
                LineSegmentPath.getInstance(getDistProportion(18),
                        new Point(JRE_SubUrbanLine.ORIGIN.x + 2000, JRE_SubUrbanLine.ORIGIN.y + 0 + offset),
                        new Point(JRE_SubUrbanLine.ORIGIN.x + 2300, JRE_SubUrbanLine.ORIGIN.y + 0 + offset)),
                // 大宮 - 日進
                LineSegmentPath.getInstance(getDistProportion(19),
                        new Point(JRE_SubUrbanLine.ORIGIN.x + 2300, JRE_SubUrbanLine.ORIGIN.y + 0 + offset),
                        new Point(JRE_SubUrbanLine.ORIGIN.x + 2350, JRE_SubUrbanLine.ORIGIN.y + 0 + offset)),
                // 日進 - 西大宮
                SingleTrackLinePath.getInstance(getDistProportion(20),
                        new Point(0, offset), staLen,
                        new Point(JRE_SubUrbanLine.ORIGIN.x + 2350, JRE_SubUrbanLine.ORIGIN.y + 0),
                        new Point(JRE_SubUrbanLine.ORIGIN.x + 2400, JRE_SubUrbanLine.ORIGIN.y + 0)),
                // 西大宮 - 指扇
                SingleTrackLinePath.getInstance(getDistProportion(21),
                        new Point(0, offset), staLen,
                        new Point(JRE_SubUrbanLine.ORIGIN.x + 2400, JRE_SubUrbanLine.ORIGIN.y + 0),
                        new Point(JRE_SubUrbanLine.ORIGIN.x + 2450, JRE_SubUrbanLine.ORIGIN.y + 0)),
                // 指扇 - 南古谷
                SingleTrackLinePath.getInstance(getDistProportion(22),
                        new Point(0, offset), staLen,
                        new Point(JRE_SubUrbanLine.ORIGIN.x + 2450, JRE_SubUrbanLine.ORIGIN.y + 0),
                        new Point(JRE_SubUrbanLine.ORIGIN.x + 2500, JRE_SubUrbanLine.ORIGIN.y + 0)),
                // 南古谷 - 川越
                SingleTrackLinePath.getInstance(getDistProportion(23),
                        new Point(0, offset), staLen,
                        new Point(JRE_SubUrbanLine.ORIGIN.x + 2500, JRE_SubUrbanLine.ORIGIN.y + 0),
                        new Point(JRE_SubUrbanLine.ORIGIN.x + 2550, JRE_SubUrbanLine.ORIGIN.y + 0)),
        };
        return generateEasyPathPoint(epp, dist);
    }

    @Override
    protected String getStationDataCsvPath() {
        return "time_table/saikyo_line_station.csv";
    }

    @Override
    protected String getTimeTableOutCsvPath() {
        return "time_table/saikyo_line_weekdays_out.csv";
    }

    @Override
    protected String getTimeTableInCsvPath() {
        return "time_table/saikyo_line_weekdays_in.csv";
    }

    @Override
    public Image getIconImg(TrainData trainData) {
        String trainID = trainData.getTimeTable().getTrainID();
        String trainType = trainData.getTimeTable().getTrainType();
        char oprID = trainID.charAt(trainID.length() - 3);
        if (oprID < '8') {
            // JR車 (01K ~ 79K)
            switch (trainType) {
                case "通快":
                    return imageIcon_K_ComRapid;
                case "快速":
                    return imageIcon_K_Rapid;
                default:
                    return imageIcon_K_Local;
            }
        } else {
            // TWR車 (81T ~ )
            switch (trainType) {
                case "通快":
                    return imageIcon_T_ComRapid;
                case "快速":
                    return imageIcon_T_Rapid;
                default:
                    return imageIcon_T_Local;
            }
        }
    }

    @Override
    public Color getTypeColor(TrainData trainData) {
        switch (trainData.getTimeTable().getTrainType()) {
            case "通快":
                return COLOR_COMMUTER_RAPID;
            case "快速":
                return COLOR_RAPID;
            default:
                return COLOR_LOCAL;
        }
    }
}