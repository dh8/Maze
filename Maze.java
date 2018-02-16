import tester.*;
import javalib.impworld.*;
import java.awt.Color;
import javalib.worldimages.*;
import java.util.*;

class EdgeComp implements Comparator<Edge> {

    public int compare(Edge e1, Edge e2) {
        return e1.compare(e2);
    }
}

// A node in a graph (a.k.a. Vertex, but too late to rename everything)
class Node {

    ArrayList<Edge> nodeEdges; // The edges this node is connected to
    int x; // x position in graph
    int y; // y position in graph

    // Constructor
    Node(int x, int y) {
        this.nodeEdges = new ArrayList<Edge>();
        this.x = x;
        this.y = y;
    }

    // Add the given edge to this node's list of connected edges
    void addEdge(Edge e) {
        if (!this.nodeEdges.contains(e) && this.nodeEdges.size() < 4) {
            this.nodeEdges.add(e);
        }
        return;
    }

    // Return the node at the other end of the given edge (if this node is
    // connected to the given edge)
    Node getNeighbor(Edge e) {

        if (this.nodeEdges.contains(e)) {
            if (e.n1.equals(this)) {
                return e.n2;
            }
            else if (e.n2.equals(this)) {
                return e.n1;
            }
        }

        throw new RuntimeException("Given edge is not connected to this node");
    }

    // Return the edge that connects this node to the given node, if one exists
    // (throws exception if not)
    Edge getNeighborEdge(Node neighbor) {

        if (neighbor.equals(this)) {
            throw new RuntimeException("Neighbor cannot be same as this node");
        }
        for (Edge e : this.nodeEdges) {
            if (e.hasNode(neighbor)) {
                return e;
            }
        }
        throw new RuntimeException(
            "Node does not connect to the given neighbor");
    }

    // EFFECT: Removes from this node any edges not in the given list
    void filterEdges(ArrayList<Edge> goodEdges) {

        for (int i = 0; i < this.nodeEdges.size(); i++) {

            if (!goodEdges.contains(this.nodeEdges.get(i))) {
                this.nodeEdges.remove(i);
                i -= 1;
            }
        }
    }

    // Produces an image of this node in the given color
    WorldImage render(int size, Color color) {
        return new RectangleImage(size, size, OutlineMode.SOLID, color);
    }

    // EFFECT: Draws this node onto the given background, in the given color
    void renderOnto(WorldScene bg, int size, Color color) {
        bg.placeImageXY(this.render(size, color), this.x * size + size / 2,
            this.y * size + size / 2);
    }

    // Is this node an EndNode? no
    boolean isEnd() {
        return false;
    }

    // Does this node have an edge in the given grid direction?
    boolean hasNodeInDir(String ke) {

        for (Edge e : this.nodeEdges) {

            Node neighbor = this.getNeighbor(e);

            if (ke.equals("up") && (neighbor.y == this.y - 1)) {
                return true;
            }
            else if (ke.equals("down") && (neighbor.y == this.y + 1)) {
                return true;
            }
            else if (ke.equals("left") && (neighbor.x == this.x - 1)) {
                return true;
            }
            else if (ke.equals("right") && (neighbor.x == this.x + 1)) {
                return true;
            }
        }
        return false;
    }

    // If this node has an edge in the given grid direction, returns it; throws
    // exception if not.
    Node getNodeInDir(String ke) {

        for (Edge e : this.nodeEdges) {

            Node neighbor = this.getNeighbor(e);

            if (ke.equals("up") && (neighbor.y == this.y - 1)) {
                return neighbor;
            }
            else if (ke.equals("down") && (neighbor.y == this.y + 1)) {
                return neighbor;
            }
            else if (ke.equals("left") && (neighbor.x == this.x - 1)) {
                return neighbor;
            }
            else if (ke.equals("right") && (neighbor.x == this.x + 1)) {
                return neighbor;
            }
        }

        throw new RuntimeException(
            "Node does not have an edge in the given direction");
    }
}

// Represents an end node in a maze
class EndNode extends Node {

    EndNode(int x, int y) {
        super(x, y);
    }

    // Produces the image of this node
    WorldImage render(int size) {
        return new RectangleImage(size, size, OutlineMode.SOLID, Color.MAGENTA);
    }

    // Is this node an end node? Yes.
    boolean isEnd() {
        return true;
    }
}

// Represents an undirected edge in a graph
class Edge {

    Node n1; // One of the nodes it connects to
    Node n2; // The other node it connects to

    int weight; // The weight of this edge

    // Constructor that assigns this edge a random weight, 0 <= weight < 1000
    Edge() {
        this.n1 = null;
        this.n2 = null;

        this.weight = (int) (Math.random() * 1000);
    }

    // Constructor assigning the edge a given weight
    Edge(int weight) {
        this.n1 = null;
        this.n2 = null;
        this.weight = weight;
    }

    // EFFECT: Sets the given node in this edge's node 1 position
    void setN1(Node n1) {
        this.n1 = n1;
    }

    // EFFECT: Sets the given node in this edge's node 2 position
    void setN2(Node n2) {
        this.n2 = n2;
    }

    // Does this edge connect to the given node?
    boolean hasNode(Node n) {
        return this.n1.equals(n) || this.n2.equals(n);
    }

    // Compare this edge's weight to that edge's weight
    // + means this>that, 0 means this==that, - means this<that
    int compare(Edge that) {
        return this.weight - that.weight;
    }

    // Draws an image of this edge
    WorldImage render(int size, boolean horizontal) {
        if (horizontal) {
            return new RectangleImage(2, size, OutlineMode.SOLID, Color.black);
        }
        else {
            return new RectangleImage(size, 2, OutlineMode.SOLID, Color.black);
        }
    }

    // EFFECT: Draws this edge onto the given background
    void renderOnto(WorldScene bg, int size) {
        boolean horizontal = (this.n1.y == this.n2.y);

        double x;
        double y;
        if (horizontal) {
            x = (this.n1.x + this.n2.x) / 2.0;
            y = this.n1.y;
            // bg.placeImageXY(this.render(size, horizontal), (int) (x * size),
            // (int) (y * size));
        }
        else {
            x = this.n1.x;
            y = (this.n1.y + this.n2.y) / 2.0;
            // bg.placeImageXY(this.render(size, horizontal), (int) (x * size),
            // (int) (y * size));
        }

        bg.placeImageXY(this.render(size, horizontal),
            (int) (x * size + size / 2), (int) (y * size + size / 2));
    }
}

// Representing a maze with exactly one start point, end point, and solution
class Maze extends World {

    ArrayList<Node> mazeNodes; // All the nodes in this maze/graph
    ArrayList<Edge> mazeEdges; // All the edges in this maze/graph
    ArrayList<Edge> spanningTree; // The edges that make up the maze part
    Node endNode;

