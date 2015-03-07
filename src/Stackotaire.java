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
 * <code>Stackotaire</code> is a new version of the classic game Solitaire, implementing 
 * Stack data structures to hold <code>Card</code> objects in <code>CardStacks</code>. It
 * allows the player to move the cards by clicking to select a card and clicking another card 
 * to move it, and also includes an Undo option, automoving, AI, and no-graphics mode. 
 *   
 * @author benito.kestelman@stonybrook.edu
 */
public class Stackotaire extends Application {
	private static CardStack deck;
	private static CardStack[] tableaus;
	private static CardStack[] foundations;
	private static CardStack stock;
	private static CardStack waste;
	private static CardStack temp; //temp should only be used immediately after assigning it a CardStack
	private static Stack<String> movesList;
	
	public static final int TABLEAUS = 7;
	public static final int FOUNDATIONS = 4;
	
	private static int traceCounter;

	public static void main(String[] args) throws InvalidTypeException
	{
		movesList = new Stack<String>();
		traceCounter = 0;
		launch(args);
	}
	
	public void start(Stage primaryStage)
	{
		int i, j;
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
		
		Label hi = new Label("Hi! Click the cards to move or enter a move here. Use the buttons as you please.");
		TextField enterMove = new TextField("Enter Move");
		Button move = new Button("Move!");
		enterMove.setMaxWidth(200);
		Button newGame = new Button("New Game");
		menuVB.getChildren().addAll(hi, enterMove, move, newGame);
		
		Label stockSize = new Label();
		
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
		
		newGame.setOnAction(new EventHandler<ActionEvent>() {
			public void handle(ActionEvent event)
			{
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
				autoMove();
				menuVB.getChildren().remove(pleaseWait);
			}
		});
		
		//create deck and populate with 52 unique cards, facedown
		deck = new CardStack('s');
		for(i = 1; i < Card.values.length; i++)
		{
			for(j = 1; j < Card.suits.length; j++)
			{
				try {
					deck.push(new Card(i, j, false, deck));
				}
				catch(ValueOutOfRangeException | InvalidSuitException e)
				{
					System.err.println(e.getMessage());
				}
				//add event handler to every card's ImageView
				//why didn't I think to do it like this sooner... so many hours... so much lost code...
				//cardStack type so useful
				Card clickedCard = deck.peek();
				clickedCard.getImageView().addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
					public void handle(MouseEvent event)
					{
						if(clickedCard.getStack().getType() == 't')
						{
							if(!clickedCard.isFaceUp())
								return;
							if(!clickedCard.isSelected())
							{
								if(Card.getSelectedStack() == null)
								{
									clickedCard.setIsSelected(true);
								}
								else if(Card.getSelectedStack().getType() == 't' && clickedCard.isTopOfStack() && clickedCard.getStack() != Card.getSelectedStack())
								{
									temp = Card.getSelectedStack();
									if(moveBetweenTableaus(Card.getSelectedCard(), clickedCard))
										flipNextCard(temp);
								}
								else if(Card.getSelectedStack().getType() == 'w' && clickedCard.isTopOfStack())
								{
									moveFromWasteToTableau(Card.getSelectedCard(), clickedCard);
								}
							}
							else 
							{
								clickedCard.setIsSelected(false);
							}
						}
						else if(clickedCard.getStack().getType() == 'f')
						{
							if(Card.getSelectedStack() == null)
								return;
							else if(Card.getSelectedStack().getType() == 't' && Card.getSelectedCard().isTopOfStack())
							{
								temp = Card.getSelectedStack();
								if(moveFromTableauToFoundation(Card.getSelectedCard(), clickedCard))
									flipNextCard(temp);
							}
							else if(Card.getSelectedStack().getType() == 'w')
							{
								moveFromWasteToFoundation(Card.getSelectedCard(), clickedCard);
							}
						}
						else if(clickedCard.getStack().getType() == 's')
						{
							if(Card.getSelectedStack() == waste)
								waste.peek().setIsSelected(false);
							draw();
							stockSize.setText(stock.size() + "");
						}
						else if(clickedCard.getStack().getType() == 'w')
						{
							if(Card.getSelectedStack() == null)
								waste.peek().setIsSelected(true);
							else if(Card.getSelectedStack() == waste)
								waste.peek().setIsSelected(false);
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
			tableaus[i] = new CardStack('t');
			HBox h = new HBox();
			tableausHB.add(h);
			tableaus[i].setContainer(h);
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
			foundations[i] = new CardStack('f');
			foundations[i].setContainerIndex(i);
			foundations[i].setContainer(topHB);
			topHB.getChildren().add(foundations[i].getEmptyCard().getImageView());
		}
		
		//display waste pile
		waste = new CardStack('w');
		topHB.getChildren().add(waste.getEmptyCard().getImageView());
		waste.setContainer(topHB);
		
		//populate and display stock deck
		stock = new CardStack('s');
		stock.setContainer(topHB);
		while(!deck.isEmpty())
		{
			stock.push(deck.pop());
		}
		topHB.getChildren().add(stock.peek().getImageView());
		stockSize.setText(stock.size() + "");
		topHB.getChildren().add(stockSize);
		
		//initial automoves
		autoMove();
		
		printAllStacks(); //print all CardStacks to console
		
		//add functionality to each CardStack's emptyCard
		for(CardStack cs : tableaus)
		{
			cs.getEmptyCard().getImageView().addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
				public void handle(MouseEvent event)
				{
					if(Card.getSelectedStack() == null)
						return;
					else if(Card.getSelectedStack().getType() == 't')
					{
						temp = Card.getSelectedStack();
						if(moveBetweenTableaus(Card.getSelectedCard(), cs.getEmptyCard()))
								flipNextCard(temp);
					}
					else if(Card.getSelectedStack().getType() == 'w')
					{
						if(Card.getSelectedStack().isEmpty())
							return;
						moveFromWasteToTableau(Card.getSelectedCard(), cs.getEmptyCard());
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
					else if(Card.getSelectedStack().getType() == 't')
					{
						temp = Card.getSelectedStack();
						if(moveFromTableauToFoundation(Card.getSelectedCard(), f.getEmptyCard()))
							flipNextCard(temp);
					}
					else if(Card.getSelectedStack().getType() == 'w')
					{
						moveFromWasteToFoundation(Card.getSelectedCard(), f.getEmptyCard());
					}
				}
			});
		}
		stock.getEmptyCard().getImageView().addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event)
			{
				draw();
				stockSize.setText(stock.size() + "");
			}
		});
		
