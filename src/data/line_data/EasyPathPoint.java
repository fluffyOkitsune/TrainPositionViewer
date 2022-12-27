package data.line_data;

import java.awt.Point;

public class EasyPathPoint {
    private Point origin;
    private float endPointDist;
    private CalcPosOnLinePath function;

    protected EasyPathPoint(){
        
    };

    public static EasyPathPoint getInstance(float endPointDist, CalcPosOnLinePath function) {
        return getInstance(endPointDist, function, new Point(0, 0));
    }

    public static EasyPathPoint getInstance(float endPointDist, CalcPosOnLinePath function, Point origin) {
        return new EasyPathPoint(endPointDist, function, origin);
    }

    private EasyPathPoint(float endPointDist, CalcPosOnLinePath function, Point origin) {
        this.endPointDist = endPointDist;
        this.function = function;
        this.origin = origin;
    }

    public float getEndPointDist() {
        return endPointDist;
    }

    public Point calcPositionOnLinePath(float dist) {
        Point p = function.calcPositionOnLinePath(dist);

        // 原点へ平行移動
        p.x += origin.x;
        p.y += origin.y;

        return p;
    }
}