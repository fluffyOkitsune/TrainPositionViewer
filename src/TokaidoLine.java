import java.awt.Color;
import java.awt.Image;
import java.awt.Point;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import data.line_data.LineData;
import data.train_data.TrainData;

public class TokaidoLine extends LineData {
    private Image imageIconJT;
    private Image imageIconJS;
    private Image imageIconLtd;

    public TokaidoLine() {
        super();
        try {
            imageIconJT = ImageIO.read(new File("icon/e233sh.png"));
            imageIconJS = ImageIO.read(new File("icon/e231tdk.png"));
            imageIconLtd = ImageIO.read(new File("icon/e185odj.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static final Color LINE_COLOR = new Color(202, 106, 31);

    @Override
    public Color getLineColor() {
        return LINE_COLOR;
    }

    @Override
    public String getLineName() {
        return "東海道線";
    }

    @Override
    public Point calcPositionOnLinePath(float dist) {
        int x = (int) (1000 - 800 * dist);
        int y = (int) (100 + 900 * dist);

        return new Point(x, y);
    }

    @Override
    protected String getStationDataCsvPath() {
        return "time_table/jre_tokaido_line_station.csv";
    }

    @Override
    protected String getTimeTableOutCsvPath() {
        return "time_table/jre_tokaido_line_weekdays_out.csv";
    }

    @Override
    protected String getTimeTableInCsvPath() {
        return "time_table/jre_tokaido_line_weekdays_in.csv";
    }

    @Override
    public Image getIconImg(TrainData trainData) {
        if (trainData.getTimeTable().trainType.equals("特急")) {
            return imageIconLtd;
        }

        if (trainData.getTimeTable().trainID.charAt(trainData.getTimeTable().trainID.length() - 1) == 'Y') {
            // 列車番号の末尾がYの列車は湘南新宿ライン
            return imageIconJS;
        } else {
            // 東海道線・上野東京ライン
            return imageIconJT;
        }
    }

    private static Color COLOR_LOCAL = new Color(24, 166, 41);
    private static Color COLOR_RAPID = new Color(246, 139, 30);
    private static Color COLOR_SPECIAL_RAPID = new Color(51, 204, 255);

    @Override
    public Color getTypeColor(TrainData trainData) {
        switch (trainData.getTimeTable().trainType) {
            case "特急":
                return Color.RED;
            case "特快":
                return COLOR_SPECIAL_RAPID;
            case "快速":
                return COLOR_RAPID;
            default:
                return COLOR_LOCAL;
        }
    }
}