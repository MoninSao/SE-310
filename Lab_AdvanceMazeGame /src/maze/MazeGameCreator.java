
/*
 * SimpleMazeGame.java
 * Copyright (c) 2008, Drexel University.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *     * Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *     * Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *     * Neither the name of the Drexel University nor the
 *       names of its contributors may be used to endorse or promote products
 *       derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY DREXEL UNIVERSITY ``AS IS'' AND ANY
 * EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL DREXEL UNIVERSITY BE LIABLE FOR ANY
 * DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package maze;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;

import maze.ui.MazeViewer;

/**
 * 
 * @author Sunny
 * @version 1.0
 * @since 1.0
 */
public abstract class MazeGameCreator {

	// Create 2 room instances first
	public static Room room0 = new Room(0);
	public static Room room1 = new Room(1);

	// Create a door between room1 and room0
	public static Door door1 = new Door(room1, room0);

	/**
	 * Creates a small maze.
	 */
	public static Maze createMaze() {

		// Create a new maze
		Maze maze = new Maze();
		// Create a 2 room maze
		maze.addRoom(room0);
		maze.addRoom(room1);
		System.out.println("number of rooms in the maze: " + maze.getNumberOfRooms());

		// a. you must set the current room number
		maze.setCurrentRoom(room0);
		System.out.println("The curretn room you are in " + maze.getCurrentRoom().toString());

		// b. Rooms must be completed with walls and doors

		// Room 0 wall and doors
		room0.setSide(Direction.North, new Wall());
		room0.setSide(Direction.South, new Wall());
		room0.setSide(Direction.East, new Wall());
		room0.setSide(Direction.West, door1); // will connect to room 2
		// Room 1 wall and doors
		room1.setSide(Direction.North, new Wall());
		room1.setSide(Direction.South, new Wall());
		room1.setSide(Direction.East, door1); // will connect to room 2
		room1.setSide(Direction.West, new Wall());

		return maze;

	}

	public static Maze loadMaze(final String path) {
		Maze maze = new Maze();
		HashMap<Integer, Room> rooms = new HashMap<>();
		HashMap<String, Door> doors = new HashMap<>();

		try {
			// Read all lines from the file into a list
			Scanner infile = new Scanner(new File(path));
			ArrayList<String[]> lines = new ArrayList<>();
			while (infile.hasNextLine()) {
				String line = infile.nextLine().trim();
				if (!line.isEmpty()) {
					lines.add(line.split("\\s+"));
				}
			}
			infile.close();

			// Create all Room objects
			for (String[] tokens : lines) {
				if (tokens[0].equals("room")) {
					int num = Integer.parseInt(tokens[1]);
					rooms.put(num, new Room(num));
				}
			}

			// Create Door objects from "door" lines
			for (String[] tokens : lines) {
				if (tokens[0].equals("door")) {
					Room r1 = rooms.get(Integer.parseInt(tokens[2]));
					Room r2 = rooms.get(Integer.parseInt(tokens[3]));
					Door d = new Door(r1, r2);
					d.setOpen(tokens[4].equalsIgnoreCase("open"));
					doors.put(tokens[1], d);
				}
			}

			// Create any doors referenced in rooms but missing a "door" line
			for (String[] tokens : lines) {
				if (tokens[0].equals("room")) {
					for (int i = 2; i <= 5; i++) {
						String side = tokens[i];
						if (side.startsWith("d") && !doors.containsKey(side)) {
							// Find both rooms that share this door
							Room r1 = null, r2 = null;
							for (String[] other : lines) {
								if (other[0].equals("room")) {
									for (int j = 2; j <= 5; j++) {
										if (other[j].equals(side)) {
											if (r1 == null)
												r1 = rooms.get(Integer.parseInt(other[1]));
											else
												r2 = rooms.get(Integer.parseInt(other[1]));
										}
									}
								}
							}
							if (r2 == null)
								r2 = r1;
							doors.put(side, new Door(r1, r2));
						}
					}
				}
			}

			// Set the four sides of each room (North, South, East, West)
			Direction[] dirs = { Direction.North, Direction.South, Direction.East, Direction.West };
			for (String[] tokens : lines) {
				if (tokens[0].equals("room")) {
					Room room = rooms.get(Integer.parseInt(tokens[1]));
					for (int i = 0; i < 4; i++) {
						String side = tokens[i + 2];
						if (side.equals("wall")) {
							room.setSide(dirs[i], new Wall());
						} else if (side.startsWith("d")) {
							room.setSide(dirs[i], doors.get(side));
						} else {
							room.setSide(dirs[i], rooms.get(Integer.parseInt(side)));
						}
					}
				}
			}

			// Add all rooms to the maze and start in room 0
			for (Room r : rooms.values()) {
				maze.addRoom(r);
			}
			maze.setCurrentRoom(rooms.get(0));

		} catch (Exception e) {
			System.err.println("Error loading maze: " + e.getMessage());
		}

		return maze;
	}

	public abstract Wall makeWall();

	public abstract Door makeDoor();

	public abstract Room makeRoom();

	public static void main(String[] args) {
		Maze maze = loadMaze("large.maze");
		MazeViewer viewer = new MazeViewer(maze);
		viewer.run();
	}
}
