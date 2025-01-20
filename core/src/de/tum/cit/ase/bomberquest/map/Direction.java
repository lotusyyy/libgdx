package de.tum.cit.ase.bomberquest.map;

public enum Direction {
    UP(0, 1),    // 向上
    DOWN(0, -1), // 向下
    LEFT(-1, 0), // 向左
    RIGHT(1, 0); // 向右

    private final int offsetX;
    private final int offsetY;

    Direction(int offsetX, int offsetY) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }
    public int getOffsetX() {
        return offsetX;
    }
    public int getOffsetY() {
        return offsetY;
    }
}
