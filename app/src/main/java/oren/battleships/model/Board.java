package oren.battleships.model;

import com.google.gson.annotations.SerializedName;

public class Board {

    public enum BoardStateEnum {EMPTY,PREPARATION, READY}
    private BoardStateEnum state;
    @SerializedName("cells")
    private Cell[][] cells;
    @SerializedName("ship_desired_length")
    private int [] ship_desired_length = {3,2,1};
    @SerializedName("ships_current_length")
    private int [] ships_current_length = {0,0,0};

    public Board(int n)
    {
        this.state = BoardStateEnum.EMPTY;
        setCells(new Cell [n][n]);
        for (int i=0; i<n; i++)
        {
            for (int j=0; j<n; j++)
            {
                getCells()[i][j] = new Cell(Cell.StateEnum.EMPTY);
            }
        }
    }
    public int getPreparationNeededShip()
    {
        for (int i=0; i<this.ships_current_length.length; i++)
        {
            if (this.ships_current_length[i]< this.ship_desired_length[i])
                return i;
        }
        return -1;
    }

    public Cell[][] getCells() {
        return cells;
    }

    public void setCells(Cell[][] cells) {
        this.cells = cells;
    }
    public BoardStateEnum getState() {
        return state;
    }

    public void setState(BoardStateEnum state) {
        this.state = state;
    }

    public int[] getShipDesiredLength() {
        return ship_desired_length;
    }

    public void setNumOfShips(int[] numOfShips) {
        this.ship_desired_length = numOfShips;
    }

    public int[] getPreparationState() {
        return ships_current_length;
    }

    public void setPreparationState(int[] preparationState) {
        this.ships_current_length = preparationState;
    }
}