    int width; // Width in nodes of the maze
    int height; // Height in nodes of the maze

    int cellSize; // Size of the node (cell) when rendered, in pixels maybe

    final static int WINDOW_WIDTH = 1000; // Width of the animation window
    final static int WINDOW_HEIGHT = 600; // Height of the animation window

    // Map of nodes to representatives. Node first, representative second
    HashMap<Node, Node> reps;

    ASolver solver;

    boolean mazeStarted;
    boolean displayColor;
    boolean playerSolving;
    boolean displayScores;

    // Constructor that does not initialize spanning tree, solver, or player
    Maze(int width, int height) {
        this.width = width;
        this.height = height;
        this.cellSize = Math.min(Maze.WINDOW_WIDTH / width,
            Maze.WINDOW_HEIGHT / height);
        this.mazeNodes = new ArrayList<Node>();
        this.mazeEdges = new ArrayList<Edge>();
        this.spanningTree = new ArrayList<Edge>();
        this.reps = new HashMap<Node, Node>();

        this.mazeStarted = false;
        this.displayColor = false;
        this.playerSolving = false;
        this.displayScores = false;
    }

    // Constructor that also initializes spanning tree etc. and solver
    Maze(int width, int height, boolean bfs) {

        this(width, height);

        this.initMaze();

        if (bfs) {
            this.solver = new BFSSolver(this.mazeNodes.get(0));
        }
        else {
            this.solver = new DFSSolver(this.mazeNodes.get(0));
        }

    }

    // Constructor that initializes maze to be solved by player
    Maze() {
        this(100, 60);
        this.initMaze();
        this.solver = new Player(this.mazeNodes.get(0));
    }

    // EFFECT: Adds nodes and edges to this maze's lists of each.
    // Initializes endNode.
    void randomEdges() {

        ArrayList<Node> prevRow = new ArrayList<Node>();
        for (int r = 0; r < this.width; r += 1) {

            ArrayList<Node> curRow = new ArrayList<Node>();

            for (int c = 0; c < this.height; c += 1) {

                Node curNode;

                if ((r == this.width - 1) && (c == this.height - 1)) {
                    curNode = new EndNode(r, c);
                    this.endNode = curNode;
                }
                else {
                    curNode = new Node(r, c);
                }

                this.mazeNodes.add(curNode);
                curRow.add(curNode);

                // If we aren't in the top row, set top edge
                if (prevRow.size() != 0) {
                    // Connect the edge properly to the current node
                    Edge e1 = new Edge(); // random value edge
                    curNode.addEdge(e1);
                    e1.setN1(curNode);
                    // Connect the edge properly to the top node
                    Node topNode = prevRow.get(c);
                    topNode.addEdge(e1);
                    e1.setN2(topNode);

                    this.mazeEdges.add(e1);
                }

                // If this isn't the first node in row, set left edge
                if (c != 0) {
                    // Connect the edge properly to the current node
                    Edge e2 = new Edge(); // random value edge
                    curNode.addEdge(e2);
                    e2.setN1(curNode);
                    // Connect the edge properly to the left node
                    Node leftNode = curRow.get(c - 1);
                    leftNode.addEdge(e2);
                    e2.setN2(leftNode);

                    this.mazeEdges.add(e2);
                }
            }

            prevRow = curRow;
            curRow = new ArrayList<Node>();
        }
    }

    // EFFECT: Sorts this maze's list of edges by edge weight
    void sortEdges() {
        Collections.sort(this.mazeEdges, new EdgeComp());
    }

    // Creates the initial mapping of nodes to representatives
    // EFFECT: adds all nodes w/ themselves as representatives
    // to this maze's HashMap of nodes to representatives
    void initReps() {
        for (Node n : this.mazeNodes) {
            reps.put(n, n);
        }
    }

    // EFFECT: Makes a minimum spanning tree out of this maze's edges, stores
    // it in this class
    void createSpanningTree() {

        for (int i = 0; i < this.mazeEdges.size(); i++) {
            Edge cur = mazeEdges.get(i);

            Node n1 = cur.n1;
            Node n2 = cur.n2;
            Node n1rep = this.findRep(reps.get(n1));
            Node n2rep = this.findRep(reps.get(n2));

            if (!n1rep.equals(n2rep)) {
                reps.put(n2rep, n1rep);
                spanningTree.add(cur);
            }
        }
    }

    // EFFECT: Modifies all of this maze's nodes to remove any references
    // to edges that are not in the spanning tree. The edges still exist
    // and point to their respective nodes, but the nodes do not point to them.
    void onlySpanningEdges() {
        for (int i = 0; i < this.mazeNodes.size(); i += 1) {
            this.mazeNodes.get(i).filterEdges(this.spanningTree);
        }
    }

    // Find the root node in Node n's spanning tree that represents it.
    // this.reps MUST be filled with this maze's nodes before method is used.
    Node findRep(Node n) {
        Node rep = reps.get(n);
        while (!rep.equals(reps.get(rep))) {
            rep = reps.get(rep);
        }
        return rep;
    }

    // EFFECT: Resets all the lists and map of edges and nodes to emptys
    void clearFields() {
        this.mazeEdges = new ArrayList<Edge>();
        this.mazeNodes = new ArrayList<Node>();
        this.spanningTree = new ArrayList<Edge>();
        this.reps = new HashMap<Node, Node>();
    }

    // Creates nodes/edges, sorts edges, creates node-representative map,
    // sorts edges, and creates spanning tree.
    // EFFECT: Modifies all the fields needed to do the above.
    void initMaze() {
        this.randomEdges();
        this.sortEdges();
        this.initReps();
        this.createSpanningTree();
        this.onlySpanningEdges();
    }

