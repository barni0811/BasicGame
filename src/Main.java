import java.util.Random;

public class Main {

    static final int GAME_LOOP_NUMBER = 1000;
    static final int HEIGHT = 20;
    static final int WIDTH = 80;
    static final Random RANDOM = new Random();

    public static void main(String[] args) throws InterruptedException {

        //pálya inicializálása
        String[][] level = new String[HEIGHT][WIDTH];
        initLevel(level);
        addObstacles(level, 3, 2);

        //Játékos kezdési adatok
        String playerMark = "O";
        int [] playerStartingCoordinates = getRandomStartingCoordinates(level);
        int playerRow = playerStartingCoordinates[0];
        int playerCol = playerStartingCoordinates[1];
        Direction playerDirection = Direction.RIGHT;

        //Ellenfél kezdési adatok
        String enemyMark = "-";
        int [] enemyStartingCoordinates = getRandomStartingCoordinatesForDistance(level, playerStartingCoordinates, 10);
        int enemyRow = enemyStartingCoordinates[0];
        int enemyCol = enemyStartingCoordinates[1];
        Direction enemyDirection = Direction.LEFT;

        //powerUP kezdési adatok
        String powerUPMark = "*";
        int [] powerUPStartingCoordinates = getRandomStartingCoordinates(level);
        int powerUPRow = powerUPStartingCoordinates[0];
        int powerUPCol = powerUPStartingCoordinates[1];
        boolean powerUPPresentOnLevel = false;
        boolean powerUPActive = false;
        int powerUPPresenceCounter = 0;
        int powerUPActiveCounter = 0;

        GameResult gameResult = GameResult.TIE;

        //Körönkénti tevékenységek
        for (int iterationNumber = 1; iterationNumber <= GAME_LOOP_NUMBER; iterationNumber++){

            //Játékos léptetése
            if (powerUPActive) {
                playerDirection = changeDirectionTowards(level, playerDirection, playerRow, playerCol, enemyRow, enemyCol);
            } else {
                if(powerUPPresentOnLevel) {
                    playerDirection = changeDirectionTowards(level, playerDirection, playerRow, playerCol, powerUPRow, powerUPCol);
                } else {
                    if(iterationNumber % 15 == 0) {
                        playerDirection = changeDirection(playerDirection);
                    }
                }
            }

            int[] playerCoordinates = makeMove(playerDirection, level, playerRow, playerCol);
            playerRow = playerCoordinates[0];
            playerCol = playerCoordinates[1];

            //Ellenfél léptetése
            if (powerUPActive) {
                Direction directionTowardsPlayer = changeDirectionTowards(level, enemyDirection, enemyRow, enemyCol, playerRow, playerCol);
                enemyDirection = getEscapeDirection(level, enemyRow, enemyCol, directionTowardsPlayer);
            }else {
                enemyDirection = changeDirectionTowards(level, enemyDirection, enemyRow, enemyCol, playerRow, playerCol);
            }

            if (iterationNumber % 2 == 0) {
                int[] enemyCoordinates = makeMove(enemyDirection, level, enemyRow, enemyCol);
                enemyRow = enemyCoordinates[0];
                enemyCol = enemyCoordinates[1];
            }

            //powerUP számlálók
            if (powerUPActive) {
                powerUPActiveCounter++;
            } else {
                powerUPPresenceCounter++;
            }

            //powerUP elhelyezése a pályán
            if (powerUPPresenceCounter >= 20){
                if (!powerUPPresentOnLevel) {
                    powerUPStartingCoordinates = getRandomStartingCoordinates(level);
                    powerUPRow = powerUPStartingCoordinates[0];
                    powerUPCol = powerUPStartingCoordinates[1];
                }
                powerUPPresentOnLevel = !powerUPPresentOnLevel;
                powerUPPresenceCounter = 0;
            }

            //powerUP időtartama
            if (powerUPActiveCounter >= 20) {
                powerUPActive = false;
                powerUPActiveCounter = 0;
                powerUPStartingCoordinates = getRandomStartingCoordinates(level);
                powerUPRow = powerUPStartingCoordinates[0];
                powerUPCol = powerUPStartingCoordinates[1];
            }

            //powerUP felvétele
            if (powerUPPresentOnLevel && (playerRow == powerUPRow) && (playerCol == powerUPCol)){
                powerUPActive = true;
                powerUPPresentOnLevel = false;
                powerUPPresenceCounter = 0;
            }

            draw(level, playerMark, playerRow, playerCol, enemyMark, enemyRow, enemyCol, powerUPMark, powerUPRow, powerUPCol, powerUPPresentOnLevel, powerUPActive);
            addSomeDelay(300L, iterationNumber);

            //Játék kimenete
            if (playerRow == enemyRow && playerCol == enemyCol){
                if (powerUPActive) {
                    gameResult = GameResult.WIN;
                } else {
                    gameResult = GameResult.LOSE;
                }
                break;
            }
        }
        switch (gameResult) {
            case WIN -> System.out.println("GYŐZTÉL");
            case LOSE -> System.out.println("VESZTETTÉL");
            case TIE -> System.out.println("DÖNTETLEN");
        }
    }

