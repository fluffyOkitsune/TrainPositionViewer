package data.time_table;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class StationData {
    private String name;
    private float distance;
    private float distProportion;

    public StationData(String name, float distance) {
        this.name = name;
        this.distance = distance;
    }
    
    // --------------------------------------------------------------------------------
    // データ作成
    // --------------------------------------------------------------------------------
    public static StationData[] createStationData(String stationDataCSVPath) throws FileNotFoundException {
        return writeDistProportion(createStationDataArray(new File(stationDataCSVPath)));
    }

    // 駅データ配列を生成する
    private static StationData[] createStationDataArray(Scanner csvScanner) {
        List<StationData> staDataList = new Vector<>();

        while (csvScanner.hasNext()) {
            final String line = csvScanner.nextLine();
            String[] items = line.split(",");
            staDataList.add(new StationData(items[0], Float.parseFloat(items[1])));
        }
        csvScanner.close();
        return staDataList.toArray(new StationData[0]);
    }

    // アダプタ用
    private static StationData[] createStationDataArray(File stationDataCSV) throws FileNotFoundException{
        Scanner csvScanner = new Scanner(stationDataCSV, "UTF-8");
        return createStationDataArray(csvScanner);
    }

    // テスト用
    static StationData[] createStationDataArray(String csvContents) throws FileNotFoundException{
        Scanner csvScanner = new Scanner(csvContents);
        return createStationDataArray(csvScanner);
    }

    // 各駅データに路線キロに対する駅の営業キロの割合を書き込む
    private static StationData[] writeDistProportion(StationData[] arrStationData){
        // 路線全体の長さを算出する
        float startStaDist = arrStationData[0].distance;
        float endStaDist = arrStationData[arrStationData.length - 1].distance;
        float linePathLength = endStaDist - startStaDist;
        for(StationData stationData : arrStationData){
            stationData.distProportion = (stationData.distance - startStaDist) / linePathLength;
        }
        
        return arrStationData;
    }

    // --------------------------------------------------------------------------------
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + Float.floatToIntBits(distance);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        StationData other = (StationData) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (Float.floatToIntBits(distance) != Float.floatToIntBits(other.distance))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "StationData [name=" + name + ", distance=" + distance + "]";
    }

    // --------------------------------------------------------------------------------
    public String getName() {
        return name;
    }

    public float getDistance() {
        return distance;
    }

    public float getDistProportion() {
        return distProportion;
    }
}