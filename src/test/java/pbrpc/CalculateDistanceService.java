package pbrpc;

import java.awt.Point;

public class CalculateDistanceService {

    public Point calc(Point first, Point second) {
        Point p = new Point();
        p.setLocation(first.getX(), second.getY());
        return p;
    }
}
