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
import java.util.Stack;

public class DFS_Solver extends Solver
{
	/*
	 * Constructor
	 * m: The maze to solve
	 */
	public DFS_Solver(Maze m)
	{
		this.maze = m;
		this.result = "";
		this.frontier = new Stack<Node<Maze>>();
		this.closedSquares = new Stack<Square>();
	}

	public String solve()
	{
		Boolean endfound = false;
		this.nodesCounter = 0;
		this.pathLength = 0;

		//Init maze
		this.closedSquares.clear(); //stack for the visited ones aka explored set
		this.maze.initMaze();

		//Init frontier
		this.frontier.clear(); //frontier is fringe (stack)
		((Stack<Node<Maze>>) this.frontier).push(new Node<Maze>(this.maze)); //Add first state

		//Measure run time
		long startTime = System.currentTimeMillis();

		//Search
		while(!endfound)
		{
			//you should check if there exist a node to expand
			if(this.frontier.isEmpty())
				break;

			else
			{
				//You should first pop it from the stack (you visit it)
				Node<Maze> current = ((Stack<Node<Maze>>) this.frontier).pop(); //Get first node from the frontier
				this.maze = (Maze) current.getContent(); //Get maze from the node
				Square currState = this.maze.getCurrState(); //Get current state from the maze

				//checking if we have found the solution
				if(currState.getLine() == this.maze.getEnd().getLine() && currState.getCol() == this.maze.getEnd().getCol())
				{
					//You should create a new Node<T> for this maze to be able to assign a new father to it
					//Set current as father (parent) for all next states
					//push the goal (end) so you can reach starting point by using fathers (parents)
					endfound = true;
				}
				else
				{
					//I explain how it is work in pdf file

					this.closedSquares.add(currState);
					LinkedList<Node<Maze>> nexts = this.getNextSquares(); //Get next possible states
					int i =0;
					while(i < nexts.size())
					{
						Node<Maze> nextNode = nexts.get(i);
						nextNode.setFather(current);
						((Stack<Node<Maze>>) this.frontier).push(nextNode);
						this.nodesCounter++;
						i++;
					}
				}
			}
		}


		long endTime = System.currentTimeMillis();

		long time = endTime - startTime;

		this.result = "    ____             __  __       _______           __     _____                      __  \r\n" +
				"   / __ \\___  ____  / /_/ /_     / ____(_)_________/ /_   / ___/___  ____ ___________/ /_ \r\n" +
				"  / / / / _ \\/ __ \\/ __/ __ \\   / /_  / / ___/ ___/ __/   \\__ \\/ _ \\/ __ `/ ___/ ___/ __ \\\r\n" +
				" / /_/ /  __/ /_/ / /_/ / / /  / __/ / / /  (__  ) /_    ___/ /  __/ /_/ / /  / /__/ / / /\r\n" +
				"/_____/\\___/ .___/\\__/_/ /_/  /_/   /_/_/  /____/\\__/   /____/\\___/\\__,_/_/   \\___/_/ /_/ \r\n" +
				"          /_/                                                                             \n";
		//You should add the result to the String variable "result"
		//which is going to be printed to a text file
		if(endfound)
		{
			//give path information first
			this.maze.resetGrid();
			Node<Maze> revertedTree = ((Stack<Node<Maze>>) this.frontier).pop();

			revertedTree = revertedTree.getFather().getFather();
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
			return "No resolution computed, please use DFS_Solver.solve() first";
		else
			return this.result;
	}

	public AbstractCollection<Node<Maze>> getFrontier()
	{
		return this.frontier;
	}
}
