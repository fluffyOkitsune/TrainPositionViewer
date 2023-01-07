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

public class ToeiShinjukuLine extends LineData {
    private Image imageIconCommuter;
    private Image imageIconSubway;

    public ToeiShinjukuLine() {
        super();
        try {
            imageIconCommuter = ImageIO.read(new File("icon/keio7001n.png"));
            imageIconSubway = ImageIO.read(new File("icon/toky10520.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static final Color LINE_COLOR = new Color(108, 187, 90);

    @Override
    public Color getLineColor() {
        return LINE_COLOR;
    }

    @Override
    public String getLineName() {
        return "都営新宿線";
    }

    @Override
    protected String getStationDataCsvPath() {
        return "time_table/toei_shinjuku_station.csv";
    }

    @Override
    protected String getTimeTableOutCsvPath() {
        return "time_table/toei_shinjuku_line_out.csv";
    }

    @Override
    protected String getTimeTableInCsvPath() {
        return "time_table/toei_shinjuku_line_in.csv";
    }

    private Point origin = new Point(200, 200);

    @Override
    public Point calcPosOnLinePath(float dist, Direction direction) {
        int offset = 0;
        if (direction == Direction.OUTBOUND) {
            offset = -20;
        }
        if (direction == Direction.INBOUND) {
            offset = +20;
        }

        EasyPathPoint[] epp = {
                // 北野 - 高尾山口
                LineSegmentPath.getInstance(1.0f,
                        new Point(origin.x + 2000, 100 + origin.y + offset),
                        new Point(origin.x + 3000, 100 + origin.y + offset)),
                // 終わり
                LineSegmentPath.getInstance(Float.MAX_VALUE,
                        new Point(origin.x + 3000, origin.y + 100 + offset),
                        new Point(origin.x + 3000, origin.y + 100 + offset))
        };
        return generateEasyPathPoint(epp, dist);
    }

    @Override
    public Image getIconImg(TrainData trainData) {
        String trainID = trainData.getTimeTable().trainID;
        switch (trainID.charAt(trainID.length() - 1)) {
            case 'K':
                // 京王線
                return imageIconCommuter;
            default:
                // 新宿線
                return imageIconSubway;
        }
    }

    private static Color COLOR_LOCAL = LINE_COLOR;
    private static Color COLOR_EXPRESS = new Color(231, 26, 15);

    @Override
    public Color getTypeColor(TrainData trainData) {
        switch (trainData.getTimeTable().trainType) {
            case "急行":
                return COLOR_EXPRESS;
            default:
                return COLOR_LOCAL;
        }
    }
}
