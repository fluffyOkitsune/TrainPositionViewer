package sample_data;

import java.awt.Color;
import java.awt.Image;
import java.awt.Point;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import data.line_data.LineData;
import data.train_data.TrainData;

public class NambuLine extends LineData {
    private Image imageIconLocal;
    private Image imageIconRapid;

    public NambuLine() {
        super();
        try {
            // 233-8000
            Image image = ImageIO.read(new File("icon/e233na.png"));
            imageIconLocal = LineData.createEdgedImage(image, COLOR_JN_LOCAL, 2);
            imageIconRapid = LineData.createEdgedImage(image, COLOR_JN_RAPID, 2);

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
            offset = -20;
        }
        if (direction == Direction.INBOUND) {
            offset = +20;
        }

        int x = startX + (int) Math.floor(2000 * dist);
        int y = startY + offset;

        return new Point(x, y);
    }

    @Override
    public Image getIconImg(TrainData trainData) {
        switch (trainData.getTimeTable().trainType) {
            case "快速":
                return imageIconRapid;
            default:
                return imageIconLocal;
        }
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