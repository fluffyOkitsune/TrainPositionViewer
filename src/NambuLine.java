import java.awt.Color;
import java.awt.Image;
import java.awt.Point;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import data.line_data.LineData;
import data.train_data.TrainData;

class NambuLine extends LineData {
    private Image imageIcon;

    NambuLine() {
        super();
        try {
            imageIcon = ImageIO.read(new File("icon/e233na.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static final Color LINE_COLOR = new Color(253, 188, 0);

    @Override
    public Color getLineColor() {
        return LINE_COLOR;
    }

    @Override
    public String getLineName() {
        return "南武線";
    }

    @Override
    protected String getStationDataCsvPath() {
        return "time_table/nambu_line_station.csv";
    }

    @Override
    protected String getTimeTableOutCsvPath() {
        return "time_table/nambu_line_weekdays_out.csv";
    }

    @Override
    protected String getTimeTableInCsvPath() {
        return "time_table/nambu_line_weekdays_in.csv";
    }

    @Override
    public Point calcPositionOnLinePath(float dist, Direction direction) {
        int startX = 100;
        int startY = 100;

        int offset = 0;
        if (direction == Direction.OUTBOUND) {
            offset = 20;
        }
        if (direction == Direction.INBOUND) {
            offset -= 20;
        }

        int x = startX + (int) Math.floor(2000 * dist);
        int y = startY + offset;

        return new Point(x, y);
    }

    @Override
    public Image getIconImg(TrainData trainData) {
        return imageIcon;
    }

    private static Color COLOR_JN_LOCAL = LINE_COLOR;
    private static Color COLOR_JN_RAPID = new Color(255, 140, 0);

    @Override
    public Color getTypeColor(TrainData trainData) {
        switch (trainData.getTimeTable().trainType) {
            case "快速":
                return COLOR_JN_RAPID;
            default:
                return COLOR_JN_LOCAL;
        }
    }
}