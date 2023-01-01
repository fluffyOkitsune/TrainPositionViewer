package window;
import java.io.*;

import javax.swing.*;
import javax.swing.event.*;

import data.Time;
import data.line_data.LineData;
import sample_data.*;

public class App implements ChangeListener {
    private MainWindow win;

    public LineData[] lineData;

    public static void main(String[] args) {
        App app = new App();
        app.win = new MainWindow(app, "テストウィンドウ", 1000, 500);
        app.update();
        app.win.setVisible(true);
        app.win.startAnim();
    }

    App() {
        try {
            // 東海道新幹線
            if (true) {
                lineData = new LineData[1];
                lineData[0] = new TokaidoShinkansen();
            }

            // 山手線 (2018[平日])
            if (false) {
                lineData = new LineData[1];
                lineData[0] = new YamanoteLine();
            }

            // 中央線 (2018[平日])
            if (false) {
                lineData = new LineData[3];
                lineData[0] = new ChuoLineRapid();
                lineData[1] = new OumeLine();
                lineData[2] = new ChuoSobuLine();
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
                lineData[4] = new ToeiShinjukuLine();
            }

            // 久留里線 (2018[平日])
            if (false) {
                lineData = new LineData[1];
                lineData[0] = new KururiLine();
            }

            // 奈良線 (2018[平日])
            if (false) {
                lineData = new LineData[1];
                lineData[0] = new NaraLine();
            }

            // 宗谷本線 (2018[平日])
            if (false) {
                lineData = new LineData[1];
                lineData[0] = new SoyaLine();
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
