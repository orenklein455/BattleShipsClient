package oren.battleships.model;

public class Board {
    private Cell[][] cells;
    public Board(int n)
    {
        setCells(new Cell [n][n]);
        for (int i=0; i<n; i++)
        {
            for (int j=0; j<n; j++)
            {

                getCells()[i][j] = new Cell(Cell.StateEnum.EMPTY);
            }
        }
    }

    public Cell[][] getCells() {
        return cells;
    }

    public void setCells(Cell[][] cells) {
        this.cells = cells;
    }
}
