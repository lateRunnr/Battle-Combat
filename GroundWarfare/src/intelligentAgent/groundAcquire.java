package intelligentAgent;

import java.io.File;
import java.io.FileNotFoundException;
import static java.lang.Integer.max;
import static java.lang.Integer.min;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;
import java.util.Scanner;
import java.util.Stack;


public class groundAcquire {

    /**
     * @param args the command line arguments
     */
    static int map_key = 0;
    static int actionkey = 0;
    static int array_size;
    static String mapKey;
    static int map_iterator = 0;
    static int depth_limit;
    static int[][] value;
    static Node root;
    static String current_player, opponent;
    static HashMap<Integer, Node> children;
    static HashMap<Integer, String> columnToAlpha;

    public static void main(String[] args) throws FileNotFoundException {
        String[][] board;
        // String[][] temp_board;
        columnToAlpha = new HashMap<>();
        createAlphabetMapping();

        String input_str;

        String[] temp;
        children = new HashMap<>();

        Scanner sc = new Scanner(new File("input.txt"));
        List input = new ArrayList();

        // Taking input from File
        for (int i = 0; i < 4; i++) {
            input_str = sc.nextLine();
            input.add(input_str);
        }
        current_player = (String) input.get(2);
        //  System.out.println("current player"+current_player);
        depth_limit = Integer.valueOf((String) input.get(3));
        array_size = Integer.valueOf((String) input.get(0));
        board = new String[array_size][array_size];

        value = new int[array_size][array_size];
        for (int i = 0; i < array_size; i++) {
            input_str = sc.nextLine();
            temp = input_str.split("\\s+");
            for (int j = 0; j < array_size; j++) {
                value[i][j] = Integer.valueOf(temp[j]);

            }
        }
        for (int i = 0; i < array_size; i++) {
            input_str = sc.nextLine();

            for (int j = 0; j < array_size; j++) {

                board[i][j] = String.valueOf(input_str.charAt(j));
            }
        }

        ////////////////////////////////////////////////
        //    ** Tree Creation**      //
        //Root Node
        String move_type = "null";
        //Node root;
        int tree_depth = 0;
        root = new Node(-1, board, tree_depth, current_player, move_type, -1, getBoardCost(board), "Root");
        tree_depth++;

        // Root children depth=1
        for (int i = 0; i < array_size; i++) {
            for (int j = 0; j < array_size; j++) { //System.out.println("Inside");
                if (root.board[i][j].compareTo(".") == 0) {

                    //Copying array
                    String temp_board[][] = new String[array_size][array_size];
                    for (int k = 0; k < array_size; k++) {
                        for (int l = 0; l < array_size; l++) {
                            temp_board[k][l] = root.board[k][l];
                        }
                    }

                    temp_board[i][j] = root.player;

                    // Getting move name logic
                    String column_name = columnToAlpha.get(j);
                    String row_name = String.valueOf(i + 1);
                    String state_name = column_name.concat(row_name);
                    //
                    root.addChild(new Node(root.action, temp_board, tree_depth, getOpponent(root.player), "Stake", actionkey, getBoardCost(temp_board), state_name));
                    actionkey++;

                    // If raid possible 
                    if (raidPossible(root.board, root.player, i, j)) {
                        int flag = 0;
                        //Copying array
                        String temp_boardRaid[][] = new String[array_size][array_size];
                        for (int k = 0; k < array_size; k++) {
                            for (int l = 0; l < array_size; l++) {
                                temp_boardRaid[k][l] = root.board[k][l];
                            }
                        }

                        temp_boardRaid[i][j] = root.player;
                        if (leftPossible(i, j)) {
                            if (temp_boardRaid[i][j - 1].compareTo(getOpponent(root.player)) == 0) {
                                temp_boardRaid[i][j - 1] = root.player;
                                flag = 1;
                            }

                        }
                        if (rightPossible(i, j)) {
                            if (temp_boardRaid[i][j + 1].compareTo(getOpponent(root.player)) == 0) {
                                temp_boardRaid[i][j + 1] = root.player;
                                flag = 1;
                            }

                        }
                        if (abovePossible(i, j)) //if(temp_board[i-1][j] == getOpponent(current.player))
                        {
                            if (temp_boardRaid[i - 1][j].compareTo(getOpponent(root.player)) == 0) {
                                temp_boardRaid[i - 1][j] = root.player;
                                flag = 1;
                            }

                        }
                        if (downPossible(i, j)) // if(temp_board[i+1][j] == getOpponnt(current.player))
                        {
                            if (temp_boardRaid[i + 1][j].compareTo(getOpponent(root.player)) == 0) {
                                temp_boardRaid[i + 1][j] = root.player;
                                flag = 1;
                            }

                        }

                        //tree_depth=current.depth+1;
                        if (flag == 1) {
                            root.addChild(new Node(root.action, temp_boardRaid, tree_depth, getOpponent(root.player), "Raid", actionkey, getBoardCost(temp_boardRaid), state_name));
                            actionkey++;
                        }

                    }

                }
            }
        }

        //Popping HashMap for branching down tree 
        while (!children.isEmpty()) {
            Node current;
            current = children.get(map_iterator);

            // If crosses depth limit
            if (current.depth >= depth_limit) {
                break;
            } else {
                int depth_flag = 0;
                for (int i = 0; i < array_size; i++) {
                    for (int j = 0; j < array_size; j++) {
                        if (current.board[i][j].compareTo(".") == 0) {
                            depth_flag = 1;
                            //temp_board=current.board.clone();
                            String temp_board[][] = new String[array_size][array_size];
                            //Copying array
                            for (int k = 0; k < array_size; k++) {
                                for (int l = 0; l < array_size; l++) {
                                    temp_board[k][l] = current.board[k][l];
                                }
                            }

                            temp_board[i][j] = current.player;
                            tree_depth = current.depth + 1;
                            //Alphabet mapping
                            String column_name = columnToAlpha.get(j);
                            String row_name = String.valueOf(i + 1);
                            String state_name = column_name.concat(row_name);

                            current.addChild(new Node(current.action, temp_board, tree_depth, getOpponent(current.player), "Stake", actionkey, getBoardCost(temp_board), state_name));
                            actionkey++;

                            if (raidPossible(current.board, current.player, i, j)) {
                                int current_flag = 0;
                                // temp_board=current.board.clone();

                                //copying array
                                String temp_board2[][] = new String[array_size][array_size];
                                for (int k = 0; k < array_size; k++) {
                                    for (int l = 0; l < array_size; l++) {
                                        temp_board2[k][l] = current.board[k][l];
                                    }
                                }
                                temp_board2[i][j] = current.player;
                                if (leftPossible(i, j)) {
                                    if (temp_board2[i][j - 1].compareTo(getOpponent(current.player)) == 0) {
                                        temp_board2[i][j - 1] = current.player;
                                        current_flag = 1;
                                    }

                                }
                                if (rightPossible(i, j)) {
                                    if (temp_board2[i][j + 1].compareTo(getOpponent(current.player)) == 0) {
                                        temp_board2[i][j + 1] = current.player;
                                        current_flag = 1;
                                    }

                                }
                                if (abovePossible(i, j)) //if(temp_board[i-1][j] == getOpponent(current.player))
                                {
                                    if (temp_board2[i - 1][j].compareTo(getOpponent(current.player)) == 0) {
                                        temp_board2[i - 1][j] = current.player;
                                        current_flag = 1;
                                    }

                                }
                                if (downPossible(i, j)) // if(temp_board[i+1][j] == getOpponent(current.player))
                                {
                                    if (temp_board2[i + 1][j].compareTo(getOpponent(current.player)) == 0) {
                                        temp_board2[i + 1][j] = current.player;
                                        current_flag = 1;
                                    }

                                }

                                if (current_flag == 1) {
                                    tree_depth = current.depth + 1;
                                    current.addChild(new Node(current.action, temp_board2, tree_depth, getOpponent(current.player), "Raid", actionkey, getBoardCost(temp_board2), state_name));
                                    actionkey++;
                                }

                            }

                        }
                        /////////////Check for dummy depth problem

                    }

                }
                ///////// check depth flag
                if (depth_flag == 0) {
                    depth_limit = current.depth;
                    break;
                }

            }

            map_iterator++;
        }

        //System.out.println(children.size()+"size");
        String algorithm = (String) input.get(1);
        MaxNode final_action;
        switch (algorithm) {
            case "MINIMAX":
                final_action = Minimax(root);
                //System.out.print(final_action.);
                System.out.print(final_action.move.state_Name);
                System.out.print(" ");
                System.out.println(final_action.move.moveType);
                printBoard(final_action.move.board);
                break;

            case "ALPHABETA":
                final_action = alpha_beta_Minimax(root);
                System.out.print(final_action.move.state_Name);
                System.out.print(" ");
                System.out.println(final_action.move.moveType);
                printBoard(final_action.move.board);
                break;

            default:
                System.out.println("Invalid Algorithm");

        }

    }

