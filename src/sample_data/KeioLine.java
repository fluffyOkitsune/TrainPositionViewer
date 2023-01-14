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

public class KeioLine extends LineData {
    private Image imageIconCommuter;
    private Image imageIconExpress;
    private Image imageIconLiner;
    private Image imageIconSubway;

    public KeioLine() {
        super();
        try {
            imageIconLiner = ImageIO.read(new File("icon/keio5003.png"));
            imageIconCommuter = ImageIO.read(new File("icon/keio7001n.png"));
            imageIconExpress = ImageIO.read(new File("icon/keio9031.png"));
            imageIconSubway = ImageIO.read(new File("icon/toky10520.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static final Color LINE_COLOR = new Color(221, 0, 119);

    @Override
    public Color getLineColor() {
        return LINE_COLOR;
    }

    @Override
    public String getLineName() {
        return "京王線";
    }

    @Override
    protected String getStationDataCsvPath() {
        return "time_table/keio_line_station.csv";
    }

    @Override
    protected String getTimeTableOutCsvPath() {
        return "time_table/keio_line_weekdays_out.csv";
    }

    @Override
    protected String getTimeTableInCsvPath() {
        return "time_table/keio_line_weekdays_in.csv";
    }

    private Point origin = new Point(200, 200);

    @Override
    public Point calcPosOnLinePath(float dist, Direction direction) {
        int offset = 0;
        if (direction == Direction.OUTBOUND) {
            offset = 20;
        }
        if (direction == Direction.INBOUND) {
            offset -= 20;
        }

        EasyPathPoint[] epp = {
                // 新宿 - 笹塚
                LineSegmentPath.getInstance(getDistProportion(1),
                        new Point(origin.x + 2000, origin.y + offset),
                        new Point(origin.x + 1900, origin.y + offset)),
                // 笹塚 - 調布
                LineSegmentPath.getInstance(getDistProportion(15),
                        new Point(origin.x + 1900, origin.y + offset),
                        new Point(origin.x + 1000, origin.y + offset)),
                // 調布 - 京王八王子
                LineSegmentPath.getInstance(getDistProportion(30),
                        new Point(origin.x + 1000, origin.y + offset),
                        new Point(origin.x + 200, origin.y + offset)),
                // 調布 - 京王八王子
                LineSegmentPath.getInstance(getDistProportion(31),
                        new Point(origin.x + 200, origin.y + offset),
                        new Point(origin.x + 0, origin.y + offset)),
                // 終わり
                LineSegmentPath.getInstance(Float.MAX_VALUE,
                        new Point(origin.x + 0, origin.y + offset),
                        new Point(origin.x + 0, origin.y + offset))
        };
        return generateEasyPathPoint(epp, dist);
    }

    @Override
    public Image getIconImg(TrainData trainData) {
        String trainID = trainData.getTimeTable().getTrainID();
        switch (trainID.substring(0, 2)) {
            case "18": // [急行]橋本行
            case "21": // [快速]高尾山口行
            case "28": // [快速]橋本行
            case "45": // [区急]高尾山口行
            case "46": // [区急]桜上水行
            case "48": // [区急]橋本行
                // 新宿線直通
                return imageIconSubway;
            default:
                // 京王線
        }

        switch (trainData.getTimeTable().getTrainType()) {
            case "ﾗｲﾅｰ":
                return imageIconLiner;
            case "特急":
            case "準特":
            case "急行":
                return imageIconExpress;
            case "区急":
            case "通快":
            case "快速":
            default:
                return imageIconCommuter;
        }
    }

    private static Color COLOR_LOCAL = new Color(255, 140, 0);
    private static Color COLOR_RAPID = new Color(0, 0, 255);
    private static Color COLOR_COM_RAPID = new Color(96, 24, 134);
    private static Color COLOR_SEMI_RAPID = new Color(128, 128, 0);
    private static Color COLOR_EXPRESS = new Color(32, 178, 170);
    private static Color COLOR_SEMI_LTD = new Color(255, 140, 0);
    private static Color COLOR_LTD = new Color(255, 20, 147);
    private static Color COLOR_LINER = new Color(0, 0, 0);

    @Override
    public Color getTypeColor(TrainData trainData) {
        switch (trainData.getTimeTable().getTrainType()) {
            case "ﾗｲﾅｰ":
                return COLOR_LINER;
            case "特急":
                return COLOR_LTD;
            case "準特":
                return COLOR_SEMI_LTD;
            case "急行":
                return COLOR_EXPRESS;
            case "区急":
                return COLOR_SEMI_RAPID;
            case "通快":
                return COLOR_COM_RAPID;
            case "快速":
                return COLOR_RAPID;
            default:
                return COLOR_LOCAL;
        }
    }
}