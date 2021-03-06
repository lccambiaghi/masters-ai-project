package level;

import communicationclient.Agent;
import goal.Goal;
import goal.GoalBoxToCell;
import graph.Graph;
import graph.Vertex;
import heuristic.CharCellComparator;
import heuristic.GoalComparator;
import heuristic.HeuristicHelper;

import java.util.*;

/**
 * Created by salik on 19-04-2017.
 */
public class LevelAnalyzer {
    private Level level;
    private PriorityQueue<CharCell> charCellsPriorityQueue = new PriorityQueue<>(new CharCellComparator());
    private PriorityQueue<Goal> goalPriorityQueue = new PriorityQueue<Goal>(new GoalComparator());
    private Graph graph;
    private ArrayList<CharCell> deadEnds = new ArrayList<>();
    private ArrayList<CharCell> corners = new ArrayList<>();
    private ArrayList<CharCell> oneWalls = new ArrayList<>();
    private ArrayList<CharCell> twoWalls = new ArrayList<>();
    private ArrayList<CharCell> noWalls = new ArrayList<>();
    private ArrayList<CharCell> allWalls = new ArrayList<>();

    public LevelAnalyzer (Graph graph) {
        this.level = Level.getInstance();
        this.graph = graph;
    }

    /**
     * Method that analyzes how many walls are around the goals in a level
     */
    public void analyzeWalls(){
        HashSet<CharCell> goalCells = this.level.getAllCharCells();

        boolean[][] walls = this.level.getWalls();
        for (CharCell c:goalCells) {
            int row = c.getRow();
            int col = c.getCol();
            int numWalls = 0;
            if(walls[row][col-1]) numWalls++;
            if(walls[row][col+1]) numWalls++;
            if(walls[row-1][col]) numWalls++;
            if(walls[row+1][col]) numWalls++;
            switch (numWalls){
                case 0:
                    noWalls.add(c);
                    c.setNumWalls(0);
                    break;
                case 1:
                    oneWalls.add(c);
                    c.setNumWalls(1);
                    break;
                case 2:
                    if((walls[row-1][col] && walls[row][col-1])|| //Northwest corner
                            (walls[row-1][col] && walls[row][col+1])|| //Northeast corner
                            (walls[row+1][col] && walls[row][col+1])|| //Southeast corner
                            (walls[row+1][col] && walls[row][col-1])//Southwest corner
                            ){
                        corners.add(c);
                        c.setNumWalls(2);
                        c.setCorner(true);
                    }else{
                        twoWalls.add(c);
                        c.setNumWalls(2);
                    }
                    break;
                case 3:
                    deadEnds.add(c);
                    c.setNumWalls(3);
                    c.setDeadEnd(true);
                    break;
                case 4:
                    allWalls.add(c);
                    c.setNumWalls(4);
                    break;
            }
            c.calculatePriority();//Calculate priority of goalcell
            charCellsPriorityQueue.add(c);//Add to priorityqueue
        }
        //return charCellsPriorityQueue;
    }

    public void assignBoxesToCells() {
        // assign a box to each charCell
        HashSet<CharCell> charCells = this.level.getAllCharCells();
        for (CharCell cc: charCells) {
            //Assign closest reachable box with correct char
            // TODO fix boxesInComponent, look at MAHiveMind.lvl
            HashMap<Vertex, HashSet<Vertex>> boxesInComponent = this.graph.getBoxesInComponent();
            HashSet<Vertex> reachableBoxes = boxesInComponent.get(new Vertex(cc.getRow(),cc.getCol()));
            HashSet<Box> goalBoxes = new HashSet<>();
            for (Vertex b:reachableBoxes) {
                if (b.getBox().getBoxChar()==Character.toUpperCase(cc.getLetter())){
                    goalBoxes.add(b.getBox());
                }
            }
            Box closest = cc.getClosestBox(goalBoxes);
            // What if closest is null?
            cc.setAssignedBox(closest);
            //Assign closest reachableAgent of correct color
            HashSet<Vertex> reachableAgents = this.graph.getAgentsInComponent().get(new Vertex(cc.getRow(),cc.getCol()));
            for (Vertex a: reachableAgents) {
                if (a.getAgent().getColor()==closest.getBoxColor()){
                    closest.setAssignedAgent(a.getAgent()); //Will override and the closest agent will get the box
                }
            }
            closest.setDestination(cc);
        }
    }

    public PriorityQueue<Goal> createInitialGoals() {
        this.graph.analyzeGraph(this.graph, this.graph.getVertices());
        this.graph.calculatePriority(this.graph);
        for (CharCell cell : this.level.getAllCharCells()){
            Box assigned = cell.getAssignedBox();
            Goal boxToChar = new GoalBoxToCell(assigned, cell);
            //Assign agent to goal...
            boxToChar.setAgent(assigned.getAssignedAgent());
           //Priority is calculated using the graph
            int priority = cell.getPriority();
            boxToChar.setPriority(priority);
            goalPriorityQueue.add(boxToChar);
        }
        return goalPriorityQueue;
    }

}