    // EFFECT: Adds the menu text to bg
    // Draws the menu that displays before maze solving begins
    void drawMainMenu(WorldScene bg, int offset) {
        int unit = Maze.WINDOW_WIDTH / 24;

        WorldImage mainMenu = new TextImage("Menu", 20, Color.red);

        WorldImage startDFS = new BesideImage(
            new TextImage("d: ", 15, Color.red),
            new TextImage("Depth-First Search", 15, Color.cyan));
        WorldImage startBFS = new BesideImage(
            new TextImage("b: ", 15, Color.red),
            new TextImage("Breadth-First Search", 15, Color.cyan));
        WorldImage startFastBFS = new BesideImage(
            new TextImage("f: ", 15, Color.red),
            new TextImage("Fast Breadth-First Search", 15, Color.cyan));
        WorldImage startPlayer = new BesideImage(
            new TextImage("p: ", 15, Color.red),
            new TextImage("Solve it yourself!", 15, Color.cyan));
        WorldImage colorStep = new BesideImage(
            new TextImage("s: ", 15, Color.red),
            new AboveImage(new TextImage("Color maze by depth", 15, Color.cyan),
                new TextImage("while solving w/ FBFS", 15, Color.cyan)));
        WorldImage color = new BesideImage(new TextImage("c: ", 15, Color.red),
            new TextImage("Color maze by depth", 15, Color.cyan));
        WorldImage hide = new BesideImage(new TextImage("h: ", 15, Color.red),
            new AboveImage(
                new TextImage("Hide coloring (if not", 15, Color.cyan),
                new TextImage("colored solved maze)", 15, Color.cyan)));
        WorldImage fight = new BesideImage(new TextImage("i: ", 15, Color.red),
            new TextImage("SOLVER FIGHT!", 15, Color.cyan));
        WorldImage colors = new AboveImage(
            new TextImage("Blue = BFS (Slow)", 15, new Color(50, 153, 255)),
            new TextImage("Red = DFS", 15, Color.red));
        WorldImage fightSolver = new AboveImage(fight, colors);

        WorldImage forNew = new TextImage("For a different maze,", 15,
            Color.red);
        WorldImage pressEnter = new TextImage("press enter", 15, Color.red);
        WorldImage enterForNew = new AboveImage(forNew, pressEnter);

        bg.placeImageXY(mainMenu, offset, unit);
        bg.placeImageXY(startDFS, offset, unit * 2);
        bg.placeImageXY(startBFS, offset, unit * 3);
        bg.placeImageXY(startFastBFS, offset, unit * 4);
        bg.placeImageXY(startPlayer, offset, unit * 5);
        bg.placeImageXY(colorStep, offset, unit * 6);
        bg.placeImageXY(color, offset, unit * 7);
        bg.placeImageXY(hide, offset, unit * 8);
        bg.placeImageXY(fightSolver, offset, (int) (unit * 9.5));

        bg.placeImageXY(enterForNew, offset, unit * 12);
    }

    // EFFECT: Adds the menu text to bg
    // Draws the menu that displays while player is solving maze
    void drawPlayerInGameMenu(WorldScene bg, int offset) {
        int unit = Maze.WINDOW_WIDTH / 24;

        WorldImage mainMenu = new TextImage("Menu", 20, Color.red);
        WorldImage stuck = new TextImage("Stuck? Finish the maze by", 15,
            Color.cyan);
        WorldImage forHelp = new AboveImage(stuck,
            new TextImage("choosing an algorithm:", 15, Color.cyan));
        WorldImage startDFS = new BesideImage(
            new TextImage("d: ", 15, Color.red),
            new TextImage("Depth-First Search", 15, Color.cyan));
        WorldImage startBFS = new BesideImage(
            new TextImage("b: ", 15, Color.red),
            new TextImage("Breadth-First Search", 15, Color.cyan));
        WorldImage startFastBFS = new BesideImage(
            new TextImage("f: ", 15, Color.red),
            new TextImage("Fast Breadth-First Search", 15, Color.cyan));
        WorldImage colorStep = new BesideImage(
            new TextImage("s: ", 15, Color.red),
            new AboveImage(new TextImage("Color maze by depth", 15, Color.cyan),
                new TextImage("while solving w/ FBFS", 15, Color.cyan)));
        WorldImage fight = new BesideImage(new TextImage("i: ", 15, Color.red),
            new TextImage("SOLVER FIGHT!", 15, Color.cyan));
        WorldImage colors = new AboveImage(
            new TextImage("Blue = BFS (Slow)", 15, new Color(50, 153, 255)),
            new TextImage("Red = DFS", 15, Color.red));
        WorldImage fightSolver = new AboveImage(fight, colors);


        bg.placeImageXY(mainMenu, offset, unit);
        bg.placeImageXY(forHelp, offset, unit * 2);
        bg.placeImageXY(startDFS, offset, unit * 4);
        bg.placeImageXY(startBFS, offset, unit * 5);
        bg.placeImageXY(startFastBFS, offset, unit * 6);
        bg.placeImageXY(colorStep, offset, unit * 7);
        bg.placeImageXY(fightSolver, offset, (int) (unit * 8.5));

    }

    // EFFECT: Adds the menu text to bg
    // Draws the menu that displays while maze is being auto-solved
    void drawInGameMenu(WorldScene bg, int offset) {
        int unit = Maze.WINDOW_WIDTH / 24;

        WorldImage mainMenu = new TextImage("Menu", 20, Color.red);
        WorldImage pressEnter = new TextImage("Press enter to stop solving", 15,
            Color.cyan);
        WorldImage stopSolving = new TextImage("and start a new maze.", 15,
            Color.cyan);
        WorldImage msg = new AboveImage(pressEnter, stopSolving);

        bg.placeImageXY(mainMenu, offset, unit);
        bg.placeImageXY(msg, offset, unit * 3);

    }

    // Draws the maze
    public WorldScene makeScene() {
        WorldScene bg = new WorldScene(Maze.WINDOW_WIDTH, Maze.WINDOW_HEIGHT);

        this.endNode.renderOnto(bg, this.cellSize, Color.magenta);

        if (this.mazeStarted || this.displayColor) {
            this.solver.renderOnto(bg, this.cellSize);
        }
        else {
            this.mazeNodes.get(0).renderOnto(bg, this.cellSize,
                new Color(0, 155, 0));
        }
        // Draw the walls of the maze
        for (Edge e : this.mazeEdges) {
            if (!this.spanningTree.contains(e)) {
                e.renderOnto(bg, this.cellSize);
            }
        }

        WorldImage outline = new RectangleImage(this.width * this.cellSize - 1,
            this.height * this.cellSize - 1, OutlineMode.OUTLINE, Color.BLACK);
        WorldImage outline2 = new RectangleImage(this.width * this.cellSize - 3,
            this.height * this.cellSize - 3, OutlineMode.OUTLINE, Color.BLACK);

        bg.placeImageXY(outline, this.width * this.cellSize / 2,
            this.height * this.cellSize / 2);
        bg.placeImageXY(outline2, this.width * this.cellSize / 2,
            this.height * this.cellSize / 2);

        WorldImage menuBG = new RectangleImage(Maze.WINDOW_WIDTH / 5,
            Maze.WINDOW_HEIGHT, OutlineMode.SOLID, Color.black);
        bg.placeImageXY(menuBG,
            Maze.WINDOW_WIDTH + (int) (menuBG.getWidth() / 2),
            Maze.WINDOW_HEIGHT / 2);

        if (!this.mazeStarted) {
            this.drawMainMenu(bg,
                Maze.WINDOW_WIDTH + (int) (menuBG.getWidth() / 2));
        }
        else if (this.playerSolving) {
            this.drawPlayerInGameMenu(bg,
                Maze.WINDOW_WIDTH + (int) (menuBG.getWidth() / 2));
        }
        else {
            this.drawInGameMenu(bg,
                Maze.WINDOW_WIDTH + (int) (menuBG.getWidth() / 2));
        }

        return bg;
    }