		primaryStage.show();
	}
	
	/*
	 * moves any card (from tableaus or waste) that can be moved to a foundation to the proper foundation
	 * called whenever a card is drawn, or a move is made, or a new game is started
	 */
	public static void autoMove() 
	{
		for(int i = 0; i < FOUNDATIONS; i++)
		{
			if(!waste.isEmpty())
			{
				if(foundations[i].isEmpty())
				{
					if(moveFromWasteToFoundation(waste.peek(), foundations[i].getEmptyCard()))
					{
						
					}
				}
				else 
				{
					if(moveFromWasteToFoundation(waste.peek(), foundations[i].peek()))
					{
						
					}
				}
			}
			for(int j = 0; j < TABLEAUS; j++)
			{
				if(!tableaus[j].isEmpty())
				{
					if(foundations[i].isEmpty())
					{
						if(moveFromTableauToFoundation(tableaus[j].peek(), foundations[i].getEmptyCard()))
						{
							
						}
					}
					else 
					{
						if(moveFromTableauToFoundation(tableaus[j].peek(), foundations[i].peek()))
						{
							
						}
					}
				}
			}
		}
	}
	
	public static void flipNextCard(CardStack cs)
	{
		if(cs.isEmpty())
		{
			cs.getContainer().getChildren().add(cs.getEmptyCard().getImageView());
		}
		else 
		{
			cs.peek().setFaceUp(true);
		}
		checkIfVictory();
	}
	
	public static void checkIfVictory()
	{
		for(int i = 0; i < TABLEAUS; i++)
		{
			if(tableaus[i].getCardsFaceUp() != tableaus[i].size())
				return;
		}
		System.out.println("VICToRRYYYY");
	}
	
	public static boolean moveFromWasteToTableau(Card from, Card to)
	{
		if(!(to.getValue() == 0 && from.getValue() == Card.KING))
		{
			if(from.isRed() && to.isRed() || !from.isRed() && !to.isRed() || !(to.getValue() == from.getValue() + 1))
				return false;
		}
		else
			to.getContainer().getChildren().remove(0);
		from.setIsSelected(false);
		to.getStack().push(from.getStack().pop());
		waste.getContainer().getChildren().remove(FOUNDATIONS);
		if(!waste.isEmpty())
			waste.getContainer().getChildren().add(FOUNDATIONS, waste.peek().getImageView());
		else
			waste.getContainer().getChildren().add(FOUNDATIONS, waste.getEmptyCard().getImageView());
		to.getContainer().getChildren().add(from.getImageView());
		return true;
	}
	
	public static void draw()
	{
		if(!stock.isEmpty())
		{
			stock.getContainer().getChildren().remove(FOUNDATIONS + 1);
			waste.getContainer().getChildren().remove(FOUNDATIONS);
			stock.peek().setFaceUp(true);
			waste.push(stock.pop());
			waste.getContainer().getChildren().add(FOUNDATIONS, waste.peek().getImageView());
			if(!stock.isEmpty())
				stock.getContainer().getChildren().add(FOUNDATIONS + 1, stock.peek().getImageView());
			else
				stock.getContainer().getChildren().add(FOUNDATIONS + 1, stock.getEmptyCard().getImageView());
		}
		else if(!waste.isEmpty())
		{
			while(!waste.isEmpty())
			{
				waste.peek().setFaceUp(false);
				stock.push(waste.pop());
			}
			stock.getContainer().getChildren().remove(FOUNDATIONS + 1);
			waste.getContainer().getChildren().remove(FOUNDATIONS);
			waste.getContainer().getChildren().add(FOUNDATIONS, waste.getEmptyCard().getImageView());
			stock.getContainer().getChildren().add(FOUNDATIONS + 1, stock.peek().getImageView());
			/*waste.getBottomCard().setFaceUp(false);
			stock.setTopCard(waste.getBottomCard()); 
			Thread myFirstThread = new Thread(new MoveWasteToDeck()); //:)
			myFirstThread.start(); //using a thread because moving all the 
			//cards from waste to stock takes noticeable time, this way the
			//user can keep playing without having to wait
			stock.getContainer().getChildren().remove(FOUNDATIONS + 1);
			waste.getContainer().getChildren().remove(FOUNDATIONS);
			waste.getContainer().getChildren().add(waste.getEmptyCard().getImageView());
			stock.getContainer().getChildren().add(stock.getTopCard().getImageView());*/ //I TRIED... IllegalStateException and other confusing problems I'm not prepared to deal with
			//I suspect I could get rid of at least some of the problems if I don't load an image for every single card, and also skip loading an image
			//every time I call something like setFaceUp(). This would also make my program more efficient, since I would only be loading images whenever I
			//add a Card's ImageView to an HBox... if I have time, I'll restructure the program to do things this way, but I have to finish other things first
		}
		autoMove();
	}
	
	/*public static class MoveWasteToDeck implements Runnable
	{
		public void run() {
			while(!waste.isEmpty())
			{
				waste.peek().setFaceUp(false);
				stock.push(waste.pop());
			}
		}
	}*/
	
	public static boolean moveFromWasteToFoundation(Card from, Card to)
	{
		if(to.getValue() != from.getValue() - 1 || to.getSuit() != 0 && to.getSuit() != from.getSuit())
			return false;
		to.getStack().push(waste.pop());
		waste.getContainer().getChildren().remove(FOUNDATIONS);
		if(!waste.isEmpty())
			waste.getContainer().getChildren().add(FOUNDATIONS, waste.peek().getImageView());
		else
			waste.getContainer().getChildren().add(FOUNDATIONS, waste.getEmptyCard().getImageView());
		to.getContainer().getChildren().remove(to.getStack().getContainerIndex());
		to.getContainer().getChildren().add(to.getStack().getContainerIndex(), from.getImageView());
		from.setIsSelected(false);
		autoMove();
		return true;
	}
	
	public static boolean moveFromTableauToFoundation(Card from, Card to) 
	{
		if(to.getValue() != from.getValue() - 1 || to.getSuit() != 0 && to.getSuit() != from.getSuit())
			return false;
		to.getContainer().getChildren().remove(to.getStack().getContainerIndex());
		to.getContainer().getChildren().add(to.getStack().getContainerIndex(), from.getImageView());
		CardStack fromStack = from.getStack();
		to.getStack().push(from.getStack().pop());
		flipNextCard(fromStack);
		from.setIsSelected(false);
		autoMove();
		return true;
	}
	
	/*
	 * moves a Card from one tableau to another, if it is a legal move
	 * @return true if move successful, else false, in which case Card from will remain selected.
	 */
	public static boolean moveBetweenTableaus(Card from, Card to)
	{
		if(!(to.getValue() == 0 && from.getValue() == Card.KING))
		{
			if(from.isRed() && to.isRed() || !from.isRed() && !to.isRed() || to.getValue() != from.getValue() + 1 || to.getValue() == 0)
				return false;
		}
		else
			to.getContainer().getChildren().remove(0);
		CardStack subStack = new CardStack('t');
		while(from != from.getStack().peek())
		{
			subStack.push(from.getStack().pop());
			from.getContainer().getChildren().remove(from.getContainer().getChildren().size() - 1);
		}
		from.getContainer().getChildren().remove(from.getContainer().getChildren().size() - 1);
		to.getContainer().getChildren().add(from.getImageView());
		to.getStack().push(from.getStack().pop());
		from.setIsSelected(false);
		while(!subStack.isEmpty())
		{
			to.getContainer().getChildren().add(subStack.peek().getImageView());
			to.getStack().push(subStack.pop());
		}
		autoMove();
		return true;
	}
	
	public static void makeMove(String code) throws InvalidCodeException
	{
		if(code == null) 
			return;
		code = code.toLowerCase();
		if(code.indexOf("move") == 0)
		{
			try
			{
				if(code.substring(5, 6).equals("t") && code.substring(8, 9).equals("t"))
				{
					CardStack a = tableaus[Integer.parseInt(code.substring(6, 7)) - 1];
					CardStack b = tableaus[Integer.parseInt(code.substring(9, 10)) - 1];
					if(b.isEmpty())
					{
						if(moveBetweenTableaus(a.peek(), b.getEmptyCard()))
							flipNextCard(a);
						else
							System.out.println("Illegal move");
					}
					else
					{
						if(moveBetweenTableaus(a.peek(), b.peek()))
							flipNextCard(a);
						else
							System.out.println("Illegal move");
					}
				}
				else if(code.substring(5, 6).equals("t") && code.substring(8, 9).equals("f"))
				{
					CardStack a = tableaus[Integer.parseInt(code.substring(6, 7)) - 1];
					CardStack b = foundations[Integer.parseInt(code.substring(9, 10)) - 1];
					if(b.isEmpty())
					{
						if(moveFromTableauToFoundation(a.peek(), b.getEmptyCard()))
							flipNextCard(a);
						else
							System.out.println("Illegal move");
					}
					else
					{
						if(moveFromTableauToFoundation(a.peek(), b.peek()))
							flipNextCard(a);
						else
							System.out.println("Illegal move");
					}
				}
				else if(code.substring(5, 6).equals("w") && code.substring(8, 9).equals("t"))
				{
					CardStack a = waste;
					CardStack b = tableaus[Integer.parseInt(code.substring(9, 10)) - 1];
					if(a.isEmpty())
						System.out.println("Waste is empty");
					else if(b.isEmpty())
						moveFromWasteToTableau(a.peek(), b.getEmptyCard());
					else
						moveFromWasteToTableau(a.peek(), b.peek());
				}
				else if(code.substring(5, 6).equals("w") && code.substring(8, 9).equals("f"))
				{
					CardStack a = waste;
					CardStack b = foundations[Integer.parseInt(code.substring(9, 10)) - 1];
					if(!a.isEmpty())
					{
						if(!b.isEmpty())
							moveFromWasteToFoundation(a.peek(), b.peek());
						else
							moveFromWasteToFoundation(a.peek(), b.getEmptyCard());
					}
				}
				else
					throw new InvalidCodeException("Code Format Error");
			}
			catch(StringIndexOutOfBoundsException | NumberFormatException | ArrayIndexOutOfBoundsException e)
			{
				throw new InvalidCodeException("Code Format Error");
			}
		}
		else if(code.indexOf("draw") == 0)
		{
			draw();
		}
		else
			throw new InvalidCodeException("Unknown command");
		printAllStacks();
	}
	
	public static void printAllStacks()
	{
		for(int i = 0; i < FOUNDATIONS; i++)
		{
			foundations[i].printStack();
		}
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
	
	/*
	 * Useful debugging method
	 */
	public static void trace(String functionName)
	{
		System.err.println(functionName + " " + traceCounter++);
	}
}

//TODO
//collect list of moves, print moves
//print all cardstacks after each move
//user input from console?
//add undo
//add ai
//add automove from waste, after draw or movefromwastetotableau
//fix victory check, shouldn't depend on stock and waste being empty
//finish text input option
//add no graphics mode