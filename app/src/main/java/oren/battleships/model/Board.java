package oren.battleships.model;

import com.google.gson.annotations.SerializedName;

public class Board {

    private String boardOwner;
    private String boardSender;
    private int boardRoom;
    public enum BoardStateEnum {EMPTY,PREPARATION, READY, NO_MORE_SHIPS}
    private BoardStateEnum state;
    @SerializedName("cells")
    private Cell[][] cells;
    @SerializedName("ship_desired_length")
    private int [] ship_desired_length = {3,2,1};
    @SerializedName("ships_current_length")
    private int [] ships_current_length = {0,0,0};
    private int size;

    public String getBoardOwner() {
        return boardOwner;
    }

    public void setBoardOwner(String boardName) {
        this.boardOwner = boardName;
    }

    public String getBoardSender() {
        return boardSender;
    }

    public void setBoardSender(String boardSender) {
        this.boardSender = boardSender;
    }

    public int getBoardRoom() {
        return this.boardRoom;
    }

    public void setBoardRoom(int room) {
        this.boardRoom = room;
    }

    public Board(int n,String boardOwner)
    {
        this.boardOwner = boardOwner;
        this.boardRoom = -1;
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

    public void resetBoard(String owner) {
        boardOwner = owner;
        boardRoom = -1;
        state = BoardStateEnum.EMPTY;
        for (int i=0; i<size; i++)
        {
            for (int j=0; j<size; j++)
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

    public int[] getPreparationState() {
        return ships_current_length;
    }

}
