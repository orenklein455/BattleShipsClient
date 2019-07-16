package oren.battleships.model;

public class Cell {
    public Cell(StateEnum state) {
        this.setState(state);
    }

    public StateEnum getState() {
        return state;
    }

    public void setState(StateEnum state) {
        this.state = state;
    }

    public enum StateEnum {EMPTY, SHIP_PART, BOMBED, BOMBED_SHIP_PART}
    private StateEnum state;

}
