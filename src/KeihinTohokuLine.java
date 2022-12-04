import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import data.Time;
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
        int x = (int) (200 + 2000 * dist);
        int y = 200;

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
    public void drawTrain(Graphics g, TrainData trainData, Time currentTime) {
        Point pos = calcTrainPos(trainData, currentTime);

        Image iconImage = getIconImg(trainData);

        // 電車は左側通行なので、下りと上りで描画位置をずらす
        switch (trainData.getDirection()) {
            case OUTBOUND:
                LineData.drawImage(g, iconImage, new Point(pos.x, pos.y - 25));

                // 種別
                g.setColor(getTypeColor(trainData));
                LineData.drawString(g, trainData.trainID, new Point(pos.x, pos.y - 60));
                break;
            case INBOUND:
                LineData.drawImage(g, iconImage, new Point(pos.x, pos.y + 25));

                // 種別
                g.setColor(getTypeColor(trainData));
                LineData.drawString(g, trainData.trainID, new Point(pos.x, pos.y + 60));
                break;
        }
    }

    private Image getIconImg(TrainData trainData) {
        if (trainData.trainID.charAt(trainData.trainID.length() - 1) == 'K') {
            // 列車番号の末尾がKの電車は横浜線直通
            return imageIconJH;
        } else {
            return imageIconJK;
        }
    }

    private static Color COLOR_LOCAL = new Color(0, 178, 229);
    private static Color COLOR_RAPID = new Color(255, 0, 128);

    private Color getTypeColor(TrainData trainData) {
        switch (trainData.trainType) {
            case "快速":
                return COLOR_RAPID;
            default:
                return COLOR_LOCAL;
        }
    }
}