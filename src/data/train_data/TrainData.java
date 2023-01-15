package data.train_data;

import data.line_data.LineData;
import data.time_table.TimeTable;

// ロード後に変化することのない列車運行データを格納する
public class TrainData {
    private TimeTable timeTable;
    private LineData lineData;

    // 列車運転日
    private int operationDate;

    public TrainData(TimeTable timeTable, LineData lineData) {
        this.timeTable = timeTable;
        this.lineData = lineData;
        operationDate = lineData.getOperationDate(this);
    }

    public TimeTable getTimeTable() {
        return timeTable;
    }

    public int getOperationDate() {
        return operationDate;
    }

    public TrainData combine(TrainData trainData) {
        TimeTable tt = this.timeTable.combine(trainData.timeTable);
        return new TrainData(tt, lineData);
    }
}
