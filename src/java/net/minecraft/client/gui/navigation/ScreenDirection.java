package net.minecraft.client.gui.navigation;

import it.unimi.dsi.fastutil.ints.IntComparator;

public enum ScreenDirection {
    UP,
    DOWN,
    LEFT,
    RIGHT;

    private final IntComparator coordinateValueComparator = (p_265081_, p_265641_) -> p_265081_ == p_265641_ ? 0 : (this.isBefore(p_265081_, p_265641_) ? -1 : 1);

    public ScreenAxis getAxis() {
        return switch (this) {
            case UP, DOWN -> ScreenAxis.VERTICAL;
            case LEFT, RIGHT -> ScreenAxis.HORIZONTAL;
        };
    }

    public ScreenDirection getOpposite() {
        return switch (this) {
            case UP -> DOWN;
            case DOWN -> UP;
            case LEFT -> RIGHT;
            case RIGHT -> LEFT;
        };
    }

    public boolean isPositive() {
        return switch (this) {
            case UP, LEFT -> false;
            case DOWN, RIGHT -> true;
        };
    }

    public boolean isAfter(int pFirst, int pSecond) {
        return this.isPositive() ? pFirst > pSecond : pSecond > pFirst;
    }

    public boolean isBefore(int pFirst, int pSecond) {
        return this.isPositive() ? pFirst < pSecond : pSecond < pFirst;
    }

    public IntComparator coordinateValueComparator() {
        return this.coordinateValueComparator;
    }
}