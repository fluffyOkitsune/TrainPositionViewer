import java.io.*;
import java.time.LocalDateTime;
import java.awt.*;
import java.awt.event.*;

import javax.swing.*;
import javax.swing.event.*;

import data.Time;
import data.line_data.LineData;
import data.line_data.LineData.Direction;
import data.time_table.StationData;
import draw.Train;

public class App implements ChangeListener {
    private MainWindow win;

    public LineData[] lineData;

    public static void main(String[] args) {
        App app = new App();
        app.win = new MainWindow(app, "テストウィンドウ", 1000, 500);
        app.update();
        app.win.setVisible(true);
    }

    App() {
        try {
            // 山手線 (2018[平日])
            if (false) {
                lineData = new LineData[1];
                lineData[0] = new YamanoteLine();
            }

            // 京浜東北線 (2018[平日])
            if (false) {
                lineData = new LineData[1];
                lineData[0] = new KeihinTohokuLine();
            }

            // 南武線 (2018[平日])
            if (false) {
                lineData = new LineData[1];
                lineData[0] = new NambuLine();
            }

            // 東海道線（東京 - 熱海） (2018[平日])
            if (false) {
                lineData = new LineData[1];
                lineData[0] = new TokaidoLine();
            }

            // 京王線 (2018[平日])
            if (false) {
                lineData = new LineData[5];
                lineData[0] = new KeioLine();
                lineData[1] = new KeioNewLine();
                lineData[2] = new KeioSagamiharaLine();
                lineData[3] = new KeioTakaoLine();
                lineData[4] = new ToeiShinjukuLune();
            }

            // 奈良線 (2018[平日])
            if (true) {
                lineData = new LineData[1];
                lineData[0] = new NaraLine();
            }

            for (LineData ld : lineData) {
                ld.importCSV();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            UIManager.setLookAndFeel("com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        update();
    }

    private void update() {
        Time currentTime = getCurrentTime();

        for (LineData ld : lineData) {
            ld.update(currentTime);
        }

        win.update();
    }

    private Time getCurrentTime() {
        int hour = win.getHour();
        int min = win.getMin();
        int sec = win.getSec();

        return new Time(hour, min, sec);
    }
}

class MainWindow extends JFrame implements ActionListener {
    // メイン描画パネル
    private Canvas cvs;

    // 表示する時刻設定用スピナー
    private JSpinner spnTime;
    private TimeSpinnerModel spnTimeModel;

    // 現在時刻を描画する用
    private JCheckBox cbAutoSetCurrTime;

    public MainWindow(App app, String title, int width, int height) {
        super(title);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(width, height);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        cvs = new Canvas(app);

        JScrollPane scrollpane = new JScrollPane(cvs);
        scrollpane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        scrollpane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);

        add(scrollpane, BorderLayout.CENTER);

        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

        // スピナーで変化させる時刻の単位を指定するボタン
        JRadioButton rdBtnH, rdBtnM, rdBtnS;
        panel.add(rdBtnH = new JRadioButton("時"));
        rdBtnH.setActionCommand("HOUR");
        rdBtnH.addActionListener(this);

        panel.add(rdBtnM = new JRadioButton("分"));
        rdBtnM.setSelected(true);
        rdBtnM.setActionCommand("MIN");
        rdBtnM.addActionListener(this);

        panel.add(rdBtnS = new JRadioButton("秒"));
        rdBtnS.setActionCommand("SEC");
        rdBtnS.addActionListener(this);

        new ButtonGroup() {
            {
                add(rdBtnH);
                add(rdBtnM);
                add(rdBtnS);
            }
        };

        // 自動で現在時刻を取得する設定
        panel.add(cbAutoSetCurrTime = new JCheckBox("現在時刻をセット"));
        cbAutoSetCurrTime.setActionCommand("AUTO");
        cbAutoSetCurrTime.addActionListener(this);

        // 現在時刻設定用スピナー
        spnTime = new JSpinner(spnTimeModel = new TimeSpinnerModel());
        spnTime.addChangeListener(app);
        spnTime.setPreferredSize(new Dimension(100, 20));
        panel.add(spnTime);

        add(panel, BorderLayout.SOUTH);
    }

    private Thread threadAutoGetCurrTime;

    @Override
    public void actionPerformed(ActionEvent e) {
        switch (e.getActionCommand()) {
            case "HOUR":
                spnTimeModel.setAddUnit(TimeSpinnerModel.AddUnit.HOUR);
                break;
            case "MIN":
                spnTimeModel.setAddUnit(TimeSpinnerModel.AddUnit.MIN);
                break;
            case "SEC":
                spnTimeModel.setAddUnit(TimeSpinnerModel.AddUnit.SEC);
                break;
            case "AUTO":
                if (cbAutoSetCurrTime.isSelected()) {
                    if (threadAutoGetCurrTime == null) {
                        threadAutoGetCurrTime = new Thread() {
                            @Override
                            public void run() {
                                while (true) {
                                    try {
                                        LocalDateTime nowDate = LocalDateTime.now();
                                        spnTime.setValue(
                                                new Time(nowDate.getHour(), nowDate.getMinute(), nowDate.getSecond()));
                                        sleep(500);
                                    } catch (InterruptedException e) {
                                        break;
                                    }
                                }
                            }
                        };
                        threadAutoGetCurrTime.start();
                    }
                } else {
                    if (threadAutoGetCurrTime != null) {
                        // 現在時刻セット処理スレッドを停止
                        threadAutoGetCurrTime.interrupt();
                        threadAutoGetCurrTime = null;
                    }
                }
                break;
        }
    }

    // --------------------------------------------------------------------------------
    // Time指定用スピナーモデル
    // --------------------------------------------------------------------------------
    class TimeSpinnerModel extends AbstractSpinnerModel {
        public enum AddUnit {
            HOUR, MIN, SEC;
        }

        private Time value;
        private AddUnit addUnit = AddUnit.MIN;

        public TimeSpinnerModel() {
            this.value = new Time(0, 0, 0);
        }

        public void setAddUnit(AddUnit addUnit) {
            this.addUnit = addUnit;
        }

        @Override
        public Object getValue() {
            return this.value;
        }

        @Override
        /**
         * @exception IllegalArgumentException : Time型以外をセットしようとした場合
         */
        public void setValue(Object value) {
            if (!(value instanceof Time time)) {
                throw new IllegalArgumentException("illegal value");
            }
            if (!time.equals(this.value)) {
                this.value = time;
                fireStateChanged();
            }
        }

        @Override
        public Object getNextValue() {
            int hour = value.hour;
            int min = value.min;
            int sec = value.sec;

            switch (addUnit) {
                case HOUR:
                    hour++;
                    break;
                case MIN:
                    min++;
                    break;
                case SEC:
                    sec++;
                    break;
                default:
                    break;
            }

            if (sec == 60) {
                sec = 0;
                min++;
            }
            if (min == 60) {
                min = 0;
                hour++;
            }
            if (hour == 24) {
                hour = 0;
            }
            return new Time(hour, min, sec);
        }

        @Override
        public Object getPreviousValue() {
            int hour = value.hour;
            int min = value.min;
            int sec = value.sec;

            switch (addUnit) {
                case HOUR:
                    hour--;
                    break;
                case MIN:
                    min--;
                    break;
                case SEC:
                    sec--;
                    break;
                default:
                    break;
            }

            if (sec == -1) {
                sec = 59;
                min--;
            }
            if (min == -1) {
                min = 59;
                hour--;
            }
            if (hour == -1) {
                hour = 23;
            }
            return new Time(hour, min, sec);
        }
    }

    public void update() {
        cvs.repaint();
    }

    public int getSec() {
        return ((Time) spnTime.getValue()).sec;
    }

    public int getMin() {
        return ((Time) spnTime.getValue()).min;
    }

    public int getHour() {
        return ((Time) spnTime.getValue()).hour;
    }
}

class Canvas extends JPanel implements MouseInputListener {
    static final Dimension SIZE = new Dimension(4000, 3000);

    private App app;

    Canvas(App app) {
        this.app = app;
        setPreferredSize(Canvas.SIZE);
        addMouseListener(this);
    }

    // --------------------------------------------------------------------------------
    // 描画処理
    // --------------------------------------------------------------------------------
    private static final Color BG_COLOR = Color.LIGHT_GRAY;

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(BG_COLOR);
        g.fillRect(0, 0, SIZE.width, SIZE.height);

        for (LineData ld : app.lineData) {
            drawRailLine(g, ld);
        }

        for (LineData ld : app.lineData) {
            ld.drawTrain(g);
        }

        for (LineData ld : app.lineData) {
            drawStation(g, ld);
        }

        drawTrainInfo(g, selectedTrain);
    }

    private void drawRailLine(Graphics g, LineData lineData) {
        g.setColor(lineData.getLineColor());
        ((Graphics2D) g).setStroke(new BasicStroke(5.0f));

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

    // --------------------------------------------------------------------------------
    // 列車の情報ウィンドウ
    // --------------------------------------------------------------------------------
    private Train selectedTrain = null;

    private void drawTrainInfo(Graphics g, Train train) {
        if (train == null) {
            return;
        }
        if (!train.onDuty) {
            return;
        }

        Rectangle rect = train.getRect();
        int posX = train.getRect().getLocation().x;
        int posY = train.getRect().getLocation().y;

        g.setColor(Color.RED);
        g.drawRect(rect.getLocation().x, rect.getLocation().y, rect.width, rect.height);

        posX += 20;
        posY += 20;

        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(posX, posY, 150, 60);

        g.setColor(train.getTypeColor());
        g.drawRect(posX, posY, 150, 60);

        // 列車番号 種別
        g.drawString(train.trainData.getTimeTable().trainID + " " + train.trainData.getTimeTable().trainType, posX + 10,
                posY + 20);
        g.drawString(generateTrainNameStr(train), posX + 10, posY + 30);
        g.drawString(train.getTerminalName() + "行", posX + 10, posY + 40);
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

    @Override
    public void mouseClicked(MouseEvent e) {
        selectedTrain = null;
        for (LineData ld : app.lineData) {
            for (Train t : ld.getTrain()) {
                if (t.getOnMouse(e)) {
                    selectedTrain = t;
                }
            }
        }
        repaint();
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }
}
