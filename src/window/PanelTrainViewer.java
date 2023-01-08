package window;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

import javax.swing.*;
import javax.swing.event.*;

import data.line_data.LineData;
import data.line_data.LineData.Direction;
import data.time_table.StationData;
import draw.Train;

public class PanelTrainViewer extends JPanel implements MouseInputListener {
    static final Dimension WINDOW_CANVAS_SIZE = new Dimension(4000, 3000);

    // オフスクリーンイメージ
    Image offscreenImg;
    Graphics2D offscreenG;

    private App app;
    public boolean enableDispID;

    // 列車情報表示ウィンドウ
    private TrainInfoWindow trainInfoWindow;

    PanelTrainViewer(App app) {
        this.app = app;

        // 変数初期値
        selectedTrain = null;
        stopsStaID = STOPS_NOTHING;

        // 画面を用意する
        setPreferredSize(PanelTrainViewer.WINDOW_CANVAS_SIZE);

        addMouseListener(this);
        addMouseMotionListener(this);
    }

    public void initialize() {
        offscreenImg = new BufferedImage(WINDOW_CANVAS_SIZE.width, WINDOW_CANVAS_SIZE.height,
                BufferedImage.TYPE_4BYTE_ABGR);
        offscreenG = (Graphics2D) offscreenImg.getGraphics();

        trainInfoWindow = new TrainInfoWindow();
    }

