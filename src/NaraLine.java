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

public class NaraLine extends LineData {
    private Image imageIconLocal;
    private Image imageIconRegRapid;
    private Image imageIconRapid;
    private Image imageIconMiyakojiRapid;

    public NaraLine() {
        super();
        try {
            imageIconLocal = ImageIO.read(new File("icon/w201ka1.png"));

            // 221系
            Image icon = ImageIO.read(new File("icon/w221n1p.png"));
            imageIconRegRapid = LineData.createEdgedImage(icon, COLOR_REG_RAPID, 2);
            imageIconRapid = LineData.createEdgedImage(icon, COLOR_RAPID, 2);
            imageIconMiyakojiRapid = LineData.createEdgedImage(icon, COLOR_MIYAKOJI_RAPID, 2);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static final Color LINE_COLOR = new Color(165, 42, 42);

    @Override
    public Color getLineColor() {
        return LINE_COLOR;
    }

    @Override
    public String getLineName() {
        return "奈良線";
    }

    private Point origin = new Point(200, 200);

    @Override
    public Point calcPositionOnLinePath(float dist, Direction direction) {
        int offset;

        if (direction == Direction.OUTBOUND) {
            offset = -30;
        } else {
            offset = 30;
        }

        float staDist = 0.01f;
        EasyPathPoint[] epp = {
                // 京都 - JR藤森間は複線
                LineSegmentPath.getInstance(getDistProportion(3),
                        new Point(origin.x + 0, origin.y + offset),
                        new Point(origin.x + 300, origin.y + offset)),
                LineSegmentPath.getInstance(getDistProportion(3) + staDist,
                        new Point(origin.x + 300, origin.y + offset),
                        new Point(origin.x + 320, origin.y)),
                // JR藤森 - 桃山
                LineSegmentPath.getInstance(getDistProportion(4) - staDist,
                        new Point(origin.x + 320, origin.y),
                        new Point(origin.x + 380, origin.y)),
                // 桃山（交換可能）
                LineSegmentPath.getInstance(getDistProportion(4),
                        new Point(origin.x + 380, origin.y),
                        new Point(origin.x + 400, origin.y + offset)),
                LineSegmentPath.getInstance(getDistProportion(4) + staDist,
                        new Point(origin.x + 400, origin.y + offset),
                        new Point(origin.x + 420, origin.y)),
                // 桃山 - 六地蔵
                LineSegmentPath.getInstance(getDistProportion(5) - staDist,
                        new Point(origin.x + 420, origin.y),
                        new Point(origin.x + 480, origin.y)),
                // 六地蔵（交換可能）
                LineSegmentPath.getInstance(getDistProportion(5),
                        new Point(origin.x + 480, origin.y),
                        new Point(origin.x + 500, origin.y + offset)),
                LineSegmentPath.getInstance(getDistProportion(5) + staDist,
                        new Point(origin.x + 500, origin.y + offset),
                        new Point(origin.x + 520, origin.y)),
                // 六地蔵 - 木幡
                LineSegmentPath.getInstance(getDistProportion(6) - staDist,
                        new Point(origin.x + 520, origin.y),
                        new Point(origin.x + 580, origin.y)),
                // 木幡（交換可能）
                LineSegmentPath.getInstance(getDistProportion(6),
                        new Point(origin.x + 580, origin.y),
                        new Point(origin.x + 600, origin.y + offset)),
                LineSegmentPath.getInstance(getDistProportion(6) + staDist,
                        new Point(origin.x + 600, origin.y + offset),
                        new Point(origin.x + 620, origin.y)),
                // 木幡 - 黄檗
                LineSegmentPath.getInstance(getDistProportion(7) - staDist,
                        new Point(origin.x + 620, origin.y),
                        new Point(origin.x + 680, origin.y)),
                // 黄檗（交換可能）
                LineSegmentPath.getInstance(getDistProportion(7),
                        new Point(origin.x + 680, origin.y),
                        new Point(origin.x + 700, origin.y + offset)),
                LineSegmentPath.getInstance(getDistProportion(7) + staDist,
                        new Point(origin.x + 700, origin.y + offset),
                        new Point(origin.x + 720, origin.y)),
                // 黄檗 - 宇治
                LineSegmentPath.getInstance(getDistProportion(8) - staDist,
                        new Point(origin.x + 720, origin.y),
                        new Point(origin.x + 780, origin.y)),
                // 宇治 - 新田間は複線
                LineSegmentPath.getInstance(getDistProportion(8),
                        new Point(origin.x + 780, origin.y),
                        new Point(origin.x + 800, origin.y + offset)),
                LineSegmentPath.getInstance(getDistProportion(10),
                        new Point(origin.x + 800, origin.y + offset),
                        new Point(origin.x + 1000, origin.y + offset)),
                LineSegmentPath.getInstance(getDistProportion(10) + staDist,
                        new Point(origin.x + 1000, origin.y + offset),
                        new Point(origin.x + 1020, origin.y)),
                // 新田 - 城陽
                LineSegmentPath.getInstance(getDistProportion(11) - staDist,
                        new Point(origin.x + 1020, origin.y),
                        new Point(origin.x + 1080, origin.y)),
                // 城陽（交換可能）
                LineSegmentPath.getInstance(getDistProportion(11),
                        new Point(origin.x + 1080, origin.y),
                        new Point(origin.x + 1100, origin.y + offset)),
                LineSegmentPath.getInstance(getDistProportion(11) + staDist,
                        new Point(origin.x + 1100, origin.y + offset),
                        new Point(origin.x + 1120, origin.y)),
                // 城陽 - 長池
                LineSegmentPath.getInstance(getDistProportion(12) - staDist,
                        new Point(origin.x + 1120, origin.y),
                        new Point(origin.x + 1180, origin.y)),
                // 長池（交換可能）
                LineSegmentPath.getInstance(getDistProportion(12),
                        new Point(origin.x + 1180, origin.y),
                        new Point(origin.x + 1200, origin.y + offset)),
                LineSegmentPath.getInstance(getDistProportion(12) + staDist,
                        new Point(origin.x + 1200, origin.y + offset),
                        new Point(origin.x + 1220, origin.y)),
                // 長池 - 山城青谷
                LineSegmentPath.getInstance(getDistProportion(13) - staDist,
                        new Point(origin.x + 1220, origin.y),
                        new Point(origin.x + 1280, origin.y)),
                // 山城青谷（交換可能）
                LineSegmentPath.getInstance(getDistProportion(13),
                        new Point(origin.x + 1280, origin.y),
                        new Point(origin.x + 1300, origin.y + offset)),
                LineSegmentPath.getInstance(getDistProportion(13) + staDist,
                        new Point(origin.x + 1300, origin.y + offset),
                        new Point(origin.x + 1320, origin.y)),
                // 山城青谷 - 山城多賀
                LineSegmentPath.getInstance(getDistProportion(14) - staDist,
                        new Point(origin.x + 1320, origin.y),
                        new Point(origin.x + 1380, origin.y)),
                // 山城多賀（交換可能）
                LineSegmentPath.getInstance(getDistProportion(14),
                        new Point(origin.x + 1380, origin.y),
                        new Point(origin.x + 1400, origin.y + offset)),
                LineSegmentPath.getInstance(getDistProportion(14) + staDist,
                        new Point(origin.x + 1400, origin.y + offset),
                        new Point(origin.x + 1420, origin.y)),
                // 山城多賀 - 玉水
                LineSegmentPath.getInstance(getDistProportion(15) - staDist,
                        new Point(origin.x + 1420, origin.y),
                        new Point(origin.x + 1480, origin.y)),
                // 玉水（交換可能）
                LineSegmentPath.getInstance(getDistProportion(15),
                        new Point(origin.x + 1480, origin.y),
                        new Point(origin.x + 1500, origin.y + offset)),
                LineSegmentPath.getInstance(getDistProportion(15) + staDist,
                        new Point(origin.x + 1500, origin.y + offset),
                        new Point(origin.x + 1520, origin.y)),
                // 玉水 - 棚倉
                LineSegmentPath.getInstance(getDistProportion(16) - staDist,
                        new Point(origin.x + 1520, origin.y),
                        new Point(origin.x + 1580, origin.y)),
                // 棚倉（交換可能）
                LineSegmentPath.getInstance(getDistProportion(16),
                        new Point(origin.x + 1580, origin.y),
                        new Point(origin.x + 1600, origin.y + offset)),
                LineSegmentPath.getInstance(getDistProportion(16) + staDist,
                        new Point(origin.x + 1600, origin.y + offset),
                        new Point(origin.x + 1620, origin.y)),
                // 棚倉 - 上狛
                LineSegmentPath.getInstance(getDistProportion(17) - staDist,
                        new Point(origin.x + 1620, origin.y),
                        new Point(origin.x + 1680, origin.y)),
                // 上狛（交換可能）
                LineSegmentPath.getInstance(getDistProportion(17),
                        new Point(origin.x + 1680, origin.y),
                        new Point(origin.x + 1700, origin.y + offset)),
                LineSegmentPath.getInstance(getDistProportion(17) + staDist,
                        new Point(origin.x + 1700, origin.y + offset),
                        new Point(origin.x + 1720, origin.y)),
                // 上狛 - 木津
                LineSegmentPath.getInstance(getDistProportion(18) - staDist,
                        new Point(origin.x + 1720, origin.y),
                        new Point(origin.x + 1780, origin.y)),
                // 木津
                LineSegmentPath.getInstance(getDistProportion(18),
                        new Point(origin.x + 1780, origin.y),
                        new Point(origin.x + 1800, origin.y + offset)),
                // 木津 - 奈良間は複線
                LineSegmentPath.getInstance(getDistProportion(20),
                        new Point(origin.x + 1800, origin.y + offset),
                        new Point(origin.x + 2000, origin.y + offset)),
                // 終わり
                LineSegmentPath.getInstance(Float.MAX_VALUE,
                        new Point(origin.x + 2000, origin.y + offset),
                        new Point(origin.x + 2000, origin.y + offset))
        };
        return generateEasyPathPoint(epp, dist);
    }

    @Override
    protected String getStationDataCsvPath() {
        return "time_table/nara_line_station.csv";
    }

    @Override
    protected String getTimeTableOutCsvPath() {
        return "time_table/nara_line_weekdays_out.csv";
    }

    @Override
    protected String getTimeTableInCsvPath() {
        return "time_table/nara_line_weekdays_in.csv";
    }

    @Override
    public Image getIconImg(TrainData trainData) {
        String trainType = trainData.getTimeTable().trainType;
        switch (trainType) {
            case "みやこ":
                return imageIconMiyakojiRapid;
            case "快速":
                return imageIconRapid;
            case "区快":
                return imageIconRegRapid;
            default:
                return imageIconLocal;
        }
    }

    private static Color COLOR_LOCAL = Color.DARK_GRAY;
    private static Color COLOR_REG_RAPID = new Color(0, 102, 33);
    private static Color COLOR_RAPID = new Color(255, 102, 0);
    private static Color COLOR_MIYAKOJI_RAPID = new Color(165, 42, 42);

    @Override
    public Color getTypeColor(TrainData trainData) {
        String trainType = trainData.getTimeTable().trainType;
        switch (trainType) {
            case "みやこ":
                return COLOR_MIYAKOJI_RAPID;
            case "快速":
                return COLOR_RAPID;
            case "区快":
                return COLOR_REG_RAPID;
            default:
                return COLOR_LOCAL;
        }
    }
}