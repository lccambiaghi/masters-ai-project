package level;

import communicationclient.Agent;
import communicationclient.Strategy;
import graph.Graph;
import graph.Vertex;
import plan.ConflictDetector;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by salik on 07-04-2017.
 * Class for parsing the level as it is given from the environment-server or reading from a file
 */
public class LevelParser {

    private BufferedReader in;
    private Strategy strategy;
    private boolean debug;
    private Graph graph;

    public LevelParser(Strategy strategy, boolean debug) throws FileNotFoundException {
        this.strategy = strategy;
        this.debug = debug;

        // If debug==true, our client parses the level instead of receiving it from the server
        if(this.debug){
            FileInputStream fis = null;
            fis = new FileInputStream("C:\\Users\\salik\\Documents\\02285-MASters-prog_proj\\competition_levels\\MAOmnics.lvl");
            in = new BufferedReader(new InputStreamReader(fis));
        }else{
            in = new BufferedReader(new InputStreamReader(System.in));
        }
    }

    /**
     * Read lines of level
     * Creates instance of level
     *
     * Creates agents, boxes, charcells
     * It adds boxes and charcells to the level
     * Sets agents in colorMap
     *
     * Create instance of graph
     * For each cell, it creates a vertex
     * If the cell contains boxes or charCells, it sets it on the vertex
     * It adds vertex to graph
     *
     * Finally, it creates the real graph and analysis it??
     */
    public void readMap() throws IOException {
        HashMap<Character, Color> colors = new HashMap<>();
        Color color;
        int MAX_COL = 0;
        int MAX_ROW = 0;

        int row = 0;
        ArrayList<String> map = new ArrayList<>();
        String line = in.readLine();

        if(this.debug){
            while(line != null){
                map.add(line);
                if(line.length() > MAX_COL) MAX_COL = line.length();
                line = in.readLine();
                row++;
                MAX_ROW = row;
            }
        }else{
            while(!line.equals("")) {
                map.add(line);
                if(line.length() > MAX_COL) MAX_COL = line.length();
                line = in.readLine();
                row++;
                MAX_ROW = row;
            }
        }

        Level level = Level.createInstance(MAX_ROW, MAX_COL);

        if(this.debug){
            //System.err.println(" ");
            //System.err.println("Debug is ON. Printing scanned map");

            for (String lineInMap: map) {
                //System.err.println(lineInMap);
            }

            //System.err.println(" ");
        }

        row = 0;
        boolean colorLevel = false;
        graph = new Graph();
        for (String lineInMap: map) {
            lineInMap = lineInMap.replaceFirst("\\s++$", "");//Remove trailing whitespaces

            // if line is a color declaration, MA agent
            if (lineInMap.matches("^[a-z]+:\\s*[0-9A-Z](,\\s*[0-9A-Z])*\\s*$")) {
                colorLevel = true;
                lineInMap = lineInMap.replaceAll("\\s", "");
                color = Color.valueOf(lineInMap.split( ":" )[0]);
                for (String id : lineInMap.split(":")[1].split(","))
                    colors.put(id.charAt(0), color);
            } else {
                // if SA, map of colors is empty -> all colors set to blue.
                for (int col = 0; col < lineInMap.length(); col++) {
                    char chr = lineInMap.charAt(col);
                    if (chr == '+') { // Wall.
                        level.setWall(true, row, col);
                    } else if ('A' <= chr && chr <= 'Z') { // Box.
                        Vertex v = new Vertex(row,col);
                        Box box = new Box(col, row, chr, Color.blue);
                        v.setBox(box);
                        graph.addVertex(v);
                        if(colorLevel) {
                            Color boxColor = colors.get(chr);

                            if(boxColor == null) boxColor = Color.blue;
                            box.setColor(boxColor);
                        }
                        level.addBox(box);
                    } else if ('a' <= chr && chr <= 'z') { // CharCell.
                        CharCell charCell = new CharCell(col, row, chr);
                        Vertex v = new Vertex(row,col);
                        level.addCharCell(charCell);
                        v.setGoalCell(charCell);
                        graph.addVertex(v);
                    } else if (chr == ' ') {
                        // Free space.
                        Vertex v = new Vertex(row,col);
                        graph.addVertex(v);
                    }else if ('0' <= chr && chr <= '9') { // Agent
                        Vertex v = new Vertex(row,col);
                        Agent newAgent = new Agent(chr, this.strategy, row, col);
                        if(colorLevel) {
                            Color agentColor = colors.get(chr);
                            if(agentColor == null) agentColor = Color.blue;
                            newAgent.setColor(agentColor);
                        }
                        level.setAgentInColorMap(newAgent);
                        v.setAgent(newAgent);
                        graph.addVertex(v);
                        if(this.debug) {
                            //System.err.println("Agent " + newAgent.getId() + " created, Color is " + newAgent.getColor().toString());
                        }
                    }
                }
                row++;
            }
        }
        // TODO is it the right place? Move it in the LevelAnalyzer?
        graph.createGraph();
        if(this.debug) {
            //System.err.println("*--------------------------------------*");
        }
    }

    public Graph getGraph() {
        return graph;
    }
}
