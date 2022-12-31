import java.awt.*;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import data.line_data.EasyPathPoint;
import data.line_data.LineData;
import data.line_data.LineSegmentPath;
import data.line_data.SingleTrackLinePath;
import data.train_data.TrainData;

class KururiLine extends LineData {
    private Image imageIcon;

    KururiLine() {
        super();
        try {
            imageIcon = ImageIO.read(new File("icon/e130kr.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static final Color LINE_COLOR = new Color(0, 180, 141);

    @Override
    public Color getLineColor() {
        return LINE_COLOR;
    }

    @Override
    public String getLineName() {
        return "久留里線";
    }

    public Image getIconImage() {
        return imageIcon;
    }

    @Override
    protected String getStationDataCsvPath() {
        return "time_table/kururi_line_station.csv";
    }

    @Override
    protected String getTimeTableOutCsvPath() {
        return "time_table/kururi_line_out.csv";
    }

    @Override
    protected String getTimeTableInCsvPath() {
        return "time_table/kururi_line_in.csv";
    }

    private Point origin = new Point(200, 200);

    @Override
    public Point calcPositionOnLinePath(float dist, Direction direction) {
        Point offset;
        if (direction == Direction.OUTBOUND) {
            offset = new Point(+20, -20);
        } else {
            offset = new Point(-20, +20);
        }

        // 駅の長さ割合
        float staLen = 0.1f;

        EasyPathPoint[] epp = {
                // 木更津 - 横田
                SingleTrackLinePath.getInstance(getDistProportion(4),
                        offset, staLen,
                        new Point(origin.x + 0, origin.y + 0),
                        new Point(origin.x + 200, origin.y + 200)),
                // 横田 - 久留里
                SingleTrackLinePath.getInstance(getDistProportion(10),
                        offset, staLen,
                        new Point(origin.x + 200, origin.y + 200),
                        new Point(origin.x + 500, origin.y + 500)),
                // 久留里 - 上総亀山
                SingleTrackLinePath.getInstance(getDistProportion(13),
                        offset, staLen,
                        new Point(origin.x + 500, origin.y + 500),
                        new Point(origin.x + 700, origin.y + 700)),
                // 終わり
                LineSegmentPath.getInstance(Float.MAX_VALUE,
                        new Point(origin.x + 700 + offset.x, origin.y + 700 + offset.y),
                        new Point(origin.x + 700 + offset.x, origin.y + 700 + offset.y))
        };
        return generateEasyPathPoint(epp, dist);
    }

    @Override
    public Image getIconImg(TrainData trainData) {
        return imageIcon;
    }

    @Override
    public Color getTypeColor(TrainData trainData) {
        return LINE_COLOR;
    }
}