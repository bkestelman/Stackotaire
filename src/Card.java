import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;


public class Card {
    public static final String values[] = 
      {" ","A","2","3","4","5","6","7","8","9","10","J","Q","K"};
    public static final char suits[]    = 
      {' ', '\u2666', '\u2663','\u2665', '\u2660'}; // {' ', '♦', '♣','♥', '♠'}
    public static final int DIAMOND = 1;
    public static final int CLUB = 2;
    public static final int HEART = 3;
    public static final int SPADE = 4;
    public static final int JACK = 11;
    public static final int QUEEN = 12;
    public static final int KING = 13;
    public static final int ACE = 1; 
    
    private int suit, value, flippedOnMove;
    private boolean isFaceUp, isSelected;

	private String imagePath, strValue, strSuit, strValue2;
    private Image faceUpImage; //each Card has unique face up image, whereas 
    //all cards have access to the Card class's facedown image
    private ImageView imageView;
    private HBox container;
    
    private CardStack myStack;
    
    private static Card selectedCard;
    private static CardStack selectedStack;
    
    private static final Image faceDownImage = new 
      Image("PNG-cards-1.3/facedown.jpg");
    
    /**
     * constructs a new, empty Card
     */
    public Card()
    {
    	suit = 0;
    	value = 0; 
    	flippedOnMove = -1;
    	isFaceUp = isSelected = false;
    	strSuit = strValue = "";
    	imageView = new ImageView();
    	imagePath = "PNG-cards-1.3/empty.png";
    	imageView.setImage(new Image(imagePath));
    	setImageView();
    }
    
    /**
     * constructs a new instance of Card for a given value, suit, isFaceUp
     * boolean, and owner CardStack, and gives it an ImageView with an 
     * appropriate Image
     * @param value the value of this Card, from 1 to 13 (value 0 indicates an
     * empty Card)
     * @param suit an int from 1 to 4 representing this Card's suit (0 
     * indicates an empty Card - no suit)
     * @param isFaceUp true if this Card is face up, else false
     * @param myStack this Card's initial owner CardStack
     * @throws InvalidSuitException
     * @throws ValueOutOfRangeException
     */
    public Card(int value, int suit, boolean isFaceUp) 
      throws InvalidSuitException, ValueOutOfRangeException
    {
    	imageView = new ImageView();
    	flippedOnMove = -1;
    	setValue(value);
    	setSuit(suit);
    	setFaceUp(isFaceUp);
    	isSelected = false;
    	switch(value)
    	{
    		case ACE: strValue = "ace";
    				  strValue2 = "A";
    				  break;
    		case JACK: strValue = "jack";
    				   strValue2 = "J";
    				   break;
    		case QUEEN: strValue = "queen";
    					strValue2 = "Q";
    					break;
    		case KING: strValue = "king";
    				   strValue2 = "K";
    				   break;
    		default: strValue = strValue2 = value + "";
    				 break;
    	}
    	switch(suit)
    	{
    		case DIAMOND: strSuit = "diamond";
    					  break;
    		case CLUB: strSuit = "club";
    				   break;
    		case HEART: strSuit = "heart";
    					break;
    		case SPADE: strSuit = "spade";
    					break;
    		default: strSuit = "diamond";
    				 break;
    	}
    	setImagePath();
    	faceUpImage = new Image(imagePath);
    	setImage();
    	setImageView();
    }
    
    /**
     * determines how deep this Card is in its CardStack - its distance from
     * the top 
     * @return this Card's distance from the top of its CardStack, where the 
     * top Card has a depth of 0, and the bottom has a depth size() - 1
     */
    public int getDepth()
    {
    	int ans = 0;
    	CardStack cs = new CardStack('t');
    	while(myStack.peek() != this)
    	{
    		cs.push(myStack.pop());
    		ans++;
    	}
    	while(!cs.isEmpty())
    		myStack.push(cs.pop());
    	return ans;
    }
    
    /**
     * records the move number this Card was flipped. Only applicable to Cards
     * in tableau CardStacks, so if that move is undone, this Card will be 
     * unflipped. Useless for other CardStack types, since foundations and 
     * waste are always face-up and stock is always face-down.
     * <dt><b>Preconditions:</b><dd>This Card should be in a tableau CardStack,
     * and moveNum should be the correct move number at which this Card was 
     * flipped face up
     * @param moveNum the move number at which this Card was flipped face-up
     */
    public void setFlippedOnMove(int moveNum)
    {
    	flippedOnMove = moveNum;
    }
    
    /**
     * gets the move number on which this Card was flipped face up on a tableau
     * (doesn't apply to Cards moved from stock to waste)
     * @return the move number on which this Card was flipped face up on a 
     * tableau. -1 if the Card was not flipped face up on a tableau.
     */
    public int getFlippedOnMove()
    {
    	return flippedOnMove;
    }
    
    /**
     * @return the currently selected CardStack, the CardStack with a Card 
     * selected
     */
    public static CardStack getSelectedStack()
    {
    	return selectedStack;
    }
    
    /**
     * @return the currently selected Card
     */
    public static Card getSelectedCard()
    {
    	return selectedCard;
    }
    
    /**
     * @return true if this Card's suit is red (heart or diamond), false else
     */
    public boolean isRed()
    {
    	return suit == HEART || suit == DIAMOND;
    }
    
    /**
     * @return the String path to this Card's face-up image
     */
    public String getImagePath()
    {
    	return imagePath;
    }
    
