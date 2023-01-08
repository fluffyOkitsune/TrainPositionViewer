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

public class TokyuTamagawaLine extends LineData {
    private Image imageIconLocal;

    private static final Color COLOR_LOCAL = new Color(0, 0, 255);

    public TokyuTamagawaLine() {
        super();
        try {
            Image img = ImageIO.read(new File("icon/toq1000g.png"));
            imageIconLocal = LineData.createEdgedImage(img, COLOR_LOCAL, 2);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static final Color LINE_COLOR = new Color(163, 1, 110);

    @Override
    public Color getLineColor() {
        return LINE_COLOR;
    }

    @Override
    public String getLineName() {
        return "[東急]東急多摩川線";
    }

    private Point origin = new Point(2000, 200);

    @Override
    public Point calcPosOnLinePath(float dist, Direction direction) {
        int offset;

        if (direction == Direction.OUTBOUND) {
            offset = +20;
        } else {
            offset = -20;
        }

        EasyPathPoint[] epp = {
                // 多摩川 - 蒲田
                LineSegmentPath.getInstance(getDistProportion(6),
                        new Point(origin.x - 700 + offset, origin.y + 600),
                        new Point(origin.x - 1000, origin.y + 900 + offset)),
                LineSegmentPath.getInstance(Float.MAX_VALUE,
                        new Point(origin.x - 1000, origin.y + 900 + offset),
                        new Point(origin.x - 1000, origin.y + 900 + offset))
        };
        return generateEasyPathPoint(epp, dist);
    }

    @Override
    protected String getStationDataCsvPath() {
        return "time_table/tokyu_tamagawa_line_station.csv";
    }

    @Override
    protected String getTimeTableOutCsvPath() {
        return "time_table/tokyu_tamagawa_line_weekdays_out.csv";
    }

    @Override
    protected String getTimeTableInCsvPath() {
        return "time_table/tokyu_tamagawa_line_weekdays_in.csv";
    }

    @Override
    public Image getIconImg(TrainData trainData) {
        return imageIconLocal;
    }

    @Override
    public Color getTypeColor(TrainData trainData) {
        return COLOR_LOCAL;
    }
}