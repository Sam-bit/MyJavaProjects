import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.*;
import java.util.*;

class AllPermutation {
    ArrayList<ArrayList<Integer>> arrayLists = new ArrayList<ArrayList<Integer>>();
    // The input array for permutation
    private final int Arr[];

    // Index array to store indexes of input array
    private int Indexes[];

    // The index of the first "increase"
    // in the Index array which is the smallest
    // i such that Indexes[i] < Indexes[i + 1]
    private int Increase;

    // Constructor
    public AllPermutation(int arr[]) {
        this.Arr = arr;
        this.Increase = -1;
        this.Indexes = new int[this.Arr.length];
    }

    // Initialize and output
    // the first permutation
    public void GetFirst() {

        // Allocate memory for Indexes array
        this.Indexes = new int[this.Arr.length];

        // Initialize the values in Index array
        // from 0 to n - 1
        for (int i = 0; i < Indexes.length; ++i) {
            this.Indexes[i] = i;
        }

        // Set the Increase to 0
        // since Indexes[0] = 0 < Indexes[1] = 1
        this.Increase = 0;

        // Output the first permutation
    }

    // Function that returns true if it is
    // possible to generate the next permutation
    public boolean HasNext() {

        // When Increase is in the end of the array,
        // it is not possible to have next one
        return this.Increase != (this.Indexes.length - 1);
    }

    // Output the next permutation
    public void GetNext() {

        // Increase is at the very beginning
        if (this.Increase == 0) {

            // Swap Index[0] and Index[1]
            this.Swap(this.Increase, this.Increase + 1);

            // Update Increase
            this.Increase += 1;
            while (this.Increase < this.Indexes.length - 1
                    && this.Indexes[this.Increase]
                    > this.Indexes[this.Increase + 1]) {
                ++this.Increase;
            }
        } else {

            // Value at Indexes[Increase + 1] is greater than Indexes[0]
            // no need for binary search,
            // just swap Indexes[Increase + 1] and Indexes[0]
            if (this.Indexes[this.Increase + 1] > this.Indexes[0]) {
                this.Swap(this.Increase + 1, 0);
            } else {

                // Binary search to find the greatest value
                // which is less than Indexes[Increase + 1]
                int start = 0;
                int end = this.Increase;
                int mid = (start + end) / 2;
                int tVal = this.Indexes[this.Increase + 1];
                while (!(this.Indexes[mid] < tVal && this.Indexes[mid - 1] > tVal)) {
                    if (this.Indexes[mid] < tVal) {
                        end = mid - 1;
                    } else {
                        start = mid + 1;
                    }
                    mid = (start + end) / 2;
                }

                // Swap
                this.Swap(this.Increase + 1, mid);
            }

            // Invert 0 to Increase
            for (int i = 0; i <= this.Increase / 2; ++i) {
                this.Swap(i, this.Increase - i);
            }

            // Reset Increase
            this.Increase = 0;
        }
        this.Output();
    }

    // Function to output the input array
    private void Output() {
        ArrayList<Integer> arrayList = new ArrayList<Integer>();
        for (int i = 0; i < this.Indexes.length; ++i) {
            arrayList.add(this.Arr[this.Indexes[i]]);
            // Indexes of the input array
            // are at the Indexes array
        }
        arrayLists.add(arrayList);
    }

    // Swap two values in the Indexes array
    private void Swap(int p, int q) {
        int tmp = this.Indexes[p];
        this.Indexes[p] = this.Indexes[q];
        this.Indexes[q] = tmp;
    }
}

class Solver {
    private int N;
    private int minMoves;
    public static int[] correctRow;
    public static int[] correctCol;

    private class Node implements Comparable<Node> {
        private Board board;
        private int moves;
        private Node prevNode;

        public Node(Board board, int moves, Node prev) {
            this.board = board;
            this.moves = moves;
            this.prevNode = prev;
        }

