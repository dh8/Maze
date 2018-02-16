import tester.*;
import javalib.impworld.*;
import java.awt.Color;
import java.util.*;

abstract class ASolver {

    ArrayList<Node> worklist = new ArrayList<Node>(); // Always an arraylist,
                                                      // only difference is
                                                      // where we take from
                                                      // (front or end)
    ArrayList<Node> visited = new ArrayList<Node>(); // All the nodes we've seen
                                                     // already
    boolean solved; // Has the maze been solved?

    HashMap<Node, Edge> cameFromEdge; // Mapping of what edge each node came
                                      // from. Used in reconstructing the path
                                      // from the end to the start
    Node first; // The starting point of the maze

    ArrayList<Node> finishedPath; // The finished path through the maze

    // Constructor
    ASolver(Node first) {
        this.solved = false;
        this.worklist.add(first);
        this.cameFromEdge = new HashMap<Node, Edge>();
        this.visited = new ArrayList<Node>();
        this.first = first;
        this.finishedPath = new ArrayList<Node>();
    }

    // Returns the path from the first node in the maze to the end of the maze
    ArrayList<Node> getFinishedPath() {

        if (this.solved) {
            return this.finishedPath;
        }

        throw new RuntimeException("The maze is not yet solved.");
    }

    // Reconstructs the path from the end of the maze to the start of the maze
    // EFFECT: Stores this path in finishedPath
    void reconstruct(Node end) {

        Node curNode = end;
        ArrayList<Node> path = new ArrayList<Node>();

        while ((curNode != this.first) && path.size() <= visited.size()) {

            path.add(curNode);
            Node neighbor = curNode.getNeighbor(this.cameFromEdge.get(curNode));
            curNode = neighbor;
        }

        if (curNode.equals(this.first)) {
            path.add(this.first);
        }

        this.finishedPath = path;

    }

    // Takes one step through the solving of the maze (for non-Player solvers).
    // Called every time onTick is called until the maze is solved
    void step() {
        // This can't be an abstract method because Player doesn't move on
        // ticks, but instead on key presses.
    }

    // Takes one step through the maze in the given direction,
    void step(String ke) {
        // only implemented in the Player class.
    }

    // Provides jraphics.
    // Renders this maze onto the given background,
    // renders the finished path if solved.
    void renderOnto(WorldScene bg, int size) {
        for (Node n : this.visited) {
            n.renderOnto(bg, size, Color.cyan);
        }

        this.first.renderOnto(bg, size, new Color(0, 155, 0));

        if (this.solved) {
            for (Node n : this.finishedPath) {
                n.renderOnto(bg, size, Color.blue);
            }
        }
    }

    // Given an integer range (max) and a point within that range (idx),
    // return the appropriate color from that relative point in the rainbow.
    Color rainbowColor(int idx, int max) {

        if (idx > max || idx < 0 || max < 0) {
            throw new RuntimeException("Color index out of bounds");
        }

        double colorIdx = idx / (double) max;

        if (colorIdx < 0.333) {
            // R constant
            // B hi to low first half, G low to hi second half
            if (colorIdx < 0.167) {
                return new Color(255, 0, 255 - (int) (255 * colorIdx / 0.167));
            }
            else {
                return new Color(255, (int) (255 * (colorIdx - 0.167) / 0.167),
                    0);
            }
        }
        else if (colorIdx < 0.667) {
            // G constant
            // R hi to low first half, B low to hi second half
            if (colorIdx < 0.5) {
                return new Color(255 - (int) (255 * (colorIdx - 0.333) / 0.167),
                    255, 0);
            }
            else {
                return new Color(0, 255,
                    (int) (255 * (colorIdx - 0.5) / 0.167));
            }
        }
        else {
            // B constant
            // G hi to low first half, R low to hi second half
            if (colorIdx < 0.833) {
                return new Color(0,
                    255 - ((int) (255 * (colorIdx - 0.667) / 0.167)), 255);
            }
            else {
                return new Color((int) (255 * (colorIdx - 0.833) / 0.167), 0,
                    255);
            }
        }
    }
}