    public static boolean raidPossible(String[][] current_board, String cur_player, int i, int j) {
        if (leftPossible(i, j)) {
            if (current_board[i][j - 1].compareTo(cur_player) == 0) {
                return true;
            }
        }
        if (rightPossible(i, j)) {
            if (current_board[i][j + 1].compareTo(cur_player) == 0) {
                return true;
            }
        }

        if (abovePossible(i, j)) {
            if (current_board[i - 1][j].compareTo(cur_player) == 0) {
                return true;
            }
        }
        if (downPossible(i, j)) {
            if (current_board[i + 1][j].compareTo(cur_player) == 0) {
                return true;
            }
        }

        return false;

    }

    public static boolean leftPossible(int i, int j) {
        if (j - 1 >= 0) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean rightPossible(int i, int j) {
        if (j + 1 < array_size) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean abovePossible(int i, int j) {
        if (i - 1 >= 0) {
            return true;
        } else {
            return false;
        }
    }

    public static boolean downPossible(int i, int j) {
        if (i + 1 < array_size) {
            return true;
        } else {
            return false;
        }
    }

    public static String getOpponent(String currentPlayer) { //System.out.println("Left player "+currentPlayer);
        if (currentPlayer.compareTo("X") == 0) {
            return "O";
        } else {
            return "X";
        }
    }

    public static MaxNode Minimax(Node parent) {
        MaxNode return_node = max_value(parent);
        return return_node;

    }

    public static MaxNode max_value(Node temp) {
        int v_temp, min_return;
        Node res = null;
        //Leaf node
        if (terminal(temp)) {
            return (new MaxNode(temp, utility(temp)));

        }

        //Non Leaf Node
        int v = Integer.MIN_VALUE;
        for (int i = 0; i < temp.child_nodes.size(); i++) {
            v_temp = v;
            min_return = min_value(temp.child_nodes.get(i));
            //Handling Tie
            if (min_return == v) {
                //If existing was Stake and new is Raid: Dont update move
                if (res.moveType.compareTo("Stake") == 0 && temp.child_nodes.get(i).moveType.compareTo("Raid") == 0) {
                    continue;
                } //If existing was Raid and new is Stake: Update move
                else if (res.moveType.compareTo("Raid") == 0 && temp.child_nodes.get(i).moveType.compareTo("Stake") == 0) {
                    v = min_return;
                    res = temp.child_nodes.get(i);
                } else {
                    continue;
                }

            } else {
                v = max(v, min_return);
            }
            if (v_temp != v) {
                res = temp.child_nodes.get(i);
            }
        }
        //Returning Object containing value and move
        return (new MaxNode(res, v));

    }

    public static int min_value(Node temp) {
        int return_val;
        MaxNode Max_res = null;
        if (terminal(temp)) {
            return_val = utility(temp);

            return return_val;
        }
        int v = Integer.MAX_VALUE;
        for (int i = 0; i < temp.child_nodes.size(); i++) {
            Max_res = max_value(temp.child_nodes.get(i));
            v = min(v, Max_res.val);
        }
        return v;

    }

    public static boolean terminal(Node temp) {
        if (temp.depth == depth_limit) {
            return true;
        } else {
            return false;
        }
    }

    public static int utility(Node temp) {
        int x_value = 0, o_value = 0;
        for (int i = 0; i < array_size; i++) {
            for (int j = 0; j < array_size; j++) {
                if (temp.board[i][j].compareTo(current_player) == 0) {
                    x_value = x_value + value[i][j];
                }
                if (temp.board[i][j].compareTo(getOpponent(current_player)) == 0) {
                    o_value = o_value + value[i][j];
                }

            }
        }

        return x_value - o_value;

    }

    public static int getBoardCost(String[][] board) {

        int x_value = 0, o_value = 0;
        for (int i = 0; i < array_size; i++) {
            for (int j = 0; j < array_size; j++) {
                if (board[i][j].compareTo(current_player) == 0) {
                    x_value = x_value + value[i][j];
                }
                if (board[i][j].compareTo(getOpponent(current_player)) == 0) {
                    o_value = o_value + value[i][j];
                }

            }
        }

        return x_value - o_value;
    }

    public static Object getNodeKey(int value) {
        for (Object o : children.keySet()) {
            if (children.get(o).board_cost == value) {
                return o;
            }
        }
        return null;
    }

    public static void printBoard(String[][] board) {

        for (int i = 0; i < array_size; i++) {
            for (int j = 0; j < array_size; j++) {
                System.out.print(board[i][j]);
            }
            System.out.println();
        }

// throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public static Node getNode(int v, Node temp) {
        Node res = null;

        for (int i = 0; i < temp.child_nodes.size(); i++) {
            if (terminal(temp.child_nodes.get(i))) {
                int val;
                val = utility(temp.child_nodes.get(i));
                //System.out.println("Tt "+val);
                if (val == v) {
                    res = temp.child_nodes.get(i);
                    return res;
                }
            } else {
                getNode(v, (temp.child_nodes.get(i)));

            }
        }
        return res;
    }

    /////////////////////////////////
    //Alpha Beta
    public static MaxNode alpha_beta_Minimax(Node parent) {
        MaxNode return_node = alpha_beta_max_value(parent, parent.alpha, parent.beta);
        return return_node;

    }

    //Alpha Beta Max and Min value function
    public static MaxNode alpha_beta_max_value(Node temp, int alpha, int beta) {
        int v_temp, min_return;
        Node res = null;
        MaxNode dummy = null;

        // Passing alpha and beta values to children
        // temp.alpha=alpha;
        //temp.beta=beta;
        // Checking if terminal value (Leaf Node)
        if (terminal(temp)) {
            dummy = new MaxNode(temp, utility(temp));
            return (dummy);
        }

        //Non Leaf nodes
        int v = Integer.MIN_VALUE;
        for (int i = 0; i < temp.child_nodes.size(); i++) {   //v_temp=v;

            //Checking condition
            min_return = alpha_beta_min_value(temp.child_nodes.get(i), alpha, beta);

            // V with same value already exists 
            if (min_return == v) {
                /*if(res.moveType.compareTo("Stake")==0 && temp.child_nodes.get(i).moveType.compareTo("Raid")==0)
                {
                    continue;
                }
                else*/
                if (res.moveType.compareTo("Raid") == 0 && temp.child_nodes.get(i).moveType.compareTo("Stake") == 0) {
                    v = min_return;
                    res = temp.child_nodes.get(i);
                }

            } else if (v < min_return) // V id different that previous value of V
            {
                v = min_return;
                res = temp.child_nodes.get(i);

                /*v=max(v,min_return);
             if(v_temp!=v)
            {
                res=temp.child_nodes.get(i);
            }*/
            }

            //Checking parents Beta value
            if (v >= beta) {
                // System.out.println("Pruned in max");
                dummy = new MaxNode(temp.child_nodes.get(i), v);
                return (dummy);
            }
            alpha = max(alpha, v);

        }
        dummy = new MaxNode(res, v);
        return (dummy);

    }

    public static int alpha_beta_min_value(Node temp, int alpha, int beta) {
        MaxNode Max_res = null;
        //int v_temp;

        // Passing alpha and beta values to children
        //  temp.alpha=alpha;
        //temp.beta=beta;
        // Checking if terminal value (Leaf Node)
        if (terminal(temp)) {
            return utility(temp);
        }

        int v = Integer.MAX_VALUE;
        //Non Leaf node
        for (int i = 0; i < temp.child_nodes.size(); i++) {   //v_temp=v;
            Max_res = alpha_beta_max_value(temp.child_nodes.get(i), alpha, beta);
            // System.out.println("State " + Max_res.move.state_Name + " Depth " + Max_res.move.depth + " Terminal Value " + Max_res.val);
            v = min(v, Max_res.val);
            if (v <= alpha) {  //System.out.println("Pruned in min");
                // System.out.println("Minimum returning after pruning " + v);
                return v;
            }
            beta = min(beta, v);
        }
        //System.out.println("Minimum returning " + v);
        return v;
    }

    public static void createAlphabetMapping() {

        columnToAlpha.put(0, "A");
        columnToAlpha.put(1, "B");
        columnToAlpha.put(2, "C");
        columnToAlpha.put(3, "D");
        columnToAlpha.put(4, "E");
        columnToAlpha.put(5, "F");
        columnToAlpha.put(6, "G");
        columnToAlpha.put(7, "H");
        columnToAlpha.put(8, "I");
        columnToAlpha.put(9, "J");
        columnToAlpha.put(10, "K");
        columnToAlpha.put(11, "L");
        columnToAlpha.put(12, "M");
        columnToAlpha.put(13, "N");
        columnToAlpha.put(14, "O");
        columnToAlpha.put(15, "P");
        columnToAlpha.put(16, "Q");
        columnToAlpha.put(17, "R");
        columnToAlpha.put(18, "S");
        columnToAlpha.put(19, "T");
        columnToAlpha.put(20, "U");
        columnToAlpha.put(21, "V");
        columnToAlpha.put(22, "W");
        columnToAlpha.put(23, "X");
        columnToAlpha.put(24, "Y");
        columnToAlpha.put(25, "Z");

    }

}
