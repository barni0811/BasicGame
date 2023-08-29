import java.util.Random;

public class Main {

    static final int GAME_LOOP_NUMBER = 100;
    static final int HEIGHT = 15;
    static final int WIDTH = 15;
    static final Random RANDOM = new Random();

    public static void main(String[] args) throws InterruptedException {

        String playerMark = "O";
        int playerRow = 2;
        int playerCol = 2;
        Direction playerDirection = Direction.RIGHT;

        String enemyMark = "-";
        int enemyRow = 7;
        int enemyCol = 4;
        Direction enemyDirection = Direction.LEFT;

        //pálya inicializálása
        String[][] level = new String[HEIGHT][WIDTH];
        initLevel(level);
        
        addObstacles(level, 1, 1);

        for (int iterationNumber = 1; iterationNumber <= GAME_LOOP_NUMBER; iterationNumber++){
            //játékos léptetése
            if(iterationNumber % 15 == 0) {
                playerDirection = changeDirection(playerDirection);
            }

            int[] playerCoordinates = makeMove(playerDirection, level, playerRow, playerCol);
            playerRow = playerCoordinates[0];
            playerCol = playerCoordinates[1];


            enemyDirection = changeEnemyDirection(level, enemyDirection, playerRow, playerCol, enemyRow, enemyCol);
            if ( iterationNumber % 2 == 0) {
                int[] enemyCoordinates = makeMove(enemyDirection, level, enemyRow, enemyCol);
                enemyRow = enemyCoordinates[0];
                enemyCol = enemyCoordinates[1];
            }

            draw(level, playerMark, playerRow, playerCol, enemyMark, enemyRow, enemyCol);

            addSomeDelay(300L, iterationNumber);

            if (playerRow == enemyRow && playerCol == enemyCol){
                break;
            }
        }
        System.out.println("Játék vége");
    }
    static void initLevel(String[][] level) {
        for(int row = 0; row < level.length; row++) {
            for(int col = 0; col < level[row].length; col++) {
                if(row == 0 || row == HEIGHT -1 || col == 0 || col == WIDTH -1) {
                    level[row][col] = "X";
                } else {
                    level[row][col] = " ";
                }
            }
        }
    }
    static Direction changeDirection(Direction direction) {
        switch (direction) {
            case RIGHT: return Direction.DOWN;
            case DOWN: return Direction.LEFT;
            case LEFT: return Direction.UP;
            case UP: return Direction.RIGHT;
        }
        return direction;
    }
    static Direction changeEnemyDirection(String [][] level, Direction originalEnemyDirection, int playerRow, int playerCol, int enemyRow, int enemyCol) {
        if (playerRow > enemyRow && level[playerRow+1][playerCol].equals(" ")) {
            return Direction.DOWN;
        }
        if (playerRow < enemyRow && level[playerRow-1][playerCol].equals(" ")) {
            return Direction.UP;
        }
        if (playerCol > enemyCol && level[playerRow][playerCol+1].equals(" ")) {
            return Direction.RIGHT;
        }
        if (playerCol < enemyCol && level[playerRow][playerCol-1].equals(" ")) {
            return Direction.LEFT;
        }
        return originalEnemyDirection;
    }
    static int[] makeMove(Direction direction, String[][] level, int row, int col){
        switch (direction) {
            case UP:
                if(level[row-1][col].equals(" ")) {
                    row--;
                }
                break;
            case DOWN:
                if(level[row+1][col].equals(" ")) {
                    row++;
                }
                break;
            case LEFT:
                if(level[row][col-1].equals(" ")) {
                    col--;
                }
                break;
            case RIGHT:
                if(level[row][col+1].equals(" ")) {
                    col++;
                }
                break;
        }
        return new int[]{ row, col };
    }
    static void draw(String[][] level, String playerMark, int playerRow, int playerCol, String enemyMark, int enemyRow, int enemyCol) {
        //pálya és játékos kirajzolása
        for(int row = 0; row < HEIGHT; row++) {
            for(int col = 0; col < WIDTH; col++) {
                if(row == playerRow && col == playerCol) {
                    System.out.print(playerMark);
                } else if (row == enemyRow && col == enemyCol) {
                    System.out.print(enemyMark);
                } else {
                    System.out.print(level[row][col]);
                }
            }
            System.out.println();
        }
    }
    static void addSomeDelay(long timeout, int iterationNumber) throws InterruptedException {
        System.out.println("--------  " + iterationNumber);
        Thread.sleep(timeout);
    }

    static void addObstacles(String[][] level, int numberOfHorizontalWall, int numberOfVerticalWall){
        for (int i = 0; i < numberOfHorizontalWall; i++){
            addHorizontalWall(level);
        }
        for (int i = 0; i < numberOfVerticalWall; i++){
            addVerticalWall(level);
        }
    }

    static void addHorizontalWall(String[][] level){
        int wallWidth = RANDOM.nextInt(WIDTH - 3);
        int wallRow = RANDOM.nextInt(HEIGHT - 2) + 1;
        int wallCol = RANDOM.nextInt(WIDTH - 2 - wallWidth);
        for (int i = 0; i < wallWidth; i++){
            level[wallRow][wallCol + i] = "X";
        }
    }

    static void addVerticalWall(String[][] level){
        int wallHeight = RANDOM.nextInt(HEIGHT - 3);
        int wallRow = RANDOM.nextInt(HEIGHT - 2 - wallHeight);
        int wallCol = RANDOM.nextInt(WIDTH - 2) + 1;
        for (int i = 0; i < wallHeight; i++){
            level[wallRow + i][wallCol] = "X";
        }
    }
}