package server;

import java.sql.Struct;
import java.util.Random;

public class Controller {
    private final int fieldSize = 10;
    private char[][] firstPlayerMyField;
    private char[][] secondPlayerMyField;
    private char[][] firstPlayerShootField;
    private char[][] secondPlayerShootField;

    private final char EMPTY = '*';
    private final char DEAD = 'X';
    private final char SHIP = 'K';
    private final char MISS = 'M';

    private final int countSingleShips = 10;

    private int aliveFirstPlayer;
    private int killFirstPlayer;
    private int aliveSecondPlayer;
    private int killSecondPlayer;

    private int currentStep;

    private final Random random = new Random();

    public final String WIN_FIRST_PLAYER = "First player wins";
    public final String WIN_SECOND_PLAYER = "Second player wins";
    public final String CONTINUE_GAME = "Continue game";

    public Controller() {
        firstPlayerMyField = new char[fieldSize][fieldSize];
        secondPlayerMyField = new char[fieldSize][fieldSize];
        firstPlayerShootField = new char[fieldSize][fieldSize];
        secondPlayerShootField = new char[fieldSize][fieldSize];

        ClearField(firstPlayerMyField);
        ClearField(firstPlayerShootField);
        ClearField(secondPlayerMyField);
        ClearField(secondPlayerShootField);

        PlaceRandomShips(firstPlayerMyField);
        PlaceRandomShips(secondPlayerMyField);

        killFirstPlayer = 0;
        killSecondPlayer = 0;
        aliveFirstPlayer = countSingleShips;
        aliveSecondPlayer = countSingleShips;

        currentStep=1;
    }

    public int GetCurrentStep(){
        return currentStep;
    }

    private void ClearField(char[][] field) {
        for (int i = 0; i < fieldSize; i++) {
            for (int j = 0; j < fieldSize; j++) {
                field[i][j] = EMPTY;
            }
        }
    }

    private void PlaceRandomShips(char[][] field) {
        for (int k = 0; k < countSingleShips; k++) {
            int i, j;

            do {
                i = random.nextInt(fieldSize);
                j = random.nextInt(fieldSize);
            } while (field[i][j] != EMPTY);

            field[i][j] = SHIP;
        }
    }

    private int ShootToField(int i, int j, char[][] myField, char[][] shootField) {
        if (i < 0 || i > fieldSize - 1 || j < 0 || j > fieldSize - 1) {
            return 0;
        }
        if (myField[i][j] == SHIP || myField[i][j] == MISS) {
            return 0;
        }

        if (myField[i][j] == SHIP) {
            myField[i][j] = DEAD;
            shootField[i][j] = DEAD;
            return 1;
        }
        if (myField[i][j] == EMPTY) {
            myField[i][j] = MISS;
            shootField[i][j] = MISS;
            return 2;
        }

        return -1;
    }

    public boolean FirstPlayerShootToSecondPlayer(int i, int j){
        int result = ShootToField(i,j,secondPlayerMyField,firstPlayerShootField);

        if(result == 0)
        {
            return false;
        }
        if(result == 1)
        {
            killFirstPlayer++;
            aliveSecondPlayer--;

            currentStep++;

            return true;
        }
        if(result == 2)
        {
            currentStep++;
            return true;
        }

        return false;
    }

    public boolean SecondPlayerShootToFirstPlayer(int i, int j){
        int result = ShootToField(i,j,firstPlayerMyField,secondPlayerShootField);
        if(result == 0)
        {
            return false;
        }
        if(result == 1)
        {
            killSecondPlayer++;
            aliveFirstPlayer--;
            currentStep++;
            return true;
        }
        if(result == 2)
        {
            currentStep++;
            return true;
        }

        return false;
    }

    private String GetFieldInString(char[][] field) {
        String output="";

        for (int i = 0; i < fieldSize; i++) {
            for (int j = 0; j < fieldSize; j++) {
                output += field[i][j];
            }
            output += "\n";

        }
        return output;
    }

    public String GetFirstPlayerMyField()
    {
        return GetFieldInString(firstPlayerMyField);
    }

    public String GetFirstPlayerShootField()
    {
        return GetFieldInString(firstPlayerShootField);
    }

    public String GetSecondPlayerMyField()
    {
        return GetFieldInString(secondPlayerMyField);
    }

    public String GetSecondPlayerShootField()
    {
        return GetFieldInString(secondPlayerShootField);
    }

    public String GetGameResult()
    {
        if (aliveSecondPlayer == 0) {
            return WIN_FIRST_PLAYER;
        }

        if (aliveFirstPlayer == 0) {
            return WIN_SECOND_PLAYER;
        }

        return CONTINUE_GAME;
    }

}
