import java.util.Random;



public class SudokuSolver {

    private static final int SIZE = 9; 
    private static final int SUBGRID_SIZE = 3; 

    private static final int MAX_ITERATIONS = 10000000; 

    private int[][] board;
    private int[][] inpBoard;
    private int faultScore;

    int iteration = 0;
    private int[][] lastBoard;
    int lastScore = MAX_ITERATIONS;

    public SudokuSolver() {
        this.board = new int[SIZE][SIZE];
        this.lastBoard = new int[SIZE][SIZE];
        this.faultScore = 0;
    }

    private void initializeBoard(int[][] inp) {
        inpBoard = inp;
        for (int i = 0; i < SIZE; i++) {
            System.arraycopy(inpBoard[i], 0, board[i], 0, SIZE);
        }

        Random random = new Random();

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if(inpBoard[i][j] == -1){
                    int randomDigit = random.nextInt(SIZE) + 1;
                    while (isInSubgrid(i, j, randomDigit)) {
                        randomDigit = random.nextInt(SIZE) + 1;
                    }
                    board[i][j] = randomDigit;
                }
            }
        }

        faultScore =  calculateFaultScore(board);
    }

    private boolean isInSubgrid(int startRow, int startCol, int num) {
        startRow /= 3;
        startRow *= 3;
        startCol /= 3;
        startCol *= 3;

        for (int row = 0; row < SUBGRID_SIZE; row++) {
            for (int col = 0; col < SUBGRID_SIZE; col++) {
                if (board[startRow + row][startCol + col] == num) {
                    return true;
                }
            }
        }
        return false;
    }

    public void solve(int[][] inp) {
        System.out.println("Inp arr:");
        printBoard(inp);
        initializeBoard(inp);

        iteration = 0;
        double temperature = 0.333333333333334;

        while (faultScore > 0 && iteration < MAX_ITERATIONS) {
            int[][] candidateBoard = tweakBoard();
            int candidateFaultScore = calculateFaultScore(candidateBoard); 

            if(candidateFaultScore < lastScore){
                for (int i = 0; i < SIZE; i++) {
                    System.arraycopy(candidateBoard[i], 0, lastBoard[i], 0, SIZE);
                }
                lastScore = candidateFaultScore;
            }

            if (acceptMove(candidateFaultScore, temperature)) {
                for (int i = 0; i < SIZE; i++) {
                    System.arraycopy(candidateBoard[i], 0, board[i], 0, SIZE);
                }
                faultScore = candidateFaultScore;
            }
            if (iteration < 1000 && iteration % 100 == 0) {
                System.out.println("Iteration: " + iteration + " - Fault Score: " + faultScore);
            } 
            else if (iteration % 25000 == 0) {
                System.out.println("Iteration: " + iteration + " - Fault Score: " + faultScore);
            }
            iteration++;
            temperature *= 0.999999995; // Cooling factor
        }

        System.out.println("Final:");
        printBoard(lastBoard);
        System.out.println("Fault Score of final Solution: " + lastScore);
        System.out.println("Found in " + iteration + " iteration");
        System.out.println(temperature);
    }

    private int[][] tweakBoard() {
        Random random = new Random();
        int[][] newBoard = new int[SIZE][SIZE];

        for (int i = 0; i < SIZE; i++) {
            System.arraycopy(board[i], 0, newBoard[i], 0, SIZE);
        }

        int row1, col1,row2, col2, newValue, gridx, gridy;
        gridx = random.nextInt(SUBGRID_SIZE);
        gridy = random.nextInt(SUBGRID_SIZE);
        do{
            row1 = random.nextInt(SUBGRID_SIZE) + 3*gridx;
            col1 = random.nextInt(SUBGRID_SIZE) + 3*gridy;
        }while(inpBoard[row1][col1] != -1);
        do{
            row2 = random.nextInt(SUBGRID_SIZE) + 3*gridx;
            col2 = random.nextInt(SUBGRID_SIZE) + 3*gridy;
        }while(inpBoard[row2][col2] != -1);
        
        int temp = newBoard[row1][col1];
        newBoard[row1][col1] = newBoard[row2][col2];
        newBoard[row2][col2] = temp;
        
        return newBoard;
    }

    private boolean acceptMove(int candidateFaultScore, double temperature) {
        if (candidateFaultScore < faultScore) {
            return true;
        }
        else {
            double delta = candidateFaultScore - faultScore;
            double probability = Math.exp((-1 * delta)/(temperature));
            return Math.random() <= probability;
        }
    }

    private int calculateFaultScore(int[][] candidateBoard) {
        int candidateFaultScore = 0;
        for (int c = 0; c < SIZE; c++) {
            for (int a = 0; a < SIZE; a++) {
                for (int b = a + 1; b < SIZE; b++) {
                    if (candidateBoard[c][a] == candidateBoard[c][b])
                        candidateFaultScore++;
                }
            }
        }

        for (int c = 0; c < SIZE; c++) {
            for (int a = 0; a < SIZE; a++) {
                for (int b = a + 1; b < SIZE; b++) {
                    if (candidateBoard[a][c] == candidateBoard[b][c])
                        candidateFaultScore++;
                }
            }
        }


        for(int i = 0; i < 3; i++){
            for(int j = 0; j < 3; j++){
                candidateFaultScore += countOverlappingValues(candidateBoard,i*3,j*3);
            }
        }

        return candidateFaultScore;
    }

    public static int countOverlappingValues(int[][] array,int row,int col) {
        int[] countArray = new int[10]; 

        for (int i = row; i < row+3; i++) {
            for (int j = col; j < col+3; j++) {
                int value = array[i][j];
                countArray[value]++;
            }
        }

        int overlapCount = 0;
        for (int count : countArray) {
            if (count > 1) {
                overlapCount += count-1;
            }
        }

        return overlapCount;
    }

    private static void printBoard(int[][] board) {
        for (int i = 0; i < SIZE; i++) {
            if (i % SUBGRID_SIZE == 0 && i != 0) {
                System.out.println("---------------------");
            }
            for (int j = 0; j < SIZE; j++) {
                if (j % SUBGRID_SIZE == 0 && j != 0) {
                    System.out.print("| ");
                }
                if (board[i][j] == -1) {
                    System.out.print("- ");
                }
                else {
                    System.out.print(board[i][j] + " ");
                }
            }
            System.out.println();
        }
        System.out.println();
    }

    public static void main(String[] args) {
        SudokuSolver solver = new SudokuSolver();
        int[][] initialBoard = {
                {-1, -1, 6, -1, -1, -1, -1, -1, -1},
                {-1, 8, -1, -1, 5, 4, 2, -1, -1},
                {-1, 4, -1, -1, 9, -1, -1, 7, -1},
                {-1, -1, 7, 9, -1, -1, 3, -1, -1},
                {-1, -1, -1, -1, 8, -1, 4, -1, -1},
                {6, -1, -1, -1, -1, -1, 1, -1, -1},
                {2, -1, 3, -1, -1, -1, -1, -1, 1},
                {-1, -1, -1, 5, -1, -1, -1, 4, -1},
                {-1, -1, 8, 3, -1, -1, 5, -1, 2}
        };
        solver.solve(initialBoard);
    }
}
