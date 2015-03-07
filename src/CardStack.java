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
	private int containerIndex;
	private Card emptyCard; //useful for keeping consistent behavior for empty decks
	private StringProperty sizeProperty;
	
	/*
	 * constructs a new instance of CardStack, for a given type
	 */
	public CardStack(char type)
	{
		super();
		setType(type);
		container = null;
		size = 0;
		containerIndex = -1;
		cardsFaceUp = 0;
		emptyCard = new Card();
		emptyCard.setStack(this);
	}
	
	public CardStack(char type, HBox container, int containerIndex)
	{
		super();
		setType(type);
		this.container = container;
		size = 0;
		cardsFaceUp = 0;
		this.containerIndex = containerIndex;
		emptyCard = new Card();
		emptyCard.setStack(this);
		emptyCard.setContainer(container);
	}
	
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
	
	public int getCardsFaceUp()
	{
		return cardsFaceUp;
	}
	
	public void incrementCardsFaceUp()
	{
		cardsFaceUp++;
	}
	
	public void decrementCardsFaceUp()
	{
		cardsFaceUp--;
	}
	
	public Card getEmptyCard()
	{
		return emptyCard;
	}
	
	public void setContainerIndex(int i)
	{
		containerIndex = i;
	}
	
	public int getContainerIndex()
	{
		return containerIndex;
	}

	public void setContainer(HBox h)
	{
		container = h;
		emptyCard.setContainer(h);
	}
	
	public HBox getContainer()
	{
		return container;
	}
	
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
