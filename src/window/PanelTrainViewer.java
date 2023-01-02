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

    // 列車ビューワのレイヤ
    Image layerTrainViewerImg;
    Graphics2D layerTrainViewerG;

    // ウィンドウのアニメーションのレイヤ
    Image layerAnimWindowImg;
    Graphics2D layerAnimWindowG;

    private App app;
    public boolean enableDispID;

    // 列車情報表示ウィンドウ
    private TrainInfoWindow trainInfoWindow;

    PanelTrainViewer(App app) {
        this.app = app;

        // 画面を用意する
        setPreferredSize(PanelTrainViewer.WINDOW_CANVAS_SIZE);

        addMouseListener(this);
        addMouseMotionListener(this);
    }

    public void initialize() {
        offscreenImg = new BufferedImage(WINDOW_CANVAS_SIZE.width, WINDOW_CANVAS_SIZE.height,
                BufferedImage.TYPE_4BYTE_ABGR);
        offscreenG = (Graphics2D) offscreenImg.getGraphics();

        layerTrainViewerImg = new BufferedImage(WINDOW_CANVAS_SIZE.width, WINDOW_CANVAS_SIZE.height,
                BufferedImage.TYPE_4BYTE_ABGR);
        layerTrainViewerG = (Graphics2D) layerTrainViewerImg.getGraphics();

        layerAnimWindowImg = new BufferedImage(WINDOW_CANVAS_SIZE.width, WINDOW_CANVAS_SIZE.height,
                BufferedImage.TYPE_4BYTE_ABGR);
        layerAnimWindowG = (Graphics2D) layerAnimWindowImg.getGraphics();

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

        offscreenG.drawImage(layerTrainViewerImg, 0, 0, null);
        offscreenG.drawImage(layerAnimWindowImg, 0, 0, null);
    }

    // 列車ビューワを描画する（update時に描画する）
    synchronized public void drawLayerTrainViewer() {
        clearImg(layerTrainViewerImg, layerTrainViewerG);

        for (LineData ld : app.lineData) {
            drawRailLine(layerTrainViewerG, ld);
        }

        for (LineData ld : app.lineData) {
            ld.drawTrain(layerTrainViewerG);
        }

        for (LineData ld : app.lineData) {
            drawStation(layerTrainViewerG, ld);
        }

        if (enableDispID) {
            for (LineData ld : app.lineData) {
                ld.drawTrainID(layerTrainViewerG);
            }
        }
    }

    // アニメーションのあるウィンドウを描画する（50[ms] で描画）
    synchronized public void drawLayerAnimWindow() {
        clearImg(layerAnimWindowImg, layerAnimWindowG);

        trainInfoWindow.drawTrainInfo(layerAnimWindowG);

    }

    private final Stroke strokeDrawLine = new BasicStroke(5.0f);

    private void drawRailLine(Graphics g, LineData lineData) {
        g.setColor(lineData.getLineColor());
        ((Graphics2D) g).setStroke(strokeDrawLine);

        // 路線を書く
        int NUM_SEPARATE = 1000;

        // 下り線
        for (int i = 0; i < NUM_SEPARATE; i++) {
            Point start = lineData.calcPositionOnLinePath((float) i / NUM_SEPARATE, Direction.OUTBOUND);
            Point end = lineData.calcPositionOnLinePath(((float) i + 1) / NUM_SEPARATE, Direction.OUTBOUND);
            g.drawLine(start.x, start.y, end.x, end.y);
        }

        // 上り線
        for (int i = 0; i < NUM_SEPARATE; i++) {
            Point start = lineData.calcPositionOnLinePath((float) i / NUM_SEPARATE, Direction.INBOUND);
            Point end = lineData.calcPositionOnLinePath(((float) i + 1) / NUM_SEPARATE, Direction.INBOUND);
            g.drawLine(start.x, start.y, end.x, end.y);
        }
    }

    private void drawStation(Graphics g, LineData lineData) {
        // 駅を書く
        final int radiusOut = 20;
        final int radiusIn = 15;
        for (StationData sd : lineData.getStationData()) {
            Point posO = lineData.calcPositionOnLinePath(sd.getDistProportion(), Direction.OUTBOUND);
            Point posI = lineData.calcPositionOnLinePath(sd.getDistProportion(), Direction.INBOUND);
            Point pos = new Point((posO.x + posI.x) / 2, (posO.y + posI.y) / 2);

            // 駅の位置を描画する
            g.setColor(lineData.getLineColor());
            g.fillOval(pos.x - radiusOut / 2, pos.y - radiusOut / 2, radiusOut, radiusOut);
            g.setColor(Color.WHITE);
            g.fillOval(pos.x - radiusIn / 2, pos.y - radiusIn / 2, radiusIn, radiusIn);

            // 駅名を描画する
            String staName = sd.getName();

            // TODO: 縁取り(暫定)
            g.setColor(Color.WHITE);
            for (int i = 0; i < 9; i++) {
                Point offsetPos = new Point(pos.x + i / 3 - 1, pos.y + i % 3 - 1);
                LineData.drawString(g, staName, offsetPos);
            }

            g.setColor(lineData.getLineColor());
            LineData.drawString(g, staName, pos);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        // クリックした列車アイコンに対応する列車データを探索し、セットする
        Train selectedTrain = null;
        for (LineData ld : app.lineData) {
            for (Train t : ld.getTrain()) {
                if (t.getOnMouse(e)) {
                    selectedTrain = t;
                }
            }
        }
        trainInfoWindow.selectTrain(selectedTrain, layerAnimWindowG);

        repaint();
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