import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.RedBlackBST;
import edu.princeton.cs.algs4.StdOut;

public class PointST<Value> {
    // ordered symbol table of points
    private RedBlackBST<Point2D, Value> st;

    // construct an empty symbol table of points
    public PointST() {
        st = new RedBlackBST<Point2D, Value>();
    }

    // is the symbol table empty?
    public boolean isEmpty() {
        return st.isEmpty();
    }

    // number of points
    public int size() {
        return st.size();
    }

    // associate the value val with point p
    public void put(Point2D p, Value val) {
        if (p == null || val == null) {
            throw new IllegalArgumentException("Arguments must be non-null");
        }
        st.put(p, val);
    }

    // value associated with point p
    public Value get(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException("Argument must be non-null");
        }
        return st.get(p);
    }

    // does the symbol table contain point p?
    public boolean contains(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException("Argument must be non-null");
        }
        return st.contains(p);
    }

    // all points in the symbol table
    public Iterable<Point2D> points() {
        return st.keys();
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) {
            throw new IllegalArgumentException("Argument must be non-null");
        }
        Queue<Point2D> queue = new Queue<Point2D>();
        // naive approach: check all points and enqueue those that are in rect
        for (Point2D p : this.points()) {
            if (rect.contains(p)) {
                queue.enqueue(p);
            }
        }
        return queue;
    }

    // a nearest neighbor of point p; null if the symbol table is empty
    public Point2D nearest(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException("Argument must be non-null");
        }
        if (st.isEmpty()) return null;
        Point2D champ = st.min();
        // naive approach: check all points and set champ to that with min dist
        for (Point2D point : this.points()) {
            if (point.distanceSquaredTo(p) < champ.distanceSquaredTo(p)) {
                champ = point;
            }
        }
        return champ;
    }

    // unit testing (required)
    public static void main(String[] args) {
        Point2D[] points = {
                new Point2D(1, 0), new Point2D(0, 0),
                new Point2D(1, 1), new Point2D(0, 1)
        };

        StdOut.println("Initializing new PointST...");
        PointST<Integer> myST = new PointST<Integer>();
        StdOut.println("Is PointST empty? " + myST.isEmpty());
        StdOut.println("PointST size: " + myST.size());

        StdOut.println("Adding 4 points to PointST...");
        for (int i = 0; i < points.length; i++) {
            myST.put(points[i], i);
        }
        StdOut.println("Is PointST empty? " + myST.isEmpty());
        StdOut.println("PointST size:  " + myST.size());

        StdOut.println("Iterating through PointST points and values...");
        for (Point2D p : myST.points()) {
            StdOut.println(p + ": " + myST.get(p));
        }

        StdOut.println("Does PointST contain (1, 0)? " +
                               myST.contains(new Point2D(1, 0)));
        StdOut.println("Does PointST contain (2, 0)? " +
                               myST.contains(new Point2D(2, 0)));

        RectHV testRect = new RectHV(0.5, -0.2, 1.1, 2);
        StdOut.println("Find the points that are in "
                               + "rectangle [0.5, 1.1] x [-0.2, 2]");
        for (Point2D p : myST.range(testRect)) {
            StdOut.println(p);
        }

        StdOut.println("Find the point closest to (0.2, 0.6): " +
                               myST.nearest(new Point2D(0.2, 0.6)));

        // Code for runtime testing:
        // String filename = args[0];
        // In in = new In(filename);
        // PointST<Integer> fileST = new PointST<Integer>();
        // while (!in.isEmpty()) {
        //     double x = in.readDouble();
        //     double y = in.readDouble();
        //     fileST.put(new Point2D(x, y), 1);
        // }
        //
        // Stopwatch timer = new Stopwatch();
        // int m = 200;
        // for (int i = 0; i < m; i++) {
        //     // StdOut.println(i);
        // Point2D p = new Point2D(StdRandom.uniform(0, 1),
        //                         StdRandom.uniform(0, 1));
        //     fileST.nearest(p);
        // }
        // StdOut.println(timer.elapsedTime());
    }

}
