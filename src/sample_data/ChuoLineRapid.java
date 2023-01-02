package sample_data;

import java.awt.Color;
import java.awt.Image;
import java.awt.Point;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import data.line_data.ArcPath;
import data.line_data.EasyPathPoint;
import data.line_data.LineData;
import data.line_data.LineSegmentPath;
import data.train_data.TrainData;

public class ChuoLineRapid extends LineData {
    private Image imageIconSobuLocal;
    private Image imageIconChuoLocal;
    private Image imageIconChuoRapid;
    private Image imageIconChuoComRapid;
    private Image imageIconChuoSpRapid;
    private Image imageIconChuoComSpRapid;
    private Image imageIconLiner;
    private Image imageIconLTD;

    private static final Color COLOR_LOCAL = new Color(255, 212, 0);
    private static final Color COLOR_RAPID = new Color(241, 90, 34);
    private static final Color COLOR_COM_RAPID = new Color(96, 24, 134);
    private static final Color COLOR_SP_RAPID = new Color(0, 0, 160);
    private static final Color COLOR_COM_SP_RAIPD = new Color(255, 128, 192);

    public ChuoLineRapid() {
        super();
        try {
            Image img;
            // 総武線（縁取りなし）
            imageIconSobuLocal = ImageIO.read(new File("icon/e231so3.png"));

            // 中央線
            img = ImageIO.read(new File("icon/e233kor.png"));
            imageIconChuoLocal = LineData.createEdgedImage(img, COLOR_LOCAL, 2);
            imageIconChuoRapid = LineData.createEdgedImage(img, COLOR_RAPID, 2);
            imageIconChuoComRapid = LineData.createEdgedImage(img, COLOR_COM_RAPID, 2);
            imageIconChuoSpRapid = LineData.createEdgedImage(img, COLOR_SP_RAPID, 2);
            imageIconChuoComSpRapid = LineData.createEdgedImage(img, COLOR_COM_SP_RAIPD, 2);

            // ライナー
            imageIconLiner = ImageIO.read(new File("icon/e257-2.png"));

            // 特急（縁取りなし）
            imageIconLTD = ImageIO.read(new File("icon/e353.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static final Color LINE_COLOR = new Color(241, 90, 34);

    @Override
    public Color getLineColor() {
        return LINE_COLOR;
    }

    @Override
    public String getLineName() {
        return "中央線快速";
    }

    @Override
    protected String getStationDataCsvPath() {
        return "time_table/chuo_line_rapid_station.csv";
    }

    @Override
    protected String getTimeTableOutCsvPath() {
        return "time_table/chuo_line_rapid_weekdays_out.csv";
    }

    @Override
    protected String getTimeTableInCsvPath() {
        return "time_table/chuo_line_rapid_weekdays_in.csv";
    }

    private Point origin = new Point(100, 100);

    @Override
    public Point calcPositionOnLinePath(float dist, Direction direction) {
        int offset;

        // 西（左）方向が下り
        if (direction == Direction.OUTBOUND) {
            offset = +30;
        } else {
            offset = -30;
        }

        EasyPathPoint[] epp = {
                // 東京 - 神田
                LineSegmentPath.getInstance(getDistProportion(1),
                        new Point(origin.x + 3000 - offset, origin.y + 1100),
                        new Point(origin.x + 3000 - offset, origin.y + 1050)),
                // 神田 - お茶の水
                ArcPath.getInstance(getDistProportion(2),
                        new Point(origin.x + 2950, origin.y + 1050),
                        50 - offset, 0, -90),
                // お茶の水 - 新宿
                LineSegmentPath.getInstance(getDistProportion(4),
                        new Point(origin.x + 2950, origin.y + 1000 + offset),
                        new Point(origin.x + 2500, origin.y + 1000 + offset)),
                // 新宿 - 三鷹
                LineSegmentPath.getInstance(getDistProportion(11),
                        new Point(origin.x + 2500, origin.y + 1000 + offset),
                        new Point(origin.x + 2000, origin.y + 1000 + offset)),
                // 三鷹 - 立川
                LineSegmentPath.getInstance(getDistProportion(18),
                        new Point(origin.x + 2000, origin.y + 1000 + offset),
                        new Point(origin.x + 1500, origin.y + 1000 + offset)),
                // 立川 - 八王子
                LineSegmentPath.getInstance(getDistProportion(21),
                        new Point(origin.x + 1500, origin.y + 1000 + offset),
                        new Point(origin.x + 1300, origin.y + 1200 + offset)),
                // 八王子 - 高尾
                LineSegmentPath.getInstance(getDistProportion(23),
                        new Point(origin.x + 1300, origin.y + 1200 + offset),
                        new Point(origin.x + 1100, origin.y + 1200 + offset)),
                // 終わり
                LineSegmentPath.getInstance(Float.MAX_VALUE,
                        new Point(origin.x + 1100, origin.y + 1200 + offset),
                        new Point(origin.x + 1100, origin.y + 1200 + offset))
        };
        return generateEasyPathPoint(epp, dist);
    }

    @Override
    public Image getIconImg(TrainData trainData) {
        String trainID = trainData.getTimeTable().trainID;
        char alphabet = trainID.charAt(trainID.length() - 1);
        switch (alphabet) {
            case 'B':
            case 'C':
                // 総武線の電車
                return imageIconSobuLocal;
            default:
                // 中央線の電車
                break;
        }

        String trainType = trainData.getTimeTable().trainType;
        switch (trainType) {
            case "快速":
                return imageIconChuoRapid;
            case "通快":
                return imageIconChuoComRapid;
            case "特快":
                return imageIconChuoSpRapid;
            case "通特":
                return imageIconChuoComSpRapid;
            case "ﾗｲﾅｰ":
                return imageIconLiner;
            case "特急":
                return imageIconLTD;
            default:
                return imageIconChuoLocal;
        }
    }

    @Override
    public Color getTypeColor(TrainData trainData) {
        String trainType = trainData.getTimeTable().trainType;
        switch (trainType) {
            case "快速":
                return COLOR_RAPID;
            case "通快":
                return COLOR_COM_RAPID;
            case "特快":
                return COLOR_SP_RAPID;
            case "通特":
                return COLOR_COM_SP_RAIPD;
            case "特急":
            case "ﾗｲﾅｰ":
                return Color.RED;
            default:
                return COLOR_LOCAL;
        }
    }
}