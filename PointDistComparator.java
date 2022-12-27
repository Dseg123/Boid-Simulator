import edu.princeton.cs.algs4.Point2D;

import java.util.Comparator;

public class PointDistComparator implements Comparator<Point2D> {

    private Point2D myCenter;

    public PointDistComparator(Point2D p) {
        myCenter = p;
    }

    @Override
    public int compare(Point2D p1, Point2D p2) {
        return Double.compare(p1.distanceSquaredTo(myCenter), p2.distanceSquaredTo(myCenter));
    }
}
