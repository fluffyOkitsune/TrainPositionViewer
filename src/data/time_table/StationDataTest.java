package data.time_table;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;

import org.junit.Test;

public class StationDataTest {   
    @Test
    public void createDataTest(){
        try {
            String csv = "A,0\nB,1.00\nC,2.5";
            StationData[] stationDatas = StationData.createStationDataArray(csv);

            assertEquals(new StationData("A", 0.0f), stationDatas[0]);
            assertEquals(new StationData("B", 1.0f), stationDatas[1]);
            assertEquals(new StationData("C", 2.5f), stationDatas[2]);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
