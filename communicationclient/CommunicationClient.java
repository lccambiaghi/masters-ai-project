package communicationclient;

import java.io.*;
import java.util.*;

import communication.MsgHub;
import heuristic.Heuristic;
import level.*;
import plan.Planner;

public class CommunicationClient {
    private BufferedReader in;
    //private List<Agent> agents;
    //private Strategy strategy;
    private LevelParser levelParser;
    private Planner planner;

    public CommunicationClient() throws IOException {
        in = new BufferedReader(new InputStreamReader(System.in));
    }


    public boolean update() throws IOException {

        planner.analysisPhase();

        planner.planningPhase();

        planner.searchingPhase();

        List<LinkedList<Node>> solutions = planner.getSolutions();

        String jointAction = "";
        String response = "";
        while(!response.contains("false")) {
            // build joint action and progress iterator of solutions
            jointAction = "[";
            Node n;
            for (int i = 0; i < solutions.size() - 1; i++) {
                n = solutions.get(i).pollFirst();
                if(n!=null)
                    jointAction += n.action.toString() + ",";
                else
                    jointAction += "NoOp,";
            }
            n = solutions.get(solutions.size() - 1).pollFirst();
            if(n!=null)
                jointAction += n.action.toString() + "]";
            else
                jointAction += "NoOp]";
            // Place message in buffer
            System.out.println(jointAction);
            // Flush buffer
            System.out.flush();
            response = in.readLine();
        }
        System.err.format("Server responsed with %s to the inapplicable action: %s\n", response, jointAction);
        //System.err.format("%s was attempted in \n%s\n", act, n.toString());
        return false;

    }


    /**
     * Starts the client and runs a infinite loop
     */
    public static void main(String[] args) {

        System.err.println("*--------------------------------------*");
        System.err.println("|     CommunicationClient started      |");
        System.err.println("*--------------------------------------*");
        try {
            CommunicationClient client = new CommunicationClient();
            Heuristic heuristic = new Heuristic.WeightedAStar(5);
            //Heuristic heuristic = new Heuristic.Greedy();
            Strategy strategy = new StrategyBestFirst(heuristic);
            //Strategy strategy = new StrategyBFS();
            //client.setStrategy(strategy);

            client.levelParser = new LevelParser(strategy,true);
            client.levelParser.readMap();

            //client.agents = client.levelParser.getAgents();

            List<Agent> agentList = client.levelParser.getAgents();

            client.planner = new Planner(agentList);
            client.planner.setStrategy(strategy);

            MsgHub.createInstance(agentList);

            while(client.update())
                // when update returns false, we need to replan
                // TODO update beliefs
                ;

        } catch (IOException ex) {
            System.err.println("IOException thrown!");
        }
    }
}
