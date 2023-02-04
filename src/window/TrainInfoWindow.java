package window;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

import data.time_table.StationData;
import draw.Train;

// 列車の情報ウィンドウ
public class TrainInfoWindow {
    private Train selectedTrain;
    private Rectangle rectWindowSize;
    private boolean hasDragStarted;

    public TrainInfoWindow() {
        selectedTrain = null;
        rectWindowSize = new Rectangle(0, 0, 150, 50);
    }

    // --------------------------------------------------------------------------------
    // 共通
    // --------------------------------------------------------------------------------
    // 列車の内容
    private String[] contents;

    public String[] getContents() {
        return contents;
    }

    public void setContents(String[] contents) {
        this.contents = contents;
    }

    private Rectangle maxStrSizeRect;

    public Rectangle getMaxStrSizeRect() {
        return maxStrSizeRect;
    }

    public void setMaxStrSizeRect(Rectangle maxStrSizeRect) {
        this.maxStrSizeRect = maxStrSizeRect;
    }

    // --------------------------------------------------------------------------------
    // 列車選択
    // --------------------------------------------------------------------------------
    public void selectTrain(Train train, Graphics2D g) {
        this.selectedTrain = train;
        this.cntAnimOpen = 0;
        if (train == null) {
            this.rectWindowSize.x = 0;
            this.rectWindowSize.y = 0;
        } else {
            // ウィンドウを移動できるようにするため、値渡しする
            this.rectWindowSize.x = train.getRect().x - train.getRect().width / 2;
            this.rectWindowSize.y = train.getRect().y - train.getRect().height / 2;

            imageTrainInfoWindow = createImageTrainInfoWindow(train, g);
            rectWindowSize.width = imageTrainInfoWindow.getWidth(null);
            rectWindowSize.height = imageTrainInfoWindow.getHeight(null);
        }
    }

    private Image createImageTrainInfoWindow(Train train, Graphics2D g) {
        StringBox[] box = generateStrBoxContents(train, g);

        int mergin = 2;

        Rectangle size = calcWindowSizeRect(box);
        BufferedImage bufferedImage = new BufferedImage(size.width + 2 * mergin, size.height + 2 * mergin,
                BufferedImage.TYPE_4BYTE_ABGR);
        Graphics2D bufImgG = (Graphics2D) bufferedImage.getGraphics();

        // 下地
        bufImgG.setStroke(STROKE_WINDOW_EDGE);
        bufImgG.setColor(Color.WHITE);
        bufImgG.fillRect(0, 0, bufferedImage.getWidth(null), bufferedImage.getHeight(null));

        // 枠（若干内側に描画する）
        bufImgG.setColor(Color.BLACK);
        bufImgG.drawRect(1, 1, bufferedImage.getWidth(null) - 2, bufferedImage.getHeight(null) - 2);

        // 路線 背景
        Color lineColor = train.getDepartedStation().getLineData().getLineColor();
        bufImgG.setColor(lineColor);
        bufImgG.fillRect(mergin, mergin, size.width, box[0].getStrRect().height + mergin);

        // 種別 背景
        Color typeColor = train.getDepartedStation().getLineData().getTypeColor(train.trainData);
        bufImgG.setColor(typeColor);
        bufImgG.fillRect(mergin, mergin + box[0].getStrRect().height, size.width, box[1].getStrRect().height + mergin);

        StringBox.drawTowardVerticalAxsis(bufImgG, box, mergin, mergin);

        return bufferedImage;
    }

    private StringBox[] generateStrBoxContents(Train train, Graphics2D g) {
        String trainName = generateTrainNameStr(train);
        if (trainName.isEmpty()) {
            StringBox[] box = {
                    new StringBox(g, generateLineInfoStr(train), null, Color.WHITE, FONT_TRAIN_INFO),
                    new StringBox(g, generateMainInfoStr(train), null, Color.WHITE, FONT_TRAIN_TYPE),
                    new StringBox(g, generateDestinationStr(train), null, Color.BLACK, FONT_TRAIN_INFO),
                    new StringBox(g, generateNoteStr(train), null, Color.BLACK, FONT_TRAIN_INFO),
            };
            return box;

        } else {
            StringBox[] box = {
                    new StringBox(g, generateLineInfoStr(train), null, Color.WHITE, FONT_TRAIN_INFO),
                    new StringBox(g, generateMainInfoStr(train), null, Color.WHITE, FONT_TRAIN_TYPE),
                    new StringBox(g, generateDestinationStr(train), null, Color.BLACK, FONT_TRAIN_INFO),
                    new StringBox(g, trainName, null, Color.BLACK, FONT_TRAIN_INFO),
                    new StringBox(g, generateNoteStr(train), null, Color.BLACK, FONT_TRAIN_INFO),
            };
            return box;
        }
    }