    static Direction getEscapeDirection(String[][] level, int enemyRow, int enemyCol, Direction directionTowardsPlayer) {
        Direction escapeDirection = getOppositeDirection(directionTowardsPlayer);
        switch(escapeDirection) {
            case UP:
                if (level[enemyRow-1][enemyCol].equals(" ")) {
                    return Direction.UP;
                } else if (level[enemyRow][enemyCol-1].equals(" ")) {
                    return Direction.LEFT;
                } else if (level[enemyRow][enemyCol+1].equals(" ")) {
                    return Direction.RIGHT;
                } else {
                    return Direction.UP;
                }
            case DOWN:
                if (level[enemyRow+1][enemyCol].equals(" ")) {
                    return Direction.DOWN;
                } else if (level[enemyRow][enemyCol-1].equals(" ")) {
                    return Direction.LEFT;
                } else if (level[enemyRow][enemyCol+1].equals(" ")) {
                    return Direction.RIGHT;
                } else {
                    return Direction.DOWN;
                }
            case RIGHT:
                if (level[enemyRow][enemyCol-1].equals(" ")) {
                    return Direction.LEFT;
                } else if (level[enemyRow-1][enemyCol].equals(" ")) {
                    return Direction.UP;
                } else if (level[enemyRow+1][enemyCol].equals(" ")) {
                    return Direction.DOWN;
                } else {
                    return Direction.LEFT;
                }
            case LEFT:
                if (level[enemyRow][enemyCol+1].equals(" ")) {
                    return Direction.RIGHT;
                } else if (level[enemyRow-1][enemyCol].equals(" ")) {
                    return Direction.UP;
                } else if (level[enemyRow+1][enemyCol].equals(" ")) {
                    return Direction.DOWN;
                } else {
                    return Direction.RIGHT;
                }
            default:
                return escapeDirection;
        }
    }

    static Direction getOppositeDirection(Direction direction) {
        return switch (direction) {
            case UP -> Direction.DOWN;
            case DOWN -> Direction.UP;
            case LEFT -> Direction.RIGHT;
            case RIGHT -> Direction.LEFT;
        };
    }

    static int[] getRandomStartingCoordinatesForDistance(String[][] level, int[] playerStartingCoordinates, int distance) {
        int playerStartingRow = playerStartingCoordinates[0];
        int playerStartingCol = playerStartingCoordinates[1];
        int randomRow;
        int randomCol;
        int counter = 0;
        do {
            randomRow = RANDOM.nextInt(HEIGHT);
            randomCol = RANDOM.nextInt(WIDTH);
        }while (counter++ < 1000 && (!level[randomRow][randomCol].equals(" ") ||
                calculateDistance(playerStartingRow, playerStartingCol, randomRow, randomCol) < distance));
        return new int[] { randomRow, randomCol };
    }

    static int calculateDistance(int row1, int col1, int row2, int col2) {
        int rowDifference = Math.abs(row1-row2);
        int colDifference = Math.abs(col1-col2);
        return rowDifference + colDifference;
    }

    static int[] getRandomStartingCoordinates(String[][] level) {
        int randomRow;
        int randomCol;
        do {
            randomRow = RANDOM.nextInt(HEIGHT);
            randomCol = RANDOM.nextInt(WIDTH);
        }while (!level[randomRow][randomCol].equals(" "));
        return new int[] { randomRow, randomCol };
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
        return switch (direction) {
            case RIGHT -> Direction.DOWN;
            case DOWN -> Direction.LEFT;
            case LEFT -> Direction.UP;
            case UP -> Direction.RIGHT;
        };
    }
    static Direction changeDirectionTowards(String [][] level, Direction originalEnemyDirection, int enemyRow, int enemyCol , int playerRow, int playerCol) {
        if (playerRow < enemyRow && level[enemyRow-1][enemyCol].equals(" ")) {
            return Direction.UP;
        }
        if (playerRow > enemyRow && level[enemyRow+1][enemyCol].equals(" ")) {
            return Direction.DOWN;
        }
        if (playerCol < enemyCol && level[enemyRow][enemyCol-1].equals(" ")) {
            return Direction.LEFT;
        }
        if (playerCol > enemyCol && level[enemyRow][enemyCol+1].equals(" ")) {
            return Direction.RIGHT;
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
    static void draw(String[][] level, String playerMark, int playerRow, int playerCol, String enemyMark, int enemyRow, int enemyCol, String powerUPMark, int powerUPRow, int powerUPCol,boolean powerUPPresentOnLevel, boolean powerUPActive) {
        //pálya és játékos kirajzolása
        for(int row = 0; row < HEIGHT; row++) {
            for(int col = 0; col < WIDTH; col++) {
                if(row == playerRow && col == playerCol) {
                    System.out.print(playerMark);
                } else if (row == enemyRow && col == enemyCol) {
                    System.out.print(enemyMark);
                } else if (powerUPPresentOnLevel && row == powerUPRow && col == powerUPCol) {
                    System.out.print(powerUPMark);
                } else {
                    System.out.print(level[row][col]);
                }
            }
            System.out.println();
        }
        if (powerUPActive) {
            System.out.println("PowerUP Active");
        }
    }
    static void addSomeDelay(long timeout, int iterationNumber) throws InterruptedException {
        System.out.println("-------- " + iterationNumber);
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