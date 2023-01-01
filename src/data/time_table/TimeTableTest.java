package data.time_table;

import static org.junit.Assert.assertEquals;

import java.awt.Color;
import java.awt.Image;
import java.awt.Point;

import org.junit.Test;

import data.Time;
import data.line_data.LineData;
import data.line_data.LineData.Direction;
import data.train_data.TrainData;
import draw.Train;

public class TimeTableTest {
    public TimeTable createTestData() {
        TimeTable tt = new TimeTable(Direction.OUTBOUND);

        // A:始発駅
        tt.setDeparture(0, "1200");

        // B:発のみの駅
        tt.setDeparture(1, "1210");

        // C:発着駅
        tt.setArrived(2, "1220");
        tt.setDeparture(2, "1230");

        // D:着のみの駅
        tt.setArrived(3, "1240");

        // E:終着駅
        tt.setArrived(4, "1250");

        tt.packData(5, Direction.OUTBOUND);

        return tt;
    }

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
        public Point calcPositionOnLinePath(float dist, Direction direction) {
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

    @Test
    public void TestGetTrainStopping() {
        LineData lineData = new TestLineData();

        StationData[] testStationDatas = {
                new StationData("A", 0.0f),
                new StationData("B", 0.1f),
                new StationData("C", 0.2f),
                new StationData("D", 0.3f),
                new StationData("E", 0.4f)
        };
        lineData.setStationData(testStationDatas);

        TimeTable timeTable = createTestData();
        Train[] train = { new Train(lineData, new TrainData(timeTable, lineData)) };
        lineData.setTrain(train);

        // 着発が同一駅ID : 駅に停車中
        // A-B

        lineData.update(Time.parseTime("1205"));
        assertEquals(0, train[0].getDepartedStaID());
        assertEquals(1, train[0].getDitinationStaID());

        // B-C
        lineData.update(Time.parseTime("1210"));
        assertEquals(1, train[0].getDepartedStaID());
        assertEquals(2, train[0].getDitinationStaID());

        // B-C
        lineData.update(Time.parseTime("1215"));
        assertEquals(1, train[0].getDepartedStaID());
        assertEquals(2, train[0].getDitinationStaID());

        // C
        lineData.update(Time.parseTime("1225"));
        assertEquals(2, train[0].getDepartedStaID());
        assertEquals(2, train[0].getDitinationStaID());

        // D-E
        lineData.update(Time.parseTime("1245"));
        assertEquals(3, train[0].getDepartedStaID());
        assertEquals(4, train[0].getDitinationStaID());

        // E
        lineData.update(Time.parseTime("1251"));
        assertEquals(4, train[0].getDepartedStaID());
        assertEquals(4, train[0].getDitinationStaID());
    }
}
