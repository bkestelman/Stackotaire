import javafx.scene.image.Image;


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
    //private Image image;
    
    public Card()
    {
    	suit = 0;
    	value = 0; 
    	isFaceUp = isSelected = false;
    	strSuit = strValue = imagePath = "";
    }
    
    public Card(int value, int suit, boolean isFaceUp) 
      throws InvalidSuitException, ValueOutOfRangeException
    {
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
    	//setImage();
    }
    
    public boolean isSelected() {
		return isSelected;
	}

	public void setIsSelected(boolean isSelected) {
		this.isSelected = isSelected;
	}

	public void setImagePath()
    {
    	if(isFaceUp)
    		imagePath = "PNG-cards-1.3/" + strValue + "_of_" + strSuit + "s.png";
    	else 
    		imagePath = "PNG-cards-1.3/facedown.jpg";
    }
    
    public String getImagePath()
    {
    	setImagePath();
    	return imagePath;
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
	}
}
