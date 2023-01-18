package window;

import javax.swing.*;

import data.Time;
import data.line_data.IndependentLineRegion;
import data.line_data.LineData;
import data.line_data.RegionData;
import sample_data.*;

public class App {
    private MainWindow win;

    public RegionData regionData;

    public static void main(String[] args) {
        App app = new App();
        app.win = new MainWindow(app, "テストウィンドウ", 1000, 500);
        app.win.update();
        app.win.setVisible(true);
        app.win.startAnim();
    }

    App() {
        // 東海道新幹線
        if (false) {
            regionData = new IndependentLineRegion(new TokaidoShinkansen());
        }

        // 山手線 (2018[平日])
        if (false) {
            regionData = new IndependentLineRegion(new YamanoteLine());
        }

        // 中央線 (2018[平日])
        if (false) {
            regionData = new JREChuoLine();
        }

        // 京浜東北線 (2018[平日])
        if (false) {
            regionData = new IndependentLineRegion(new KeihinTohokuLine());
        }

        // 南武線 (2018[平日])
        if (false) {
            regionData = new IndependentLineRegion(new NambuLine());
        }

        // 上野東京ライン・湘南新宿ライン (2018[平日])
        if (true) {
            regionData = new JRE_SubUrbanLine();
        }

        // 東海道線（東京 - 名古屋） (1968)
        if (false) {
            regionData = new IndependentLineRegion(new JNR_TokaidoLine());
        }

        // 東急電鉄線 (2018[平日])
        if (false) {
            LineData[] lineData = {
                    new TokyuIkegamiLine(),
                    new TokyuTamagawaLine(),
                    new TokyuDenentoshiLine(),
                    new TokyuOimachiLine(),
                    new TokyuMeguroLine(),
                    new TokyuToyokoLine()
            };
            regionData = new IndependentLineRegion(lineData);
        }

        // 京王線 (2018[平日])
        if (false) {
            regionData = new KeioCorp();
        }

        // 久留里線 (2018[平日])
        if (false) {
            regionData = new IndependentLineRegion(new KururiLine());
        }

        // 京阪本線 (2017[平日])
        if (false) {
            regionData = new IndependentLineRegion(new KeihanMainLine());
        }

        // 奈良線 (2018[平日])
        if (false) {
            regionData = new IndependentLineRegion(new NaraLine());
        }

        // 宗谷本線 (2018[平日])
        if (false) {
            regionData = new IndependentLineRegion(new SoyaLine());
        }

        regionData.init();
        regionData.defineThroughService();
        regionData.combineTrainData();

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

    public void update(Time currentTime) {
        regionData.update(currentTime);
    }
}