// A depth-first maze solver
class DFSSolver extends ASolver {

    DFSSolver(Node first) {
        super(first);
    }

    // Takes one step through the solving of the maze.
    // Called every time onTick is called until the maze is solved
    // EFFECT: worklist updated every time, solved and finishedPath changed on
    // final tick
    @Override
    void step() {

        if (this.worklist.size() != 0) {

            Node next = this.worklist.get(this.worklist.size() - 1);

            if (this.visited.contains(next)) {
                this.worklist.remove(this.worklist.size() - 1);
            }
            else if (next.isEnd()) {
                this.visited.add(next);
                this.reconstruct(next);
                this.solved = true;

            }
            else {
                this.worklist.remove(this.worklist.size() - 1);
                for (int i = 0; i < next.nodeEdges.size(); i += 1) {

                    Node neighbor = next.getNeighbor(next.nodeEdges.get(i));
                    this.worklist.add(neighbor);

                    if (!cameFromEdge.containsKey(neighbor)) {
                        cameFromEdge.put(neighbor, next.nodeEdges.get(i));
                    }
                }
                this.visited.add(next);
            }
        }
    }
}

// A breadth-first solver for mazes
class BFSSolver extends ASolver {

    BFSSolver(Node first) {
        super(first);
    }

    // Takes one step through the solving of the maze.
    // Called every time onTick is called until the maze is solved
    // EFFECT: worklist updated every time, solved and finishedPath changed on
    // final tick
    @Override
    void step() {

        if (this.worklist.size() != 0) {

            Node next = this.worklist.get(0);

            if (this.visited.contains(next)) {
                this.worklist.remove(0);
            }
            else if (next.isEnd()) {
                this.visited.add(next);
                this.reconstruct(next);
                this.solved = true;
            }
            else {
                for (int i = 0; i < next.nodeEdges.size(); i += 1) {
                    Node neighbor = next.getNeighbor(next.nodeEdges.get(i));
                    this.worklist.add(neighbor);
                    if (!cameFromEdge.containsKey(neighbor)) {
                        cameFromEdge.put(neighbor, next.nodeEdges.get(i));
                    }
                }
                this.worklist.remove(0);
                this.visited.add(next);
            }
        }
    }
}

// A class representing a DFS solver and BFS solver competing to find the end.
// (A DFS solver containing a BFS solver).
class SolverFight extends ASolver {
    ASolver myBFS;
    boolean dfsIsSolved;
    // The superclass boolean parameter refers to if the overall maze is solved
    // with one of the algorithms. this boolean and the one inside the BFS
    // solver class will be used to decide which solver's path to draw.

    SolverFight(Node n) {
        super(n);
        this.myBFS = new BFSSolver(n);
    }

    // Takes one step through the solving of the maze.
    // Steps this class's DFS solving progress and its BFS solver.
    // Called every time onTick is called until the maze is solved
    // EFFECT: worklist updated every time, solved and finishedPath changed on
    // final tick
    @Override
    void step() {

        if (this.worklist.size() != 0) {

            Node next = this.worklist.get(this.worklist.size() - 1);

            if (this.visited.contains(next)) {
                this.worklist.remove(this.worklist.size() - 1);
            }
            else if (next.isEnd()) {
                this.visited.add(next);
                this.reconstruct(next);
                this.solved = true;
                this.dfsIsSolved = true;

            }
            else {
                this.worklist.remove(this.worklist.size() - 1);
                for (int i = 0; i < next.nodeEdges.size(); i += 1) {

                    Node neighbor = next.getNeighbor(next.nodeEdges.get(i));
                    this.worklist.add(neighbor);

                    if (!cameFromEdge.containsKey(neighbor)) {
                        cameFromEdge.put(neighbor, next.nodeEdges.get(i));
                    }
                }
                this.visited.add(next);
            }
        }

        this.myBFS.step();
    }

