package window;

import java.awt.*;
import java.awt.event.*;

import draw.Train;

// 列車の情報ウィンドウ
public class TrainInfoWindow {
    private Train selectedTrain;
    private Rectangle rect;
    private boolean hasDragStarted;

    public TrainInfoWindow() {
        selectedTrain = null;
        rect = new Rectangle(0, 0, 150, 70);
    }

    private final Stroke strokeDrawTrainIndicationFrame = new BasicStroke(5.0f);
    private final Stroke strokeDrawWindowFrame = new BasicStroke(2.0f);

    private int cntAnimOpen;
    private final int cntAnimOpenTh = 5;

    private String[] contents;
    private Rectangle maxStrSizeRext;

    public void drawTrainInfo(Graphics g) {
        if (selectedTrain == null) {
            return;
        }
        if (!selectedTrain.onDuty) {
            return;
        }

        drawTrainIndicationFrame(g);

        // 最大サイズの文字に合わせる
        rect.width = maxStrSizeRext.width;
        rect.height = maxStrSizeRext.height * contents.length;

        if (cntAnimOpen < cntAnimOpenTh) {
            cntAnimOpen++;
            float h, y;

            // 下地（若干広く描画する）
            ((Graphics2D) g).setStroke(strokeDrawWindowFrame);
            g.setColor(genComplementaryColor(selectedTrain.getTypeColor()));
            h = (rect.height + 2) * cntAnimOpen / cntAnimOpenTh;
            y = (rect.height + 2) * (cntAnimOpenTh - cntAnimOpen) / cntAnimOpenTh;
            g.fillRect(rect.x - 1, (int) (rect.y - 1 + y / 2), rect.width + 2, (int) h);

            // 枠（若干広く描画する）
            g.setColor(selectedTrain.getTypeColor());
            h = (rect.height + 4) * cntAnimOpen / cntAnimOpenTh;
            y = (rect.height + 4) * (cntAnimOpenTh - cntAnimOpen) / cntAnimOpenTh;
            g.drawRect(rect.x - 2, (int) (rect.y - 2 + y / 2), rect.width + 4, (int) h);

        } else {
            // 下地（若干広く描画する）
            ((Graphics2D) g).setStroke(strokeDrawWindowFrame);
            g.setColor(genComplementaryColor(selectedTrain.getTypeColor()));
            g.fillRect(rect.x - 1, rect.y - 1, rect.width + 2, rect.height + 2);

            // 枠（若干広く描画する）
            g.setColor(selectedTrain.getTypeColor());
            g.drawRect(rect.x - 2, rect.y - 2, rect.width + 4, rect.height + 4);

            // 文字を描画する
            drawStringsTowardVerticalAxsis(g, contents, rect.getLocation());
        }
    }

    // 補色を計算する
    private Color genComplementaryColor(Color color) {
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

    // 選択した列車を指し示す枠
    private void drawTrainIndicationFrame(Graphics g) {
        Rectangle frameRect = selectedTrain.getRect();

        g.setColor(Color.RED);
        ((Graphics2D) g).setStroke(strokeDrawTrainIndicationFrame);
        g.drawRect(frameRect.x, frameRect.y, frameRect.width, frameRect.height);
    }

    // 各文字列の最大サイズを計算する
    private Rectangle calcWindowSizeRect(Graphics g, String[] str) {
        int maxWidth = 0;
        int maxHeight = 0;
        for (String s : str) {
            if (s.isEmpty()) {
                continue;
            } else {
                Rectangle rectText = g.getFontMetrics().getStringBounds(s, g).getBounds();
                maxWidth = Integer.max(maxWidth, rectText.width);
                maxHeight = Integer.max(maxHeight, rectText.height);
            }
        }
        return new Rectangle(0, 0, maxWidth, maxHeight);
    }

    private void drawStringsTowardVerticalAxsis(Graphics g, String[] str, Point startDrawStr) {
        int posY = 0;
        for (String s : str) {
            Rectangle rectText = g.getFontMetrics().getStringBounds(s, g).getBounds();
            posY += rectText.height;
            g.drawString(s, startDrawStr.x, startDrawStr.y + posY);
        }
    }

    private String generateTrainNameStr(Train train) {
        String str = "";
        String trainName = train.trainData.getTimeTable().trainName;
        if (trainName.isEmpty()) {
            return str;
        }
        str += "\n" + trainName;

        // 号
        String trainNo = train.trainData.getTimeTable().trainNo;
        if (trainNo.isEmpty()) {
            return str;
        }
        str += " " + trainNo + "号";

        return str;
    }

    public void selectTrain(Train train, Graphics2D g) {
        this.selectedTrain = train;
        this.cntAnimOpen = 0;
        if (train == null) {
            this.rect.x = 0;
            this.rect.y = 0;
        } else {
            // ウィンドウを移動できるようにするため、値渡しする
            this.rect.x = train.getRect().x - train.getRect().width / 2;
            this.rect.y = train.getRect().y - train.getRect().height / 2;

            // 表示する情報
            String trainID = selectedTrain.trainData.getTimeTable().trainID;
            String trainType = selectedTrain.trainData.getTimeTable().trainType;

            String trainMainInfo = String.format("%s [%s]", trainID, trainType);
            String trainName = generateTrainNameStr(selectedTrain);
            String destination = selectedTrain.getTerminalName() + "行";
            String note = "";
            if (selectedTrain.trainData.isExtra()) {
                note = "◆《運転日注意》";
            }

            contents = new String[4];
            contents[0] = trainMainInfo;
            contents[1] = trainName;
            contents[2] = destination;
            contents[3] = note;

            // ウィンドウのサイズを計算する
            maxStrSizeRext = calcWindowSizeRect(g, contents);
        }
    }

    // --------------------------------------------------------------------------------
    // マウスイベント
    // --------------------------------------------------------------------------------
    public boolean getOnMouse(MouseEvent e) {
        if (selectedTrain == null) {
            return false;
        } else {
            return this.rect.contains(new Point(e.getX(), e.getY()));
        }
    }

    private Point mouseDragStartingOffset = new Point(0, 0);

    public boolean dragWindow(MouseEvent e) {
        if (!hasDragStarted) {
            return false;
        } else {
            this.rect.x = e.getX() + mouseDragStartingOffset.x;
            this.rect.y = e.getY() + mouseDragStartingOffset.y;
            return true;
        }
    }

    public void dragStarted(MouseEvent e) {
        final int offsetX = rect.x - e.getX();
        final int offsetY = rect.y - e.getY();
        mouseDragStartingOffset.setLocation(offsetX, offsetY);

        hasDragStarted = getOnMouse(e);
    }

    public void dragFinished(MouseEvent e) {
        hasDragStarted = false;
    }

}