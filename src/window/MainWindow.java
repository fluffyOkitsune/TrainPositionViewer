package window;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.time.LocalDateTime;

import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;

import data.Time;

public class MainWindow extends JFrame implements ActionListener {
    // メイン描画パネル
    private PanelTrainViewer panelTrainViewer;

    // 表示する時刻設定用スピナー
    private JSpinner spnTime;
    private TimeSpinnerModel spnTimeModel;

    // 現在時刻を描画するチェックボックス
    private JCheckBox cbAutoSetCurrTime;
    // 列車番号を表示するチェックボックス
    private JCheckBox cbDispID;

    public MainWindow(App app, String title, int width, int height) {
        super(title);
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

        // 現在時刻設定用スピナー
        spnTime = new JSpinner(spnTimeModel = new TimeSpinnerModel());
        spnTime.addChangeListener(app);
        spnTime.setPreferredSize(new Dimension(100, 20));
        panel.add(spnTime);

        add(panel, BorderLayout.SOUTH);

    }

    private Thread threadAnimation;

    public void startAnim() {
        threadAnimation = new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        panelTrainViewer.drawLayerAnimWindow();
                        sleep(25);
                    } catch (InterruptedException e) {
                        break;
                    }
                }
            }
        };
        threadAnimation.start();
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
                switchAutoMode();
                break;
            case "DISP_ID":
                panelTrainViewer.enableDispID = cbDispID.isSelected();
                update();
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
    }

    public void update() {
        panelTrainViewer.drawLayerTrainViewer();
        panelTrainViewer.drawLayerAnimWindow();
        panelTrainViewer.repaint();
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