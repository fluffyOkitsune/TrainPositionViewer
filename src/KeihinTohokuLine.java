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

public class KeihinTohokuLine extends LineData {
    private Image imageIconJK;
    private Image imageIconJH;

    public KeihinTohokuLine() {
        super();
        try {
            imageIconJK = ImageIO.read(new File("icon/e233kt.png"));
            imageIconJH = ImageIO.read(new File("icon/e233yo.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static final Color LINE_COLOR = new Color(0, 178, 229);

    @Override
    public Color getLineColor() {
        return LINE_COLOR;
    }

    @Override
    public String getLineName() {
        return "京浜東北線・根岸線";
    }

    private Point origin = new Point(200, 200);

    @Override
    public Point calcPositionOnLinePath(float dist, Direction direction) {
        float[] point = { 0.0f, 0.3f, 0.4f, 0.6f, 0.7f, 1.0f };
        int offset = 30;

        // 南行
        if (direction == Direction.OUTBOUND) {
            EasyPathPoint[] epp = {
                    LineSegmentPath.getInstance(point[1],
                            new Point(origin.x + 0, origin.y + 0 - offset),
                            new Point(origin.x + 1000, origin.y + 0 - offset)),
                    ArcPath.getInstance(point[2],
                            new Point(origin.x + 1000, origin.y + 200),
                            200 + offset, -90, 0),
                    LineSegmentPath.getInstance(point[3],
                            new Point(origin.x + 1200 + offset, origin.y + 200),
                            new Point(origin.x + 1200 + offset, origin.y + 800)),
                    ArcPath.getInstance(point[4],
                            new Point(origin.x + 1000, origin.y + 800),
                            200 + offset, 0, 90),
                    LineSegmentPath.getInstance(point[5],
                            new Point(origin.x + 1000, origin.y + 1000 + offset),
                            new Point(origin.x + 0, origin.y + 1000 + offset)),
                    LineSegmentPath.getInstance(Float.MAX_VALUE,
                            new Point(origin.x + 0, origin.y + 1000 + offset),
                            new Point(origin.x + 0, origin.y + 1000 + offset))
            };
            return generateEasyPathPoint(epp, dist);
        }

        // 北行
        if (direction == Direction.INBOUND) {
            EasyPathPoint[] epp = {
                    LineSegmentPath.getInstance(point[1],
                            new Point(origin.x + 0, origin.y + 0 + offset),
                            new Point(origin.x + 1000, origin.y + 0 + offset)),
                    ArcPath.getInstance(point[2],
                            new Point(origin.x + 1000, origin.y + 200),
                            200 - offset, -90, 0),
                    LineSegmentPath.getInstance(point[3],
                            new Point(origin.x + 1200 - offset, origin.y + 200),
                            new Point(origin.x + 1200 - offset, origin.y + 800)),
                    ArcPath.getInstance(point[4],
                            new Point(origin.x + 1000, origin.y + 800),
                            200 - offset, 0, 90),
                    LineSegmentPath.getInstance(point[5],
                            new Point(origin.x + 1000, origin.y + 1000 - offset),
                            new Point(origin.x + 0, origin.y + 1000 - offset)),
                    LineSegmentPath.getInstance(Float.MAX_VALUE,
                            new Point(origin.x + 0, origin.y + 1000 - offset),
                            new Point(origin.x + 0, origin.y + 1000 - offset))
            };
            return generateEasyPathPoint(epp, dist);
        }

        return new Point(0, 0);
    }

    @Override
    protected String getStationDataCsvPath() {
        return "time_table/keihin-tohoku_line_station.csv";
    }

    @Override
    protected String getTimeTableOutCsvPath() {
        return "time_table/keihin-tohoku_line_weekdays_out.csv";
    }

    @Override
    protected String getTimeTableInCsvPath() {
        return "time_table/keihin-tohoku_line_weekdays_in.csv";
    }

    @Override
    public Image getIconImg(TrainData trainData) {
        String trainID = trainData.getTimeTable().trainID;

        if (trainID.charAt(trainID.length() - 1) == 'K') {
            // 列車番号の末尾がKの電車は横浜線直通
            return imageIconJH;
        } else {
            return imageIconJK;
        }
    }

    private static Color COLOR_JK_LOCAL = new Color(0, 178, 229);
    private static Color COLOR_JK_RAPID = new Color(255, 0, 128);
    private static Color COLOR_JH_LOCAL = new Color(127, 195, 66);
    private static Color COLOR_JH_RAPID = new Color(255, 69, 0);

    @Override
    public Color getTypeColor(TrainData trainData) {
        String trainID = trainData.getTimeTable().trainID;
        String trainType = trainData.getTimeTable().trainType;

        if (trainID.charAt(trainID.length() - 1) == 'K') {
            // 列車番号の末尾がKの電車は横浜線直通
            switch (trainType) {
                case "快速":
                    return COLOR_JH_RAPID;
                default:
                    return COLOR_JH_LOCAL;
            }
        } else {
            switch (trainType) {
                case "快速":
                    return COLOR_JK_RAPID;
                default:
                    return COLOR_JK_LOCAL;
            }
        }
    }
}