    // --------------------------------------------------------------------------------
    // 描画処理
    // --------------------------------------------------------------------------------
    private static final Color BG_COLOR = Color.LIGHT_GRAY;

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        drawOffscreen();
        g.drawImage(offscreenImg, 0, 0, null);
    }

    private void clearImg(Image image, Graphics2D g) {
        Composite temp = g.getComposite();

        g.setComposite(AlphaComposite.Clear);
        g.fillRect(0, 0, image.getWidth(null), image.getHeight(null));

        g.setComposite(temp);
    }

    synchronized public void drawOffscreen() {
        offscreenG.setColor(BG_COLOR);
        offscreenG.fillRect(0, 0, offscreenImg.getWidth(null), offscreenImg.getHeight(null));

        for (LineData ld : app.lineData) {
            drawRailLine(offscreenG, ld);
        }

        drawTrainViewer(offscreenG);
        drawAnimWindows(offscreenG);
    }

    private void drawTrainViewer(Graphics2D g) {
        for (LineData ld : app.lineData) {
            ld.drawTrain(g);
        }

        for (LineData ld : app.lineData) {
            drawStation(g, ld);
        }

        drawStops(g, selectedTrain, stopsStaID);

        for (LineData ld : app.lineData) {
            drawStaName(g, ld);
        }

        drawDestination(g, selectedTrain);

        if (enableDispID) {
            for (LineData ld : app.lineData) {
                ld.drawTrainID(g);
            }
        }
    }

    // アニメーションのあるウィンドウを描画する（50[ms] で描画）
    private void drawAnimWindows(Graphics2D g) {
        trainInfoWindow.drawTrainInfo(offscreenG);
    }

    private final Stroke strokeDrawLine = new BasicStroke(5.0f);

    // --------------------------------------------------------------------------------
    // 描画処理（路線）
    // --------------------------------------------------------------------------------
    private void drawRailLine(Graphics g, LineData lineData) {
        g.setColor(lineData.getLineColor());
        ((Graphics2D) g).setStroke(strokeDrawLine);
        lineData.drawLinePath(g);
    }

    // --------------------------------------------------------------------------------
    // 描画処理（駅）
    // --------------------------------------------------------------------------------
    private void drawStation(Graphics g, LineData lineData) {
        // 駅を書く
        final int radiusOut = 20;
        final int radiusIn = 15;
        for (StationData sd : lineData.getStationData()) {
            Point posO = lineData.calcPosOnLinePath(sd.getDistProportion(), Direction.OUTBOUND);
            Point posI = lineData.calcPosOnLinePath(sd.getDistProportion(), Direction.INBOUND);
            Point pos = new Point((posO.x + posI.x) / 2, (posO.y + posI.y) / 2);

            // 駅の位置を描画する
            g.setColor(lineData.getLineColor());
            g.fillOval(pos.x - radiusOut / 2, pos.y - radiusOut / 2, radiusOut, radiusOut);
            g.setColor(Color.WHITE);
            g.fillOval(pos.x - radiusIn / 2, pos.y - radiusIn / 2, radiusIn, radiusIn);
        }
    }

    private static final Font FONT_STA_NAME = new Font(null, Font.PLAIN, 10);

    private void drawStaName(Graphics g, LineData lineData) {
        for (StationData stationData : lineData.getStationData()) {
            Point pos = calcStationPos(lineData, stationData);
            String staName = stationData.getName();

            // TODO: 縁取り(暫定)
            g.setFont(FONT_STA_NAME);
            g.setColor(Color.WHITE);
            for (int i = 0; i < 9; i++) {
                Point offsetPos = new Point(pos.x + i / 3 - 1, pos.y + i % 3 - 1);
                LineData.drawString(g, staName, offsetPos);
            }

            g.setColor(lineData.getLineColor());
            LineData.drawString(g, staName, pos);
        }
    }

    private Point calcStationPos(LineData lineData, StationData sd) {
        Point posO = lineData.calcPosOnLinePath(sd.getDistProportion(), Direction.OUTBOUND);
        Point posI = lineData.calcPosOnLinePath(sd.getDistProportion(), Direction.INBOUND);
        return new Point((posO.x + posI.x) / 2, (posO.y + posI.y) / 2);
    }

    private static final Font FONT_DESTINATION = new Font(null, Font.BOLD, 16);

    private void drawDestination(Graphics2D g, Train train) {
        if (train == null) {
            return;
        }

        LineData lineData = train.getLineData();
        StationData stationData = lineData.getStationData(train.getTerminalStaID());
        Point pos = calcStationPos(lineData, stationData);
        String staName = stationData.getName();

        // TODO: 縁取り(暫定)
        g.setFont(FONT_DESTINATION);
        g.setColor(Color.WHITE);
        for (int i = 0; i < 9; i++) {
            Point offsetPos = new Point(pos.x + i / 3 - 1, pos.y + i % 3 - 1);
            LineData.drawString(g, staName, offsetPos);
        }

        g.setColor(train.getTypeColor());
        LineData.drawString(g, staName, pos);
    }

    private int[] stopsStaID;
    private Train selectedTrain;
    private int drawStopsAnimCnt = 0;
    private int drawStopsAnimCntTh = 50;

    private static final int[] STOPS_NOTHING = new int[0];

    public void drawStops(Graphics2D g, Train train, int[] stopsStaID) {
        if (train == null || stopsStaID.length == 0) {
            return;
        }
        if (!train.onDuty) {
            return;
        }

        final int radiusOut = 30;
        final int radiusMid = 25;
        final int radiusIn = 20;

        LineData lineData = train.getLineData();
        Color baseColor = train.getTypeColor();

        for (int staID : stopsStaID) {
            StationData sd = lineData.getStationData(staID);
            Point posO = lineData.calcPosOnLinePath(sd.getDistProportion(), Direction.OUTBOUND);
            Point posI = lineData.calcPosOnLinePath(sd.getDistProportion(), Direction.INBOUND);
            Point pos = new Point((posO.x + posI.x) / 2, (posO.y + posI.y) / 2);

            // これから停車する駅は目立つようにして、すでに過ぎた駅は半透明にする。

            if (hasPassedStation(train, staID)) {
                baseColor = new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(),
                        getStopsColorAlpha());
            } else {
                baseColor = new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), 0x40);
            }

            g.setColor(baseColor);
            g.fillOval(pos.x - radiusOut / 2, pos.y - radiusOut / 2, radiusOut, radiusOut);
            g.setColor(Color.WHITE);
            g.fillOval(pos.x - radiusMid / 2, pos.y - radiusMid / 2, radiusMid, radiusMid);
            g.setColor(baseColor);
            g.fillOval(pos.x - radiusIn / 2, pos.y - radiusIn / 2, radiusIn, radiusIn);
        }
        if (drawStopsAnimCnt < drawStopsAnimCntTh) {
            drawStopsAnimCnt++;
        } else {
            drawStopsAnimCnt = 0;
        }
    }

    private boolean hasPassedStation(Train train, int staID) {
        // 上りの場合はstaIDが小さくなる方向に列車が進むことになる。
        if (train.getDirection() == Direction.OUTBOUND) {
            return staID > train.getDepartedStaID();
        } else {
            return staID < train.getDepartedStaID();
        }
    }

    private int getStopsColorAlpha() {
        int harfOfdrawStopsAnimCntTh = drawStopsAnimCntTh / 2;
        if (drawStopsAnimCnt < harfOfdrawStopsAnimCntTh) {
            return 255 * drawStopsAnimCnt / harfOfdrawStopsAnimCntTh;
        } else {
            return 255 * (drawStopsAnimCntTh - drawStopsAnimCnt) / harfOfdrawStopsAnimCntTh;
        }
    }

    private int[] getStopsStaID(Train train) {
        if (train == null) {
            return STOPS_NOTHING;
        } else {
            return train.trainData.getTimeTable().getStopsStaID();
        }
    }

    // --------------------------------------------------------------------------------
    // マウスイベント
    // --------------------------------------------------------------------------------
    @Override
    public void mouseClicked(MouseEvent e) {
        selectTrain(seekClickedTrain(e));
        repaint();
    }

    // 列車をクリックして選択したときの処理
    private void selectTrain(Train train) {
        selectedTrain = train;

        trainInfoWindow.selectTrain(train, offscreenG);
        stopsStaID = getStopsStaID(train);
    }

    // クリックした列車アイコンに対応する列車データを探索する
    private Train seekClickedTrain(MouseEvent e) {
        Train train = null;
        for (LineData ld : app.lineData) {
            for (Train t : ld.getTrain()) {
                if (t.getOnMouse(e)) {
                    train = t;
                }
            }
        }
        return train;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        trainInfoWindow.dragStarted(e);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        trainInfoWindow.dragFinished(e);
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (trainInfoWindow.dragWindow(e)) {
            repaint();
        }
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }
}