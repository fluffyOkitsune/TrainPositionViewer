package window;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;

import javax.swing.*;
import javax.swing.event.*;

import data.line_data.LineData;
import data.line_data.RegionData;
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

    // 列車選択表示枠
    private TrainCursol trainIndicationWindow;

    PanelTrainViewer(App app) {
        this.app = app;

        // 変数初期値
        selectedTrain = null;
        stopsStations = RegionData.STOPS_NOTHING;

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
        trainIndicationWindow = new TrainCursol();
    }

    private StationData[] getStopsStations(Train train) {
        if (train == null) {
            return RegionData.STOPS_NOTHING;
        } else {
            return train.getTimeTable().getStopStations();
        }
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

    synchronized public void drawOffscreen() {
        offscreenG.setColor(BG_COLOR);
        offscreenG.fillRect(0, 0, offscreenImg.getWidth(null), offscreenImg.getHeight(null));

        app.regionData.drawRailLine(offscreenG);

        drawTrainViewer(offscreenG);
        drawAnimWindows(offscreenG);
    }

    private StationData[] stopsStations;
    private Train selectedTrain;

    private int operationDateMask;

    private void drawTrainViewer(Graphics2D g) {

        app.regionData.drawTrain(g, operationDateMask);
        trainIndicationWindow.drawTrainCursol(offscreenG);
        
        app.regionData.drawStation(g);

        app.regionData.drawStops(g, selectedTrain, stopsStations);

        app.regionData.drawStaName(g);

        drawDestination(g, selectedTrain);

        if (enableDispID) {
            app.regionData.drawTrainID(g);
        }
    }

    private static final Font FONT_DESTINATION = new Font(null, Font.BOLD, 16);

    private void drawDestination(Graphics2D g, Train train) {
        if (train == null) {
            return;
        }

        StationData stationData = train.getTerminalStation();
        Point pos = stationData.calcStationPos();
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

    // アニメーションのあるウィンドウを描画する（50[ms] で描画）
    private void drawAnimWindows(Graphics2D g) {
        trainInfoWindow.drawTrainInfo(offscreenG);        
    }

    // --------------------------------------------------------------------------------
    // マウスイベント
    // --------------------------------------------------------------------------------
    @Override
    public void mouseClicked(MouseEvent e) {
        selectTrain(app.regionData.seekClickedTrain(e));
        repaint();
    }

    // 列車をクリックして選択したときの処理
    private void selectTrain(Train train) {
        selectedTrain = train;

        trainIndicationWindow.selectTrain(train);
        trainInfoWindow.selectTrain(train, offscreenG);

        stopsStations = getStopsStations(train);
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

    public void setOperationDateMask(int operationDateMask) {
        this.operationDateMask = operationDateMask;
    }
}