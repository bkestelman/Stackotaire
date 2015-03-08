import javafx.scene.effect.ColorAdjust;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;


public class Card {
    public static final String values[] = {" ","A","2","3","4","5","6","7","8","9","10","J","Q","K"};
    public static final char suits[]    = {' ', '\u2666', '\u2663','\u2665', '\u2660'};   // {' ', '♦', '♣','♥', '♠'}
    public static final int DIAMOND = 1;
    public static final int CLUB = 2;
    public static final int HEART = 3;
    public static final int SPADE = 4;
    public static final int JACK = 11;
    public static final int QUEEN = 12;
    public static final int KING = 13;
    public static final int ACE = 1; 
    
    private int suit, value;
    private boolean isFaceUp, isSelected;

	private String imagePath, strValue, strSuit, strValue2;
    private Image image;
    private ImageView imageView;
    private HBox container;
    
    private CardStack myStack;
    
    private static Card selectedCard;
    private static CardStack selectedStack;
    
    /*
     * constructs a new, empty Card
     */
    public Card()
    {
    	suit = 0;
    	value = 0; 
    	isFaceUp = isSelected = false;
    	strSuit = strValue = "";
    	imageView = new ImageView();
    	imagePath = "PNG-cards-1.3/empty.png";
    	setImage();
    	setImageView();
    }
    
    /*
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
    	setImage();
    	setImageView();
    }
    
    /*
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
    
    public static CardStack getSelectedStack()
    {
    	return selectedStack;
    }
    
    public static Card getSelectedCard()
    {
    	return selectedCard;
    }
    
    public boolean isRed()
    {
    	return suit == HEART || suit == DIAMOND;
    }
    
    public String getImagePath()
    {
    	return imagePath;
    }
    
    public void pushToHBox(HBox h)
    {
    	h.getChildren().add(imageView);
    	container = h;
    }
    
    public HBox getContainer()
    {
    	return container;
    }
    
    public void setContainer(HBox h)
    {
    	container = h;
    }
    
    /*
     * determines if this Card instance is the top of its stack
     * @return true if this Card instance is the top of its stack, else false
     */
    public boolean isTopOfStack()
    {
    	return this == myStack.peek();
    }
    
    public void setStack(CardStack s)
    {
    	myStack = s;
    }
    
    public CardStack getStack()
    {
    	return myStack;
    }
    
    public void setImageView()
    {
    	imageView.setFitHeight(75);
    	imageView.setPreserveRatio(true);
    }
    
    public void resetImageView()
    {
    	imageView = new ImageView();
    }
    
    public ImageView getImageView()
    {
    	return imageView;
    }
    
    public void setImageView(ImageView imageView)
    {
    	this.imageView = imageView;
    }
    
    public Image getImage()
    {
    	return image;
    }
    
    public void setImage()
    {
    	image = new Image(imagePath);
    	imageView.setImage(image);
    }
    
    public void setImage(Image image)
    {
    	this.image = image;
    	imageView.setImage(image);
    }
    
    public boolean isSelected() {
		return isSelected;
	}

    /*
     * changes this Card's isSelected value, changes this Card's color to show 
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

	private void setImagePath()
    {
    	if(isFaceUp)
    		imagePath = "PNG-cards-1.3/" + strValue + "_of_" + strSuit + "s.png";
    	else 
    		imagePath = "PNG-cards-1.3/facedown.jpg";
    }

	@Override
	public String toString() {
		if(value == 0)
			return "[  ]";
		if(isFaceUp)
			return "[" + strValue2 + "" + suits[suit] + "]";
		else
			return "[XX]";
	}

	public int getSuit() {
		return suit;
	}
	
	public void setSuit(int suit) throws InvalidSuitException {
		if(suit > 4 || suit <= 0)
			throw new InvalidSuitException();
		else
			this.suit = suit;
	}
	
	public int getValue() {
		return value;
	}
	
	/*
	 * 
	 */
	public void setValue(int value) throws ValueOutOfRangeException {
		if(value <= 0 || value >= values.length)
			throw new ValueOutOfRangeException();
		else
			this.value = value;
	}
	
	/*
	 * determines if a Card is face up
	 * @return isFaceUp self explanatory
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
		setImagePath();
		setImage();
	}
}
