package data.time_table;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.awt.*;

import org.junit.Test;

import data.line_data.LineData;
import data.train_data.TrainData;

class TestLineData extends LineData {

    @Override
    public Color getLineColor() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getLineName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected String getStationDataCsvPath() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected String getTimeTableOutCsvPath() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected String getTimeTableInCsvPath() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Point calcPosOnLinePath(float dist, Direction direction) {
        return new Point(0, 0);
    }

    @Override
    public Image getIconImg(TrainData trainData) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Color getTypeColor(TrainData trainData) {
        // TODO Auto-generated method stub
        return null;
    }

}

public class StationDataTest {
    @Test
    public void createDataTest() {
        try {
            String csv = "A,0\nB,1.00\nC,2.5";
            LineData lineData = new TestLineData();
            StationData[] stationDatas = StationData.createStationDataArray(lineData, csv);

            assertEquals(new StationData(0, lineData, "A", 0.0f), stationDatas[0]);
            assertEquals(new StationData(1, lineData, "B", 1.0f), stationDatas[1]);
            assertEquals(new StationData(2, lineData, "C", 2.5f), stationDatas[2]);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