    private Rectangle calcWindowSizeRect(StringBox[] str) {
        int maxWidth = 0;
        int height = 0;

        for (StringBox s : str) {
            maxWidth = Integer.max(maxWidth, s.getStrRect().width);
            height += s.getStrRect().height;
        }

        return new Rectangle(0, 0, maxWidth, height);
    }

    // --------------------------------------------------------------------------------
    // 列車の情報ウィンドウの項目
    // --------------------------------------------------------------------------------
    // 路線名
    private String generateLineInfoStr(Train train) {
        String depatured = train.getDepartedStation().getLineData().getLineName();
        String terminal = train.getTerminalStation().getLineData().getLineName();
        if (depatured.equals(terminal)) {
            return depatured;
        } else {
            return String.format("%s (%s直通)", depatured, terminal);
        }
    }

    // 種別と行先
    private String generateMainInfoStr(Train train) {
        String trainID = selectedTrain.trainData.getTimeTable().getTrainID();
        String trainType = selectedTrain.trainData.getTimeTable().getTrainType();
        return String.format("%s [%s]", trainID, trainType);
    }

    // 列車名
    private String generateTrainNameStr(Train train) {
        String str = "";
        String trainName = train.trainData.getTimeTable().getTrainName();
        if (trainName.isEmpty()) {
            return str;
        }
        str += "\n" + trainName;

        // 号
        String trainNo = train.trainData.getTimeTable().getTrainNo();
        if (trainNo.isEmpty()) {
            return str;
        }
        str += " " + trainNo + "号";

        return str;
    }

    // 行先表示
    private String generateDestinationStr(Train train) {
        StationData depatured = train.getDepartedStation();
        StationData terminal = train.getTerminalStation();

        if (depatured.getLineData() == terminal.getLineData()) {
            return String.format("%s行", terminal.getName());
        } else {
            return String.format("%s経由 %s行", searchNextBorderStation(train).getName(),
                    terminal.getName());
        }
    }

    // 注記
    private String generateNoteStr(Train train) {
        return train.trainData.getTimeTable().getNote().trim();
    }

    // 直通列車の境界駅を調べる。
    private StationData searchNextBorderStation(Train train) {
        StationData stationData = train.getDepartedStation();
        int depStaID = 0;

        // 出発駅のIDを探索
        // 3以上の路線を直通する場合があるので、列車がすでに発車した駅から境界駅を探さないとダメ
        for (int staID = 0; staID < train.trainData.getTimeTable().getTimeDataSize(); staID++) {
            stationData = train.trainData.getTimeTable().getTimeData(staID).getStationData();
            if (stationData == train.getDepartedStation()) {
                depStaID = staID;
                break;
            }
        }

        for (int staID = depStaID; staID < train.trainData.getTimeTable().getTimeDataSize(); staID++) {
            stationData = train.trainData.getTimeTable().getTimeData(staID).getStationData();

            // 境界駅は直通運転で路線が切り替わる境界の前と後で、両方の路線データで定義されているため、
            // 実際には同じ駅であるが、駅データとしては異なる駅として定義されている。
            // 例） 東京駅（上野東京ライン所属）→東京駅（東海道線所属）
            if (stationData.getLineData() != train.getDepartedStation().getLineData()) {
                return stationData;
            }
        }

        return stationData;
    }

    // --------------------------------------------------------------------------------
    // 描画処理
    // --------------------------------------------------------------------------------
    private static final Font FONT_TRAIN_INFO = new Font(null, Font.PLAIN, 10);
    private static final Font FONT_TRAIN_TYPE = new Font(null, Font.PLAIN, 18);
    private static final Stroke STROKE_WINDOW_EDGE = new BasicStroke(2.0f);

    private Image imageTrainInfoWindow;
    private int cntAnimOpen;
    private final int cntAnimOpenTh = 5;

