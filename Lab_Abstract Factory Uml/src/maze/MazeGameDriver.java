package maze;

import java.util.Scanner;

import maze.ui.MazeViewer;

public class MazeGameDriver {
    public MazeGameDriver(String filePath) {
    }

    public static Maze loadMaze(String path, MazeFactory factory) {
        return factory.loadMaze(path);
    }

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        System.out.println("Please choose the type of maze you want: red or blue");
        String choice = in.nextLine().toLowerCase().trim();
        in.close();

        MazeFactory factory;

        switch (choice) {
            case "red":
                factory = new RedMazeFactory();
                break;
            case "blue":
                factory = new BlueMazeFactory();
                break;
            default:
                System.out.println("Invalid choice, defaulting to red.");
                factory = new RedMazeFactory();
                break;
        }

        // Try large.maze first, fall back to small.maze
        Maze maze = loadMaze("large.maze", factory);
        if (maze.getNumberOfRooms() == 0) {
            System.out.println("Could not load large.maze, trying small.maze...");
            maze = loadMaze("small.maze", factory);
        }

        MazeViewer viewer = new MazeViewer(maze);
        viewer.run();
    }
}