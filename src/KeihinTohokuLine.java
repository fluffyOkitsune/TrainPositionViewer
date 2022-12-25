import java.awt.Color;
import java.awt.Image;
import java.awt.Point;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import data.line_data.LineData;
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

    @Override
    public Point calcPositionOnLinePath(float dist, Direction direction) {
        int x, y;
        float[] point = { 0.0f, 0.3f, 0.7f, 1.0f };
        int offset = 30;

        // 南行
        if (direction == Direction.OUTBOUND) {
            if (dist < point[1]) {
                x = 200 + (int) ((1000 + offset) * (dist - point[0]) / (point[1] - point[0]));
                y = 200 - offset;
            } else if (dist < point[2]) {
                x = 200 + (1000 + offset);
                y = 200 - offset + (int) ((1000 + offset + offset) * (dist - point[1]) / (point[2] - point[1]));
            } else {
                x = 200 + (1000 + offset) - (int) ((1000 + offset) * (dist - point[2]) / (point[3] - point[2]));
                y = 200 + (1000 + offset);
            }
            return new Point(x, y);
        }

        // 北行
        if (direction == Direction.INBOUND) {
            if (dist < point[1]) {
                x = 200 + (int) ((1000 - offset) * (dist - point[0]) / (point[1] - point[0]));
                y = 200 + offset;
            } else if (dist < point[2]) {
                x = 200 + (1000 - offset);
                y = 200 + offset + (int) ((1000 - offset - offset) * (dist - point[1]) / (point[2] - point[1]));

            } else {
                x = 200 + (1000 - offset) - (int) ((1000 - offset) * (dist - point[2]) / (point[3] - point[2]));
                y = 200 + (1000 - offset);
            }
            return new Point(x, y);
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