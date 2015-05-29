import java.math.BigDecimal;


/** 
 *  @author Viswanadha Pratap
 *  NetID : vxk147730
 */
public class Customer {

	long id; // Customer identification number
	int[] categories; // customer interests
	int noOfCategories; 
	int noOfPurchases;
	BigDecimal amount;
	boolean included;
	boolean seen;
	Customer()
	{
		categories = new int[1000];
		noOfCategories = 0;
		noOfPurchases = 0;
		seen = false;
		included = false;
	}
	
}
