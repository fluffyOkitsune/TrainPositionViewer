package data.train_data;

import data.time_table.TimeTable;

// ロード後に変化することのない列車運行データを格納する
public class TrainData {
    private TimeTable timeTable;

    public TrainData(TimeTable timeTable) {
        this.timeTable = timeTable;
    }

    public TimeTable getTimeTable(){
        return timeTable;
    }
}
