package goal;

import communicationclient.Agent;
import communicationclient.Node;
import level.Box;
import level.Cell;
import level.CharCell;

import java.util.LinkedList;

/**
 * Created by lucacambiaghi on 17/04/2017.
 */
public class GoalBoxToCell extends Goal {

    // inherits | LinkedList<Goal> subgoals | from Goal

    protected Box box;
    protected Cell destination;

    private Goal toBox;
    private Goal toChar;

    // empty constructor for subclasses
    public GoalBoxToCell(){}

    public GoalBoxToCell(Box box, CharCell destination){
        this.box = box;
        this.destination = destination;

        subgoals = new LinkedList<>();

    }

    @Override
    public void refine(){
        if(!isRefined){
            toBox = new SubGoalMoveToBox(this.box);
            toChar = new SubGoalPushBox(this.box, this.destination);
            subgoals.add(toBox);
            subgoals.add(toChar);
            toBox.setPriority(this.priority);
            toChar.setPriority(this.priority);
        }
        isRefined = true;

    }

    @Override
    public Agent getAgent() {
        return agent;
    }

    @Override
    public void setAgent(Agent agent) {
        super.agent = agent;
    }

    @Override
    public String toString() {
        return "GoalBoxToCell "+box.getBoxChar();
    }

    @Override
    public boolean isGoalSatisfied(Node node) {
        int goalCharRow = destination.getRow();
        int goalCharCol = destination.getCol();
        CharCell goalCharCell = (CharCell)destination;
        char goalChar = goalCharCell.getLetter();

        Box[][] boxes = node.getBoxes();

        Box box = boxes[goalCharRow][goalCharCol];
        if (box!=null){
            char b = Character.toLowerCase(box.getBoxChar());
            if (b == goalChar) {
                return true;
            }
        }
        return false;
    }

    public Box getBox() {
        return box;
    }

    public Cell getDestination() {
        return destination;
    }
}