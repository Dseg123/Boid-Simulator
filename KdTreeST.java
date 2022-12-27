import edu.princeton.cs.algs4.MaxPQ;
import edu.princeton.cs.algs4.Point2D;
import edu.princeton.cs.algs4.Queue;
import edu.princeton.cs.algs4.RectHV;
import edu.princeton.cs.algs4.StdOut;

import java.util.Comparator;

public class KdTreeST<Value> {

    // number of nodes in tree
    private int size;
    // pointer to root node
    private Node root;

    private class Node {
        // point corresponding to the node
        private Point2D point;
        // value corresponding to the node
        private Value val;
        // pointer to node's left child
        private Node left;
        // pointer to node's right child
        private Node right;
        // bounding rectangle corresponding to node
        private RectHV rect;

        // constructor for node that assings point, value, rectangle
        public Node(Point2D p, Value v, RectHV r) {
            point = p;
            val = v;
            rect = r;
            left = null;
            right = null;
        }

    }

    // construct an empty symbol table of points
    public KdTreeST() {
        size = 0;
        root = null;
    }

    // is the symbol table empty?
    public boolean isEmpty() {
        return size == 0;
    }

    // number of points
    public int size() {
        return size;
    }

    // associate the value val with point p
    public void put(Point2D p, Value val) {
        if (p == null || val == null) {
            throw new IllegalArgumentException("Arguments must be non-null");
        }

        // only increment size if p not already in tree
        if (!this.contains(p)) size++;

        // mutable array of bounds to update during insertion
        // (starts with infinite bounds)
        double[] bounds = {
                Double.NEGATIVE_INFINITY,
                Double.NEGATIVE_INFINITY,
                Double.POSITIVE_INFINITY,
                Double.POSITIVE_INFINITY
        };

        root = put(root, p, val, 0, bounds);
    }

    // private helper method for associating value val with point p, starting
    // at root h on level "level" and with current bounding rectangle "bounds"
    private Node put(Node h, Point2D p, Value val, int level, double[] bounds) {
        // when we reach the bottom, create a new node
        if (h == null) {
            RectHV myRect = new RectHV(bounds[0], bounds[1], bounds[2], bounds[3]);
            return new Node(p, val, myRect);
        }

        // if desired point is found, update value
        if (h.point.equals(p)) {
            h.val = val;
            return h;
        }

        // change comparison and bounds depending on even/odd level
        double cmp;
        if (level % 2 == 0) {
            cmp = p.x() - h.point.x();
            if (cmp < 0) bounds[2] = h.point.x();
            else bounds[0] = h.point.x();
        }
        else {
            cmp = p.y() - h.point.y();
            if (cmp < 0) bounds[3] = h.point.y();
            else bounds[1] = h.point.y();
        }

        // recurse left or right depending on comparison
        if (cmp < 0) h.left = put(h.left, p, val, level + 1, bounds);
        else h.right = put(h.right, p, val, level + 1, bounds);

        return h;
    }

    // value associated with point p
    public Value get(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException("Argument must be non-null");
        }

