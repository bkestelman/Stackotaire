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

	private String imagePath, strValue, strSuit;
    private Image image;
    private ImageView imageView;
    private HBox container;
    
    private CardStack myStack;
    
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
     * constructs a new instance of Card for a given value, suit, and isFaceUp boolean, and gives it an ImageView with an appropriate Image
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
    				  break;
    		case JACK: strValue = "jack";
    				   break;
    		case QUEEN: strValue = "queen";
    					break;
    		case KING: strValue = "king";
    				   break;
    		default: strValue = value + "";
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
    	imageView.setImage(image);
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
    }
    
    public void setImage(Image image)
    {
    	this.image = image;
    	imageView.setImage(image);
    }
    
    public boolean isSelected() {
		return isSelected;
	}

	public void setIsSelected(boolean isSelected) {
		this.isSelected = isSelected;
		ColorAdjust c = new ColorAdjust();
		if(isSelected) 
			c.setSaturation(.5);
		else
			c.setSaturation(0);
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
		return "Card [suit=" + suits[suit] + ", value=" + values[value] + ", isFaceUp="
				+ isFaceUp + "]";
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
	
	public void setValue(int value) throws ValueOutOfRangeException {
		if(value <= 0 || value >= values.length)
			throw new ValueOutOfRangeException();
		else
			this.value = value;
	}
	
	public boolean isFaceUp() {
		return isFaceUp;
	}
	
	public void setFaceUp(boolean isFaceUp) {
		this.isFaceUp = isFaceUp;
		setImagePath();
		setImage();
		setImageView();
	}
}
