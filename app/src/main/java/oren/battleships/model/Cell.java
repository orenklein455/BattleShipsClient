package oren.battleships.model;

import com.google.gson.annotations.SerializedName;

public class Cell {

    public enum StateEnum {EMPTY, SHIP_PART, BOMBED, BOMBED_SHIP_PART}
    @SerializedName("state")
    private StateEnum state;

    public Cell(StateEnum state) {
        this.setState(state);
    }

    public StateEnum getState() {
        return state;
    }

    public void setState(StateEnum state) {
        this.state = state;
    }



}
