import java.util.ArrayList;
import java.util.Collections;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Stack;

import javafx.application.Application;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Benito Kestelman
 * ID 109292160
 * Homework #3
 * CSE 214: R06
 * Recitation TA: Kevin Flyangolts
 * Grading TA: Zheyuan Gao
 * 
 * <code>Stackotaire</code> is a new version of the classic game Solitaire, 
 * implementing Stack data structures to hold <code>Card</code> objects in 
 * <code>CardStacks</code>. It allows the player to move the cards by clicking
 * to select a card and clicking another card to move it, and also includes an
 * Undo option, automoving, AI, and text commands for moving Cards 
 *   
 * @author benito.kestelman@stonybrook.edu
 */
public class Stackotaire extends Application {
	private static CardStack deck;
	private static CardStack[] tableaus;
	private static CardStack[] foundations;
	private static CardStack stock;
	private static CardStack waste;
	//private static CardStack temp; //temp should only be used immediately after assigning it a CardStack
	private static Stack<String> movesList; //keeps a list of all legal moves performed, including automoves
	
	public static final int TABLEAUS = 7;
	public static final int FOUNDATIONS = 4;
	
	private static int traceCounter;
	
	private static Label stockSize;

	public static void main(String[] args) throws InvalidTypeException
	{
		stockSize = new Label();
		movesList = new Stack<String>();
		traceCounter = 0;
		launch(args);
	}
	
