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

public class ChuoSobuLine extends LineData {
    private Image imageIconSobuLocal;
    private Image imageIconTozaiLocal;
    private Image imageIconChuoLocal;
    private Image imageIconLTD;

    private static final Color COLOR_LOCAL = new Color(255, 212, 0);

    public ChuoSobuLine() {
        super();
        try {
            Image img;
            // 総武線
            imageIconSobuLocal = ImageIO.read(new File("icon/e231so3.png"));
            imageIconTozaiLocal = ImageIO.read(new File("icon/e231-8b1.png"));

            // 中央線（快速用の電車なので縁取りあり）
            img = ImageIO.read(new File("icon/e233kor.png"));
            imageIconChuoLocal = LineData.createEdgedImage(img, COLOR_LOCAL, 2);

            // 千葉発着のあずさ
            imageIconLTD = ImageIO.read(new File("icon/e353.png"));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static final Color LINE_COLOR = new Color(255, 212, 0);

    @Override
    public Color getLineColor() {
        return LINE_COLOR;
    }

    @Override
    public String getLineName() {
        return "中央・総武線";
    }

    @Override
    protected String getStationDataCsvPath() {
        return "time_table/chuo_line_local_station.csv";
    }

    @Override
    protected String getTimeTableOutCsvPath() {
        return "time_table/chuo_line_local_weekdays_out.csv";
    }

    @Override
    protected String getTimeTableInCsvPath() {
        return "time_table/chuo_line_local_weekdays_in.csv";
    }

    private Point origin = new Point(100, 100);

    @Override
    public Point calcPosOnLinePath(float dist, Direction direction) {
        int offset;

        // 西（左）方向が下り
        if (direction == Direction.OUTBOUND) {
            offset = +30;
        } else {
            offset = -30;
        }

        EasyPathPoint[] epp = {
                // 千葉 - 津田沼
                LineSegmentPath.getInstance(getDistProportion(6),
                        new Point(origin.x + 3600 - offset, origin.y + 1500),
                        new Point(origin.x + 3600 - offset, origin.y + 1000)),
                // 津田沼 - 船橋
                ArcPath.getInstance(getDistProportion(8),
                        new Point(origin.x + 3500, origin.y + 1000),
                        100 - offset, 0, -90),
                // 船橋 - 西船橋
                LineSegmentPath.getInstance(getDistProportion(9),
                        new Point(origin.x + 3500, origin.y + 900 + offset),
                        new Point(origin.x + 3450, origin.y + 900 + offset)),
                // 船橋 - お茶の水
                LineSegmentPath.getInstance(getDistProportion(21),
                        new Point(origin.x + 3450, origin.y + 900 + offset),
                        new Point(origin.x + 2950, origin.y + 900 + offset)),
                // お茶の水 - 新宿
                LineSegmentPath.getInstance(getDistProportion(29),
                        new Point(origin.x + 2950, origin.y + 900 + offset),
                        new Point(origin.x + 2500, origin.y + 900 + offset)),
                // 新宿 - 三鷹
                LineSegmentPath.getInstance(getDistProportion(38),
                        new Point(origin.x + 2500, origin.y + 900 + offset),
                        new Point(origin.x + 2000, origin.y + 900 + offset)),
                // 終わり
                LineSegmentPath.getInstance(Float.MAX_VALUE,
                        new Point(origin.x + 2000, origin.y + 900 + offset),
                        new Point(origin.x + 2000, origin.y + 900 + offset))
        };
        return generateEasyPathPoint(epp, dist);
    }

    @Override
    public Image getIconImg(TrainData trainData) {
        String trainID = trainData.getTimeTable().getTrainID();
        char alphabet = trainID.charAt(trainID.length() - 1);
        switch (alphabet) {
            case 'T':
            case 'H':
                // 中央線の電車
                return imageIconChuoLocal;
            case 'A':
            case 'Y':
                // 東西線の電車
                return imageIconTozaiLocal;
            default:
                // 総武線の電車
                break;
        }

        String trainType = trainData.getTimeTable().getTrainType();
        switch (trainType) {
            case "特急":
                return imageIconLTD;
            default:
                return imageIconSobuLocal;
        }
    }

    @Override
    public Color getTypeColor(TrainData trainData) {
        String trainType = trainData.getTimeTable().getTrainType();
        switch (trainType) {
            case "特急":
                return Color.RED;
            default:
                return COLOR_LOCAL;
        }
    }
}
