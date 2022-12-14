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

public class KeihanMainLine extends LineData {
    private Image imageIconLiner;
    private Image imageIconRapidLtd;
    private Image imageIconLtd;
    private Image imageIconRapidExp;
    private Image imageIconComRapidLtd;
    private Image imageIconExp;
    private Image imageIconSubExp;
    private Image imageIconComSubExp;
    private Image imageIconSemiExp;
    private Image imageIconLocal;

    public KeihanMainLine() {
        super();
        try {
            Image img;
            img = ImageIO.read(new File("icon/khan8002.png"));
            imageIconLiner = LineData.createEdgedImage(img, COLOR_LINER, 2);
            imageIconLtd = LineData.createEdgedImage(img, COLOR_LTD, 2);
            img = LineData.createEdgedImage(img, Color.WHITE, 1);
            imageIconRapidLtd = LineData.createEdgedImage(img, COLOR_LTD, 1);

            img = ImageIO.read(new File("icon/khan3001.png"));
            imageIconRapidExp = LineData.createEdgedImage(img, COLOR_RAPID_EXP, 2);
            img = LineData.createEdgedImage(img, Color.WHITE, 1);
            imageIconComRapidLtd = LineData.createEdgedImage(img, COLOR_LTD, 1);

            img = ImageIO.read(new File("icon/khan6002.png"));
            imageIconExp = LineData.createEdgedImage(img, COLOR_EXP, 2);
            imageIconSemiExp = LineData.createEdgedImage(img, COLOR_SEMI_EXP, 2);
            imageIconSubExp = LineData.createEdgedImage(img, COLOR_SUB_EXP, 2);
            img = LineData.createEdgedImage(img, Color.WHITE, 1);
            imageIconComSubExp = LineData.createEdgedImage(img, COLOR_SUB_EXP, 1);

            img = ImageIO.read(new File("icon/khan1002.png"));
            imageIconLocal = LineData.createEdgedImage(img, COLOR_LOCAL, 2);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static final Color LINE_COLOR = new Color(29, 32, 136);

    @Override
    public Color getLineColor() {
        return LINE_COLOR;
    }

    @Override
    public String getLineName() {
        return "[??????]????????????";
    }

    @Override
    protected String getStationDataCsvPath() {
        return "time_table/keihan_main_line_station.csv";
    }

    @Override
    protected String getTimeTableOutCsvPath() {
        return "time_table/keihan_main_line_weekdays_out.csv";
    }

    @Override
    protected String getTimeTableInCsvPath() {
        return "time_table/keihan_main_line_weekdays_in.csv";
    }

    private Point origin = new Point(200, 200);

    @Override
    public Point calcPosOnLinePath(float dist, Direction direction) {
        int offset = 0;
        if (direction == Direction.OUTBOUND) {
            offset = -20;
        }
        if (direction == Direction.INBOUND) {
            offset = +20;
        }

        EasyPathPoint[] epp = {
                // ????????? - ??????
                LineSegmentPath.getInstance(getDistProportion(39),
                        new Point(origin.x, origin.y + offset),
                        new Point(origin.x + 2500, origin.y + offset)),
                // ?????????
                LineSegmentPath.getInstance(Float.MAX_VALUE,
                        new Point(origin.x + 2500, origin.y + offset),
                        new Point(origin.x + 2500, origin.y + offset))
        };
        return generateEasyPathPoint(epp, dist);
    }

    @Override
    public Image getIconImg(TrainData trainData) {
        switch (trainData.getTimeTable().trainType) {
            case "????????????":
            case "??????":
                return imageIconRapidLtd;
            case "??????":
                return imageIconLtd;
            case "??????":
                return imageIconRapidExp;
            case "??????":
                return imageIconComRapidLtd;
            case "??????":
            case "??????":
                return imageIconExp;
            case "??????":
                return imageIconSubExp;
            case "??????":
                return imageIconComSubExp;
            case "??????":
                return imageIconSemiExp;
            default:
                return imageIconLocal;
        }
    }

    private static Color COLOR_LOCAL = new Color(64, 64, 64);
    private static Color COLOR_SEMI_EXP = new Color(0, 153, 51);
    private static Color COLOR_SUB_EXP = new Color(0, 51, 204);
    private static Color COLOR_EXP = new Color(255, 102, 0);
    private static Color COLOR_RAPID_EXP = new Color(128, 0, 128);
    private static Color COLOR_LTD = new Color(255, 0, 51);
    private static Color COLOR_LINER = new Color(255, 0, 255);

    @Override
    public Color getTypeColor(TrainData trainData) {
        switch (trainData.getTimeTable().trainType) {
            case "????????????":
                return COLOR_LINER;
            case "??????":
            case "??????":
                return COLOR_LTD;
            case "??????":
            case "??????":
                return COLOR_RAPID_EXP;
            case "??????":
            case "??????":
                return COLOR_EXP;
            case "??????":
            case "??????":
                return COLOR_SUB_EXP;
            case "??????":
                return COLOR_SEMI_EXP;
            default:
                return COLOR_LOCAL;
        }
    }
}