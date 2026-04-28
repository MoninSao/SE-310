package maze;

public class RedMazeGameCreator extends MazeGameCreator {
    @Override
    public Wall makeWall() {
        return new RedWall();
    }

    @Override
    public Door makeDoor(Room r1, Room r2) {
        return new BrownDoor(r1, r2);
    }
    // Red maze uses a plain door — you can use BrownDoor or a separate RedDoor

    @Override
    public Room makeRoom(int num) {
        return new PinkRoom(num);
    }
}