    public void drawTrainInfo(Graphics2D g) {
        if (imageTrainInfoWindow == null) {
            return;
        }
        if (selectedTrain == null) {
            return;
        }
        if (!selectedTrain.onDuty) {
            return;
        }

        if (cntAnimOpen < cntAnimOpenTh) {
            cntAnimOpen++;
            Rectangle frameSize = getWindowSizeDuringAnimWindowOpen(g, imageTrainInfoWindow,
                    rectWindowSize.getLocation());
            g.drawImage(imageTrainInfoWindow, frameSize.x, frameSize.y, frameSize.width, frameSize.height, null);
        } else {
            g.drawImage(imageTrainInfoWindow, rectWindowSize.x, rectWindowSize.y, null);
        }
    }

    private Rectangle getWindowSizeDuringAnimWindowOpen(Graphics2D g, Image image, Point pos) {
        // 高さが一定の割合で増えるアニメーション
        float height = image.getHeight(null) * cntAnimOpen / cntAnimOpenTh;

        // ウィンドウは高さの中心から上下に向かって開くので、枠のheightも移動する
        float yOffset = image.getHeight(null) * (cntAnimOpenTh - cntAnimOpen) / cntAnimOpenTh / 2;

        return new Rectangle(pos.x, pos.y + (int) yOffset, image.getWidth(null), (int) height);
    }

    // 補色を計算する
    private static Color genComplementaryColor(Color color) {
        int rgb = color.getRGB();
        int r = (rgb & 0x00FF0000) >> 16;
        int g = (rgb & 0x0000FF00) >> 8;
        int b = (rgb & 0x000000FF) >> 0;

        int max = r;
        max = Integer.max(max, g);
        max = Integer.max(max, b);

        int min = r;
        min = Integer.min(min, g);
        min = Integer.min(min, b);

        if (max == min) {
            // 彩度ゼロなので明るさを変えて補色とする。
            return new Color(255 - max, 255 - max, 255 - max);
        } else {
            return new Color(max + min - r, max + min - g, max + min - b);
        }
    }

    // --------------------------------------------------------------------------------
    // マウスイベント
    // --------------------------------------------------------------------------------
    public boolean getOnMouse(MouseEvent e) {
        if (selectedTrain == null) {
            return false;
        } else {
            return this.rectWindowSize.contains(new Point(e.getX(), e.getY()));
        }
    }

    private Point mouseDragStartingOffset = new Point(0, 0);

    public boolean dragWindow(MouseEvent e) {
        if (!hasDragStarted) {
            return false;
        } else {
            this.rectWindowSize.x = e.getX() + mouseDragStartingOffset.x;
            this.rectWindowSize.y = e.getY() + mouseDragStartingOffset.y;
            return true;
        }
    }

    public void dragStarted(MouseEvent e) {
        final int offsetX = rectWindowSize.x - e.getX();
        final int offsetY = rectWindowSize.y - e.getY();
        mouseDragStartingOffset.setLocation(offsetX, offsetY);

        hasDragStarted = getOnMouse(e);
    }

    public void dragFinished(MouseEvent e) {
        hasDragStarted = false;
    }
}

class StringBox {
    public static void drawTowardVerticalAxsis(Graphics g, StringBox[] str, int x, int y) {
        int offset = 0;
        for (StringBox s : str) {
            s.draw(g, x, y + offset);
            offset += s.strRect.height;
        }
    }

    private final String str;
    private final Color colorBG;
    private final Color colorStr;
    private final Font font;

    private final Rectangle strRect;

    public StringBox(Graphics g, String str, Color colorBG, Color colorStr, Font font) {
        this.str = str;
        this.colorBG = colorBG;
        this.colorStr = colorStr;
        this.font = font;

        Font tmpFont = g.getFont();
        g.setFont(font);
        this.strRect = g.getFontMetrics().getStringBounds(this.str, g).getBounds();
        g.setFont(tmpFont);
    }

    public void draw(Graphics g, int x, int y) {
        Font tmpFont = g.getFont();
        g.setFont(font);

        Color tmpColor = g.getColor();
        if (colorBG != null) {
            g.setColor(colorBG);
            g.fillRect(x, y, strRect.width, strRect.height);
        }

        if (colorStr != null) {
            g.setColor(colorStr);
            g.drawString(str, x - strRect.x, y - strRect.y);
            g.setColor(tmpColor);
        } else {
            g.setColor(tmpColor);
            g.drawString(str, x - strRect.x, y - strRect.y);
        }

        g.setFont(tmpFont);
    }

    public Rectangle getStrRect() {
        return strRect;
    }
}