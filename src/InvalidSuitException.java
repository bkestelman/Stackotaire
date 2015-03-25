/**
 * Benito Kestelman
 * ID 109292160
 * Homework #3
 * CSE 214: R06
 * Recitation TA: Kevin Flyangolts
 * Grading TA: Zheyuan Gao
 * 
 * <code>InvalidSuitException</code> extends Exception and indicates a Card
 * was initialized or set to an inappropriate suit, likely less than or equal 
 * to 0 (suit 0 is reserved for a CardStack's emptyCard), or greater than 4
 * (since there are only 4 suits in a standard deck). The acceptable value 
 * range may differ if Card is modified to accommodate non-standard decks, 
 * or this exception is used for a different Object.
 *   
 * @author benito.kestelman@stonybrook.edu
 */
public class InvalidSuitException extends Exception {
	
	/**
	 * creates a new InvalidSuitException
	 */
	public InvalidSuitException() {
		super();
	}
	
	/**
	 * creates a new InvalidSuitException with a message that can be accessed
	 * through the inherited getMessage() method from the Exception class
	 * @param message a friendly error message
	 */
	public InvalidSuitException(String message) {
		super(message);
	}

}
