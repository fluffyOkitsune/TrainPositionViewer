import java.awt.Color;
import java.awt.Image;
import java.awt.Point;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import data.line_data.ArcPath;
import data.line_data.EasyPathPoint;
import data.line_data.LineData;
import data.train_data.TrainData;

class YamanoteLine extends LineData {
    private Image imageIcon;

    YamanoteLine() {
        super();
        try {
            imageIcon = ImageIO.read(new File("icon/e235ya.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static final Color LINE_COLOR = new Color(123, 171, 79);

    @Override
    public Color getLineColor() {
        return LINE_COLOR;
    }

    @Override
    public String getLineName() {
        return "山手線";
    }

    @Override
    protected String getStationDataCsvPath() {
        return "time_table/yamanote_line_station.csv";
    }

    @Override
    protected String getTimeTableOutCsvPath() {
        return "time_table/yamanote_line_weekdays_out.csv";
    }

    @Override
    protected String getTimeTableInCsvPath() {
        return "time_table/yamanote_line_weekdays_in.csv";
    }

    @Override
    public Point calcPositionOnLinePath(float dist, Direction direction) {
        Point center = new Point(500, 500);
        float radius = 0.0f;

        // 外回り
        if (direction == Direction.OUTBOUND) {
            radius = 400 + 15;
        }
        // 内回り
        if (direction == Direction.INBOUND) {
            radius = 400 - 15;
        }

        EasyPathPoint[] epp = {
                ArcPath.getInstance(1.0f, center,
                        radius, 90, 90 + 360),
                ArcPath.getInstance(Float.MAX_VALUE, center,
                        radius, 90, 90)
        };
        return generateEasyPathPoint(epp, dist);
    }

    @Override
    public Image getIconImg(TrainData trainData) {
        return imageIcon;
    }

    @Override
    public Color getTypeColor(TrainData trainData) {
        return getLineColor();
    }
}