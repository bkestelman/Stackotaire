import java.util.ArrayList;
import java.util.Collections;
import java.util.Optional;
import java.util.Stack;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
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

/*
 * Pending Updates:
 * Allow for up to three Cards to be drawn at a time, as in classic Solitaire
 * 
 * Display console output in the game window (a simple Label should do, I
 * just have to clear and update it after every print statement, or figure out
 * how to redirect console output to a Label, which would be super cool).
 * 
 * Optional: push an emptyCard to every CardStack instead of having it as a
 * member, and make isEmpty() return true if size() is 1. This way, it
 * becomes possible to peek() an empty CardStack (the result will be the 
 * emptyCard), so no checks have to be made for this, and the code for the 
 * eventHandlers to emptyCards can probably be merged with that for every other
 * Card. 
 * 
 * Improve the AI. Dur. AI is stupid, even though it occasionally wins. For one
 * thing, add ability to move from foundations at the end if it will help. Also
 * add ability to moven to open up a Card that can be moved to a foundation
 * (currently it only movens if it will allow a facedown Card to flipped up, to
 * save time). Finally, a Card should only be brought down from Waste to a 
 * Tableau if it will help (or if the other Card of same value and color is 
 * already in play, or if both Cards with opposite color and one value higher
 * are face-up on tableaus). Otherwise, the Card is just occupying space that 
 * a to-be-revealed tableau Card could have moved to, thus revealing another 
 * Card. A Card is considered helpful if a tableau Card can be moved to it, or 
 * if there exists a helpful Card in waste or stock (BAM! recursion! or a loop
 * through a saved ArrayList/LinkedList of Cards that would be helpful but 
 * can't be directly played). 
 * 
 * Pause for a quarter second after every automove, and a smaller fraction of a
 * second after every AI move, so the user can see what's going on in real
 * time (these moves are already recorded and displayed in the console). It's 
 * not possible to simply sleep() the JavaFX application thread, however, say
 * after an AI move is displayed but before the next move is made. The result 
 * of inserting a sleep() there is JavaFX goes to sleep for the specified 
 * amount of time without updating the display, moves on to calculate the next
 * AI move, then displays the final result, after all calculating is done. More
 * complicated Thread stuff is required for the functionality I want. 
 * 
 * ANIMATIONS!!!! :D:D:D:D
 * 
 * More Card games. Stackotaire's kinda lame :P
 */
public class Stackotaire extends Application {
	private static CardStack deck;
	private static CardStack[] tableaus;
	private static CardStack[] foundations;
	private static CardStack stock;
	private static CardStack waste;
	private static Stack<String> movesList; //keeps a list of all legal moves 
	//performed, including automoves
	
	public static final int TABLEAUS = 7;
	public static final int FOUNDATIONS = 4;
	
	public static boolean aiUsed, victory, autoMove;
	
	private static Label stockSize;
	private static Button newGame;
	private static Button undo;
	private static Button quit;

	public static void main(String[] args) throws InvalidTypeException
	{
		autoMove = true;
		aiUsed = victory = false;
		stockSize = new Label();
		movesList = new Stack<String>();
		movesList.push("");
		launch(args);
	}
	