    // Provides jraphics. Renders this maze onto the given background,
    // renders the finished path if solved. Renders both solvers in different
    // colors and the shared nodes in a third color.
    void renderOnto(WorldScene bg, int size) {
        // Draw the DFS visited nodes in one color, and any doubly visited
        // nodes in a different color
        for (Node n : this.visited) {
            if (this.myBFS.visited.contains(n)) {
                n.renderOnto(bg, size, new Color(178, 102, 255));
            }
            else {
                n.renderOnto(bg, size, Color.red);
            }
        }
        // Draw any BFS-only visited nodes in a third color
        for (Node n : this.myBFS.visited) {
            if (!this.visited.contains(n)) {
                n.renderOnto(bg, size, Color.blue);
            }
        }

        this.first.renderOnto(bg, size, new Color(0, 155, 0));

        if (this.solved) {
            if (this.dfsIsSolved) {
                for (Node n : this.finishedPath) {
                    n.renderOnto(bg, size, new Color(153, 0, 0));
                }
            }
            else {
                for (Node n : this.myBFS.finishedPath) {
                    n.renderOnto(bg, size, new Color(0, 0, 153));
                }
            }
        }
    }

    // rendering path: if this.dfsIsSolved is solved, render this.path,
    // else render myBFS.path

}

// A "solver" for mazes that maps each cell to its distance from the start.
class ColorMazeFromStart extends ASolver {

    // Map of nodes to their distances from the start
    HashMap<Node, Integer> distances = new HashMap<Node, Integer>();
    int furthestDist;

    ColorMazeFromStart(Node first) {
        super(first);
        distances.put(first, 0);
    }

    // Maps each node to its distance from the start of the maze
    // EFFECT: Adds to the map of nodes->distances, changes furthestDist
    void step() {

        while (!worklist.isEmpty()) {

            Node cur = this.worklist.remove(0);
            int curDist = this.distances.get(cur);
            this.furthestDist = Math.max(this.furthestDist, curDist);

            for (Edge e : cur.nodeEdges) {

                Node neighbor = cur.getNeighbor(e);

                if (!this.visited.contains(neighbor)) {

                    this.distances.put(neighbor, curDist + 1);
                    this.worklist.add(neighbor);
                    this.visited.add(neighbor);
                }
            }
        }

        this.solved = true;
    }

    // Render all nodes onto bg, with nodes colored by their distances
    void renderOnto(WorldScene bg, int size) {

        for (Node n : this.visited) {
            int nDist = this.distances.get(n);
            n.renderOnto(bg, size, this.rainbowColor(nDist, this.furthestDist));
        }
    }

}

class StepColorer extends ASolver {

    // each step thru maze track the
    ArrayList<ArrayList<Node>> allPaths;
    HashMap<Node, Integer> distances;
    int furthestDist;

    StepColorer(Node first) {
        super(first);
        allPaths = new ArrayList<ArrayList<Node>>();
        ArrayList<Node> startPath = new ArrayList<Node>();
        startPath.add(first);
        allPaths.add(startPath);
        this.furthestDist = 0;
        this.distances = new HashMap<Node, Integer>();
        this.distances.put(first, this.furthestDist);
    }

