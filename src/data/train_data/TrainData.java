package data.train_data;

import data.line_data.LineData;
import data.time_table.TimeTable;

// ロード後に変化することのない列車運行データを格納する
public class TrainData {
    private TimeTable timeTable;

    // 列車運転日
    private int operationDate;

    public TrainData(TimeTable timeTable) {
        this.timeTable = timeTable;
    }

    public TrainData(TimeTable timeTable, LineData lineData) {
        this(timeTable);
        operationDate = lineData.getOperationDate(this);
    }

    public TimeTable getTimeTable() {
        return timeTable;
    }

    public int getOperationDate() {
        return operationDate;
    }

    public TrainData combine(TrainData trainData) {
        TrainData res = new TrainData(this.timeTable.combine(trainData.timeTable));
        res.operationDate = this.operationDate;
        return res;
    }

    @Override
    public String toString() {
        return "TrainData[" + timeTable.getTrainID() + "]";
    }
}
