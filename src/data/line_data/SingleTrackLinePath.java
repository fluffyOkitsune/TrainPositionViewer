package data.line_data;

import java.awt.Point;

public class SingleTrackLinePath extends EasyPathPoint {
    public static EasyPathPoint getInstance(float endPointDist, Point offset, float stationLength, Point begin,
            Point end) {
        return getInstance(endPointDist, d -> {
            Point newOffset;

            // 交換駅付近の上下線のズレ（offset）を計算
            if (d < stationLength) {
                float proportion = 1.0f - (d / stationLength);
                newOffset = new Point((int) (offset.x * proportion), (int) (offset.y * proportion));

            } else if (d < 1.0f - stationLength) {
                // 間の部分
                newOffset = new Point(0, 0);

            } else {
                float proportion = (d - (1.0f - stationLength)) / stationLength;
                newOffset = new Point((int) (offset.x * proportion), (int) (offset.y * proportion));
            }

            // パスを計算
            int pathLengthX = end.x - begin.x;
            int pathLengthY = end.y - begin.y;
            return new Point((int) (begin.x + pathLengthX * d) + newOffset.x,
                    (int) (begin.y + pathLengthY * d) + newOffset.y);
        });
    }
}