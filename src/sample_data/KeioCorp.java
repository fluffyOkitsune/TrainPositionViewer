package sample_data;

import data.line_data.LineData;
import data.line_data.RegionData;
import data.time_table.StationData;

public class KeioCorp extends RegionData {
    public KeioCorp() {
        LineData[] lineData = {
                new KeioLine(),
                new KeioNewLine(),
                new KeioSagamiharaLine(),
                new KeioTakaoLine(),
                new ToeiShinjukuLine()
        };

        setLineData(lineData);
    }

    @Override
    public void defineThroughService() {
        StationData from, to;

        // 京王線＆新線 : 笹塚
        from = lineData[0].getStationData("笹塚");
        to = lineData[1].getStationData("笹塚");
        addTrainsfer(from, to);

        // 京王線＆相模原線 : 調布
        from = lineData[0].getStationData("調布");
        to = lineData[2].getStationData("調布");
        addTrainsfer(from, to);

        // 京王線＆高尾線 : 北野
        from = lineData[0].getStationData("北野");
        to = lineData[3].getStationData("北野");
        addTrainsfer(from, to);
    }
}
