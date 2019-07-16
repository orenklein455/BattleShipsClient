package oren.battleships.model;

import android.graphics.Color;

public class Ship {
    private Color color;
    private int size;
    private Position[] parts;

    public Color getColor() {
        return color;
    }

    public void setColor(Color color) {
        this.color = color;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public Position[] getParts() {
        return parts;
    }

    public void setParts(Position[] parts) {
        this.parts = parts;
    }
}
