package data.line_data;

// 独立した路線用
public class IndependentLineRegion extends RegionData {
    public IndependentLineRegion(LineData lineData) {
        this.lineData = new LineData[1];
        this.lineData[0] = lineData;
    }

    public IndependentLineRegion(LineData[] lineData) {
        this.lineData = lineData;
    }

    @Override
    public void defineThroughService() {
    }

}
