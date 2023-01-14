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

public class TokaidoShinkansen extends LineData {
    private Image imageIconKodama;
    private Image imageIconHikari;
    private Image imageIconNozomi;

    private static final Color COLOR_LINE = new Color(246, 139, 30);

    private static final Color COLOR_KODAMA = new Color(0, 0, 255);
    private static final Color COLOR_HIKARI = new Color(255, 0, 0);
    private static final Color COLOR_NOZOMI = new Color(255, 216, 0);

    public TokaidoShinkansen() {
        super();
        try {
            Image img = ImageIO.read(new File("icon/c700ns.png"));
            imageIconKodama = LineData.createEdgedImage(img, COLOR_KODAMA, 2);
            imageIconHikari = LineData.createEdgedImage(img, COLOR_HIKARI, 2);
            imageIconNozomi = LineData.createEdgedImage(img, COLOR_NOZOMI, 2);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Color getLineColor() {
        return COLOR_LINE;
    }

    @Override
    public String getLineName() {
        return "東海道新幹線";
    }

    private Point origin = new Point(200, 200);

    @Override
    public Point calcPosOnLinePath(float dist, Direction direction) {
        int offset;

        if (direction == Direction.OUTBOUND) {
            offset = +20;
        } else {
            offset = -20;
        }

        EasyPathPoint[] epp = {
                // 東京 - 新大阪
                LineSegmentPath.getInstance(getDistProportion(16),
                        new Point(origin.x + 3000, origin.y + 0 + offset),
                        new Point(origin.x + 0, origin.y + 0 + offset)),
                LineSegmentPath.getInstance(Float.MAX_VALUE,
                        new Point(origin.x + 0, origin.y + 0 + offset),
                        new Point(origin.x + 0, origin.y + 0 + offset))
        };
        return generateEasyPathPoint(epp, dist);
    }

    @Override
    protected String getStationDataCsvPath() {
        return "time_table/tokaido-shinkansen_station.csv";
    }

    @Override
    protected String getTimeTableOutCsvPath() {
        return "time_table/tokaido-shinkansen_weekdays_out.csv";
    }

    @Override
    protected String getTimeTableInCsvPath() {
        return "time_table/tokaido-shinkansen_weekdays_in.csv";
    }

    @Override
    public Image getIconImg(TrainData trainData) {
        String trainName = trainData.getTimeTable().getTrainName();
        switch (trainName) {
            case "のぞみ":
                return imageIconNozomi;
            case "ひかり":
                return imageIconHikari;
            default:
                return imageIconKodama;
        }
    }

    @Override
    public Color getTypeColor(TrainData trainData) {
        String trainName = trainData.getTimeTable().getTrainName();
        switch (trainName) {
            case "のぞみ":
                return COLOR_NOZOMI;
            case "ひかり":
                return COLOR_HIKARI;
            default:
                return COLOR_KODAMA;
        }
    }
}