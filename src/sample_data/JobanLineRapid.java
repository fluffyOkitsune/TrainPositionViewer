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

public class JobanLineRapid extends LineData {
    private Image imageIcon_JJ_Rapid;

    private static final Color COLOR_JJ_RAPID = new Color(24, 166, 41);
    public JobanLineRapid() {
        super();
        try {
            Image img = ImageIO.read(new File("icon/e231jy1.png"));
            imageIcon_JJ_Rapid = LineData.createEdgedImage(img, COLOR_JJ_RAPID, 2);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static final Color LINE_COLOR = COLOR_JJ_RAPID;

    @Override
    public Color getLineColor() {
        return LINE_COLOR;
    }

    @Override
    public String getLineName() {
        return "常磐線[快速]";
    }

    @Override
    public Point calcPosOnLinePath(float dist, Direction direction) {
        int offset;

        if (direction == Direction.OUTBOUND) {
            offset = -24;
        } else {
            offset = +24;
        }

        EasyPathPoint[] epp = {
            // 上野 - 南千住
            LineSegmentPath.getInstance(getDistProportion(3),
                    new Point(JRE_SubUrbanLine.ORIGIN.x + 1900, JRE_SubUrbanLine.ORIGIN.y + 200 + offset),
                    new Point(JRE_SubUrbanLine.ORIGIN.x + 2000, JRE_SubUrbanLine.ORIGIN.y + 300 + offset)),
            // 南千住 - 我孫子
            LineSegmentPath.getInstance(getDistProportion(7),
                    new Point(JRE_SubUrbanLine.ORIGIN.x + 2000, JRE_SubUrbanLine.ORIGIN.y + 300 + offset),
                    new Point(JRE_SubUrbanLine.ORIGIN.x + 2300, JRE_SubUrbanLine.ORIGIN.y + 300 + offset)),
            // 我孫子 - 取手
            LineSegmentPath.getInstance(getDistProportion(9),
                    new Point(JRE_SubUrbanLine.ORIGIN.x + 2300, JRE_SubUrbanLine.ORIGIN.y + 300 + offset),
                    new Point(JRE_SubUrbanLine.ORIGIN.x + 2400, JRE_SubUrbanLine.ORIGIN.y + 300 + offset)),
        };
        return generateEasyPathPoint(epp, dist);
    }

    @Override
    protected String getStationDataCsvPath() {
        return "time_table/joban_line_rapid_station.csv";
    }

    @Override
    protected String getTimeTableOutCsvPath() {
        return "time_table/joban_line_rapid_weekdays_out.csv";
    }

    @Override
    protected String getTimeTableInCsvPath() {
        return "time_table/joban_line_rapid_weekdays_in.csv";
    }

    @Override
    public Image getIconImg(TrainData trainData) {
                return imageIcon_JJ_Rapid;
    }

    @Override
    public Color getTypeColor(TrainData trainData) {
        return COLOR_JJ_RAPID;
    }
}