    // Steps one step forward in maze if the maze is active but not solved.
    public void onTick() {

        if (!this.mazeStarted || this.solver.solved) {
            return;
        }
        else {
            this.solver.step();
        }
    }

    // Handles key events
    // EFFECT: May initialize this maze's solver, reset all maze-related fields,
    // or move player position if player is solving maze.
    public void onKeyEvent(String ke) {

        // If the game is not started, start it when these keys are pressed
        if (!this.mazeStarted
            && (ke.equals("d") || ke.equals("b") || ke.equals("p")
                || ke.equals("f") || ke.equals("s") || ke.equals("i"))) {

            this.displayColor = false;
            this.displayScores = false;

            if (ke.equals("d")) {
                this.solver = new DFSSolver(this.mazeNodes.get(0));
            }
            else if (ke.equals("b")) {
                this.solver = new BFSSolver(this.mazeNodes.get(0));
            }
            else if (ke.equals("p")) {
                this.solver = new Player(this.mazeNodes.get(0));
                this.playerSolving = true;
            }
            else if (ke.equals("f")) {
                this.solver = new FastBFSSolver(this.mazeNodes.get(0));
            }
            else if (ke.equals("s")) {
                this.solver = new StepColorer(this.mazeNodes.get(0));
            }
            else if (ke.equals("i")) {
                this.solver = new SolverFight(this.mazeNodes.get(0));
            }
            this.mazeStarted = true;
        }

        // display maze coloring when "c" is pressed
        else if (!this.mazeStarted && !this.displayColor && ke.equals("c")) {
            this.solver = new ColorMazeFromStart(this.mazeNodes.get(0));
            this.solver.step();
            this.displayColor = true;
        }
        // hide maze coloring when "h" is pressed
        else if (this.displayColor && ke.equals("h")) {
            this.displayColor = false;
        }
        else if (!this.displayScores && ke.equals("x")) {
            // display scores
        }
        else if (this.displayScores && ke.equals("y")) {
            // hide scores
        }

        // if the player presses b, d, f, or s to finish solving
        else if (this.mazeStarted && this.playerSolving
            && (ke.equals("s") || ke.equals("d") || ke.equals("b")
                || ke.equals("f") || ke.equals("i"))) {
            if (ke.equals("d")) {
                this.solver = new DFSSolver(this.mazeNodes.get(0));
            }
            else if (ke.equals("b")) {
                this.solver = new BFSSolver(this.mazeNodes.get(0));
            }
            else if (ke.equals("f")) {
                this.solver = new FastBFSSolver(this.mazeNodes.get(0));
            }

            else if (ke.equals("s")) {
                this.solver = new StepColorer(this.mazeNodes.get(0));
            }
            else if (ke.equals("i")) {
                this.solver = new SolverFight(this.mazeNodes.get(0));
            }
            this.playerSolving = false;
        }

        else if (ke.equals("\n")) {
            this.mazeStarted = false;
            this.clearFields();
            this.initMaze();
            this.playerSolving = false;
            this.displayColor = false;
            this.displayScores = false;
        }
        // Don't process player movement if game is not started
        else if (!this.mazeStarted) {
            return;
        }
        // Handle player movement on arrow key presses
        else if (this.playerSolving && (ke.equals("up") || ke.equals("down")
            || ke.equals("left") || ke.equals("right"))) {
            this.solver.step(ke);
        }
    }

}

// Examples and tests
class ExamplesMazes {

    Maze maze1;
    Maze maze2;

    void init1() {
        maze1 = new Maze(4, 3);
        maze2 = new Maze(2, 2);

    }

    void testDrawIt(Tester t) {
        Maze mainMaze = new Maze(100, 60);
        mainMaze.initMaze();

        mainMaze.bigBang(Maze.WINDOW_WIDTH * 6 / 5, Maze.WINDOW_HEIGHT, 0.001);

        // this class got no side mazes
    }

    // ------ Node Methods ----------

    void testAddEdge(Tester t) {

        Node n1 = new Node(1, 2);
        Node n2 = new Node(1, 3);

        Edge e1 = new Edge(10);
        Edge e2 = new Edge(20);
        Edge e3 = new Edge(30);
        Edge e4 = new Edge(40);
        Edge e5 = new Edge(50);

        t.checkExpect(n1.nodeEdges.size(), 0);
        n1.addEdge(e1);
        t.checkExpect(n1.nodeEdges.size(), 1);
        n1.addEdge(e2);
        t.checkExpect(n1.nodeEdges.size(), 2);
        n1.addEdge(e3);
        t.checkExpect(n1.nodeEdges.size(), 3);
        n1.addEdge(e4);
        t.checkExpect(n1.nodeEdges.size(), 4);
        n1.addEdge(e5);
        // Can't add more than 4 edges to a node
        t.checkExpect(n1.nodeEdges.size(), 4);

        t.checkExpect(n2.nodeEdges.size(), 0);
        n2.addEdge(e1);
        t.checkExpect(n2.nodeEdges.size(), 1);
        n2.addEdge(e1);
        // can't add the same node to nodeEdges twice
        t.checkExpect(n2.nodeEdges.size(), 1);

    }

    void testGetNeighbor(Tester t) {

        Node n1 = new Node(1, 2);
        Node n2 = new Node(1, 1);

        Edge e1 = new Edge(10);
        Edge e2 = new Edge(20);

        n1.addEdge(e1);
        n2.addEdge(e1);
        e1.setN1(n1);
        e1.setN2(n2);

        t.checkExpect(n1.getNeighbor(e1), n2);
        t.checkExpect(n2.getNeighbor(e1), n1);
        t.checkException(
            new RuntimeException("Given edge is not connected to this node"),
            n1, "getNeighbor", e2);
    }

    void testGetNeighborEdge(Tester t) {
        Node n1 = new Node(1, 2);
        Node n2 = new Node(1, 1);
        Node n3 = new Node(1, 0);

        Edge e1 = new Edge(10);
        Edge e2 = new Edge(20);

        n1.addEdge(e1); // n1 and n2 connected by e1
        n2.addEdge(e1);
        e1.setN1(n1);
        e1.setN2(n2);
        n2.addEdge(e2); // n2 and n3 connected by e2
        n3.addEdge(e2);
        e2.setN1(n2);
        e2.setN2(n3);

        t.checkExpect(n1.getNeighborEdge(n2), e1);
        t.checkExpect(n2.getNeighborEdge(n1), e1);
        t.checkExpect(n3.getNeighborEdge(n2), e2);
        t.checkExpect(n2.getNeighborEdge(n3), e2);

        t.checkException(
            new RuntimeException("Neighbor cannot be same as this node"), n1,
            "getNeighborEdge", n1);
        t.checkException(
            new RuntimeException("Node does not connect to the given neighbor"),
            n1, "getNeighborEdge", n3);
    }