    // Takes one step through the solving of the maze.
    // This involves taking one step through each path in this.allPaths
    // Called every time onTick is called until the maze is solved
    // EFFECT: allPaths is updated every time, solved and finishedPath changed
    // on final tick
    void step() {
        for (int i = 0; i < this.allPaths.size(); i++) {

            ArrayList<Node> path = this.allPaths.get(i);

            if (path.size() != 0) {
                Node next = path.get(0);

                if (this.visited.contains(next)) {
                    path.remove(0);
                }
                else if (next.isEnd()) {
                    this.visited.add(next);
                    this.furthestDist += 1;
                    this.distances.put(next, this.furthestDist);
                    this.reconstruct(next);
                    this.solved = true;
                }
                else {

                    Node neighbor0 = next.getNeighbor(next.nodeEdges.get(0));

                    path.add(neighbor0);
                    if (!cameFromEdge.containsKey(neighbor0)) {
                        cameFromEdge.put(neighbor0, next.nodeEdges.get(0));
                    }
                    for (int j = 1; j < next.nodeEdges.size(); j += 1) {
                        Node neighbor = next.getNeighbor(next.nodeEdges.get(j));
                        ArrayList<Node> newPath = new ArrayList<Node>();
                        newPath.add(neighbor);
                        allPaths.add(newPath);
                        i = i + 1;
                        if (!cameFromEdge.containsKey(neighbor)) {
                            cameFromEdge.put(neighbor, next.nodeEdges.get(j));
                        }
                    }

                    path.remove(0);
                    this.visited.add(next);
                    this.furthestDist += 1;
                    this.distances.put(next, this.furthestDist);
                }
            }
        }
    }

    // Provides graphics. Renders this maze onto the given background,
    // renders the finished path if solved.
    void renderOnto(WorldScene bg, int size) {
        for (Node n : this.visited) {
            n.renderOnto(bg, size,
                this.rainbowColor(this.distances.get(n), this.furthestDist));
        }

        this.first.renderOnto(bg, size, new Color(0, 155, 0));

        if (this.solved) {
            for (Node n : this.finishedPath) {
                n.renderOnto(bg, size, new Color(210, 192, 192));
            }
        }
        // To not show the finished path, make solved false? unless that
        // conflicts with something else like in onTick.
    }
}

// A fast breadth-first solver (all paths searched simultaneously) for mazes
class FastBFSSolver extends ASolver {

    ArrayList<ArrayList<Node>> allPaths;

    FastBFSSolver(Node first) {
        super(first);
        allPaths = new ArrayList<ArrayList<Node>>();
        ArrayList<Node> startPath = new ArrayList<Node>();
        startPath.add(first);
        allPaths.add(startPath);
    }

    // Takes one step through the solving of the maze.
    // This involves taking one step through each path in this.allPaths
    // Called every time onTick is called until the maze is solved
    // EFFECT: allPaths is updated every time, solved and finishedPath changed
    // on final tick
    void step() {

        for (int i = 0; i < this.allPaths.size(); i++) {

            ArrayList<Node> path = this.allPaths.get(i);

            if (path.size() != 0) {
                Node next = path.get(0);

                if (this.visited.contains(next)) {
                    path.remove(0);
                }
                else if (next.isEnd()) {
                    this.visited.add(next);
                    this.reconstruct(next);
                    this.solved = true;
                }
                else {

                    Node neighbor0 = next.getNeighbor(next.nodeEdges.get(0));
                    path.add(neighbor0);
                    if (!cameFromEdge.containsKey(neighbor0)) {
                        cameFromEdge.put(neighbor0, next.nodeEdges.get(0));
                    }
                    for (int j = 1; j < next.nodeEdges.size(); j += 1) {
                        Node neighbor = next.getNeighbor(next.nodeEdges.get(j));
                        ArrayList<Node> newPath = new ArrayList<Node>();
                        newPath.add(neighbor);
                        allPaths.add(newPath);
                        i = i + 1;
                        if (!cameFromEdge.containsKey(neighbor)) {
                            cameFromEdge.put(neighbor, next.nodeEdges.get(j));
                        }
                    }

                    path.remove(0);
                    this.visited.add(next);
                }
            }
        }
    }
}

// A maze solver of varying intelligence
class Player extends ASolver {

    Node loc; // The solver's location in the maze

    // Constructor
    Player(Node first) {
        super(first);
        this.loc = first;
    }

