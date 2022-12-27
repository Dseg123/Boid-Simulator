Programming Assignment 5: K-d Trees

/* *****************************************************************************
 *  First, fill out the mid-semester survey:
 *  https://forms.gle/LdhX4bGvaBYYYXs97
 *
 *  If you're working with a partner, please do this separately.
 *
 *  Type your initials below to confirm that you've completed the survey.
 **************************************************************************** */
DE


/* *****************************************************************************
 *  Describe the Node data type you used to implement the
 *  2d-tree data structure.
 **************************************************************************** */
The Node data type stores a Point2D "point" representing the geometric
point corresponding to the Node, and a Value "val" representing the
value corresponding to the Node. It also stores pointers to Nodes
"left" and "right", the left and right children of the Node in the
kd-tree. Finally, Node stores a RectHV "rect" that bounds the subtree
rooted at that Node.


/* *****************************************************************************
 *  Describe your method for range search in a k-d tree.
 **************************************************************************** */
The range search starts by creating an empty queue to store the points.
Then it recursively calls the function rangeQ to fill the queue. RangeQ
prunes the subtree it is searching if the root's bounding rect does not
intersect the query rect. Otherwise, it enqueues the current root if it 
is in the query rect, then recurses on the left and right subtrees if they
are non-null. This way, every point that could be inside the query rect is
checked at some point.

/* *****************************************************************************
 *  Describe your method for nearest neighbor search in a k-d tree.
 **************************************************************************** */
The nearest neighbor search keeps track of the current closest point
"champ" and the level of the tree "level" in each recursive call.
If the root's bounding rect is further from the query point than the champ is,
we prune the entire subtree and maintain the current champ because
no point in the subtree can be closer. Otherwise, we check if the root
is better than the champ, and set it to be champ if so. Then we use
the level's parity to decide whether to compare the x- or y-coordinates
for the query point and root point. If the query is less than the root,
we recursively search in the left subtree before the right; otherwise
we search right before left. This way, we only check points that could
possibly be the nearest and optimally update the champ at every step.


/* *****************************************************************************
 *  How many nearest-neighbor calculations can your PointST implementation
 *  perform per second for input1M.txt (1 million points), where the query
 *  points are random points in the unit square?
 *
 *  Fill in the table below, rounding each value to use one digit after
 *  the decimal point. Use at least 1 second of CPU time. Do not use -Xint.
 *  (Do not count the time to read the points or to build the 2d-tree.)
 *
 *  Repeat the same question but with your KdTreeST implementation.
 *
 **************************************************************************** */


                 # calls to         /   CPU time     =   # calls to nearest()
                 client nearest()       (seconds)        per second
                ------------------------------------------------------
PointST:         200                /   8.2          =   24.4

KdTreeST:        10000000           /   1.0          =   9689922.5 (wow!)

Note: more calls per second indicates better performance.


/* *****************************************************************************
 *  Known bugs / limitations.
 **************************************************************************** */
None.

/* *****************************************************************************
 *  Describe whatever help (if any) that you received.
 *  Don't include readings, lectures, and precepts, but do
 *  include any help from people (including course staff, lab TAs,
 *  classmates, and friends) and attribute them by name.
 **************************************************************************** */
No help was received.


/* *****************************************************************************
 *  Describe any serious problems you encountered.                    
 **************************************************************************** */
None.

/* *****************************************************************************
 *  If you worked with a partner, assert below that you followed
 *  the protocol as described on the assignment page. Give one
 *  sentence explaining what each of you contributed.
 **************************************************************************** */
Did not work with a partner.



/* *****************************************************************************
 *  List any other comments here. Feel free to provide any feedback   
 *  on  how helpful the class meeting was and on how much you learned 
 * from doing the assignment, and whether you enjoyed doing it.
 **************************************************************************** */