    void testFilterEdges(Tester t) {
        Node n1 = new Node(1, 2);
        Node n2 = new Node(1, 1);
        Node n3 = new Node(2, 2);

        Edge e1 = new Edge(10);
        Edge e2 = new Edge(20);
        Edge e3 = new Edge(30);
        Edge e4 = new Edge(40);
        Edge e5 = new Edge(50);

        ArrayList<Edge> edges = new ArrayList<Edge>(
            Arrays.asList(e1, e2, e3, e4));

        n1.addEdge(e1); // n1 and n2 connected by e1
        n2.addEdge(e1);
        e1.setN1(n1);
        e1.setN2(n2);
        n1.addEdge(e2); // n1 and n3 connected by e2
        n3.addEdge(e2);
        e2.setN1(n1);
        e2.setN2(n3);
        n1.addEdge(e3);
        n1.addEdge(e4);

        t.checkExpect(n1.nodeEdges.size(), 4);
        n1.filterEdges(edges);
        // All edges were good edges so size did not change
        t.checkExpect(n1.nodeEdges.size(), 4);

        edges = new ArrayList<Edge>(Arrays.asList(e1));

        t.checkExpect(n1.nodeEdges.size(), 4);
        n1.filterEdges(edges);
        // Only one edge was a good edge so size did not change
        t.checkExpect(n1.nodeEdges.size(), 1);

        n1.addEdge(e2);
        n1.addEdge(e3);
        n1.addEdge(e4);

        edges = new ArrayList<Edge>();

        t.checkExpect(n1.nodeEdges.size(), 4);
        n1.filterEdges(edges);
        // No edges were good edges so size did not change
        t.checkExpect(n1.nodeEdges.size(), 0);

        n1.addEdge(e1);
        n1.addEdge(e2);
        n1.addEdge(e3);
        n1.addEdge(e4);

        edges = new ArrayList<Edge>();
        edges.add(e5);

        t.checkExpect(n1.nodeEdges.size(), 4);
        n1.filterEdges(edges);
        // No edges were good edges so size did not change
        t.checkExpect(n1.nodeEdges.size(), 0);

    }

    void testIsEnd(Tester t) {
        Node n1 = new Node(0, 0);
        Node n2 = new EndNode(300, 300);
        EndNode n3 = new EndNode(420, 420);

        t.checkExpect(n1.isEnd(), false);
        t.checkExpect(n2.isEnd(), true);
        t.checkExpect(n3.isEnd(), true);
    }

    void testHasNodeInDir(Tester t) {
        Node cn = new Node(1, 1);
        Node tn = new Node(0, 1);
        Node ln = new Node(1, 0);
        Node rn = new Node(1, 2);
        Node bn = new Node(2, 1);

        Edge te = new Edge();
        Edge le = new Edge();
        Edge re = new Edge();
        Edge be = new Edge();

        cn.addEdge(te);
        cn.addEdge(le);
        cn.addEdge(re);
        cn.addEdge(be);

        tn.addEdge(te);
        ln.addEdge(le);
        rn.addEdge(re);
        bn.addEdge(be);

        te.setN1(cn);
        te.setN2(tn);
        le.setN1(cn);
        le.setN2(ln);
        re.setN1(cn);
        re.setN2(rn);
        be.setN1(cn);
        be.setN2(bn);

        t.checkExpect(cn.hasNodeInDir("up"), true);
        t.checkExpect(cn.hasNodeInDir("left"), true);
        t.checkExpect(cn.hasNodeInDir("right"), true);
        t.checkExpect(cn.hasNodeInDir("down"), true);

        t.checkExpect(tn.hasNodeInDir("up"), false);
        t.checkExpect(bn.hasNodeInDir("down"), false);
        t.checkExpect(ln.hasNodeInDir("left"), false);
        t.checkExpect(rn.hasNodeInDir("right"), false);

    }

    void getNodeInDir(Tester t) {

        Node cn = new Node(1, 1);
        Node tn = new Node(0, 1);
        Node ln = new Node(1, 0);
        Node rn = new Node(1, 2);
        Node bn = new Node(2, 1);

        Edge te = new Edge();
        Edge le = new Edge();
        Edge re = new Edge();
        Edge be = new Edge();

        cn.addEdge(te);
        cn.addEdge(le);
        cn.addEdge(re);
        cn.addEdge(be);

        tn.addEdge(te);
        ln.addEdge(le);
        rn.addEdge(re);
        bn.addEdge(be);

        te.setN1(cn);
        te.setN2(tn);
        le.setN1(cn);
        le.setN2(ln);
        re.setN1(cn);
        re.setN2(rn);
        be.setN1(cn);
        be.setN2(bn);

        t.checkExpect(cn.getNodeInDir("up"), tn);
        t.checkExpect(cn.getNodeInDir("left"), ln);
        t.checkExpect(cn.getNodeInDir("right"), rn);
        t.checkExpect(cn.getNodeInDir("down"), bn);

        t.checkException(
            new RuntimeException(
                "Node does not have an edge in the given direction"),
            tn, "getNodeInDir", "up");
        t.checkException(
            new RuntimeException(
                "Node does not have an edge in the given direction"),
            ln, "getNodeInDir", "left");
        t.checkException(
            new RuntimeException(
                "Node does not have an edge in the given direction"),
            rn, "getNodeInDir", "right");
        t.checkException(
            new RuntimeException(
                "Node does not have an edge in the given direction"),
            bn, "getNodeInDir", "down");
    }

    // --------- Edge Methods ----------

    void testMostEdgeMethods(Tester t) {

        Edge e1 = new Edge(3);
        Edge e2 = new Edge(4);
        Edge e3 = new Edge(4);

        Node n1 = new Node(0, 0);
        Node n2 = new Node(1, 0);
        Node n3 = new Node(0, 1);
        Node n4 = new Node(1, 1);

        e1.setN1(n1);
        e1.setN2(n2);
        t.checkExpect(e1.n1, n1);
        t.checkExpect(e1.n2, n2);

        e2.setN1(n3);
        e2.setN2(n4);
        t.checkExpect(e2.n1, n3);
        t.checkExpect(e2.n2, n4);

        // compare
        t.checkExpect(e1.compare(e2) < 0, true);
        t.checkExpect(e1.compare(e1) == 0, true);
        t.checkExpect(e2.compare(e1) > 0, true);
        t.checkExpect(e3.compare(e2) == 0, true);
    }

