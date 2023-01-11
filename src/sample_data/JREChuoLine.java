package sample_data;

import data.line_data.LineData;
import data.line_data.RegionData;
import data.time_table.StationData;

public class JREChuoLine extends RegionData {
    public JREChuoLine() {
        LineData[] lineData = {
                new ChuoSobuLine(),
                new ChuoLineRapid(),
                new OumeLine()
        };

        setLineData(lineData);
    }

    @Override
    public void defineThroughService() {
        StationData from, to;

        // 中央線[各停]＆中央線[快速] : 御茶ノ水
        from = lineData[0].getStationData("御茶ノ水");
        to = lineData[1].getStationData("御茶ノ水");
        addTrainsfer(from, to);

        // 中央線[各停]＆中央線[快速] : 三鷹
        from = lineData[0].getStationData("三鷹");
        to = lineData[1].getStationData("三鷹");
        addTrainsfer(from, to);

        // 中央線[快速]＆青梅線 : 立川
        from = lineData[1].getStationData("立川");
        to = lineData[2].getStationData("立川");
        addTrainsfer(from, to);
    }
}
