package sample_data;

import java.awt.Point;

import data.line_data.LineData;
import data.line_data.RegionData;
import data.time_table.StationData;

public class JRE_SubUrbanLine extends RegionData {
    public JRE_SubUrbanLine() {
        LineData[] lineData = {
                new UtsunomiyaLine(),
                new TakasakiLine(),
                new JobanLine(),
                new JobanLineRapid(),
                new UenoTokyoLine(),
                new ShonanShinjukuLine(),
                new TokaidoLine(),
                new YokosukaLine(),
                new SaikyoLine()
        };

        setLineData(lineData);
    }

    public static final Point ORIGIN = new Point(50, 50);

    @Override
    public void defineThroughService() {
        StationData from, to;

        // 湘南新宿ライン⇔宇都宮線・高崎線
        from = lineData[5].getStationData("大宮");
        to = lineData[0].getStationData("大宮");
        addTrainsfer(from, to);
        to = lineData[1].getStationData("大宮");
        addTrainsfer(from, to);

        // 上野東京ライン⇔宇都宮線・高崎線・常磐線・常磐線[快速]
        from = lineData[4].getStationData("上野");
        to = lineData[0].getStationData("上野");
        addTrainsfer(from, to);
        to = lineData[1].getStationData("上野");
        addTrainsfer(from, to);
        to = lineData[2].getStationData("上野");
        addTrainsfer(from, to);
        to = lineData[3].getStationData("上野");
        addTrainsfer(from, to);

        // 上野東京ライン⇔東海道線
        from = lineData[4].getStationData("東京");
        to = lineData[6].getStationData("東京");
        addTrainsfer(from, to);

        // 湘南新宿ライン⇔東海道線・横須賀線
        from = lineData[5].getStationData("大船");
        to = lineData[6].getStationData("大船");
        addTrainsfer(from, to);
        to = lineData[7].getStationData("大船");
        addTrainsfer(from, to);
    }
}
