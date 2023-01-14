package data.train_data;

import data.line_data.LineData;
import data.time_table.TimeTable;

// ロード後に変化することのない列車運行データを格納する
public class TrainData {
    private TimeTable timeTable;
    private LineData lineData;

    // 臨時列車
    private boolean isExtra;

    public TrainData(TimeTable timeTable, LineData lineData) {
        this.timeTable = timeTable;
        this.lineData = lineData;
        isExtra = containsExtraKeyWord(lineData, timeTable.note);
    }

    private boolean containsExtraKeyWord(LineData lineData, String note) {
        String extrakeyWord = lineData.getExtraKeyWord();
        return note.contains(extrakeyWord);
    }

    public TimeTable getTimeTable() {
        return timeTable;
    }

    public boolean isExtra() {
        return isExtra;
    }

    public TrainData combine(TrainData trainData) {
        TimeTable tt = this.timeTable.combine(trainData.timeTable);
        return new TrainData(tt, lineData);
    }
}
