/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

/**
 *
 * @author 4hm3t
 */
import java.util.AbstractCollection;
import java.util.Iterator;
import java.util.LinkedList;

public class BFS_Solver extends Solver
{
	/*
	 * Constructor
	 * m: Maze to solve
	 */
	public BFS_Solver(Maze m)
	{
		this.maze = m;
		this.result = "";
		this.frontier = new LinkedList<Node<Maze>>();
		this.closedSquares = new LinkedList<Square>();
	}

	public String solve()
	{
		Boolean endfound = false;
		this.nodesCounter = 0;
		this.pathLength = 0;

		//Init maze
		this.closedSquares.clear(); //list for the visited ones aka explored set
		this.maze.initMaze();

		//Init frontier
		this.frontier.clear(); //frontier is fringe (queue implemented as linkedlist)
		this.frontier.add(new Node<Maze>(this.maze)); //Add initial state

		//Measure run time
		long startTime = System.currentTimeMillis();

		//Search
		while(!endfound)
		{
			//you should check if there exist a node to expand
			if(this.frontier.isEmpty()) //Check if the frontier is empty
				break;

			else
			{
				//You should first remove it from the list (you visit it)
				Node<Maze> current = ((LinkedList<Node<Maze>>) this.frontier).removeFirst(); //Get first node from the frontier
				this.maze = (Maze) current.getContent(); //Get maze from the node
				Square currState = this.maze.getCurrState(); //Get current state from the maze

				//checking if we have found the solution
				if(currState.getLine() == this.maze.getEnd().getLine() && currState.getCol() == this.maze.getEnd().getCol())
				{
					//You should create a new Node<T> for this maze to be able to assign a new father to it
					//Set current as father (parent) for all next states
					//add the goal (end) so you can reach starting point by using fathers (parents)
					endfound = true;
				}
				else
				{
					LinkedList<Node<Maze>> nexts = this.getNextSquares();

					//I only implement this if part to the code while I doing it I wasn't getting to
					//Sample outputs because I don't put my while loop in the if part because of that I get
					// 2780 nodes if you want to try you can try this was very upseting problem for me I spend too
					// much time to fix that problem when ı did it DFS is too easy but beginning was too hard
					if (!this.closedSquares.contains(currState)) {
						closedSquares.add(currState); // Push
						currState.setAttribute("*");

						// Add next possible states to the stack
						int i = nexts.size() - 1;
						while (i >= 0) {
							Node<Maze> nextNode = nexts.get(i);
							nextNode.setFather(current);
							frontier.add(nextNode);
							nodesCounter++;
							i--;
						}
					}
				}

			}
		}

		long endTime = System.currentTimeMillis();

		long time = endTime - startTime;

		this.result = "    ____                      ____  __       _______           __     _____                      __  \r\n" +
				"   / __ )________  ____ _____/ / /_/ /_     / ____(_)_________/ /_   / ___/___  ____ ___________/ /_ \r\n" +
				"  / __  / ___/ _ \\/ __ `/ __  / __/ __ \\   / /_  / / ___/ ___/ __/   \\__ \\/ _ \\/ __ `/ ___/ ___/ __ \\\r\n" +
				" / /_/ / /  /  __/ /_/ / /_/ / /_/ / / /  / __/ / / /  (__  ) /_    ___/ /  __/ /_/ / /  / /__/ / / /\r\n" +
				"/_____/_/   \\___/\\__,_/\\__,_/\\__/_/ /_/  /_/   /_/_/  /____/\\__/   /____/\\___/\\__,_/_/   \\___/_/ /_/ \n";

		if(endfound)
		{
			this.maze.resetGrid();
			Node<Maze> revertedTree = ((LinkedList<Node<Maze>>) this.frontier).removeLast();

			revertedTree = revertedTree.getFather();
			this.result += "Path: " + this.maze.getEnd().toString() + "(End) <- ";
			this.pathLength++;

			while(revertedTree.hasFather())
			{
				Maze temp = revertedTree.getContent();
				Square state = temp.getCurrState();

				if(!state.equals(this.maze.getEnd()))
				{
					this.result += state.toString() + " <- ";
					this.maze.getGrid()[state.getLine()][state.getCol()].setAttribute("*");
					this.pathLength++;
				}
				revertedTree = revertedTree.getFather();
			}

			this.result += this.maze.getStart().toString() + "(Start) \n" + "Path length: " + this.pathLength + "\nNumber of nodes created: " + this.nodesCounter + "\nExecution time: " + time/1000d + " seconds\n";
			this.result += this.maze.printMaze();
		}
		else
		{
			this.result += "Failed : Unable to go further and/or end is unreachable.";
		}

		return this.result;
	}

	public LinkedList<Node<Maze>> getNextSquares()
	{
		LinkedList<Node<Maze>> res = new LinkedList<Node<Maze>>();

		//Get 4 next squares
		LinkedList<Maze> nexts = this.maze.getCurrState().getNexts();

		for(int i = 0; i < nexts.size(); i++)
		{
			Square tempSq = nexts.get(i).getCurrState();
			if(!this.closedSquares.contains(tempSq))
			{
				Node<Maze> tempNode = new Node<Maze>(nexts.get(i));
				res.add(tempNode); //Add the state
			}
		}

		return res;
	}

	public String getResult()
	{
		if(result == "")
			return "No resolution computed, please use BFS_Solver.solve() first";
		else
			return this.result;
	}

	public AbstractCollection<Node<Maze>> getFrontier()
	{
		return this.frontier;
	}
}
