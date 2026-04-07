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

import maze.ui.MazeViewer;

/**
 * 
 * @author Sunny
 * @version 1.0
 * @since 1.0
 */
public class SimpleMazeGame {

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
		System.out.println("Please load a maze from the file!");
		return maze;
	}

	public static void main(String[] args) {
		Maze maze = createMaze();
		MazeViewer viewer = new MazeViewer(maze);
		viewer.run();
	}
}
