import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import data.Time;
import data.line_data.EasyPathPoint;
import data.line_data.LineData;
import data.line_data.LineSegmentPath;
import data.train_data.TrainData;

class OumeLine extends LineData {
    private Image imageIconOume;
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

    OumeLine() {
        super();
        try {
            Image img;
            // 青梅線（縁取りなし）
            imageIconOume = ImageIO.read(new File("icon/e233or.png"));

            // 中央線
            img = ImageIO.read(new File("icon/e233kor.png"));
            imageIconChuoLocal = LineData.createEdgedImage(img, COLOR_LOCAL, 2);
            imageIconChuoRapid = LineData.createEdgedImage(img, COLOR_RAPID, 2);
            imageIconChuoComRapid = LineData.createEdgedImage(img, COLOR_COM_RAPID, 2);
            imageIconChuoSpRapid = LineData.createEdgedImage(img, COLOR_SP_RAPID, 2);
            imageIconChuoComSpRapid = LineData.createEdgedImage(img, COLOR_COM_SP_RAIPD, 2);

            // ライナー
            imageIconLiner = ImageIO.read(new File("icon/e257-2.png"));

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
        return "青梅線";
    }

    private Point origin = new Point(100, 100);

    @Override
    protected String getStationDataCsvPath() {
        return "time_table/oume_line_station.csv";
    }

    @Override
    protected String getTimeTableOutCsvPath() {
        return "time_table/oume_line_weekdays_out.csv";
    }

    @Override
    protected String getTimeTableInCsvPath() {
        return "time_table/oume_line_weekdays_in.csv";
    }

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
                // 立川 - 拝島
                LineSegmentPath.getInstance(getDistProportion(5),
                        new Point(origin.x + 1500, origin.y + 1000 + offset),
                        new Point(origin.x + 1300, origin.y + 800 + offset)),
                // 拝島 - 青梅
                LineSegmentPath.getInstance(getDistProportion(12),
                        new Point(origin.x + 1300, origin.y + 800 + offset),
                        new Point(origin.x + 1000, origin.y + 800 + offset)),
                // 青梅 - 奥多摩
                LineSegmentPath.getInstance(1.0f,
                        new Point(origin.x + 1000, origin.y + 800 + offset),
                        new Point(origin.x + 300, origin.y + 100 + offset)),
                // 終わり
                LineSegmentPath.getInstance(Float.MAX_VALUE,
                        new Point(origin.x + 300, origin.y + 100 + offset),
                        new Point(origin.x + 300, origin.y + 100 + offset))
        };
        return generateEasyPathPoint(epp, dist);
    }

    @Override
    public Image getIconImg(TrainData trainData) {
        String trainType = trainData.getTimeTable().trainType;
        switch (trainType) {
            case "ﾗｲﾅｰ":
                return imageIconLiner;
            case "特急":
                return imageIconLTD;
            default:
                break;
        }

        String trainID = trainData.getTimeTable().trainID;
        char alphabet = trainID.charAt(trainID.length() - 1);
        switch (alphabet) {
            case 'T':
            case 'H':
                // 中央線の電車
                break;
            default:
                return imageIconOume;
        }

        // 中央線の電車
        switch (trainType) {
            case "快速":
                return imageIconChuoRapid;
            case "通快":
                return imageIconChuoComRapid;
            case "特快":
                return imageIconChuoSpRapid;
            case "通特":
                return imageIconChuoComSpRapid;
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