	public void start(Stage primaryStage)
	{
		int i, j;
		
		//set up layout
		HBox root = new HBox();
		VBox gameVB = new VBox();
		VBox menuVB = new VBox();
		HBox topHB = new HBox();
		ArrayList<HBox> tableausHB = new ArrayList<HBox>();
		root.getChildren().addAll(gameVB, menuVB);
		gameVB.getChildren().addAll(topHB);
		topHB.setSpacing(2);
		gameVB.setSpacing(2);
		gameVB.setMinWidth(700);
		Scene mainScene = new Scene(root);
		primaryStage.setScene(mainScene);
		
		//set up menu
		Label hi = new Label("Hi! Click the cards to move or enter a move here. Use the buttons as you please.");
		TextField enterMove = new TextField("Enter Move");
		Button move = new Button("Move!");
		enterMove.setMaxWidth(200);
		Button newGame = new Button("New Game");
		Button printMoves = new Button("Print Moves History");
		Button undo = new Button("Undo");
		Button ai = new Button("Turn on AI");
		menuVB.getChildren().addAll(hi, enterMove, move, newGame, printMoves, undo, ai);
		
		ai.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event)
			{
				boolean movesAvailable = true;
				String lastMove = "";
				movesList.push("");
				int prevStockSize = 53;
				boolean didDrawHelp = true;
				boolean finalTry = false;
				while(prevStockSize != stock.size())
				{
					prevStockSize = stock.size();
					while(!stock.isEmpty() || finalTry)
					{
						lastMove = movesList.peek(); 
						if(didDrawHelp)
						{
							//find useful moves between tableaus
							for(int i = 0; i < TABLEAUS; i++)
							{
								if(!tableaus[i].isEmpty())
								{
									for(int j = 0; j < TABLEAUS; j++)
									{
										if(j != i)
										{
											//prevent the empty stacks king loop
											if(tableaus[j].isEmpty() && tableaus[i].getCardsFaceUp() == tableaus[i].size())
												continue;
											try
											{
												//try to move from a tableau so that a card will open up
												makeMove("moven t" + (i + 1) + " t" + (j + 1) + " " + tableaus[i].getCardsFaceUp() + " ai");
											}
											catch(InvalidCodeException e)
											{
											}
											if(movesList.peek() != lastMove)
											{
												lastMove = movesList.peek();
												i = 0;
												break;
											}
										}
									}
								}
							}
						}
						if(!stock.isEmpty() || !waste.isEmpty())
						{
							//draw
							try
							{
								makeMove("draw");
							}
							catch(InvalidCodeException e)
							{
							}
							lastMove = movesList.peek();
							didDrawHelp = false;
							//try to move from waste to tableaus (automove takes care of waste to foundations)
							for(int i = 0; i < TABLEAUS; i++)
							{
								try 
								{
									makeMove("move w1 t" + (i + 1) + " ai");
								}
								catch(InvalidCodeException e)
								{
								}
								if(lastMove != movesList.peek())
								{
									didDrawHelp = true;
									lastMove = movesList.peek();
									break;
								}
							}
							if(stock.isEmpty())
							{
								try
								{
									makeMove("draw");
									break;
								}
								catch(InvalidCodeException e)
								{
								}
							}
						}
						else if(!finalTry)
						{
							finalTry = true;
						}
					}
				}
			}
		});
		
		undo.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event)
			{
				try
				{
					if(!movesList.empty())
					{
						makeMove(reverseMove(movesList.pop()) + "override");
						movesList.pop(); //pop the undo move from movesList (don't want that hanging around)
					}
				}
				catch(InvalidCodeException e)
				{
					System.out.println(e.getMessage());
				}
			}
		});
		
		printMoves.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event)
			{
				printMovesHistory();
			}
		});
		
		//move button
		move.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) {
				try 
				{
					makeMove(enterMove.getText());
				}
				catch(InvalidCodeException e)
				{
					System.err.println(e.getMessage());
				}
			}
		});
		
		//new game button
		newGame.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event)
			{
				movesList.clear();
				if(Card.getSelectedCard() != null)
					Card.getSelectedCard().setIsSelected(false);
				Label pleaseWait = new Label("Please wait...");
				menuVB.getChildren().add(pleaseWait);
				topHB.getChildren().clear();
				for(int i = 0; i < FOUNDATIONS; i++)
				{
					while(!foundations[i].isEmpty())
						stock.push(foundations[i].pop());
					topHB.getChildren().add(foundations[i].getEmptyCard().getImageView());
				}
				for(int i = 0; i < TABLEAUS; i++)
				{
					while(!tableaus[i].isEmpty())
						stock.push(tableaus[i].pop());
				}
				while(!waste.isEmpty())
				{
					stock.push(waste.pop());
				}
				topHB.getChildren().add(waste.getEmptyCard().getImageView());
				Collections.shuffle(stock);
				for(int i = 0; i < TABLEAUS; i++)
				{
					tableausHB.get(i).getChildren().clear();
					for(int j = 0; j < TABLEAUS - i; j++)
					{
						tableaus[i].push(stock.pop());
						if(j == TABLEAUS - i - 1)
							tableaus[i].peek().setFaceUp(true);
						tableausHB.get(i).getChildren().add(tableaus[i].peek().getImageView());
					}
				}
				stockSize.setText(stock.size() + "");
				topHB.getChildren().addAll(stock.peek().getImageView(), stockSize);
				printAllStacks();
				autoMove("all");
				menuVB.getChildren().remove(pleaseWait);
			}
		});
		
		//create deck and populate with 52 unique cards, facedown
		deck = new CardStack('s'); //deck is not displayed, so has no container
		for(i = 1; i < Card.values.length; i++)
		{
			for(j = 1; j < Card.suits.length; j++)
			{
				try {
					deck.push(new Card(i, j, false));
				}
				catch(ValueOutOfRangeException | InvalidSuitException e)
				{
					System.err.println(e.getMessage());
				}
				//add event handler to every card's ImageView
				//why didn't I think to do it like this sooner... so many hours... so much lost code...
				//CardStack type so useful... greatest idea since fried butter
				Card clickedCard = deck.peek();
				clickedCard.getImageView().addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
					public void handle(MouseEvent event)
					{
						try
						{
						//if the stock is clicked
						if(clickedCard.getStack().getType() == 's')
						{
							makeMove("draw");
							stockSize.setText(stock.size() + "");
						}
						//if a foundation is clicked and a Card is selected
						else if(clickedCard.getStack().getType() == 'f' && Card.getSelectedStack() != null)
						{
							makeMove("move " + Card.getSelectedStack().getType() + "" + Card.getSelectedStack().getStackNum() + " f" + clickedCard.getStack().getStackNum());
						}
						//if a tableau or waste is clicked
						else 
						{
							//if no Card is selected
							if(Card.getSelectedStack() == null && clickedCard.isFaceUp())
								clickedCard.setIsSelected(true);
							//if a Card is already selected
							else if(clickedCard.isTopOfStack())
							{
								if(Card.getSelectedCard().isTopOfStack())
									makeMove("move " + Card.getSelectedStack().getType() + "" + Card.getSelectedStack().getStackNum() + " " + clickedCard.getStack().getType() + "" + clickedCard.getStack().getStackNum());
								else
									makeMove("moven " + 
									  Card.getSelectedStack().getType() + "" + 
									  Card.getSelectedStack().getStackNum() + 
									  " " + clickedCard.getStack().getType() +
									  "" + clickedCard.getStack().getStackNum()
									  + " " + (1 + 
									  Card.getSelectedCard().getDepth()));
							}
							else
								Card.getSelectedCard().setIsSelected(false);
						}
						}
						catch(InvalidCodeException e)
						{
							System.out.println(e.getMessage());
						}
					}
				});
			}
		}
						
		Collections.shuffle(deck);
		
		//distribute initial tableaus
		tableaus = new CardStack[TABLEAUS];
		for(i = 0; i < TABLEAUS; i++)
		{
			HBox h = new HBox();
			tableausHB.add(h);
			tableaus[i] = new CardStack('t', h, i + 1);
			for(j = 0; j < TABLEAUS - i; j++)
			{
				tableaus[i].push(deck.pop());
				if(j == TABLEAUS - i - 1)
					tableaus[i].peek().setFaceUp(true);
				h.getChildren().add(tableaus[i].peek().getImageView());
			}
			gameVB.getChildren().add(h);
		}
		
		//display empty foundations
		foundations = new CardStack[FOUNDATIONS];
		for(i = 0; i < FOUNDATIONS; i++)
		{
			foundations[i] = new CardStack('f', topHB, i + 1);
			topHB.getChildren().add(foundations[i].getEmptyCard().getImageView());
		}
		
		//display waste pile
		waste = new CardStack('w', topHB, 1);
		topHB.getChildren().add(waste.getEmptyCard().getImageView());
		
		//populate and display stock deck
		stock = new CardStack('s', topHB, 1);
		while(!deck.isEmpty())
		{
			stock.push(deck.pop());
		}
		topHB.getChildren().add(stock.peek().getImageView());
		stockSize.setText(stock.size() + "");
		topHB.getChildren().add(stockSize);
		
		printAllStacks();
		//initial automoves
		autoMove("all");
		
		//add functionality to each CardStack's emptyCard
		for(CardStack cs : tableaus)
		{
			cs.getEmptyCard().getImageView().addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
				public void handle(MouseEvent event)
				{
					if(Card.getSelectedStack() == null)
						return;
					try
					{
						if(Card.getSelectedCard().isTopOfStack())
							makeMove("move " + Card.getSelectedStack().getType()
							  + "" + Card.getSelectedStack().getStackNum() + 
							  " t" + cs.getStackNum());
						else
							makeMove("moven t" +
							  Card.getSelectedStack().getStackNum() + " t" + 
							  cs.getStackNum() + " " +
							  (1 + Card.getSelectedCard().getDepth()));
					}
					catch(InvalidCodeException e)
					{
						System.out.println(e.getMessage());
					}
				}
			});
		}
		for(CardStack f : foundations)
		{
			f.getEmptyCard().getImageView().addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
				public void handle(MouseEvent event)
				{
					if(Card.getSelectedStack() == null)
						return;
					else 
					{
						try
						{
							makeMove("move " + Card.getSelectedStack().getType() + "" + Card.getSelectedStack().getStackNum() + " f" + f.getStackNum());
						}
						catch(InvalidCodeException e)
						{
							System.out.println(e.getMessage());
						}
					}
				}
			});
		}
		stock.getEmptyCard().getImageView().addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event)
			{
				try
				{
					makeMove("draw");
				}
				catch(InvalidCodeException e)
				{
					System.out.println(e.getMessage());
				}
				stockSize.setText(stock.size() + "");
			}
		});

		
		primaryStage.show();
	}
	
	/**
	 * makes a move based on the given code String, which is a command followed
	 * by the type of CardStack to move Card(s) from, followed by its stackNum,
	 * followed by a space, followed by the type of CardStack to move Card(s)
	 * to, followed by its stackNum. Deselects whatever Card is selected, 
	 * regardless of what Card(s) are moved.
	 * @param code a String of tokens separated by spaces. First token is a 
	 * command (move, moveN, draw), second and third tokens are stack types
	 * followed by stackNums (T1 F3, w1 t2, etc.), fourth token is the number 
	 * of cards to move when using the moven command. After all tokens, the 
	 * override command may be included to make an illegal move, or to allow 
	 * an undo, and the ai token cancels printing illegal move attempts. All
	 * tokens are case-insensitive.
	 * @throws InvalidCodeException when code contains an unrecognized command,
	 * or is not formatted correctly
	 */
	public static void makeMove(String code) throws InvalidCodeException
	{
		if(Card.getSelectedCard() != null) 
			Card.getSelectedCard().setIsSelected(false);
		if(code == null) 
			throw new InvalidCodeException("No code entered");
		code = code.toLowerCase();
		boolean overRide = code.indexOf("override") > 0;
		boolean ai = code.indexOf("ai") > 0;
		CardStack csa, csb, temp;
		//if moving multiple Cards
		if(code.indexOf("moven") == 0)
		{
			//a and b are the containerIndexes of each CardStack, not the stackNums
			int a = Integer.parseInt(code.substring(7, 8)) - 1;
			int b = Integer.parseInt(code.substring(10, 11)) - 1;
			//the number of Cards to move
			int n = Integer.parseInt(code.substring(12, 13));
			//determine the CardStack to move from
			//multiple Cards can only be moved from a tableau
			if(code.substring(6, 7).equals("t"))
				csa = tableaus[a];
			else
				throw new InvalidCodeException("Illegal Move: " + code);
			//determine the CardStack to move to
			//multiple Cards can only be moved to another tableau
			if(code.substring(9, 10).equals("t"))
				csb = tableaus[b];
			else
				throw new InvalidCodeException("Illegal Move: " + code);
			//can't move from an empty CardStack
			if(csa.isEmpty())
				throw new InvalidCodeException("Empty CardStack selected: " + code);
			//move the Cards
			temp = new CardStack('t');
			for(int i = 0; i < n; i++)
				temp.push(csa.pop());
			if(csb.isEmpty()) 
			{
				if(!validMove(temp.peek(), csb.getEmptyCard()) && !overRide)
				{
					if(!ai)
						System.out.println("Illegal Move: " + code);
					while(!temp.isEmpty())
						csa.push(temp.pop());
					return;
				}
			}
			else if(!validMove(temp.peek(), csb.peek()) && !overRide)
			{
				if(!ai)
					System.out.println("Illegal Move: " + code);
				while(!temp.isEmpty())
					csa.push(temp.pop());
				return;
			}
			while(!temp.isEmpty())
				csb.push(temp.pop());
			csa.display();
			csb.display();
			movesList.push(code);
			System.out.println(movesList.peek());
			printAllStacks();
			autoMove("" + csa.getContainerIndex());
		}
		//if moving one Card
		else if(code.indexOf("move") == 0)
		{
			try
			{
				//a and b are the containerIndexes of each CardStack, not the stackNums
				int a = Integer.parseInt(code.substring(6, 7)) - 1;
				int b = Integer.parseInt(code.substring(9, 10)) - 1;
				//determine the CardStack to move from
				//a Card can be moved from a tableau, waste, or a foundation 
				//(draw is handled separately)
				if(code.substring(5, 6).equals("t"))
					csa = tableaus[a];
				else if(code.substring(5, 6).equals("w"))
					csa = waste;
				else if(code.substring(5, 6).equals("f"))
					csa = foundations[a];
				else
					throw new InvalidCodeException("Illegal Move: " + code);
				//determine the CardStack to move to
				//a Card can be moved to a tableau or to a foundation
				//a Card can also be moved to waste, if using override (such as
				//in the case of an undo)
				if(code.substring(8, 9).equals("t"))
					csb = tableaus[b];
				else if(code.substring(8, 9).equals("f"))
					csb = foundations[b];
				else if(code.substring(8, 9).equals("w") && overRide)
					csb = waste;
				else
					throw new InvalidCodeException("Illegal Move: " + code);
				//can't move from an empty CardStack
				if(csa.isEmpty())
					throw new InvalidCodeException("Empty CardStack selected: " + code);
				//if moving from to the same CardStack, do nothing
				if(csa == csb)
					return;
				//move the Card
				if(!csb.isEmpty())
				{
					if(validMove(csa.peek(), csb.peek()) || overRide)
					{
						csb.push(csa.pop());
						movesList.push(code);
						if(csa.getType() == 't')
							autoMove("" + csa.getContainerIndex());
						System.out.println(movesList.peek());
						csa.display();
						csb.display();
						printAllStacks();
					}
					if(!ai)
						System.out.println("Illegal Move: " + code);
				}
				else 
				{
					if(validMove(csa.peek(), csb.getEmptyCard()) || overRide)
					{
						csb.push(csa.pop());
						movesList.push(code);
						if(csa.getType() == 't')
							autoMove("" + csa.getContainerIndex());
						System.out.println(movesList.peek());
						csa.display();
						csb.display();
						printAllStacks();
					}
					if(!ai)
						System.out.println("Illegal Move");
				}
			}
			catch(StringIndexOutOfBoundsException | NumberFormatException | ArrayIndexOutOfBoundsException e)
			{
				throw new InvalidCodeException("Code Format Error: " + code);
			}
		}
		else if(code.indexOf("draw") == 0)
		{
			if(!stock.isEmpty())
			{
				waste.push(stock.pop());
				waste.peek().setFaceUp(true);
				stock.display();
				waste.display();
				movesList.push(code);
				System.out.println(movesList.peek());
				printAllStacks();
				autoMove("w");
			}
			else
			{
				while(!waste.isEmpty())
					stock.push(waste.pop());
				movesList.push(code);
				waste.display();
				stock.display();
				System.out.println(movesList.peek());
				printAllStacks();
			}
			stockSize.setText(stock.size() + "");
		}
		else if(code.indexOf("undraw") == 0 && overRide)
		{
			if(!waste.isEmpty())
			{
				stock.push(waste.pop());
				waste.display();
				stock.display();
				movesList.push(code);
				System.out.println(movesList.peek());
				printAllStacks();
			}
			else
			{
				while(!stock.isEmpty())
					waste.push(stock.pop());
				stock.display();
				waste.display();
				movesList.push(code);
				System.out.println(movesList.peek());
				printAllStacks();
			}
			stockSize.setText(stock.size() + "");
		}
		else
			throw new InvalidCodeException("Unknown command");
		checkIfVictory();
	}
	
	/**
	 * reverses the command of a given move
	 * @param move the move to reverse
	 * @return the reverse command of move. In most cases this is the same 
	 * command as the one given with its stack tokens flipped (for example, 
	 * the reverse of "move T1 T2" is "move T2 T1". The reverse of "draw" is
	 * "undraw"
	 */
	public static String reverseMove(String move)
	{
		if(move.equals("draw"))
			return "undraw";
		if(move.equals(""))
			return "";
		String[] reverse = move.split(" ");
		String temp = reverse[1];
		reverse[1] = reverse[2];
		reverse[2] = temp;
		temp = "";
		for(int i = 0; i < reverse.length; i++)
			temp += reverse[i] + " ";
		return temp;
	}
	
	/**
	 * prints history of all legal moves made to console
	 */
	public static void printMovesHistory()
	{
		Stack<String> tempMoves = new Stack<String>();
		while(!movesList.empty())
			tempMoves.push(movesList.pop());
		while(!tempMoves.empty())
		{
			movesList.push(tempMoves.pop());
			System.out.println(movesList.peek());
		}
	}
	
	/**
	 * checks if the stalwart player of Stackotaire has finally achieved
	 * deserved victory. Prints a victory message if all cards in tableaus are
	 * face up.
	 */
	public static void checkIfVictory()
	{
		for(int i = 0; i < TABLEAUS; i++)
		{
			if(tableaus[i].getCardsFaceUp() != tableaus[i].size())
				return;
		}
		System.out.println("VICToRRYYYY");
	}
	
	/**
	 * checks if a Card can be legally moved on top of another
	 * @param a the Card being moved
	 * @param b the Card a is being moved to
	 * @return true if move is valid/legal, false else
	 */
	public static boolean validMove(Card a, Card b)
	{
		//if moving to a tableau
		if(b.getStack().getType() == 't')
		{
			if(b.getValue() == 0 && a.getValue() == Card.KING)
			{
				return true;
			}
			else if(b.isRed() != a.isRed() && a.getValue() == b.getValue() - 1)
			{
				return true;
			}
			else
				return false;
		}
		//if moving to a foundation
		else if(b.getStack().getType() == 'f')
		{
			if(b.getValue() == 0 && a.getValue() == Card.ACE)
				return true;
			else if(a.getSuit() == b.getSuit() && a.getValue() == b.getValue() + 1)
				return true;
			else
				return false;
		}
		return false;
	}
	
	/**
	 * prints all CardStacks to the console
	 */
	public static void printAllStacks()
	{
		for(int i = 0; i < FOUNDATIONS; i++)
		{
			foundations[i].printStack();
		}
		System.out.print(" ");
		waste.printStack();
		stock.printStack();
		System.out.println("\n---------------------------------");
		for(int i = 0; i < TABLEAUS; i++)
		{
			System.out.print("T" + (i + 1 ) + " ");
			tableaus[i].printStack();
			System.out.println("");
		}
	}

	/**
	 * performs the appropriate automove (moves an open Card to a foundation
	 * if possible) for a given code. If an automove is made, a check will be
	 * made for any new available automoves
	 * @param code can be "all" to check every tableau and waste for possible
	 * automoves, "w" to only check waste, or an index (as a String) to check a
	 * single tableau (the one with containerIndex == index)
	 */
	public static void autoMove(String code) 
	{
		if(code == "all")
		{
			for(int i = 0; i < FOUNDATIONS; i++)
			{
				if(!waste.isEmpty())
				{
					if(!foundations[i].isEmpty())
					{
						if(validMove(waste.peek(), foundations[i].peek()))
						{
							foundations[i].push(waste.pop());
							waste.display();
							foundations[i].display();
							movesList.push("move w1 f" + (i + 1));
							System.out.println(movesList.peek());
							printAllStacks();
							autoMove("all");
							return;
						}
					}
					else
					{
						if(validMove(waste.peek(), foundations[i].getEmptyCard()))
						{
							foundations[i].push(waste.pop());
							waste.display();
							foundations[i].display();
							movesList.push("move w1 f" + (i + 1));
							System.out.println(movesList.peek());
							printAllStacks();
							autoMove("all");
							return;
						}
					}
				}
				for(int j = 0; j < TABLEAUS; j++)
				{
					if(!tableaus[j].isEmpty())
					{
						if(!foundations[i].isEmpty())
						{
							if(validMove(tableaus[j].peek(), foundations[i].peek()))
							{
								foundations[i].push(tableaus[j].pop());
								tableaus[j].display();
								foundations[i].display();
								movesList.push("move t" + (j + 1) + " f" + (i + 1));
								System.out.println(movesList.peek());
								printAllStacks();
								autoMove("all");
								return;
							}
						}
						else
						{
							if(validMove(tableaus[j].peek(), foundations[i].getEmptyCard()))
							{
								foundations[i].push(tableaus[j].pop());
								tableaus[j].display();
								foundations[i].display();
								movesList.push("move t" + (j + 1) + " f" + (i + 1));
								System.out.println(movesList.peek());
								printAllStacks();
								autoMove("all");
								return;
							}
						}
					}
				}
			}
		}
		else if(code == "w")
		{
			for(int i = 0; i < FOUNDATIONS; i++)
			{
				if(!waste.isEmpty())
				{
					if(!foundations[i].isEmpty())
					{
						if(validMove(waste.peek(), foundations[i].peek()))
						{
							foundations[i].push(waste.pop());
							waste.display();
							foundations[i].display();
							movesList.push("move w1 f" + (i + 1));
							System.out.println(movesList.peek());
							printAllStacks();
							autoMove("all");
							return;
						}
					}
					else
					{
						if(validMove(waste.peek(), foundations[i].getEmptyCard()))
						{
							foundations[i].push(waste.pop());
							waste.display();
							foundations[i].display();
							movesList.push("move w1 f" + (i + 1));
							System.out.println(movesList.peek());
							printAllStacks();
							autoMove("all");
							return;
						}
					}
				}
			}
		}
		else 
		{
			int index = Integer.parseInt(code);
			for(int i = 0; i < FOUNDATIONS; i++)
			{
				if(!tableaus[index].isEmpty())
				{
					if(!foundations[i].isEmpty())
					{
						if(validMove(tableaus[index].peek(), foundations[i].peek()))
						{
							foundations[i].push(tableaus[index].pop());
							tableaus[index].display();
							foundations[i].display();
							movesList.push("move t" + (index + 1) + " f" + (i + 1));
							System.out.println(movesList.peek());
							autoMove("all");
							return;
						}
					}
					else
					{
						if(validMove(tableaus[index].peek(), foundations[i].getEmptyCard()))
						{
							foundations[i].push(tableaus[index].pop());
							tableaus[index].display();
							foundations[i].display();
							movesList.push("move t" + (index + 1) + " f" + (i + 1));
							System.out.println(movesList.peek());
							autoMove("all");
							return;
						}			
					}
				}
			}
		}
	}
}

//bugs: undraw error?, the subtle flip face down after undo bug, undoing at end of game and newgame