    // Move a step in the given direction in the maze, if possible.
    // EFFECT: Adds the move to the map of nodes to edges, if the move is
    // onto the end node of the maze, reconstructs the path to the start
    // and marks the maze as solved (changes this.solved to true).
    void step(String ke) {

        if (this.loc.hasNodeInDir(ke)) {
            Node newLoc = this.loc.getNodeInDir(ke);
            Edge connectingEdge = this.loc.getNeighborEdge(newLoc);

            this.loc = newLoc;

            if (!this.visited.contains(newLoc)) {
                this.visited.add(newLoc);
                this.cameFromEdge.put(newLoc, connectingEdge);
            }
            if (newLoc.isEnd()) {
                this.reconstruct(newLoc);
                this.solved = true;
            }
        }
    }

    // Renders the maze onto the background normally, draws this player on top
    void renderOnto(WorldScene bg, int size) {
        super.renderOnto(bg, size);

        this.loc.renderOnto(bg, size, Color.black);
    }
}

class ExamplesSolver {

    Maze m;
    Node n1;
    Node n2;
    Node n3;
    Node n4;
    Node n5;
    Node n6;
    Node n7;
    Node n8;
    Node n9;

    Edge e1 = new Edge();
    Edge e2 = new Edge();
    Edge e3 = new Edge();
    Edge e4 = new Edge();

    void init() {

        m = new Maze(3, 3);
        m.initMaze();
        n1 = new Node(0, 0);
        n2 = new Node(0, 1);
        n3 = new Node(0, 2);
        n4 = new Node(1, 0);
        n5 = new Node(1, 1);
        n6 = new Node(1, 2);
        n7 = new Node(2, 0);
        n8 = new Node(2, 1);
        n9 = new Node(2, 2);

        n1.addEdge(e1);
        n2.addEdge(e1);
        e1.setN1(n1);
        e1.setN2(n2);

        n2.addEdge(e2);
        n3.addEdge(e2);
        e2.setN1(n2);
        e2.setN2(n3);

        n3.addEdge(e3);
        n6.addEdge(e3);
        e3.setN1(n3);
        e3.setN2(n6);

        n6.addEdge(e4);
        n9.addEdge(e4);
        e4.setN1(n6);
        e4.setN2(n9);

    }

    void testGetFinishedPath(Tester t) {

        this.init();

        DFSSolver d = new DFSSolver(m.mazeNodes.get(0));
        t.checkException(new RuntimeException("The maze is not yet solved."), d,
            "getFinishedPath", (Object[]) null);

        d.finishedPath = new ArrayList<Node>();
        d.solved = true;
        t.checkExpect(d.getFinishedPath(), new ArrayList<Node>());

        this.init();

        BFSSolver b = new BFSSolver(m.mazeNodes.get(0));
        t.checkException(new RuntimeException("The maze is not yet solved."), b,
            "getFinishedPath", (Object[]) null);

        ArrayList<Node> path = new ArrayList<Node>();
        path.add(new Node(1, 2));
        path.add(new Node(1, 3));
        b.finishedPath = path;
        b.solved = true;
        t.checkExpect(b.getFinishedPath(), path);

    }

    void testReconstruct(Tester t) {

        this.init();
        BFSSolver b = new BFSSolver(m.mazeNodes.get(0));
        b.cameFromEdge.put(n9, e4);
        b.cameFromEdge.put(n6, e3);
        b.cameFromEdge.put(n3, e2);
        b.cameFromEdge.put(n2, e1);

        ArrayList<Node> refPath = new ArrayList<Node>();
        refPath.add(n9);
        refPath.add(n6);
        refPath.add(n3);
        refPath.add(n2);
        refPath.add(n1);

        b.solved = true;

        b.reconstruct(n9);

        ArrayList<Node> actualPath = b.getFinishedPath();

        t.checkExpect(actualPath.get(0), refPath.get(0));

    }