    void testHasNode(Tester t) {
        Node cn = new Node(1, 1);
        Node tn = new Node(0, 1);
        Node ln = new Node(1, 0);
        Node rn = new Node(1, 2);
        Node bn = new Node(2, 1);

        Edge te = new Edge();
        Edge le = new Edge();
        Edge re = new Edge();
        Edge be = new Edge();

        cn.addEdge(te);
        cn.addEdge(le);
        cn.addEdge(re);
        cn.addEdge(be);

        tn.addEdge(te);
        ln.addEdge(le);
        rn.addEdge(re);
        bn.addEdge(be);

        te.setN1(cn);
        te.setN2(tn);
        le.setN1(cn);
        le.setN2(ln);
        re.setN1(cn);
        re.setN2(rn);
        be.setN1(cn);
        be.setN2(bn);

        t.checkExpect(te.hasNode(cn), true);
        t.checkExpect(te.hasNode(tn), true);
        t.checkExpect(be.hasNode(cn), true);
        t.checkExpect(be.hasNode(ln), false);
        t.checkExpect(re.hasNode(bn), false);
    }

    void testRandomEdges(Tester t) {
        this.init1();
        // Check that lists are initialized to proper sizes
        t.checkExpect(maze1.mazeEdges.size(), 0);
        t.checkExpect(maze1.mazeNodes.size(), 0);
        maze1.randomEdges();
        t.checkExpect(maze1.mazeEdges.size(), 17);
        t.checkExpect(maze1.mazeNodes.size(), 12);
        maze1.sortEdges();

        t.checkExpect(maze2.mazeEdges.size(), 0);
        t.checkExpect(maze2.mazeNodes.size(), 0);
        maze2.randomEdges();
        t.checkExpect(maze2.mazeEdges.size(), 4);
        t.checkExpect(maze2.mazeNodes.size(), 4);

        // Edge weights are within range
        for (Edge e : maze1.mazeEdges) {
            t.checkExpect(e.weight < 1000, true);
            t.checkExpect(e.weight >= 0, true);
        }

        Edge e1 = maze1.mazeEdges.get(0);
        Edge e2 = maze1.mazeEdges.get(1);
        Edge e3 = maze1.mazeEdges.get(2);
        Edge e4 = maze1.mazeEdges.get(3);
        Edge e5 = maze1.mazeEdges.get(4);
        Edge e6 = maze1.mazeEdges.get(5);

        // The weights are not all the same
        t.checkExpect(e1.weight != e2.weight || e2.weight != e3.weight
            || e3.weight != e4.weight || e4.weight != e5.weight
            || e5.weight != e6.weight, true);

    }

    // --------- Maze methods -----------

    void testClearFields(Tester t) {
        Maze m = new Maze(3, 2);

        t.checkExpect(m.mazeNodes.size(), 0);
        t.checkExpect(m.mazeEdges.size(), 0);
        t.checkExpect(m.reps.isEmpty(), true);
        t.checkExpect(m.spanningTree.size(), 0);

        m.initMaze();

        t.checkExpect(m.mazeNodes.size(), 6);
        t.checkExpect(m.mazeEdges.size(), 7);
        t.checkExpect(m.reps.isEmpty(), false);
        t.checkExpect(m.spanningTree.size(), 5);

        m.clearFields();

        t.checkExpect(m.mazeNodes.size(), 0);
        t.checkExpect(m.mazeEdges.size(), 0);
        t.checkExpect(m.reps.isEmpty(), true);
        t.checkExpect(m.spanningTree.size(), 0);
    }

    void testInitMaze(Tester t) {

        Maze m = new Maze(2, 3);

        t.checkExpect(m.mazeEdges.size(), 0);
        t.checkExpect(m.mazeNodes.size(), 0);
        t.checkExpect(m.reps.isEmpty(), true);
        t.checkExpect(m.spanningTree.size(), 0);

        m.initMaze();

        t.checkExpect(m.mazeEdges.size(), 7);
        t.checkExpect(m.mazeNodes.size(), 6);
        t.checkExpect(m.reps.isEmpty(), false);
        t.checkExpect(m.spanningTree.size(), 5);
    }

    void testOnlySpanningTree(Tester t) {
        Maze m = new Maze(3, 2);

        Node n00 = new Node(0, 0);
        Node n01 = new Node(0, 1);
        Node n02 = new Node(0, 2);
        Node n10 = new Node(1, 0);
        Node n11 = new Node(1, 1);
        Node n12 = new Node(1, 2);

        Edge e1 = new Edge(1); // 1 2
        Edge e2 = new Edge(2); // 3 4 5
        Edge e3 = new Edge(3); // 6 7
        Edge e4 = new Edge(4);
        Edge e5 = new Edge(5);
        Edge e6 = new Edge(6);
        Edge e7 = new Edge(7);

        n00.addEdge(e1);
        e1.setN1(n00);
        n00.addEdge(e6);
        e6.setN1(n00);

        n01.addEdge(e1);
        e1.setN2(n01);
        n01.addEdge(e2);
        e2.setN1(n01);
        n01.addEdge(e7);
        e7.setN1(n01);

        n02.addEdge(e7);
        e7.setN2(n02);
        n02.addEdge(e4);
        e4.setN1(n02);

        n10.addEdge(e6);
        e6.setN2(n10);
        n10.addEdge(e5);
        e5.setN1(n10);

        n11.addEdge(e5);
        e5.setN2(n11);
        n11.addEdge(e2);
        e2.setN2(n11);
        n11.addEdge(e3);
        e3.setN1(n11);

        n12.addEdge(e3);
        e3.setN2(n12);
        n12.addEdge(e4);
        e4.setN2(n12);
        // All nodes have a full number of edges (2 for corner, 3 for middle)
        t.checkExpect(n00.nodeEdges.size(), 2);
        t.checkExpect(n01.nodeEdges.size(), 3);
        t.checkExpect(n02.nodeEdges.size(), 2);
        t.checkExpect(n10.nodeEdges.size(), 2);
        t.checkExpect(n11.nodeEdges.size(), 3);
        t.checkExpect(n12.nodeEdges.size(), 2);

        ArrayList<Node> nodes = new ArrayList<Node>(
            Arrays.asList(n00, n01, n02, n10, n11, n12));
        ArrayList<Edge> edges = new ArrayList<Edge>(
            Arrays.asList(e1, e2, e3, e4, e5, e6, e7));

        m.mazeNodes = nodes;
        m.mazeEdges = edges;

        m.sortEdges();
        m.initReps();
        m.createSpanningTree();
        m.onlySpanningEdges();

        // There are a total of 5 edges among the 6 nodes for a spanning tree
        t.checkExpect(n00.nodeEdges.size(), 1);
        t.checkExpect(n01.nodeEdges.size(), 2);
        t.checkExpect(n02.nodeEdges.size(), 1);
        t.checkExpect(n10.nodeEdges.size(), 1);
        t.checkExpect(n11.nodeEdges.size(), 3);
        t.checkExpect(n12.nodeEdges.size(), 2);

    }

