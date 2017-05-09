package goal;

import communicationclient.Agent;
import communicationclient.Node;
import heuristic.HeuristicHelper;
import level.Box;
import level.Cell;
import level.CharCell;

import java.util.ArrayList;
import java.util.LinkedList;

/**
 * Created by arhjo on 09/05/2017.
 */
public class SubGoalMoveBoxOutTheWay extends GoalFreeAgent {
    ArrayList<Cell> requestedCells = new ArrayList<>();
    public SubGoalMoveBoxOutTheWay(Box box, LinkedList<Node> requestedCells, Agent agentToFree) {
        super(box, requestedCells,agentToFree);
        for (Node n: requestedCells) {
            this.requestedCells.add(new Cell(n.agentRow,n.agentCol));
        }
    }
    @Override
    public boolean isGoalSatisfied(Node node) {
        //TODO box and agent not in path
        Box[][] boxes = node.getBoxes();
        for (Cell c:this.requestedCells) {
            if (c.getRow()==node.agentRow && c.getCol() == node.agentCol) return false;
            Box box = boxes[c.getRow()][c.getCol()];
            if (box!=null){
                if(this.box==box) return false;
            }
        }
        return true;
    }

    @Override
    public Integer calculateHeuristic(Node n){

        //TODO Create Heuristics for optimal "parkingspot" maybe use graph :-)
        int boxRow = box.getRow();
        int boxCol = box.getCol();
//        int goalCharRow = destination.getRow();
//        int goalCharCol = destination.getCol();

        Integer h=0;
//        h = HeuristicHelper.manhattanDistance(boxRow, boxCol, goalCharRow, goalCharCol);

        return h;
    }
}
