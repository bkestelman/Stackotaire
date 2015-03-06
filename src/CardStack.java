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
public class CardStack extends Stack<Card> {
	private char type;
	private int size;
	private HBox container;
	private int containerIndex;
	private Card emptyCard, bottomCard, topCard; //emptyCard is useful,
	//bottomCard and topCard are necessary to make transferring cards
	//from waste to stock more efficient
	//Note: bottomCard and topCard must be set manually; they are NOT
	//automatically updated
	
	public CardStack(char type)
	{
		super();
		setType(type);
		container = null;
		size = 0;
		containerIndex = -1;
		bottomCard = null;
		topCard = null;
		emptyCard = new Card();
		emptyCard.setStack(this);
	}
	
	public CardStack(char type, HBox container, int containerIndex)
	{
		super();
		setType(type);
		this.container = container;
		size = 0;
		bottomCard = null;
		topCard = null;
		this.containerIndex = containerIndex;
		emptyCard = new Card();
		emptyCard.setStack(this);
		emptyCard.setContainer(container);
	}
	
	public void setTopCard(Card topCard)
	{
		this.topCard = topCard;
		topCard.setContainer(container);
		topCard.setStack(this);
	}
	
	public Card getTopCard()
	{
		return topCard;
	}
	
	public void setBottomCard(Card bottomCard)
	{
		this.bottomCard = bottomCard;
		bottomCard.setContainer(container);
		bottomCard.setStack(this);
	}
	
	public Card getBottomCard()
	{
		return bottomCard;
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
	
	public int mysize()
	{
		return size;
	}
}
