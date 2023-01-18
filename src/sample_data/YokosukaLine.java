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

public class YokosukaLine extends LineData {
    // 横須賀線
    private Image imageIconLocalS;
    private Image imageIconLocalH;

    // 湘南新宿ライン
    private Image imageIcon_JS_Local;
    // 特急
    private Image imageIconLtdNEX;

    private static final Color LINE_COLOR = new Color(0, 63, 108);
    private static final Color COLOR_JS_LINE = new Color(226, 31, 38);

    private static final Color COLOR_LOCAL = new Color(24, 166, 41);

    public YokosukaLine() {
        super();
        try {
            Image img;
            // 横須賀線
            imageIconLocalH = ImageIO.read(new File("icon/e217nk1.png"));
            img = ImageIO.read(new File("icon/e217n1.png"));
            imageIconLocalS = LineData.createEdgedImage(img, LINE_COLOR, 2);

            // 湘南新宿ライン
            img = ImageIO.read(new File("icon/e231tdk.png"));
            img = LineData.createEdgedImage(img, COLOR_JS_LINE, 2);
            imageIcon_JS_Local = LineData.createEdgedImage(img, COLOR_LOCAL, 2);

            imageIconLtdNEX = ImageIO.read(new File("icon/e259.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Color getLineColor() {
        return LINE_COLOR;
    }

    @Override
    public String getLineName() {
        return "横須賀線";
    }

    @Override
    public Point calcPosOnLinePath(float dist, Direction direction) {
        int offset;

        if (direction == Direction.OUTBOUND) {
            offset = +20;
        } else {
            offset = -20;
        }
        // 駅の長さ割合
        float staLen = 0.2f;

        EasyPathPoint[] epp = {
                // 東京 - 品川
                LineSegmentPath.getInstance(getDistProportion(2),
                        new Point(JRE_SubUrbanLine.ORIGIN.x + 1800, JRE_SubUrbanLine.ORIGIN.y + 300 + offset),
                        new Point(JRE_SubUrbanLine.ORIGIN.x + 1600, JRE_SubUrbanLine.ORIGIN.y + 300 + offset)),
                // 品川 - 西大井
                LineSegmentPath.getInstance(getDistProportion(3),
                        new Point(JRE_SubUrbanLine.ORIGIN.x + 1600, JRE_SubUrbanLine.ORIGIN.y + 300 + offset),
                        new Point(JRE_SubUrbanLine.ORIGIN.x + 1400, JRE_SubUrbanLine.ORIGIN.y + 100 + offset)),
                // 西大井 - 横浜
                LineSegmentPath.getInstance(getDistProportion(6),
                        new Point(JRE_SubUrbanLine.ORIGIN.x + 1400, JRE_SubUrbanLine.ORIGIN.y + 100 + offset),
                        new Point(JRE_SubUrbanLine.ORIGIN.x + 1200, JRE_SubUrbanLine.ORIGIN.y + 100 + offset)),
                // 横浜 - 戸塚
                LineSegmentPath.getInstance(getDistProportion(9),
                        new Point(JRE_SubUrbanLine.ORIGIN.x + 1200, JRE_SubUrbanLine.ORIGIN.y + 100 + offset),
                        new Point(JRE_SubUrbanLine.ORIGIN.x + 1000, JRE_SubUrbanLine.ORIGIN.y + 100 + offset)),
                // 戸塚 - 大船
                LineSegmentPath.getInstance(getDistProportion(10),
                        new Point(JRE_SubUrbanLine.ORIGIN.x + 1000, JRE_SubUrbanLine.ORIGIN.y + 100 + offset),
                        new Point(JRE_SubUrbanLine.ORIGIN.x + 900, JRE_SubUrbanLine.ORIGIN.y + 200 + offset)),
                // 大船 - 逗子
                LineSegmentPath.getInstance(getDistProportion(13),
                        new Point(JRE_SubUrbanLine.ORIGIN.x + 900, JRE_SubUrbanLine.ORIGIN.y + 200 + offset),
                        new Point(JRE_SubUrbanLine.ORIGIN.x + 800, JRE_SubUrbanLine.ORIGIN.y + 300 + offset)),
                // 逗子 - 横須賀
                LineSegmentPath.getInstance(getDistProportion(16),
                        new Point(JRE_SubUrbanLine.ORIGIN.x + 800, JRE_SubUrbanLine.ORIGIN.y + 300 + offset),
                        new Point(JRE_SubUrbanLine.ORIGIN.x + 700, JRE_SubUrbanLine.ORIGIN.y + 300 + offset)),
                // 横須賀 - 衣笠
                SingleTrackLinePath.getInstance(getDistProportion(17),
                        new Point(0, offset), staLen,
                        new Point(JRE_SubUrbanLine.ORIGIN.x + 700, JRE_SubUrbanLine.ORIGIN.y + 300),
                        new Point(JRE_SubUrbanLine.ORIGIN.x + 650, JRE_SubUrbanLine.ORIGIN.y + 300)),
                // 衣笠 - 久里浜
                SingleTrackLinePath.getInstance(getDistProportion(18),
                        new Point(0, offset), staLen,
                        new Point(JRE_SubUrbanLine.ORIGIN.x + 650, JRE_SubUrbanLine.ORIGIN.y + 300),
                        new Point(JRE_SubUrbanLine.ORIGIN.x + 600, JRE_SubUrbanLine.ORIGIN.y + 300)),
        };
        return generateEasyPathPoint(epp, dist);
    }

    @Override
    protected String getStationDataCsvPath() {
        return "time_table/yokosuka_line_station.csv";
    }

    @Override
    protected String getTimeTableOutCsvPath() {
        return "time_table/yokosuka_line_weekdays_out.csv";
    }

    @Override
    protected String getTimeTableInCsvPath() {
        return "time_table/yokosuka_line_weekdays_in.csv";
    }

    @Override
    public Image getIconImg(TrainData trainData) {
        String trainType = trainData.getTimeTable().getTrainType();
        if (trainType.equals("特急")) {
            String trainName = trainData.getTimeTable().getTrainName();
            switch (trainName) {
                default:
                    return imageIconLtdNEX;
            }
        }

        String trainID = trainData.getTimeTable().getTrainID();
        char alphabet = trainID.charAt(trainID.length() - 1);
        switch (alphabet) {
            case 'Y':
                // 湘南新宿ライン
                return imageIcon_JS_Local;
            case 'H':
                // 区間電車
                return imageIconLocalH;
            default:
                return imageIconLocalS;
        }
    }

    @Override
    public Color getTypeColor(TrainData trainData) {
        switch (trainData.getTimeTable().getTrainType()) {
            case "★彡": // 寝台特急
                return Color.RED;
            case "特急":
                return Color.RED;
            default:
                return COLOR_LOCAL;
        }
    }
}