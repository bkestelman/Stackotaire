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
	public static boolean isACardSelected;
	public static CardStack selectedStack;
	public static Stack<ImageView> selectedIVStack;

	public static void main(String[] args) throws InvalidTypeException
	{
		isACardSelected = false;
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		int i, j;
		CardStack deck = new CardStack('s');
		CardStack[] tableaus = new CardStack[7];
		CardStack[] foundations = new CardStack[4];
		CardStack stock = deck;
		selectedStack = tableaus[0];
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
		//distribute and dispay cards
		VBox vb = new VBox();
		//display empty foundations
		HBox topHB = new HBox();
		ImageView[] foundationImages = new ImageView[4];
		for(ImageView iv : foundationImages)
		{
			iv = new ImageView();
			iv.setImage(new Image("PNG-cards-1.3/empty_ace_4.png"));
			iv.setFitHeight(75);
			iv.setPreserveRatio(true);
			topHB.getChildren().add(iv);
		}
		//display stock
		ImageView stockImage = new ImageView();
		stockImage.setImage(new Image(stock.peek().getImagePath()));
		stockImage.setFitHeight(75);
		stockImage.setPreserveRatio(true);
		topHB.getChildren().add(stockImage);
		vb.getChildren().add(topHB);
		stockImage.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() 
		{
			public void handle(MouseEvent event)
			{
				stock.peek().setFaceUp(true);
				stockImage.setImage(new Image(stock.peek().getImagePath()));
			}
		});
		//display initial tableaus
		ArrayList<HBox> hb = new ArrayList<HBox>();
		ArrayList<Stack<ImageView>> tableauImages = new ArrayList<Stack<ImageView>>();
		for(i = 0; i < tableaus.length; i++)
		{
			tableaus[i] = new CardStack('t');
			tableauImages.add(new Stack<ImageView>());
			hb.add(new HBox());
			for(j = 0; j < tableaus.length - i; j++)
			{
				tableaus[i].push(deck.pop());
				Image a = new Image(tableaus[i].peek().getImagePath());
				tableauImages.get(i).push(new ImageView());
				tableauImages.get(i).peek().setImage(a);
				tableauImages.get(i).peek().setFitHeight(75);
				tableauImages.get(i).peek().setPreserveRatio(true);
				hb.get(i).getChildren().add(tableauImages.get(i).peek());
			}
			tableaus[i].peek().setFaceUp(true);
			Card clickedCard = tableaus[i].peek();
			CardStack currentStack = tableaus[i];
			Stack<ImageView> currentIVStack = tableauImages.get(i);
			tableauImages.get(i).peek().setImage(new Image(tableaus[i].peek().getImagePath()));
			tableauImages.get(i).peek().addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>()
			{
				public void handle(MouseEvent event)
				{
					ColorAdjust c = new ColorAdjust();
					if(clickedCard.isSelected())
					{
						c.setSaturation(0);
						isACardSelected = false;
						clickedCard.setIsSelected(false);
					}
					else if(isACardSelected)
					{
						System.out.println("moving " + selectedStack.peek() + " to " + currentStack.peek());
						if(makeMove(selectedStack, currentStack))
							currentIVStack.push(selectedIVStack.pop());
					}
					else
					{
						c.setSaturation(.5);
						selectedStack = currentStack;
						selectedIVStack = currentIVStack;
						System.out.println(selectedStack.peek() + " has been selected");
						isACardSelected = true;
					}
					
					//yo.setStyle("-fx-background-color: black");
					//yo.setFitHeight(100);
					currentIVStack.peek().setEffect(c);
				}
			});
			vb.getChildren().add(hb.get(i));
		}

		Scene scene = new Scene(vb);
		primaryStage.setScene(scene);
		primaryStage.setMinWidth(700);
		primaryStage.show();
		
		/*
		iv.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>()
		{
			public void handle(MouseEvent event)
			{
				System.out.println("it works?");
			}
		});
		*/
	}
	
	public static boolean makeMove(CardStack from, CardStack to)
	{
		to.push(from.pop());
		to.peek().setIsSelected(false);
		isACardSelected = false;
		return true;
	}
}