    /**
     * @return the HBox in which this Card is displayed 
     */
    public HBox getContainer()
    {
    	return container;
    }
    
    /**
     * changes the HBox where this Card is displayed
     * @param h the new HBox container to display this Card in
     */
    public void setContainer(HBox h)
    {
    	container = h;
    }
    
    /**
     * determines if this Card is the top of its stack
     * @return true if this Card instance is the top of its stack, else false
     */
    public boolean isTopOfStack()
    {
    	return this == myStack.peek();
    }
    
    /**
     * changes the CardStack this Card belongs to
     * @param s the new CardStack this Card belongs to
     */
    public void setStack(CardStack s)
    {
    	myStack = s;
    }
    
    /**
     * @return the CardStack this Card belongs to
     */
    public CardStack getStack()
    {
    	return myStack;
    }
    
    /**
     * adjusts the size settings of this Card's ImageView to standard defaults
     */
    public void setImageView()
    {
    	imageView.setFitHeight(75);
    	imageView.setPreserveRatio(true);
    }

    /**
     * @return this Card's ImageView
     */
    public ImageView getImageView()
    {
    	return imageView;
    }
    
    /**
     * @return this Card's Image, depending on its isFaceUp state
     */
    public Image getImage()
    {
    	if(isFaceUp)
    		return faceUpImage;
    	else
    		return faceDownImage;
    }
    
    /**
     * sets this Card's ImageView's Image according to this Card's isFaceUp 
     * state
     */
    public void setImage()
    {
    	if(isFaceUp)
    		imageView.setImage(faceUpImage);
    	else
    		imageView.setImage(faceDownImage);
    }

    /**
     * @return true if this Card is selected, false else
     */
    public boolean isSelected() {
		return isSelected;
	}

    /**
     * changes this Card's isSelected state, changes this Card's color to show 
     * if it is selected or not, and changes Card's static variables
     * selectedCard and selectedStack accordingly. This assumes only one Card
     * may be selected at a time.
     * @param isSelected if true, selects the Card, else deselects it
     */
	public void setIsSelected(boolean isSelected) {
		this.isSelected = isSelected;
		ColorAdjust c = new ColorAdjust();
		if(isSelected) 
		{
			c.setSaturation(.5);
			selectedCard = this;
			selectedStack = this.myStack;
		}
		else
		{
			c.setSaturation(0);
			selectedCard = null;
			selectedStack = null;
		}
		imageView.setEffect(c);
	}

	/**
	 * sets this Card faceUpImage path String, according to its value and suit
	 */
	private void setImagePath()
    {
   		imagePath = "PNG-cards-1.3/" + strValue + "_of_" + strSuit + "s.png"; 
   		//faceup Image path. To access facedown Image, use static faceDownImage
    }

	/**
	 * @return a String representation of this Card
	 */
	@Override
	public String toString() {
		if(value == 0)
			return "[  ]"; //emptyCard
		if(isFaceUp)
			return "[" + strValue2 + "" + suits[suit] + "]";
		else
			return "[XX]"; //faceDown Card
	}

	/**
	 * @return this Card's suit int. Use suits[suit] to access the char 
	 * representation of a Card's suit
	 */
	public int getSuit() {
		return suit;
	}
	
	/**
	 * changes this Card's suit
	 * @param suit the new suit int
	 * @throws InvalidSuitException if the suit int provided is out of range of
	 * suits represented in the suits array 
	 */
	public void setSuit(int suit) throws InvalidSuitException {
		if(suit > 4 || suit <= 0)
			throw new InvalidSuitException();
		else
			this.suit = suit;
	}
	
	/**
	 * @return this Card's value int (J = 11, Q = 12, K = 13, A = 1)
	 */
	public int getValue() {
		return value;
	}
	
	/**
	 * changes this Card's value int
	 * @throws ValueOutOfRangeException if value is greater than 13 or less 
	 * than 1 (emptyCards can have a value of 0, but they are initialized with
	 * it - a Card cannot be changed to an emptyCard if it is not initialized 
	 * as one)
	 */
	public void setValue(int value) throws ValueOutOfRangeException {
		if(value <= 0 || value >= values.length)
			throw new ValueOutOfRangeException();
		else
			this.value = value;
	}
	
	/**
	 * determines if a Card is face up
	 * @return isFaceUp true if it is, false else
	 */
	public boolean isFaceUp() {
		return isFaceUp;
	}
	
	/**
	 * set this Card's isFaceUp value to a given value, and change its Image
	 * accordingly
	 * @param isFaceUp the new value
	 */
	public void setFaceUp(boolean isFaceUp) {
		//if isFaceUp has the same value as this.isFaceUp, do nothing
		if(this.isFaceUp == isFaceUp) 
			return;
		//if isFaceUp is false and this Card is face up, flip it face down
		else if(this.isFaceUp && !isFaceUp)
		{
			this.isFaceUp = isFaceUp;
			//decrement the number of face up Cards in this Card's CardStack
			myStack.decrementCardsFaceUp(); 
		}
		//if isFaceUp is true and this Card is face down, flip it face up
		else if(!this.isFaceUp && isFaceUp)
		{
			this.isFaceUp = isFaceUp;
			//increment the number of face up Cards in this Card's CardStack
			myStack.incrementCardsFaceUp();
		}
		//change this Card's image accordingly
		setImage();
	}
}
