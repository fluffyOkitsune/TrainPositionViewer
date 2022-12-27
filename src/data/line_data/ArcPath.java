package data.line_data;

import java.awt.Point;

public class ArcPath extends EasyPathPoint {
    public static EasyPathPoint getInstance(float endPointDist, Point center, float radius, float degBegin,
            float degEnd) {

        return getInstance(endPointDist, d -> {
            // deg -> rad
            float radBegin = (float) (degBegin * 2 * Math.PI / 360.0f);
            float radEnd = (float) (degEnd * 2 * Math.PI / 360.0f);
            float angle = radBegin + d * (radEnd - radBegin);

            int x = center.x + (int) (radius * Math.cos(angle));
            int y = center.y + (int) (radius * Math.sin(angle));
            return new Point(x, y);
        });
    }

}