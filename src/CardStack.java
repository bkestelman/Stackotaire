import java.util.List;
import java.util.Stack;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.StringProperty;
import javafx.event.EventHandler;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

/**
 * 
 * @author Benito
 *
 */
public class CardStack extends Stack<Card> {
	private char type;
	private int size, cardsFaceUp;
	private HBox container;
	private int containerIndex, stackNum;
	private Card emptyCard; //useful for displaying functional empty CardStacks
	private StringProperty sizeProperty;
	
	/*
	 * constructs a new instance of CardStack, for a given type
	 * Note: if using a graphical display of Cards, it is strongly advised to
	 * use the constructor CardStack(char type, HBox container, int 
	 * containerIndex), or to implement your own similar constructor depending
	 * on your layout needs.
	 * @param type the type of CardStack: stock, waste, foundation, or tableau
	 */
	public CardStack(char type)
	{
		super();
		setType(type);
		container = null;
		size = stackNum = 0;
		containerIndex = -1;
		cardsFaceUp = 0;
		emptyCard = new Card();
		emptyCard.setStack(this);
	}
	
	/*
	 * constructs a new CardStack for a given type, and assigns it an HBox 
	 * container and containerIndex, specifying its position in its container
	 * Note: the way to implement this will vary depending on how you lay out
	 * the CardStacks. In my Stackotaire implementation, since foundations,
	 * waste, and stock are in the same HBox, their containerIndex will
	 * represent their positions in that HBox (foundations will have 
	 * containerIndex 0 to 3, waste will have 4, stock will have 5), whereas
	 * each tableau has its own HBox and together they are in a VBox, so while
	 * each tableau's container represents its HBox, its containerIndex will 
	 * represent its position in the VBox (from 0 to 7)
	 * @param type the type of CardStack: stock, waste, foundation, or tableau
	 * @param container Explained above
	 * @param containerIndex Explained above
	 */
	public CardStack(char type, HBox container, int stackNum)
	{
		super();
		setType(type);
		this.container = container;
		size = 0;
		cardsFaceUp = 0;
		this.stackNum = stackNum;
		if(type == 'f' || type == 't')
			containerIndex = stackNum - 1;
		else if(type == 'w')
			containerIndex = Stackotaire.FOUNDATIONS;
		else if(type == 's')
			containerIndex = Stackotaire.FOUNDATIONS + 1;
		emptyCard = new Card();
		emptyCard.setStack(this);
		emptyCard.setContainer(container);
	}
	
	/*
	 * displays the current CardStack in its container
	 */
	public void display()
	{
		if(type == 't')
		{
			container.getChildren().clear();
			CardStack temp = new CardStack('t');
			while(!isEmpty())
				temp.push(pop());
			while(!temp.isEmpty())
			{
				container.getChildren().add(temp.peek().getImageView());
				push(temp.pop());
			}
			if(isEmpty())
				container.getChildren().add(emptyCard.getImageView());
			else
				peek().setFaceUp(true);
		}
		else
		{
			container.getChildren().remove(containerIndex);
			if(!isEmpty())
				container.getChildren().add(containerIndex, peek().getImageView());
			else
				container.getChildren().add(containerIndex, emptyCard.getImageView());
		}
	}
	
	/*
	 * gets this CardStack's stackNum, which represents this CardStack's 
	 * position relative to other CardStacks of the same type. Thus, a 
	 * foundation's stackNum will range from 1 to 4, a tableau's from 1 to 7,
	 * and a waste's and stock's will be 1. If Stackotaire is modified to 
	 * include non-traditional numbers of a certain type of CardStack, these 
	 * values will differ. 
	 * @return stackNum
	 */
	public int getStackNum()
	{
		return stackNum;
	}
	
	/*
	 * prints this CardStack to the console, depending on the type of CardStack
	 */
	public void printStack()
	{
		if(type == 's') 
		{
			if(isEmpty())
				System.out.print(getEmptyCard() + " 0");
			else
				System.out.print(peek() + " " + size());
		}
		else if(type == 'w')
		{
			System.out.print("W1: ");
			if(isEmpty())
				System.out.print(getEmptyCard());
			else
				System.out.print(peek() + " ");
		}
		else if(type == 'f')
		{
			if(isEmpty())
				System.out.print("[F" + (getContainerIndex() + 1) + "]");
			else
				System.out.print(peek());
		}
		else if(type == 't')
		{
			CardStack t = new CardStack('t');
			while(!isEmpty())
				t.push(pop());
			while(!t.isEmpty())
			{
				System.out.print(t.peek() + " ");
				push(t.pop());
			}
		}
	}
	
	/*
	 * returns the number of Cards that are face up in this CardStack
	 */
	public int getCardsFaceUp()
	{
		return cardsFaceUp;
	}
	
	/*
	 * records the addition of a face up card to this CardStack, either from a 
	 * face up Card being pushed, or a face down Card being flipped
	 */
	public void incrementCardsFaceUp()
	{
		cardsFaceUp++;
	}
	
	/*
	 * records the subtraction of a face up card to this CardStack, either from
	 * a face up Card being popped, or a face up Card being flipped
	 */
	public void decrementCardsFaceUp()
	{
		cardsFaceUp--;
	}
	
	/*
	 * returns this CardStack's empty Card, useful for displaying an empty 
	 * CardStack
	 */
	public Card getEmptyCard()
	{
		return emptyCard;
	}
	
	/*
	 * returns this CardStack's container index
	 */
	public int getContainerIndex()
	{
		return containerIndex;
	}

	/*
	 * returns this CardStack's container
	 */
	public HBox getContainer()
	{
		return container;
	}
	
	/*
	 * flips this CardStack's top Card to face up, or does nothing if it was
	 * already face up
	 */
	public void setTopFaceUp()
	{
		if(size == 0)
			return;
		peek().setFaceUp(true);
	}
	
	
	public Card push(Card item) {
		size++;
		item.setStack(this);
		item.setContainer(container);
		if(type == 's')
			item.setFaceUp(false);
		else if(item.isFaceUp())
			incrementCardsFaceUp();
		return (Card)super.push(item);
	}

	public Card pop() {
		size--;
		if(peek().isFaceUp())
			decrementCardsFaceUp();
		//imageViews.pop();
		return (Card)super.pop();
	}

	public Card peek() {
		return (Card)super.peek();
	}

	public boolean isEmpty() {
		return size == 0;
	}

	@Override
	public synchronized int search(Object o) {
		return super.search(o);
	}

	public char getType()
	{
		return type;
	}

	public void setType(char type) 
	{
		this.type = type;
	}
	
	public int size()
	{
		return super.size();
	}
	
	public int mysize()
	{
		return size;
	}
}
