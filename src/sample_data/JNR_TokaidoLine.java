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

public class JNR_TokaidoLine extends LineData {
    private Image imageIconLocal;
    private Image imageIconExp;
    private Image imageIconExpSchool;
    private Image imageIconSleepingExp;
    private Image imageIconLtd;
    private Image imageIconSleepingLtd;

    public JNR_TokaidoLine() {
        super();
        try {
            imageIconLocal = ImageIO.read(new File("icon/e113sh.png"));
            imageIconExp = ImageIO.read(new File("icon/c165.png"));
            imageIconExpSchool = ImageIO.read(new File("icon/j167a.png"));
            imageIconSleepingExp = ImageIO.read(new File("icon/ef65_rp.png"));
            imageIconLtd = ImageIO.read(new File("icon/j151.png"));
            imageIconSleepingLtd = ImageIO.read(new File("icon/ef66-1.png"));
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
                LineSegmentPath.getInstance(getDistProportion(70),
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
        return "time_table/jnr_tokaido_line_station.csv";
    }

    @Override
    protected String getTimeTableOutCsvPath() {
        return "time_table/jnr_tokaido_line_out.csv";
    }

    @Override
    protected String getTimeTableInCsvPath() {
        return "time_table/jnr_tokaido_line_in.csv";
    }

    @Override
    public Image getIconImg(TrainData trainData) {
        switch (trainData.getTimeTable().getTrainType()) {
            case "★彡": // 寝台特急
                return imageIconSleepingLtd;
            case "特急":
                return imageIconLtd;
            case "☆彡": // 寝台急行
                return imageIconSleepingExp;
            case "急行":
                return imageIconExp;
            default:
                return imageIconLocal;
        }
    }

    @Override
    public Color getTypeColor(TrainData trainData) {
        switch (trainData.getTimeTable().getTrainType()) {
            case "★彡": // 寝台特急
            case "特急":
                return Color.RED;
            case "☆彡": // 寝台急行
            case "急行":
                return Color.ORANGE;
            default:
                return Color.BLACK;
        }
    }
}