import java.util.Collections;
import java.util.List;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
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

	public static void main(String[] args) throws InvalidTypeException
	{
		launch(args);
	}
	
	@Override
	public void start(Stage primaryStage) throws Exception {
		int i = 0, j = 0;
		CardStack deck = new CardStack('s');
		CardStack[] tableaus = new CardStack[7];
		CardStack[] foundations = new CardStack[4];
		CardStack stock = deck;
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
				//System.out.println(deck.peek());
			}
		}
		Collections.shuffle(deck);
		//distribute tableau piles
		VBox vb = new VBox();
		HBox[] hb = new HBox[tableaus.length];
		ImageView[][] tableauImages = new ImageView[tableaus.length][tableaus.length];
		for(i = 0; i < tableaus.length; i++)
		{
			//tableauImages[i] = new ImageView[tableaus.length - i];
			tableaus[i] = new CardStack('t');
			hb[i] = new HBox();
			for(j = 0; j < tableaus.length - i; j++)
			{
				System.out.println("i,j:[" + i + "," + j);
				tableaus[i].push(deck.pop());
				//tableaus[i].peek().setFaceUp(true);
				System.out.println(tableaus[i].peek().getImagePath());
				Image a = new Image(tableaus[i].peek().getImagePath());
				tableauImages[i][j] = new ImageView();
				tableauImages[i][j].setImage(a);
				tableauImages[i][j].setFitHeight(75);
				tableauImages[i][j].setPreserveRatio(true);
				hb[i].getChildren().add(tableauImages[i][j]);
			}
			tableaus[i].peek().setFaceUp(true);
			tableauImages[i][tableaus.length - i - 1].setImage(new Image(tableaus[i].peek().getImagePath()));
			vb.getChildren().add(hb[i]);
			//System.out.println(tableaus[i].peek());
		}
		
		//display -- we should display as we populate!
		/*
		VBox vb = new VBox();
		HBox[] hb = new HBox[tableaus.length];
		ImageView[][] tableauImages = new ImageView[tableaus.length][];
		for(i = 0; i < tableaus.length; i++)
		{
			tableauImages[i] = new ImageView[tableaus.length - i];
			for(j = 0; j < tableaus.length - i; j++)
			{
				System.out.println("i,j:[" + i + "," + j + "]");
				tableauImages[i][j] = new ImageView();
				tableauImages[i][j].setImage(new Image(tableaus[i].peek().getImagePath()));
				tableauImages[i][j].setFitHeight(75);
				tableauImages[i][j].setPreserveRatio(true);
				hb[i].getChildren().add(tableauImages[i][j]);
			}
			hb[i].getChildren().add(tableauImages[i][i]);
			vb.getChildren().add(hb[i]);
		}
		*/

		Scene scene = new Scene(vb);
		primaryStage.setScene(scene);
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
}