        return get(root, p, 0);
    }

    // private helper method for getting value associated with point p, starting
    // at root h on level "level"
    private Value get(Node h, Point2D p, int level) {
        if (h == null) return null; // p not in tree
        // if we found point, return its value
        if (h.point.equals(p)) return h.val;
        // change comparison depending on even/odd level
        double cmp = p.x() - h.point.x();
        if (level % 2 == 1) {
            cmp = p.y() - h.point.y();
        }

        // recurse left or right depending on comparison
        if (cmp < 0) return get(h.left, p, level + 1);
        else return get(h.right, p, level + 1);
    }

    // does the symbol table contain point p?
    public boolean contains(Point2D p) {
        return get(p) != null;
    }

    // all points in the symbol table IN LEVEL ORDER
    public Iterable<Point2D> points() {
        // queue that points will be added to
        Queue<Point2D> queue = new Queue<Point2D>();
        int counter = 0;
        // mini queue for keeping track of what points to search
        Queue<Node> mini = new Queue<Node>();
        // starting with root, repeatedly dequeue and add children to maintain
        // level order
        mini.enqueue(root);
        // only stop when we have read exactly size points
        while (counter < size) {
            Node curr = mini.dequeue();
            queue.enqueue(curr.point);
            counter++;
            if (curr.left != null) mini.enqueue(curr.left);
            if (curr.right != null) mini.enqueue(curr.right);
        }
        return queue;
    }

    // all points that are inside the rectangle (or on the boundary)
    public Iterable<Point2D> range(RectHV rect) {
        if (rect == null) {
            throw new IllegalArgumentException("Argument must be non-null");
        }
        Queue<Point2D> q = new Queue<Point2D>();
        rangeQ(rect, root, q);
        return q;
    }

    // private helper method to fill a queue with points inside bounding
    // rect, starting at root h
    private void rangeQ(RectHV rect, Node h, Queue<Point2D> q) {
        // prune subtree if rect does not intersect bounding rect
        if (h == null || !h.rect.intersects(rect)) return;

        // otherwise, enqueue point and recurse on left and right
        if (rect.contains(h.point)) q.enqueue(h.point);
        rangeQ(rect, h.left, q);
        rangeQ(rect, h.right, q);
    }

    // a nearest neighbor of point p; null if the symbol table is empty
    public Point2D nearest(Point2D p) {
        if (p == null) {
            throw new IllegalArgumentException("Argument must be non-null");
        }
        if (root == null) return null;
        return nearest(p, root, root.point, 0);
    }

    public Iterable<Point2D> nearest(Point2D p, int k) {
        if (p == null) {
            throw new IllegalArgumentException("Argument must be non-null");
        }
        if (root == null) return null;
        Comparator<Point2D> comp = new PointDistComparator(p);
        MaxPQ<Point2D> myQ = new MaxPQ<Point2D>(comp);
        nearest(p, root, 0, k, myQ);
        return myQ;
    }

    // private helper method to find nearest neighbor of point p, starting
    // at root h with current champion champ and level "level"
    private Point2D nearest(Point2D p, Node h, Point2D champ, int level) {
        if (h == null) return champ;
        // prune if champ is closer to p than current bounding rect
        if (h.rect.distanceSquaredTo(p) > champ.distanceSquaredTo(p)) return champ;

        // set new champ if h is closer to p than champ is
        if (h.point.distanceSquaredTo(p) < champ.distanceSquaredTo(p)) {
            champ = h.point;
        }

        // different comparison depending on even/odd level
        double cmp;
        if (level % 2 == 0) {
            cmp = p.x() - h.point.x();
        }
        else {
            cmp = p.y() - h.point.y();
        }

        // check subtree with p before subtree without p
        if (cmp < 0) {
            champ = nearest(p, h.left, champ, level + 1);
            return nearest(p, h.right, champ, level + 1);
        }
        else {
            champ = nearest(p, h.right, champ, level + 1);
            return nearest(p, h.left, champ, level + 1);
        }
    }

    private String toString(Iterable<Point2D> q) {
        String myStr = "";
        for (Point2D p : q) {
            myStr += p.toString() + " ";
        }
        return myStr;

    }

    private void nearest(Point2D p, Node h, int level, int k,
                         MaxPQ<Point2D> q) {
        if (h == null) return;

        if (q.size() < k) {
            q.insert(h.point);
        }
        else {
            Point2D worstIn = q.max();
            if (h.rect.distanceSquaredTo(p) > worstIn.distanceSquaredTo(p)) {
                return;
            }
            else if (h.point.distanceSquaredTo(p) < worstIn.distanceSquaredTo(p)) {
                q.delMax();
                q.insert(h.point);
            }
        }

        // different comparison depending on even/odd level
        double cmp;
        if (level % 2 == 0) {
            cmp = p.x() - h.point.x();
        }
        else {
            cmp = p.y() - h.point.y();
        }

        // check subtree with p before subtree without p
        if (cmp < 0) {
            nearest(p, h.left, level + 1, k, q);
            nearest(p, h.right, level + 1, k, q);
        }
        else {
            nearest(p, h.right, level + 1, k, q);
            nearest(p, h.left, level + 1, k, q);
        }
    }

    // unit testing (required)
    public static void main(String[] args) {
        Point2D[] points = {
                new Point2D(1, 0), new Point2D(0, 0),
                new Point2D(1, 1), new Point2D(0, 1)
        };

        StdOut.println("Initializing new KdTreeST...");
        KdTreeST<Integer> myST = new KdTreeST<Integer>();
        StdOut.println("Is KdTreeST empty? " + myST.isEmpty());
        StdOut.println("KdTreeST size: " + myST.size());

        StdOut.println("Adding 4 points to KdTreeST...");
        for (int i = 0; i < points.length; i++) {
            myST.put(points[i], i);
        }
        StdOut.println("Is KdTreeST empty? " + myST.isEmpty());
        StdOut.println("KdTreeST size:  " + myST.size());

        StdOut.println("Iterating through KdTreeST points and values...");
        for (Point2D p : myST.points()) {
            StdOut.println(p + ": " + myST.get(p));
        }

        StdOut.println("Does KdTreeST contain (1, 0)? " +
                               myST.contains(new Point2D(1, 0)));
        StdOut.println("Does KdTreeST contain (2, 0)? " +
                               myST.contains(new Point2D(2, 0)));

        RectHV testRect = new RectHV(0.5, -0.2, 1.1, 2);
        StdOut.println("Find the points that are in "
                               + "rectangle [0.5, 1.1] x [-0.2, 2]");
        for (Point2D p : myST.range(testRect)) {
            StdOut.println(p);
        }

        StdOut.println("Find the point closest to (0.2, 0.6): " +
                               myST.nearest(new Point2D(0.2, 0.6)));

        StdOut.println("Find the points closest to (0.2, 0.6): ");
        Iterable<Point2D> q = myST.nearest(new Point2D(0.2, 0.6), 2);
        for (Point2D p : q) {
            StdOut.println(p);
        }


        // Code for runtime testing:
        // String filename = args[0];
        // In in = new In(filename);
        // KdTreeST<Integer> fileST = new KdTreeST<Integer>();
        // while (!in.isEmpty()) {
        //     double x = in.readDouble();
        //     double y = in.readDouble();
        //     fileST.put(new Point2D(x, y), 1);
        // }
        //
        // Stopwatch timer = new Stopwatch();
        // int m = 10000000;
        // for (int i = 0; i < m; i++) {
        //     // StdOut.println(i);
        // Point2D p = new Point2D(StdRandom.uniform(0, 1),
        //                         StdRandom.uniform(0, 1));
        //     fileST.nearest(p);
        // }
        // StdOut.println(timer.elapsedTime());
    }
}
