package window;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;

import data.Time;
import data.line_data.LineData;

public class MainWindow extends JFrame implements ActionListener {
    private App app;

    // メイン描画パネル
    private PanelTrainViewer panelTrainViewer;

    // 表示する日付設定用コンボボックス
    private JComboBox<String> comOperationDate;

    // 表示する時刻設定用スピナー
    private JSpinner spnTime;
    private TimeSpinnerModel spnTimeModel;

    // 現在時刻を描画するチェックボックス
    private JCheckBox cbAutoSetCurrTime;

    // 列車番号を表示するチェックボックス
    private JCheckBox cbDispID;

    public MainWindow(App app, String title, int width, int height) {
        super(title);
        this.app = app;

        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(width, height);
        setLocationRelativeTo(null);

        setLayout(new BorderLayout());

        panelTrainViewer = new PanelTrainViewer(app);
        panelTrainViewer.initialize();

        JScrollPane scrollpane = new JScrollPane(panelTrainViewer);
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

        // 列車番号を表示するチェックボックス
        panel.add(cbDispID = new JCheckBox("列車番号を表示"));
        cbDispID.setActionCommand("DISP_ID");
        cbDispID.addActionListener(this);

        // 運転日時設定用コンボボックス
        comOperationDate = new JComboBox<>();
        comOperationDate.addItem("平日");
        comOperationDate.addItem("土曜");
        comOperationDate.addItem("休日");
        comOperationDate.setActionCommand("DATE");
        comOperationDate.addActionListener(this);
        panel.add(comOperationDate);
        comOperationDate.setSelectedIndex(0);

        // 現在時刻設定用スピナー
        spnTime = new JSpinner(spnTimeModel = new TimeSpinnerModel());
        spnTime.addChangeListener(e -> {
            update();
        });
        spnTime.setPreferredSize(new Dimension(100, 20));
        panel.add(spnTime);

        add(panel, BorderLayout.SOUTH);
    }

    // --------------------------------------------------------------------------------
    // アニメーション用スレッド
    // --------------------------------------------------------------------------------
    private Thread threadAnimation;
    private static final long ANIM_CYCLIC_TIME_MS = 50;

    public void startAnim() {
        threadAnimation = new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        long time = System.currentTimeMillis();
                        panelTrainViewer.drawOffscreen();
                        panelTrainViewer.repaint();
                        long erapsedTime = System.currentTimeMillis() - time;
                        System.out.println(String.format("Erapsed: %d", erapsedTime));

                        long sleepTime = ANIM_CYCLIC_TIME_MS - erapsedTime;
                        if (sleepTime > 0) {
                            sleep(sleepTime);
                        }
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }
        };
        threadAnimation.start();
    }

    // --------------------------------------------------------------------------------
    // 現在時刻をセット処理
    // --------------------------------------------------------------------------------
    private Thread threadAutoGetCurrTime;

    // --------------------------------------------------------------------------------
    // 値変更イベント処理
    // --------------------------------------------------------------------------------
    public void update() {
        Time currentTime = new Time(getHour(), getMin(), getSec());

        // 現在時刻で列車位置データを更新
        app.regionData.update(currentTime);

        panelTrainViewer.repaint();
    }

    // --------------------------------------------------------------------------------
    // ボタン操作処理
    // --------------------------------------------------------------------------------
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
                switchAutoMode();
                break;
            case "DATE":
                setOparationDate(comOperationDate.getSelectedIndex());
                break;
            case "DISP_ID":
                panelTrainViewer.enableDispID = cbDispID.isSelected();
                update();
                break;
        }
    }

    private void setOparationDate(int selectedIndex) {
        switch (selectedIndex) {
            case 0: // 平日
                panelTrainViewer.setOperationDateMask(LineData.OPR_DATE_MASK_WEEKDAYS | LineData.OPR_DATE_MASK_EXTRA);
                break;
            case 1: // 土曜
                panelTrainViewer.setOperationDateMask(LineData.OPR_DATE_MASK_SATURDAY | LineData.OPR_DATE_MASK_EXTRA);
                break;
            case 2: // 休日
                panelTrainViewer.setOperationDateMask(LineData.OPR_DATE_MASK_HOLIDAY | LineData.OPR_DATE_MASK_EXTRA);
                break;
        }
    }

    private void switchAutoMode() {
        if (cbAutoSetCurrTime.isSelected()) {
            if (threadAutoGetCurrTime == null) {
                threadAutoGetCurrTime = new Thread() {
                    @Override
                    public void run() {
                        while (true) {
                            try {
                                LocalDateTime nowDate = LocalDateTime.now();
                                spnTime.getModel().setValue(
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