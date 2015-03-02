import java.util.List;
import java.util.Stack;

/**
 * 
 * @author Benito
 *
 */
public class CardStack extends Stack {
	char type;
	int size;
	
	public CardStack(char type) throws InvalidTypeException
	{
		super();
		setType(type);
		size = 0;
	}
	
	public Card push(Card item) {
		size++;
		return (Card)super.push(item);
	}

	public Card pop() {
		size--;
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

	public void setType(char type) throws InvalidTypeException 
	{
		if(type != 'w' && type != 's' && type != 'f' && type != 't')
			throw new InvalidTypeException();
		else
			this.type = type;
	}
}