        public int compareTo(Node that) {
            int thisPriority = this.moves + this.board.manhattan();
            int thatPriority = that.moves + that.board.manhattan();
            if (thisPriority < thatPriority) {
                return -1;
            } else if (thisPriority > thatPriority) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    private Node lastNode;
    private boolean solvable;

    public Solver(Board initial) {
        N = initial.dimension();
        PriorityQueue<Node> pq = new PriorityQueue<Node>();
        PriorityQueue<Node> pq2 = new PriorityQueue<Node>();
        pq.add(new Node(initial, 0, null));
        pq2.add(new Node(initial.twin(), 0, null));
        while (true) {
            Node removed = pq.poll();
            Node removed2 = pq2.poll();
            if (removed.board.isGoal()) {
                minMoves = removed.moves;
                lastNode = removed;
                solvable = true;
                break;
            }
            if (removed2.board.isGoal()) {
                minMoves = -1;
                solvable = false;
                break;
            }

            Iterable<Board> neighbors = removed.board.neighbors();
            Iterable<Board> neighbors2 = removed2.board.neighbors();
            for (Board board : neighbors) {
                if (removed.prevNode != null && removed.prevNode.board.equals(board)) {
                    continue;
                }
                pq.add(new Node(board, removed.moves + 1, removed));
            }
            for (Board board : neighbors2) {
                if (removed2.prevNode != null && removed2.prevNode.board.equals(board)) {
                    continue;
                }
                pq2.add(new Node(board, removed2.moves + 1, removed2));
            }
        }
    }

    public boolean isSolvable() {
        return solvable;
    }

    public int moves() {
        return minMoves;
    }

    public Iterable<Board> solution() {
        if (!isSolvable()) {
            return null;
        }
        Stack<Board> stack = new Stack<Board>();
        Node node = lastNode;
        while (true) {
            if (node == null) break;
            Board board = node.board;
            node = node.prevNode;
            stack.push(board);
        }
        return stack;
    }

    static void initCorrectRowsCols(int N) {
        correctRow = new int[N * N];
        int z = 0;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                correctRow[z++] = i;
            }
        }
        z = 0;
        correctCol = new int[N * N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                correctCol[z++] = j;
            }
        }
    }

    public static void main(String[] args) throws Exception {
        int dimension = 2;
        JSONParser parser = new JSONParser();
        JSONArray jsonArray = new JSONArray();

        String fileName = "LEVEL_" + dimension + ".json";
        File file = new File(fileName);
        if (file.exists()) {
            Reader reader = new FileReader(fileName);
            jsonArray = (JSONArray) parser.parse(reader);
        }
        int nbrOfCells = dimension * dimension;
        int[] goalCells = new int[nbrOfCells];
        for (int i = 1; i < goalCells.length; i++) {
            goalCells[i - 1] = i;
        }
        goalCells[nbrOfCells - 1] = 0;
        initCorrectRowsCols(dimension);

        AllPermutation perm = new AllPermutation(goalCells);
        perm.GetFirst();
        while (perm.HasNext()) {
            perm.GetNext();
        }
        System.out.println(perm.arrayLists);
        int limit = perm.arrayLists.size() > 500 ? 500 : perm.arrayLists.size();
        for (int k = 0; k < limit; k++) {
            ArrayList<Integer> myList = perm.arrayLists.get(k);
            int[] intList = myList.stream().mapToInt(Integer::intValue).toArray();

            int[][] blocks = new int[dimension][dimension];
            for (int j = 0; j < dimension; j++)
                for (int i = 0; i < dimension; i++)
                    blocks[j][i] = intList[j * dimension + i];

            Board initial = new Board(blocks);

            // solve the puzzle
            Solver solver = new Solver(initial);

            JSONObject obj = new JSONObject();
            // print solution to standard output
            if (solver.isSolvable()) {
                obj.put("start", Arrays.toString(intList));
                obj.put("goal", Arrays.toString(goalCells));
                obj.put("moves", solver.moves());
                jsonArray.add(obj);

            }
        }
        try (FileWriter f = new FileWriter(fileName)) {
            f.write(jsonArray.toJSONString());
            f.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

class Board {
    private int[][] array;
    private int N;
    int emptyRow;
    int emptyCol;
    boolean reached;
    int manhattan = 0;

    public Board(int[][] blocks) {
        N = blocks.length;
        array = new int[N][N];
        reached = true;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                array[i][j] = blocks[i][j];
                if (array[i][j] == 0) {
                    emptyRow = i;
                    emptyCol = j;
                }
                if (array[i][j] != N * i + j + 1) {
                    if (!(i == N - 1 && j == N - 1)) {
                        reached = false;
                    }
                }
                int num = array[i][j];
                if (num == 0) {
                    continue;
                }
                int indManhattan = Math.abs(Solver.correctRow[num - 1] - i)
                        + Math.abs(Solver.correctCol[num - 1] - j);
                manhattan += indManhattan;
            }
        }
    }

    public int dimension() {
        return N;
    }

    public int hamming() {
        int outOfPlace = 0;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (i == N - 1 && j == N - 1) {
                    break;
                }
                if (array[i][j] != i * N + j + 1) {
                    outOfPlace++;
                }
            }
        }
        return outOfPlace;
    }

    public int manhattan() {
        return manhattan;
    }

    public boolean isGoal() {
        return reached;
    }

    public Board twin() {
        int[][] newArray = new int[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                newArray[i][j] = array[i][j];
            }
        }
        for (int i = 0; i < 2; i++) {
            if (newArray[i][0] == 0 || newArray[i][1] == 0) {
                continue;
            }
            int temp = newArray[i][0];
            newArray[i][0] = newArray[i][1];
            newArray[i][1] = temp;
            break;

        }
        return new Board(newArray);
    }

    public boolean equals(Object y) {
        if (y == this) {
            return true;
        }
        if (y == null) {
            return false;
        }
        if (y.getClass() != this.getClass()) {
            return false;
        }
        Board that = (Board) y;
        if (that.array.length != this.array.length) {
            return false;
        }
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (that.array[i][j] != this.array[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    public Iterable<Board> neighbors() {
        Queue<Board> q = new ArrayDeque<Board>();
        int firstIndex0 = 0;
        int secondIndex0 = 0;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                if (array[i][j] == 0) {
                    firstIndex0 = i;
                    secondIndex0 = j;
                    break;
                }
            }
        }
        if (secondIndex0 - 1 > -1) {
            int[][] newArr = getCopy();
            exch(newArr, firstIndex0, secondIndex0, firstIndex0, secondIndex0 - 1);
            q.add(new Board(newArr));
        }
        if (secondIndex0 + 1 < N) {
            int[][] newArr = getCopy();
            exch(newArr, firstIndex0, secondIndex0, firstIndex0, secondIndex0 + 1);
            q.add(new Board(newArr));
        }
        if (firstIndex0 - 1 > -1) {
            int[][] newArr = getCopy();
            exch(newArr, firstIndex0, secondIndex0, firstIndex0 - 1, secondIndex0);
            q.add(new Board(newArr));
        }
        if (firstIndex0 + 1 < N) {
            int[][] newArr = getCopy();
            exch(newArr, firstIndex0, secondIndex0, firstIndex0 + 1, secondIndex0);
            q.add(new Board(newArr));
        }
        return q;
    }

    private int[][] getCopy() {
        int[][] copy = new int[N][N];
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                copy[i][j] = array[i][j];
            }
        }
        return copy;
    }

    private void exch(int[][] arr, int firstIndex, int secIndex, int firstIndex2, int secIndex2) {
        int temp = arr[firstIndex][secIndex];
        arr[firstIndex][secIndex] = arr[firstIndex2][secIndex2];
        arr[firstIndex2][secIndex2] = temp;
    }

    public String toString() {
        StringBuilder s = new StringBuilder();
        s.append(N + "\n");
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                s.append(String.format("%4d", array[i][j]));
            }
            s.append("\n");
        }
        return s.toString();
    }
}