    void testOnTick(Tester t) {

        Maze m = new Maze(2, 3);

        m.initMaze();
        ASolver s = new DFSSolver(m.mazeNodes.get(0));
        m.solver = s;

        t.checkExpect(s.worklist.size(), 1);
        t.checkExpect(s.visited.size(), 0);
        t.checkExpect(s.cameFromEdge.isEmpty(), true);

        m.onTick();
        // Nothing happened because m.mazeStarted is not true
        t.checkExpect(s.worklist.size(), 1);
        t.checkExpect(s.visited.size(), 0);
        t.checkExpect(s.cameFromEdge.isEmpty(), true);

        m.mazeStarted = true;

        m.onTick();

        // Ugly test but shows all the nodes connecting to the first node
        // have been added to the worklist
        t.checkExpect(s.worklist.size(), m.mazeNodes.get(0).nodeEdges.size());
        // The first node has been added to visited nodes
        t.checkExpect(s.visited.get(0), m.mazeNodes.get(0));
        // The node-edge map is no longer empty
        t.checkExpect(s.cameFromEdge.isEmpty(), false);

        m.clearFields();
        m.initMaze();
        ASolver s1 = new DFSSolver(m.mazeNodes.get(0));
        m.solver = s1;

        t.checkExpect(s1.worklist.size(), 1);
        t.checkExpect(s1.visited.size(), 0);
        t.checkExpect(s1.cameFromEdge.isEmpty(), true);

        s1.solved = true;
        m.onTick();
        // Nothing happened because the solver is solved
        t.checkExpect(s1.worklist.size(), 1);
        t.checkExpect(s1.visited.size(), 0);
        t.checkExpect(s1.cameFromEdge.isEmpty(), true);

    }

    Maze m0;

    void initForOnKey() {
        m0 = new Maze(2, 3);
        m0.initMaze();
    }

    void testOnKeyEvent(Tester t) {
        this.initForOnKey();

        t.checkExpect(m0.mazeStarted, false);

        ASolver dfs = new DFSSolver(m0.mazeNodes.get(0));

        t.checkExpect(m0.solver, null);
        m0.onKeyEvent("d");
        t.checkExpect(m0.mazeStarted, true);
        t.checkExpect(m0.solver, dfs);

        this.initForOnKey();
        ASolver bfs = new BFSSolver(m0.mazeNodes.get(0));

        t.checkExpect(m0.solver, null);
        m0.onKeyEvent("b");
        t.checkExpect(m0.solver, bfs);

        this.initForOnKey();
        ASolver stepColor = new StepColorer(m0.mazeNodes.get(0));

        t.checkExpect(m0.solver, null);
        m0.onKeyEvent("s");
        t.checkExpect(m0.solver, stepColor);

        this.initForOnKey();
        ASolver fight = new SolverFight(m0.mazeNodes.get(0));

        t.checkExpect(m0.solver, null);
        m0.onKeyEvent("i");
        t.checkExpect(m0.solver, fight);

        this.initForOnKey();
        ASolver fbfs = new FastBFSSolver(m0.mazeNodes.get(0));

        t.checkExpect(m0.solver, null);
        m0.onKeyEvent("f");
        t.checkExpect(m0.solver, fbfs);

        this.initForOnKey();
        ASolver player = new Player(m0.mazeNodes.get(0));

        t.checkExpect(m0.solver, null);
        m0.onKeyEvent("p");
        t.checkExpect(m0.solver, player);
        // Color solver initialization
        this.initForOnKey();
        ASolver color = new ColorMazeFromStart(m0.mazeNodes.get(0));
        color.step();

        t.checkExpect(m0.solver, null);
        t.checkExpect(m0.displayColor, false);
        m0.onKeyEvent("c");
        t.checkExpect(m0.solver, color);
        t.checkExpect(m0.displayColor, true);
        // Hide color
        m0.onKeyEvent("h");
        t.checkExpect(m0.displayColor, false);

        // Start auto-solve from player
        this.initForOnKey();
        m0.solver = player;
        m0.playerSolving = true;

        t.checkExpect(m0.solver, player);
        m0.onKeyEvent("d");
        t.checkExpect(m0.solver, new DFSSolver(m0.mazeNodes.get(0)));

        this.initForOnKey();
        m0.solver = player;
        m0.playerSolving = true;

        t.checkExpect(m0.solver, player);
        m0.onKeyEvent("b");
        t.checkExpect(m0.solver, new BFSSolver(m0.mazeNodes.get(0)));

        this.initForOnKey();
        m0.solver = player;
        m0.playerSolving = true;

        t.checkExpect(m0.solver, player);
        m0.onKeyEvent("f");
        t.checkExpect(m0.solver, new FastBFSSolver(m0.mazeNodes.get(0)));

        // Test that pressing enter resets everything
        this.initForOnKey();
        m0.mazeStarted = true;
        t.checkExpect(m0.mazeNodes.size() > 0, true);
        m0.playerSolving = true;
        m0.displayColor = true;
        m0.displayScores = true;

        m0.onKeyEvent("\n");
        t.checkExpect(m0.mazeStarted, false);
        t.checkExpect(m0.playerSolving, false);
        t.checkExpect(m0.displayColor, false);
        t.checkExpect(m0.displayScores, false);

        // Test player movement
        m0 = new Maze(2, 3);
        m0.clearFields();
        m0.randomEdges();
        m0.sortEdges();
        m0.initReps();
        m0.spanningTree = m0.mazeEdges;
        Player pplayer = new Player(m0.mazeNodes.get(0));
        m0.solver = pplayer;
        m0.mazeStarted = true;

        t.checkExpect(pplayer.loc.x, player.first.x);
        t.checkExpect(pplayer.loc.y, player.first.y);
        m0.onKeyEvent("up");
        t.checkExpect(pplayer, new Player(m0.mazeNodes.get(0)));
        t.checkExpect(pplayer.loc.x, player.first.x);
        t.checkExpect(pplayer.loc.y, player.first.y);

        m0.playerSolving = true;
        m0.onKeyEvent("down");
        t.checkExpect(pplayer.loc.x, 0);
        t.checkExpect(pplayer.loc.y, 1);

        m0.onKeyEvent("right");
        t.checkExpect(pplayer.loc.x, 1);
        t.checkExpect(pplayer.loc.y, 1);

        m0.onKeyEvent("up");
        t.checkExpect(pplayer.loc.x, 1);
        t.checkExpect(pplayer.loc.y, 0);

        m0.onKeyEvent("left");
        t.checkExpect(pplayer.loc.x, 0);
        t.checkExpect(pplayer.loc.y, 0);

    }

