import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * 
 * @author Benito
 *
 */
public class Stackotaire extends Application {
	public static CardStack deck;
	public static CardStack[] tableaus;
	public static CardStack[] foundations;
	public static CardStack stock;
	public static CardStack waste;
	
	public static CardStack selectedStack;
	
	public static final int TABLEAUS = 7;
	public static final int FOUNDATIONS = 4;

	public static void main(String[] args) throws InvalidTypeException
	{
		selectedStack = null;
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
		Scene mainScene = new Scene(root);
		primaryStage.setScene(mainScene);
		primaryStage.setMinWidth(500);
		
		//create deck and populate with 52 unique cards, facedown
		deck = new CardStack('s');
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
				//cardStack type so useful
				Card clickedCard = deck.peek();
				clickedCard.getImageView().addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
					public void handle(MouseEvent event)
					{
						if(clickedCard.getStack().getType() == 't')
						{
							if(!clickedCard.isTopOfStack())
								return;
							if(!clickedCard.isSelected())
							{
								if(selectedStack == null)
								{
									clickedCard.setIsSelected(true);
									selectedStack = clickedCard.getStack();
								}
								else if(selectedStack.getType() == 't')
								{
									moveBetweenTableaus(selectedStack.pop(), clickedCard);
									flipNextCard(selectedStack);
									selectedStack = null;
								}
								else if(selectedStack.getType() == 'w')
								{
									moveFromWasteToTableau(waste.pop(), clickedCard);
									selectedStack = null;
								}
							}
							else 
							{
								clickedCard.setIsSelected(false);
								selectedStack = null;
							}
						}
						else if(clickedCard.getStack().getType() == 'f')
						{
							if(selectedStack == null)
								return;
							if(selectedStack.getType() == 't')
							{
								moveFromTableauToFoundation(selectedStack.pop(), clickedCard);
								flipNextCard(selectedStack);
								selectedStack = null;
							}
						}
						else if(clickedCard.getStack().getType() == 's')
						{
							draw();
						}
						else if(clickedCard.getStack().getType() == 'w')
						{
							if(selectedStack == null)
							{
								if(clickedCard.isSelected())
								{
									clickedCard.setIsSelected(false);
									selectedStack = null;
								}
								else
								{
									clickedCard.setIsSelected(true);
									selectedStack = waste;
								}
							}
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
		
		//add functionality to each CardStack's emptyCard
		for(CardStack cs : tableaus)
		{
			cs.getEmptyCard().getImageView().addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
				public void handle(MouseEvent event)
				{
					if(selectedStack == null)
						return;
					else if(selectedStack.getType() == 't')
					{
						cs.getContainer().getChildren().remove(0);
						moveBetweenTableaus(selectedStack.pop(), cs.getEmptyCard());
						flipNextCard(selectedStack);
						selectedStack = null;
					}
					else if(selectedStack.getType() == 'w')
					{
						if(selectedStack.isEmpty())
							return;
					}
				}
			});
		}
		for(CardStack f : foundations)
		{
			f.getEmptyCard().getImageView().addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
				public void handle(MouseEvent event)
				{
					if(selectedStack == null)
						return;
					else if(selectedStack.getType() == 't')
					{
						moveFromTableauToFoundation(selectedStack.pop(), f.getEmptyCard());
						flipNextCard(selectedStack);
						selectedStack = null;
					}
				}
			});
		}
		stock.getEmptyCard().getImageView().addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
			public void handle(MouseEvent event)
			{
				draw();
			}
		});
		
		primaryStage.show();
	}
	
	public static void flipNextCard(CardStack cs)
	{
		if(cs.isEmpty())
		{
			cs.getContainer().getChildren().add(cs.getEmptyCard().getImageView());
		}
		else 
			cs.peek().setFaceUp(true);
	}
	
	public void moveFromWasteToTableau(Card from, Card to)
	{
		from.setIsSelected(false);
		from.getContainer().getChildren().remove(FOUNDATIONS);
		if(!waste.isEmpty())
			from.getContainer().getChildren().add(FOUNDATIONS, waste.peek().getImageView());
		else
			from.getContainer().getChildren().add(FOUNDATIONS, waste.getEmptyCard().getImageView());
		to.getContainer().getChildren().add(from.getImageView());
		to.getStack().push(from);
	}
	
	public static void draw()
	{
		if(!stock.isEmpty())
		{
			if(waste.isEmpty())
				waste.setBottomCard(stock.peek());
			stock.getContainer().getChildren().remove(FOUNDATIONS + 1);
			waste.getContainer().getChildren().remove(FOUNDATIONS);
			stock.peek().setFaceUp(true);
			waste.push(stock.pop());
			waste.getContainer().getChildren().add(waste.peek().getImageView());
			if(!stock.isEmpty())
				stock.getContainer().getChildren().add(stock.peek().getImageView());
			else
				stock.getContainer().getChildren().add(stock.getEmptyCard().getImageView());
		}
		else
		{
			while(!waste.isEmpty())
			{
				waste.peek().setFaceUp(false);
				stock.push(waste.pop());
			}
			stock.getContainer().getChildren().remove(FOUNDATIONS + 1);
			waste.getContainer().getChildren().remove(FOUNDATIONS);
			waste.getContainer().getChildren().add(waste.getEmptyCard().getImageView());
			stock.getContainer().getChildren().add(stock.peek().getImageView());
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
	
	public static void moveFromTableauToFoundation(Card from, Card to)
	{
		to.getContainer().getChildren().remove(to.getStack().getContainerIndex());
		to.getContainer().getChildren().add(to.getStack().getContainerIndex(), from.getImageView());
		to.getStack().push(from);
		from.setIsSelected(false);
	}
	
	public static void moveBetweenTableaus(Card from, Card to)
	{
		from.getContainer().getChildren().remove(from.getContainer().getChildren().size() - 1);
		to.getContainer().getChildren().add(from.getImageView());
		to.getStack().push(from);
		from.setIsSelected(false);
	}
}