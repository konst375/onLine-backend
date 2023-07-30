package com.chirko.onLine.entities.enums;

public enum InterestPoints {
    LIKE_POINTS(1),
    UNLIKE_POINTS(-1),
    SHARE_POINTS(2),
    COMMENT_POINTS(3),
    UNCOMMENT_POINTS(-3),
    FRIENDSHIP_POINTS(1);

    private final int points;

    InterestPoints(int points) {
        this.points = points;
    }

    public int getPoints() {
        return points;
    }
}
