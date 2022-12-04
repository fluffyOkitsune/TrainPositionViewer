package data.time_table;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import data.Time;
import data.line_data.LineData.Direction;
import data.train_data.TrainData;

public class TimeTableTest {
    public TimeTable createTestData() {
        TimeTable tt = new TimeTable();

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

    @Test
    public void TestGetTrainStopping() {
        TimeTable timeTable = createTestData();

        // 着発が同一駅ID : 駅に停車中
        // A-B
        assertEquals(0, timeTable.createCurrTrainData(Direction.OUTBOUND, Time.parseTime("1205")).getDepartedStaID());
        assertEquals(1, timeTable.createCurrTrainData(Direction.OUTBOUND, Time.parseTime("1205")).getDitinationStaID());
        // B-C
        assertEquals(1, timeTable.createCurrTrainData(Direction.OUTBOUND, Time.parseTime("1210")).getDepartedStaID());
        assertEquals(2, timeTable.createCurrTrainData(Direction.OUTBOUND, Time.parseTime("1210")).getDitinationStaID());
        // B-C
        assertEquals(1, timeTable.createCurrTrainData(Direction.OUTBOUND, Time.parseTime("1215")).getDepartedStaID());
        assertEquals(2, timeTable.createCurrTrainData(Direction.OUTBOUND, Time.parseTime("1215")).getDitinationStaID());
        // C
        assertEquals(2, timeTable.createCurrTrainData(Direction.OUTBOUND, Time.parseTime("1225")).getDepartedStaID());
        assertEquals(2, timeTable.createCurrTrainData(Direction.OUTBOUND, Time.parseTime("1225")).getDitinationStaID());
        // D-E
        assertEquals(3, timeTable.createCurrTrainData(Direction.OUTBOUND, Time.parseTime("1245")).getDepartedStaID());
        assertEquals(4, timeTable.createCurrTrainData(Direction.OUTBOUND, Time.parseTime("1245")).getDitinationStaID());
        assertEquals(4, timeTable.createCurrTrainData(Direction.OUTBOUND, Time.parseTime("1251")).getDepartedStaID());
        assertEquals(4, timeTable.createCurrTrainData(Direction.OUTBOUND, Time.parseTime("1251")).getDitinationStaID());
    }
}
