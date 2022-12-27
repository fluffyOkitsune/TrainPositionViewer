package data.line_data;

import java.awt.Point;

public class LineSegmentPath extends EasyPathPoint {
    public static EasyPathPoint getInstance(float endPointDist, Point begin, Point end) {
        int pathLengthX = end.x - begin.x;
        int pathLengthY = end.y - begin.y;
        return getInstance(endPointDist, d -> {
            return new Point((int) (begin.x + pathLengthX * d), (int) (begin.y + pathLengthY * d));
        });
    }

}