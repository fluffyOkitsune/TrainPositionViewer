package data.line_data;

import java.awt.Point;

@FunctionalInterface
public interface CalcPosOnLinePath {
    public abstract Point calcPositionOnLinePath(float dist);
}