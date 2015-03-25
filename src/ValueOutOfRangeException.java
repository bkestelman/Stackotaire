/**
 * Benito Kestelman
 * ID 109292160
 * Homework #3
 * CSE 214: R06
 * Recitation TA: Kevin Flyangolts
 * Grading TA: Zheyuan Gao
 * 
 * <code>ValueOutOfRangeException</code> extends Exception and indicates a Card
 * was initialized or set to an inappropriate value, likely less than or equal 
 * to 0 (value 0 is reserved for a CardStack's emptyCard), or greater than 13
 * (the max int value of a standard deck of Cards, with J, Q, K). The 
 * acceptable value range may differ if Card is modified to accommodate non-
 * standard decks, or this exception is used for a different Object.
 *   
 * @author benito.kestelman@stonybrook.edu
 */
public class ValueOutOfRangeException extends Exception {
	
	/**
	 * creates a new ValueOutOfRangeException
	 */
	public ValueOutOfRangeException() {
		super();
	}
	
	/**
	 * creates a new ValueOutOfRangeException, with a message String that can
	 * be accessed through Exception's getMessage() method
	 * @param message a friendly error message
	 */
	public ValueOutOfRangeException(String message) {
		super(message);
	}
}