    void testBFSStep(Tester t) {

        this.init();
        BFSSolver b = new BFSSolver(m.mazeNodes.get(0));

        t.checkExpect(b.cameFromEdge.size(), 0);
        while (!b.solved) {
            b.step();
        }
        t.checkExpect(b.solved, true);
        b.reconstruct(m.endNode);
        t.checkExpect(b.getFinishedPath().get(0), m.endNode);
        t.checkExpect(b.getFinishedPath().get(b.getFinishedPath().size() - 1),
            b.first);
        for (Node n : b.getFinishedPath()) {
            t.checkExpect(b.visited.contains(n), true);
        }

    }

    void testDFSStep(Tester t) {
        this.init();
        DFSSolver d = new DFSSolver(m.mazeNodes.get(0));

        t.checkExpect(d.cameFromEdge.size(), 0);
        while (!d.solved) {
            d.step();
        }
        t.checkExpect(d.solved, true);
        d.reconstruct(m.endNode);
        t.checkExpect(d.getFinishedPath().get(0), m.endNode);
        t.checkExpect(d.getFinishedPath().get(d.getFinishedPath().size() - 1),
            d.first);
        for (Node n : d.getFinishedPath()) {
            t.checkExpect(d.visited.contains(n), true);
        }
    }

    void testFastStep(Tester t) {
        this.init();
        FastBFSSolver f = new FastBFSSolver(m.mazeNodes.get(0));

        t.checkExpect(f.cameFromEdge.size(), 0);
        while (!f.solved) {
            f.step();
        }
        t.checkExpect(f.solved, true);
        f.reconstruct(m.endNode);
        t.checkExpect(f.getFinishedPath().get(0), m.endNode);
        t.checkExpect(f.getFinishedPath().get(f.getFinishedPath().size() - 1),
            f.first);
        for (Node n : f.getFinishedPath()) {
            t.checkExpect(f.visited.contains(n), true);
        }
    }

    void testSolverFight(Tester t) {
        this.init();
        SolverFight f = new SolverFight(m.mazeNodes.get(0));

        t.checkExpect(f.cameFromEdge.size(), 0);
        while (!f.solved) {
            f.step();
        }
        t.checkExpect(f.solved, true);
        // One or the other of the solvers is solved
        t.checkExpect(f.dfsIsSolved || f.myBFS.solved, true);
        // But not both of them are solved
        t.checkExpect(!(f.dfsIsSolved && f.myBFS.solved), true);
        f.reconstruct(m.endNode);
        t.checkExpect(f.getFinishedPath().get(0), m.endNode);
        t.checkExpect(f.getFinishedPath().get(f.getFinishedPath().size() - 1),
            f.first);
        for (Node n : f.getFinishedPath()) {
            t.checkExpect(f.visited.contains(n), true);
        }
    }

    void testColorStep(Tester t) {
        this.init();
        StepColorer s = new StepColorer(m.mazeNodes.get(0));

        t.checkExpect(s.cameFromEdge.size(), 0);
        t.checkExpect(s.visited.size(), 0);

        while (!s.solved) {

            s.step();
        }
        t.checkExpect(s.solved, true);
        s.reconstruct(m.endNode);

        t.checkExpect(s.visited.size() > 1, true);

        t.checkExpect(s.getFinishedPath().get(0), m.endNode);
        t.checkExpect(s.getFinishedPath().get(s.getFinishedPath().size() - 1),
            s.first);

        for (Node n : s.getFinishedPath()) {

            t.checkExpect(s.visited.contains(n), true);

        }

    }

    void testPlayerStep(Tester t) {

        this.init();
        Player p = new Player(m.mazeNodes.get(0));
        t.checkExpect(p.cameFromEdge.size(), 0);
        p.step("down");
        if (p.cameFromEdge.size() > 0) {
            t.checkExpect(p.cameFromEdge.size(), 1);
            p.step("left");
            if (p.cameFromEdge.size() > 1) {
                t.checkExpect(p.cameFromEdge.size(), 2);
            }
        }
    }
}