	/**
	 * Do everything.
	 */
	public void start(Stage primaryStage)
	{
		//set up layout
		HBox root = new HBox();
		VBox gameVB = new VBox();
		VBox menuVB = new VBox();
		HBox topHB = new HBox();
		ArrayList<HBox> tableausHB = new ArrayList<HBox>();
		root.getChildren().addAll(gameVB, menuVB);
		root.setStyle("-fx-background-color: linear-gradient(blue, red);");
		stockSize.setStyle("-fx-text-fill: yellow;");
		gameVB.getChildren().addAll(topHB);
		topHB.setSpacing(2);
		gameVB.setSpacing(2);
		gameVB.setMinWidth(700);
		Scene mainScene = new Scene(root);
		primaryStage.setScene(mainScene);
		
		//set up menu
		Label hi = new Label("Hi! Click the cards to move or enter a move here."
		  + " Use the buttons as you please.");
		hi.setStyle("-fx-text-fill: gold;");
		Button instructions = new Button("Instructions");
		TextField enterMove = new TextField("Enter Move");
		Button move = new Button("Move!");
		enterMove.setMaxWidth(200);
		newGame = new Button("New Game");
		Button printMoves = new Button("Print Moves History");
		Button startOver = new Button("Start Over");
		undo = new Button("Undo");
		Button ai = new Button("Turn on AI");
		ToggleGroup autoMoveToggle = new ToggleGroup();
		RadioButton autoMoveOn = new RadioButton("Automove On");
		autoMoveOn.setToggleGroup(autoMoveToggle);
		autoMoveOn.setSelected(true);
		autoMoveOn.setStyle("-fx-text-fill: yellow;");
		RadioButton autoMoveOff = new RadioButton("Automove Off");
		autoMoveOff.setStyle("-fx-text-fill: orange;");
		autoMoveOff.setToggleGroup(autoMoveToggle);
		quit = new Button("Quit");
		menuVB.getChildren().addAll(hi, enterMove, move, newGame, startOver, 
		  printMoves, undo, autoMoveOn, autoMoveOff, ai, instructions, quit);
		menuVB.setSpacing(4);
		
		autoMoveOff.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) 
			{
				autoMoveOff.setSelected(true);
				autoMoveOff.setStyle("-fx-text-fill: yellow;");
				autoMoveOn.setStyle("-fx-text-fill: orange;");
				autoMove = false;
			}
		});
		
		autoMoveOn.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event)
			{
				autoMoveOn.setSelected(true);
				autoMoveOn.setStyle("-fx-text-fill: yellow;");
				autoMoveOff.setStyle("-fx-text-fill: orange");
				autoMove = true;
			}
		});
		
		instructions.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event)
			{
				Alert a = new Alert(AlertType.INFORMATION);
				a.setTitle("Stackotaire Instructions");
				a.setHeaderText("Instructions");
				a.setContentText("Click a Card to select it, and click another"
				  + " Card to move the selected Card on top of it, provided the "
				  + "move is legal, according to the rules laid down by the "
				  + "Stackotaire Gods (see your village Stackotaire priest for"
				  + " details).\nTo deselect a Card, click any Card which would"
				  + " be an illegal move. To move multiple Cards, select a Card"
				  + " that is not at the top (rightmost) of a tableau, and "
				  + " click the top Card of another tableau.\nTo draw a Card, "
				  + "click the Stock pile (at the right of the top row). This "
				  + "Card will be placed in the Waste pile next to it, where it"
				  + " can be used as long as it is the top Card in the pile.\n"
				  + "You must discover how the foundation piles work. Only then"
				  + " will your worth as a potential legendary Stackotaire be"
				  + " realized.\nIf you get nervous using a mouse, you may use"
				  + " the text command option to play the game. Use \"draw\" to"
				  + " draw a Card, \"move AN BM\" to move the Card at the top "
				  + "of the Nth pile of type A to the top of the Mth pile of"
				  + " type B. Use \"moven TN TM X\" to move X Cards from the "
				  + "Nth tableau to the Mth tableau, \"restart\" to start a new"
				  + " game, \"undo\" to undo a move, and \"quit\" to quit "
				  + "(please don't quit, it gets so lonely here).\nAfter "
				  + "typing a command, press the \"Move!\" button or hit ENTER."
				  + " The results of your actions, as well as automove actions "
				  + "can be seen on the graphical display, as well as in text "
				  + "representation through the console."
				  + "\nGood luck! Have fun!");
				a.showAndWait();
			}
		});
		
		quit.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event) 
			{
				Alert alert = new Alert(AlertType.CONFIRMATION);
				alert.setTitle("Quit");
				alert.setHeaderText("Do you really want to quit?");
				alert.setContentText("The Stackotaire Gods will not be pleased");
				Optional<ButtonType> confirm = alert.showAndWait();
				if(confirm.get() == ButtonType.OK)
				{
					System.out.println("Stackotaire quit successfully");
					primaryStage.close();
				}
			}
		});
		
		enterMove.setOnKeyPressed(new EventHandler<KeyEvent>() {
			public void handle(KeyEvent event) 
			{
				if(event.getCode().equals(KeyCode.ENTER))
				{
					try
					{
						makeMove(enterMove.getText());
					}
					catch(InvalidCodeException e)
					{
						System.out.println(e.getMessage());
					}
				}
			}
		});
		
		ai.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event)
			{
				String lastMove = "";
				autoMoveOn.fire();
				int prevStockSize = 53;
				boolean didDrawHelp = true;
				while(prevStockSize != stock.size())
				{
					prevStockSize = stock.size();
					while(!stock.isEmpty() || didDrawHelp)
					{
						lastMove = movesList.peek(); 
						if(didDrawHelp)
						{
							didDrawHelp = false;
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
											if(tableaus[j].isEmpty() && 
											  tableaus[i].getCardsFaceUp() == 
											  tableaus[i].size())
												continue;
											try
											{
												//try to move from a tableau so 
												//that a card will open up
												makeMove("moven t" + (i + 1) + 
												  " t" + (j + 1) + " " + 
												  tableaus[i].getCardsFaceUp()
												  + " ai");
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
							lastMove = movesList.peek();
							//draw
							try
							{
								makeMove("draw ai");
							}
							catch(InvalidCodeException e)
							{
							}
							didDrawHelp = false;
							//try to move from waste to tableaus (automove 
							//takes care of waste to foundations)
							for(int i = 0; i < TABLEAUS; i++)
							{
								try 
								{
									makeMove("move w1 t" + (i + 1) + " ai");
								}
								catch(InvalidCodeException e)
								{
								}
								if(movesList.peek().indexOf("draw") < 0)
								{
									didDrawHelp = true;
									lastMove = movesList.peek();
									break;
								}
							}
							if(stock.isEmpty() && !waste.isEmpty())
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
					}
				}
			}
		});
		
		startOver.setOnAction(new EventHandler<ActionEvent>()
			{
			public void handle(ActionEvent event) 
			{
				while(!movesList.isEmpty())
				{
					try
					{
						makeMove(reverseMove(movesList.pop()) + " override");
						movesList.pop();
					}
					catch(InvalidCodeException e) 
					{
						if(e.getMessage().equals("No moves made"))
							break;
					}
				}
				movesList.push("");
				victory = false;
			}
		});
		
		undo.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event)
			{
				try
				{
					if(!movesList.empty())
					{
						makeMove(reverseMove(movesList.pop()) + " override");
						movesList.pop(); //pop the undo move from movesList 
						//(don't want that hanging around)
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
				Alert a = new Alert(AlertType.CONFIRMATION);
				a.setTitle("New Game");
				a.setHeaderText("GIVE UP?");
				Optional<ButtonType> confirm = a.showAndWait();
				if(confirm.get() != ButtonType.OK)
					return;
				a = new Alert(AlertType.INFORMATION);
				a.setHeaderText("Noob");
				a.setContentText("Press ok ->");
				a.showAndWait();
				victory = false;
				System.out.println("New Game");
				movesList.clear();
				movesList.push("");
				if(Card.getSelectedCard() != null)
					Card.getSelectedCard().setIsSelected(false);
				Label pleaseWait = new Label("Please wait...");
				menuVB.getChildren().add(pleaseWait);
				topHB.getChildren().clear();
				for(int i = 0; i < FOUNDATIONS; i++)
				{
					while(!foundations[i].isEmpty())
					{
						stock.push(foundations[i].pop());
						stock.peek().setFlippedOnMove(-1);
					}
					topHB.getChildren().add(
					  foundations[i].getEmptyCard().getImageView());
				}
				for(int i = 0; i < TABLEAUS; i++)
				{
					while(!tableaus[i].isEmpty())
					{
						stock.push(tableaus[i].pop());
						stock.peek().setFlippedOnMove(-1);
					}
				}
				while(!waste.isEmpty())
				{
					stock.push(waste.pop());
					stock.peek().setFlippedOnMove(-1);
				}
				topHB.getChildren().add(
				  waste.getEmptyCard().getImageView());
				Collections.shuffle(stock);
				for(int i = 0; i < TABLEAUS; i++)
				{
					tableausHB.get(i).getChildren().clear();
					for(int j = 0; j < TABLEAUS - i; j++)
					{
						tableaus[i].push(stock.pop());
						if(j == TABLEAUS - i - 1)
							tableaus[i].peek().setFaceUp(true);
						tableausHB.get(i).getChildren().add(
						  tableaus[i].peek().getImageView());
					}
				}
				stockSize.setText(stock.size() + "");
				topHB.getChildren().addAll(
				  stock.peek().getImageView(), stockSize);
				printAllStacks();
				autoMove("all");
				menuVB.getChildren().remove(pleaseWait);
			}
		});
		
		//create deck and populate with 52 unique cards, facedown
		deck = new CardStack('s'); //deck is not displayed, so has no container
		for(int i = 1; i < Card.values.length; i++)
		{
			for(int j = 1; j < Card.suits.length; j++)
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
				clickedCard.getImageView().addEventHandler(
				  MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
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
						else if(clickedCard.getStack().getType() == 'f' && 
						  Card.getSelectedStack() != null)
						{
							makeMove("move " + Card.getSelectedStack().getType()
							  + "" + Card.getSelectedStack().getStackNum() + 
							  " f" + clickedCard.getStack().getStackNum());
						}
						//if a tableau or waste is clicked
						else 
						{
							//if no Card is selected
							if(Card.getSelectedStack() == null && 
							  clickedCard.isFaceUp())
								clickedCard.setIsSelected(true);
							//if a Card is already selected
							else if(clickedCard.isTopOfStack())
							{
								if(Card.getSelectedCard().isTopOfStack())
								{
									makeMove("move " + 
									  Card.getSelectedStack().getType() + "" + 
									  Card.getSelectedStack().getStackNum() + 
									  " " + clickedCard.getStack().getType() + 
									  "" + clickedCard.getStack().getStackNum());
								}
								else
								{
									makeMove("moven " + 
									  Card.getSelectedStack().getType() + "" + 
									  Card.getSelectedStack().getStackNum() + 
									  " " + clickedCard.getStack().getType() +
									  "" + clickedCard.getStack().getStackNum()
									  + " " + (1 + 
									  Card.getSelectedCard().getDepth()));
								}
							}
							//necessary check in case a face down Card was clicked
							else if(Card.getSelectedStack() != null)
								Card.getSelectedCard().setIsSelected(false);
						}//don't take points off for this >:(
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
		for(int i = 0; i < TABLEAUS; i++)
		{
			HBox h = new HBox();
			tableausHB.add(h);
			tableaus[i] = new CardStack('t', h, i + 1);
			for(int j = 0; j < TABLEAUS - i; j++)
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
		for(int i = 0; i < FOUNDATIONS; i++)
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
			cs.getEmptyCard().getImageView().addEventHandler(
			  MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
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
			f.getEmptyCard().getImageView().addEventHandler(
			  MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
				public void handle(MouseEvent event)
				{
					if(Card.getSelectedStack() == null)
						return;
					else 
					{
						try
						{
							makeMove("move " + Card.getSelectedStack().getType()
							  + "" + Card.getSelectedStack().getStackNum() + 
							  " f" + f.getStackNum());
						}
						catch(InvalidCodeException e)
						{
							System.out.println(e.getMessage());
						}
					}
				}
			});
		}
		stock.getEmptyCard().getImageView().addEventHandler(
		  MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
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
	 * tokens are case-insensitive. Note 
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
			int a, b, n;
			try
			{
				//a and b are the containerIndexes of each CardStack, not the stackNums
				a = Integer.parseInt(code.substring(7, 8)) - 1;
				b = Integer.parseInt(code.substring(10, 11)) - 1;
				//the number of Cards to move
				n = Integer.parseInt(code.substring(12, 13));
			}
			catch(NumberFormatException | StringIndexOutOfBoundsException e) 
			{
				throw new InvalidCodeException("Could not read moven");
			}
			//for two-digit n values:
			int n2;
			try
			{
				n2 = Integer.parseInt(code.substring(13, 14));
				n *= 10;
				n += n2;
			}
			catch(NumberFormatException | StringIndexOutOfBoundsException e)
			{
			}
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
			if(n > csa.getCardsFaceUp())
				throw new InvalidCodeException("Can't move facedown Cards: " + code);
			//move the Cards
			temp = new CardStack('t');
			for(int i = 0; i < n && !csa.isEmpty(); i++)
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
			movesList.push(code);
			csa.display();
			csb.display();
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
					throw new InvalidCodeException("Attempting to move from "
					  + "illegal CardStack: " + code);
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
					throw new InvalidCodeException("Attempting to move to "
					  + "illegal CardStack: " + code);
				//can't move from an empty CardStack
				if(csa.isEmpty())
					throw new InvalidCodeException("Empty CardStack selected: "
					  + code);
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
						System.out.println(movesList.peek());
						csa.display();
						csb.display();
						printAllStacks();
						if(csa.getType() == 't')
							autoMove("" + csa.getContainerIndex());
					}
					else if(!ai && !overRide)
						System.out.println("Illegal Move: " + code);
				}
				else 
				{
					if(validMove(csa.peek(), csb.getEmptyCard()) || overRide)
					{
						csb.push(csa.pop());
						movesList.push(code);
						System.out.println(movesList.peek());
						csa.display();
						csb.display();
						printAllStacks();
						if(csa.getType() == 't')
							autoMove("" + csa.getContainerIndex());
					}
					else if(!ai && !overRide)
						System.out.println("Illegal Move");
				}
			}
			catch(StringIndexOutOfBoundsException | NumberFormatException | 
			  ArrayIndexOutOfBoundsException e)
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
				movesList.push(code);
				stock.display();
				waste.display();
				
				System.out.println(movesList.peek());
				printAllStacks();
				autoMove("w");
			}
			else
			{
				while(!waste.isEmpty())
					stock.push(waste.pop());
				if(!stock.isEmpty())
				{
					movesList.push(code);
					waste.display();
					stock.display();
					System.out.println(movesList.peek());
					printAllStacks();
				}
				else
					System.out.println("No cards left");
			}
			stockSize.setText(stock.size() + "");
		}
		else if(code.indexOf("undraw") == 0 && overRide)
		{
			if(!waste.isEmpty())
			{
				stock.push(waste.pop());
				movesList.push(code);
				waste.display();
				stock.display();
				
				System.out.println(movesList.peek());
				printAllStacks();
			}
			else
			{
				while(!stock.isEmpty())
					waste.push(stock.pop());
				movesList.push(code);
				stock.display();
				waste.display();
				
				System.out.println(movesList.peek());
				printAllStacks();
			}
			stockSize.setText(stock.size() + "");
		}
		else if(code.equals(" override")) //undoing with no moves
		{
			movesList.push("");
			throw new InvalidCodeException("No moves made");
		}
		else if(code.equals("quit"))
			quit.fire();
		else if(code.equals("restart"))
			newGame.fire();
		else if(code.equals("undo"))
			undo.fire();
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
		if(move.indexOf("draw") >= 0)
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
		if(!victory)
		{
			victory = true;
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("VICTORY!");
			alert.setHeaderText("You Win!");
			alert.setContentText("You will not be sacrificed after all.");
			alert.showAndWait();
		}
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
	 * determines the number of moves made, not counting undos or moves that 
	 * were undone. 
	 * @return the number of moves made (one less than the size of movesList)
	 */
	public static int getMoveNum()
	{
		return movesList.size() - 1; //subtract the initial empty String in movesList
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
		//System.out.print("move " + getMoveNum());
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
		if(!autoMove)
			return;
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
							movesList.push("move w1 f" + (i + 1));
							waste.display();
							foundations[i].display();
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
							movesList.push("move w1 f" + (i + 1));
							foundations[i].push(waste.pop());
							waste.display();
							foundations[i].display();
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
								movesList.push("move t" + (j + 1) + " f" + (i + 1));
								foundations[i].push(tableaus[j].pop());
								tableaus[j].display();
								foundations[i].display();
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
								movesList.push("move t" + (j + 1) + " f" + (i + 1));
								foundations[i].push(tableaus[j].pop());
								tableaus[j].display();
								foundations[i].display();
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
							movesList.push("move w1 f" + (i + 1));
							foundations[i].push(waste.pop());
							waste.display();
							foundations[i].display();
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
							movesList.push("move w1 f" + (i + 1));
							foundations[i].push(waste.pop());
							waste.display();
							foundations[i].display();
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
							movesList.push("move t" + (index + 1) + " f" + (i + 1));
							foundations[i].push(tableaus[index].pop());
							tableaus[index].display();
							foundations[i].display();
							System.out.println(movesList.peek());
							printAllStacks();
							autoMove("all");
							return;
						}
					}
					else
					{
						if(validMove(tableaus[index].peek(), foundations[i].getEmptyCard()))
						{
							movesList.push("move t" + (index + 1) + " f" + (i + 1));
							foundations[i].push(tableaus[index].pop());
							tableaus[index].display();
							foundations[i].display();
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
}