    void testFindRep(Tester t) {
        Maze m = new Maze(3, 2);

        m.randomEdges();
        m.sortEdges();
        m.initReps();

        Node n1 = m.mazeNodes.get(0);
        Node n2 = m.mazeNodes.get(1);
        Node n3 = m.mazeNodes.get(2);
        Node n4 = m.mazeNodes.get(3);

        t.checkExpect(m.reps.get(n1), n1);
        t.checkExpect(m.reps.get(n2), n2);
        t.checkExpect(m.reps.get(n3), n3);
        t.checkExpect(m.reps.get(n4), n4);

        t.checkExpect(m.findRep(n1), n1);
        t.checkExpect(m.findRep(n2), n2);
        t.checkExpect(m.findRep(n3), n3);
        t.checkExpect(m.findRep(n4), n4);

        m.reps.put(n2, n1);
        t.checkExpect(m.findRep(n2), n1);
        m.reps.put(n3, n2);
        t.checkExpect(m.findRep(n3), n1);
        m.reps.put(n1, n4);
        t.checkExpect(m.findRep(n3), n4);
        t.checkExpect(m.findRep(n1), n4);
        t.checkExpect(m.findRep(n4), n4);

    }

    void testSortEdges(Tester t) {

        this.init1();

        Edge e1a = new Edge(1);
        Edge e2a = new Edge(2);
        Edge e3a = new Edge(3);
        Edge e4a = new Edge(4);
        Edge e5 = new Edge(5);
        Edge e6 = new Edge(6);
        Edge e7 = new Edge(7);
        Edge e8 = new Edge(8);

        maze1.mazeEdges = new ArrayList<Edge>(
            Arrays.asList(e5, e2a, e8, e7, e4a, e3a, e1a, e6));

        t.checkExpect(maze1.mazeEdges.get(0), e5);
        t.checkExpect(maze1.mazeEdges.get(1), e2a);
        t.checkExpect(maze1.mazeEdges.get(2), e8);
        t.checkExpect(maze1.mazeEdges.get(3), e7);
        t.checkExpect(maze1.mazeEdges.get(4), e4a);
        t.checkExpect(maze1.mazeEdges.get(5), e3a);
        t.checkExpect(maze1.mazeEdges.get(6), e1a);
        t.checkExpect(maze1.mazeEdges.get(7), e6);

        maze1.sortEdges();

        t.checkExpect(maze1.mazeEdges.get(0), e1a);
        t.checkExpect(maze1.mazeEdges.get(1), e2a);
        t.checkExpect(maze1.mazeEdges.get(2), e3a);
        t.checkExpect(maze1.mazeEdges.get(3), e4a);
        t.checkExpect(maze1.mazeEdges.get(4), e5);
        t.checkExpect(maze1.mazeEdges.get(5), e6);
        t.checkExpect(maze1.mazeEdges.get(6), e7);
        t.checkExpect(maze1.mazeEdges.get(7), e8);

        Edge e1 = new Edge(30);
        Edge e2 = new Edge(50);
        Edge e3 = new Edge(20);
        Edge e4 = new Edge(90);

        Maze m1 = new Maze(2, 2);

        m1.mazeEdges.add(e1);
        m1.mazeEdges.add(e2);
        m1.mazeEdges.add(e3);
        m1.mazeEdges.add(e4);

        t.checkExpect(m1.mazeEdges.get(0).weight, 30);
        t.checkExpect(m1.mazeEdges.get(3).weight, 90);

        m1.sortEdges();

        t.checkExpect(m1.mazeEdges.get(0).weight, 20);
        t.checkExpect(m1.mazeEdges.get(1).weight, 30);
        t.checkExpect(m1.mazeEdges.get(2).weight, 50);
        t.checkExpect(m1.mazeEdges.get(3).weight, 90);

    }

    void testCreateSpanningTree(Tester t) {

        this.init1();
        this.maze1.randomEdges();
        t.checkExpect(maze1.mazeEdges.size(), 17);
        t.checkExpect(maze1.mazeNodes.size(), 12);
        this.maze1.sortEdges();
        t.checkExpect(maze1.mazeEdges.size(), 17);
        t.checkExpect(maze1.mazeNodes.size(), 12);
        t.checkExpect(maze1.spanningTree.size(), 0);
        t.checkExpect(maze1.reps.size(), 0);
        this.maze1.initReps();
        this.maze1.createSpanningTree();
        t.checkExpect(maze1.spanningTree.size(), 11);

        this.maze2.randomEdges();
        this.maze2.sortEdges();
        this.maze2.initReps();
        this.maze2.createSpanningTree();

        t.checkExpect(this.maze2.spanningTree.size(), 3);

        // this.isMinimumSpanningTree(t, this.maze1);

        Maze giantMaze = new Maze(100, 60);
        giantMaze.randomEdges();
        giantMaze.sortEdges();
        giantMaze.initReps();
        giantMaze.createSpanningTree();

        // this.isMinimumSpanningTree(t, giantMaze);

    }

    void isMinimumSpanningTree(Tester t, Maze m) {
        int spanningSize1 = 0;

        // is the spanning tree the right size?
        t.checkExpect(m.spanningTree.size(), m.height * m.width - 1);
        for (Edge e : m.spanningTree) {
            spanningSize1 += e.weight;
        }

        Edge lastEdge = m.spanningTree.get(m.spanningTree.size() - 1);
        m.reps.put(lastEdge.n1, lastEdge.n1);
        m.reps.put(lastEdge.n2, lastEdge.n2);

        for (Edge e : m.mazeEdges) {
            // for every edge not in the spanning tree
            if (!m.spanningTree.contains(e)
                && !m.findRep(e.n1).equals(m.findRep(e.n2))) {
                t.checkExpect((spanningSize1 - lastEdge.weight)
                    + e.weight >= spanningSize1, true);
            }
        }
    }

    void testInitReps(Tester t) {

        Maze testM = new Maze(3, 3);

        testM.randomEdges();
        testM.sortEdges();
        testM.initReps();

        for (int i = 0; i < testM.reps.size(); i += 1) {
            t.checkExpect(testM.reps.get(testM.mazeNodes.get(i)),
                testM.mazeNodes.get(i));
        }
    }

}
