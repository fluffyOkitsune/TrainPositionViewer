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
    public Point calcPositionOnLinePath(float dist) {
        int x, y;
        if (dist < 0.3) {
            x = 200 + (int) (1000 * dist / 0.3);
            y = 200;
        } else if (dist < 0.7) {
            x = 200 + 1000;
            y = 200 + (int) (1000 * (dist - 0.3) / (0.7 - 0.3));

        } else {
            x = 200 + 1000 - (int) (1000 * (dist - 0.7) / (1.0 - 0.7));
            y = 200 + 1000;
        }

        return new Point(x, y);
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

    private static Color COLOR_LOCAL = new Color(0, 178, 229);
    private static Color COLOR_RAPID = new Color(255, 0, 128);

    @Override
    public Color getTypeColor(TrainData trainData) {
        String trainType = trainData.getTimeTable().trainID;

        switch (trainType) {
            case "快速":
                return COLOR_RAPID;
            default:
                return COLOR_LOCAL;
        }
    }
}