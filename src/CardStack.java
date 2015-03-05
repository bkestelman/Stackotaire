import java.util.List;
import java.util.Stack;

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
public class CardStack extends Stack {
	private char type;
	private int size;
	private HBox container;
	private int containerIndex;
	private Card emptyCard;
	
	public CardStack(char type)
	{
		super();
		setType(type);
		container = null;
		size = 0;
		containerIndex = -1;
		emptyCard = new Card();
		emptyCard.setStack(this);
	}
	
	public CardStack(char type, HBox container, int containerIndex)
	{
		super();
		setType(type);
		this.container = container;
		size = 0;
		this.containerIndex = containerIndex;
		emptyCard = new Card();
		emptyCard.setStack(this);
		emptyCard.setContainer(container);
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
		return (Card)super.push(item);
	}

	public Card pop() {
		size--;
		//imageViews.pop();
		return (Card)super.pop();
	}

	public Card peek() {
		//if(isEmpty()) 
			//return emptyCard;
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
	
	public int mysize()
	{
		return size;
	}
}
