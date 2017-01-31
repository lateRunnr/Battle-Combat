
package intelligentAgent;

//import static usc_two.USC_Two.children;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Node extends groundAcquire {

    static int chillo = 0;
    String[][] board;
    int parent;
    int depth;
    String player;
    String moveType;
    String state_Name;
    int action;
    int board_cost;
    int alpha;
    int beta;
    String key;
    //public int map_key=0;
    List<Node> child_nodes = new ArrayList<>();
    HashMap<Integer, Node> childoo = new HashMap<>();

    public Node(int parent, String[][] boardState, int d, String p, String mT, int act, int bc, String statename) {
        this.parent = parent;
        this.board = boardState;
        this.depth = d;
        this.player = p;
        this.moveType = mT;
        this.action = act;
        this.board_cost = bc;
        this.state_Name = statename;
        this.alpha = Integer.MIN_VALUE;
        this.beta = Integer.MAX_VALUE;
        //this.children=new HashMap<>();
    }

    public void addChild(Node child) {
        this.child_nodes.add(child);
        Node test;
        children.put(map_key, child);
        test = children.get(map_key);
        map_key++;

    }

}
