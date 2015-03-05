import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
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
	
	public static boolean mouseClicked, eventHandled;
	
	public static final int TABLEAUS = 7;

	public static void main(String[] args) throws InvalidTypeException
	{
		mouseClicked = eventHandled = false;
		selectedStack = null;
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		int i, j;
		i = j = 0;
		deck = new CardStack('s');
		tableaus = new CardStack[TABLEAUS];
		foundations = new CardStack[4];
		stock = deck;
		CardStack waste = new CardStack('s');
		//fill deck
		for(int v = 1; v < Card.values.length; v++)
		{
			for(int s = 1; s < Card.suits.length; s++)
			{
				try 
				{
					deck.push(new Card(v, s, false));
				} 
				catch(ValueOutOfRangeException|InvalidSuitException e)
				{
					System.err.println(e.getMessage());
				}
			}
		}
		Collections.shuffle(deck);
		VBox root = new VBox();
		//set up foundations
		HBox topHB = new HBox();
		for(i = 0; i < foundations.length; i++)
		{
			foundations[i] = new CardStack('f', topHB, i);
			foundations[i].push(new Card());
			CardStack cs = foundations[i];
			cs.peek().getImageView().addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
				public void handle(MouseEvent event)
				{
					System.out.println("hello");
					if(selectedStack == null)
						return;
					Image selectedImage = selectedStack.peek().getImage(); //save the selected card's image
					selectedStack.peek().setImageView(cs.peek().getImageView()); //keep the foundation's original imageView
					selectedStack.peek().setImage(selectedImage); //restore the original image
					cs.push(selectedStack.pop());
					cs.peek().setIsSelected(false);
					selectedStack.getContainer().getChildren().remove(selectedStack.getContainer().getChildren().size() - 1);
					topHB.getChildren().remove(cs.getContainerIndex()); //remove old foundation imageView
					//cs.getContainer().getChildren().add(cs.peek().getImageView());
					selectedStack.setTopFaceUp();
					//if(selectedStack.size() > 0)
						//selectedStack.getContainer().getChildren().remove(selectedStack.getContainer().getChildren().size() - 1);
					//if(selectedStack.size() > 0)
						//selectedStack.getContainer().getChildren().add(selectedStack.peek().getImageView());
					topHB.getChildren().add(cs.getContainerIndex(), cs.peek().getImageView());
					System.out.println(cs.peek().getImagePath());
					if(selectedStack.isEmpty())
						selectedStack.getContainer().getChildren().add(selectedStack.getEmptyCard().getImageView());
					selectedStack = null;
				}
			});
			topHB.getChildren().add(cs.peek().getImageView());
		}
		root.getChildren().add(topHB);
		root.setSpacing(2);
		//set up tableaus
		ArrayList<HBox> hb = new ArrayList<HBox>();
		for(i = 0; i < TABLEAUS; i++)
		{
			HBox h = new HBox();
			hb.add(h);
			tableaus[i] = new CardStack('t', h, i);
			Card emptyCard = tableaus[i].getEmptyCard();
			emptyCard.getImageView().addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
				public void handle(MouseEvent event)
				{
					if(selectedStack == null)
						return;
					else 
					{
						selectedStack.peek().setIsSelected(false);
						emptyCard.getStack().push(selectedStack.pop());
						emptyCard.getContainer().getChildren().remove(0);
						emptyCard.getContainer().getChildren().add(emptyCard.getStack().peek().getImageView());
						selectedStack.setTopFaceUp();
						if(selectedStack.isEmpty())
							selectedStack.getContainer().getChildren().add(selectedStack.getEmptyCard().getImageView());
						selectedStack = null;
					}
				}
			});
			for(j = 0; j < TABLEAUS - i - 1; j++)
			{
				tableaus[i].push(deck.pop());
				Card card = tableaus[i].peek();
				card.getImageView().addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
					public void handle(MouseEvent event)
					{
						if(!card.isTopOfStack()) return;
						if(card.isSelected())
						{
							card.setIsSelected(false);
							selectedStack = null;
						}
						else
						{
							if(selectedStack == null)
							{
								card.setIsSelected(true);
								selectedStack = card.getStack();
							}
							else
							{
								selectedStack.peek().setIsSelected(false);
								if(selectedStack.size() > 0)
									selectedStack.getContainer().getChildren().remove(selectedStack.getContainer().getChildren().size() - 1);
								card.getContainer().getChildren().add(selectedStack.peek().getImageView());
								card.getStack().push(selectedStack.pop());
								//selectedStack.getContainer().getChildren().remove(selectedStack.getContainer().getChildren().size() - 1);
								selectedStack.setTopFaceUp();
								if(selectedStack.isEmpty())
									selectedStack.getContainer().getChildren().add(selectedStack.getEmptyCard().getImageView());
								//selectedStack.getContainer().getChildren().add(selectedStack.peek().getImageView());
								selectedStack = null;
							}
						}
					}
				});
				hb.get(i).getChildren().add(tableaus[i].peek().getImageView());
			}
			tableaus[i].push(deck.pop());
			tableaus[i].peek().setFaceUp(true);
			Card card = tableaus[i].peek();
			card.getImageView().addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
				public void handle(MouseEvent event)
				{
					if(!card.isTopOfStack()) return;
					if(card.isSelected())
					{
						card.setIsSelected(false);
						selectedStack = null;
					}
					else
					{
						if(selectedStack == null)
						{
							card.setIsSelected(true);
							selectedStack = card.getStack();
						}
						else
						{
							selectedStack.peek().setIsSelected(false);
							//System.out.println("hello");
							//System.out.println(selectedStack.peek());
							card.getContainer().getChildren().add(selectedStack.peek().getImageView());
							card.getStack().push(selectedStack.pop());
							//selectedStack.getContainer().getChildren().remove(selectedStack.getContainer().getChildren().size() - 1);
							selectedStack.setTopFaceUp();
							//if(selectedStack.size() > 0)
								//selectedStack.getContainer().getChildren().remove(selectedStack.getContainer().getChildren().size() - 1);
							//selectedStack.getContainer().getChildren().add(selectedStack.peek().getImageView());
							if(selectedStack.isEmpty())
								selectedStack.getContainer().getChildren().add(selectedStack.getEmptyCard().getImageView());
							selectedStack = null;
						}
					}
				}
			});
			hb.get(i).getChildren().add(tableaus[i].peek().getImageView());
			root.getChildren().add(hb.get(i));
		}
		System.out.println(stock.peek());
		topHB.getChildren().add(stock.peek().getImageView());
		
		Scene scene = new Scene(root);
		primaryStage.setScene(scene);
		primaryStage.setMinWidth(700);
		primaryStage.show();
	}
	
	
}


	
//BAZINGA
//implement movement rules
//add foundation piles, stock, waste and functionality
//add text option