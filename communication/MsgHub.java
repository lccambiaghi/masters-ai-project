/**
 * Shared msghub for all agents to post and
 * recive msgs from.
 */
package communication;

import communicationclient.Agent;

import java.util.*;

/**
 * An agent broadcasts his plan -> All other agents receive the plan
 * If they find conflicts, they reply with a request containing a solution enriched with NoOp to fix the conflict
 *
 * After broadcasting, the agent checks for requests
 * If found, it replaces his solution with the enriched one
 *
 */
public class MsgHub {

    private static MsgHub instance;

    private List<Agent> agents;

    // in this map we store each message and its replies
    private HashMap<Message, Queue<Message>> messageMap;

    private MsgHub(List<Agent> agents){
        this.agents = agents;
        messageMap = new HashMap<>();
    }

    public void broadcast(Message announcement) {
        Queue<Message> responses = new ArrayDeque<>();

        messageMap.put(announcement, responses);

        char sender = announcement.getSender();

        for (Agent agent : agents)
            if (agent.getId() != sender)
                agent.receiveAnnouncement(announcement);

    }

    public Queue<Message> getResponses(Message announcement) {
        return messageMap.get(announcement);
    }

    public static MsgHub createInstance(List<Agent> agents){
        if(instance == null) {
            instance = new MsgHub(agents);
        }
        return instance;
    }

    public static MsgHub getInstance() {
        return instance;
    }

    public void reply(Message message, Message response) {
        Queue<Message> responses = new ArrayDeque<>();
        responses.add(response);

        messageMap.put(message, responses);
    }
}