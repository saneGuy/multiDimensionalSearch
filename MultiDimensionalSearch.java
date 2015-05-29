
import java.util.*;
import java.io.*;
import java.math.BigDecimal;

/** 
 *  @author Viswanadha Pratap
 *  NetID : vxk147730
 */
public class MultiDimensionalSearch {
    static int[] categories;
    static final int NUM_CATEGORIES = 1000, MOD_NUMBER = 997;
    static int DEBUG = 9;
    private int phase = 0;
    private long startTime, endTime, elapsedTime;
    
    // HashMap to store the <id,Customer> pairs
    private HashMap<Long,Customer> haMap;
    /*HashMap<category,TreeSet<Customer>> where the TreeSet is
      ordered by decreasing values of amounts spent by the 
      Customers in that category
    */
    private HashMap<Integer,TreeSet<Customer>> haMapPerCategory;
    /*
     * HashMap which stores the <noOfCategories,setOfCustomersWithNoOfCategories>
     */
    private HashMap<Integer,TreeSet<Customer>> haMapPerNoOfCategories;

    public static void main(String[] args)  throws FileNotFoundException {
	categories = new int[NUM_CATEGORIES];
	Scanner in;
	if(args.length > 0) {
	    in = new Scanner(new File(args[0]));
        } else {
	    in = new Scanner(System.in);
	}
	MultiDimensionalSearch x = new MultiDimensionalSearch();
	x.haMap = new HashMap<>();
	x.haMapPerCategory =  new HashMap<>();
	x.haMapPerNoOfCategories = new HashMap<>();
	x.timer();
	long rv = x.driver(in);
	System.out.println(rv);
	x.timer();
    }

    /** Read categories from in until a 0 appears.
     *  Values are copied into static array categories.  Zero marks end.
     * @param in : Scanner from which inputs are read
     * @return : Number of categories scanned
     */

    public static int readCategories(Scanner in) {
    // taken a set to remove duplicate interests for customers
   	Set<Integer> set = new HashSet<>();
	int cat = in.nextInt();
	int index = 0;
	while(cat != 0 ) {
		if(set.add(new Integer(cat)))
		{
	      categories[index++] = cat;
		}
	    cat = in.nextInt();
	}
	categories[index] = 0;
	set.removeAll(set);
	return index;
    }

    public long driver(Scanner in) {
      String s;
      long rv = 0, id;
      int cat;
      double purchase;

      while(in.hasNext()) {
	  s = in.next();
	  if(s.charAt(0) == '#') {
	      s = in.nextLine();
	      continue;
	  }
	  if(s.equals("Insert")) {
	      id = in.nextLong();
	      readCategories(in);
	      rv += insert(id, categories);
	     } else if(s.equals("Find")) {
	      id = in.nextLong();
	      rv += find(id);
	  } else if(s.equals("Delete")) {
	      id = in.nextLong();
	      rv += delete(id);
	     } else if(s.equals("TopThree")) {
	      cat = in.nextInt();
	      rv += topthree(cat);
	  } else if(s.equals("AddInterests")) {
	      id = in.nextLong();
	      readCategories(in);
	      rv += addinterests(id, categories);
	    } else if(s.equals("RemoveInterests")) {
	      id = in.nextLong();
	      readCategories(in);
	      rv += removeinterests(id, categories);
	     } else if(s.equals("AddRevenue")) {
	      id = in.nextLong();
	      purchase = in.nextDouble();
	      rv += addrevenue(id, purchase);
	     } else if(s.equals("Range")) {
	      double low = in.nextDouble();
	      double high = in.nextDouble();
	      rv += range(low, high);
	  } else if(s.equals("SameSame")) {
		 rv += samesame();
	  } else if(s.equals("NumberPurchases")) {
	      id = in.nextLong();
	     rv += numberpurchases(id);
	  } else if(s.equals("End")) {
		 return rv % 997 ;
	    } else {
	      System.out.println("Houston, we have a problem.\nUnexpected line in input: "+ s);
	      System.exit(0);
	  }
      }
      // This can be inside the loop, if overflow is a problem
      rv = rv % MOD_NUMBER;
      
      return rv;
    }

    public void timer()
    {
        if(phase == 0) {
	    startTime = System.currentTimeMillis();
	    phase = 1;
	} else {
	    endTime = System.currentTimeMillis();
            elapsedTime = endTime-startTime;
            System.out.println("Time: " + elapsedTime + " msec.");
            memory();
            phase = 0;
        }
    }

    public void memory() {
        long memAvailable = Runtime.getRuntime().totalMemory();
        long memUsed = memAvailable - Runtime.getRuntime().freeMemory();
        System.out.println("Memory: " + memUsed/1000000 + " MB / " + memAvailable/1000000 + " MB.");
    }


    /*
     * insert will insert the customers into the the hashMaps
     *  haMap,haMapPerCategory,haMapPerNoOfCategories
     */
    int insert(long id, int[] categories) { 
    	if(haMap.containsKey(id))
    	{
    		return -1;
    	}
    	else
    	{
    		Customer newCustomer = new Customer();
    		newCustomer.id = id;
    		int i = 0;
    		for( i = 0;i < categories.length && categories[i] != 0;i++)
    		{
    		newCustomer.categories[i] = categories[i];	
    	    }
    		newCustomer.noOfCategories = i;
    		newCustomer.amount = new BigDecimal("0.0");
    		haMap.put(id,newCustomer);
    		TreeSet<Customer> setOfCustomersWithSameCategory;
    		TreeSet<Customer> setOfCustomersWithSameNoOfCategories;
    		boolean flag;
    		for( i = 0; i < categories.length && categories[i]!=0; i++)
    		{
    			flag = haMapPerCategory.containsKey(newCustomer.categories[i]);
    			if(flag == false)
    			{
    				setOfCustomersWithSameCategory = new TreeSet<Customer>(new OrderByAmount());
    				setOfCustomersWithSameCategory.add(newCustomer);
    				haMapPerCategory.put(new Integer(categories[i]),setOfCustomersWithSameCategory);
    			}
    			else
    			{
    			    setOfCustomersWithSameCategory = haMapPerCategory.get(newCustomer.categories[i]);
    			    setOfCustomersWithSameCategory.add(newCustomer);
				    haMapPerCategory.put(new Integer(categories[i]),setOfCustomersWithSameCategory);
    			}
    		}
    		/*
    		 * Inserting into the HashMap<noOfCategories,TreeSet<Customers>>
    		 */
    		setOfCustomersWithSameNoOfCategories = haMapPerNoOfCategories.get(newCustomer.noOfCategories);
    		if(setOfCustomersWithSameNoOfCategories == null)
    		{
    			setOfCustomersWithSameNoOfCategories = new TreeSet<>(new OrderByAmount());
    			setOfCustomersWithSameNoOfCategories.add(newCustomer);
    		}
    		else
    		{
    			setOfCustomersWithSameNoOfCategories.add(newCustomer);
    		}
    		haMapPerNoOfCategories.put(newCustomer.noOfCategories,setOfCustomersWithSameNoOfCategories);
    		return 1;
    	}
    	}
    /*
     * find will get the amount spent by the customer with id from the 
     * haMap which has <id,Customer> pairs stored in it
     */
    int find(long id) { 
    	Customer customerWithID = haMap.get(id);
    	if(customerWithID == null)
    	{
    		return -1;
    	}
    	return (customerWithID.amount).intValue();
    	}
    /*
     * delete will delete the customer with id and returns the amount
     * spent by the customer. If the customer doesn'e exist then it will
     * return -1
     */
    int delete(long id) { 
    	Customer existingCustomer = this.haMap.get(id);
    	if(existingCustomer == null)
    	{
    		return -1;
    	}
    	BigDecimal amountSpentByThisCustomer = new BigDecimal("0.0");
    	amountSpentByThisCustomer = existingCustomer.amount;
    	TreeSet<Customer> ts;
    	for( int i = 0;i<existingCustomer.categories.length && existingCustomer.categories[i] != 0;i++)
    	{
    		//removing the customer object from each of the set of customers 
    		//corresponding to each category
    		ts = haMapPerCategory.get(existingCustomer.categories[i]);
    		if(ts == null)
    		{
    			return -1;
    		}
    		ts.remove(existingCustomer);
    		haMapPerCategory.put(existingCustomer.categories[i], ts);
    	}
    	haMap.remove(id); // removing from the first hashMap
    	return amountSpentByThisCustomer.intValue();
    	}
    /*
     * topthree will return the sum of the amounts spent by top three 
     * customers in category given as input i.e cat
     */
    int topthree(int cat)
    {
    	TreeSet<Customer> setOfCustomersInCategory;
    	setOfCustomersInCategory = haMapPerCategory.get(cat);
    	int count = 0 ;BigDecimal TotalAmountSpentByThreeCustomers = new BigDecimal("0.0");
    	Iterator<Customer> it = setOfCustomersInCategory.descendingIterator();
    	while(it.hasNext() && count <3 )
    	{
    		TotalAmountSpentByThreeCustomers = TotalAmountSpentByThreeCustomers.add((it.next()).amount);
    		count++;
    	}
    	return TotalAmountSpentByThreeCustomers.intValue();
    	}
    
    /* we will search if the category exists in the haMapPerCategory
	   if it doesn't exist we will add an entry to it else we will add the
	   customerWithID object to the set in category and append the category value
	   in the categories[] in the haMap
	 */
    int addinterests(long id, int[] categories) {
    	Customer customerWithID = haMap.get(id);
    	TreeSet<Customer> setOfCustomersInCategory;
    	TreeSet<Customer> setOfCustomersInNoOfCategories;
    	int noOfNewlyAddedInterestsToTheCustomer = 0;
    	for( int i = 0 ; i < categories.length && categories[i] != 0 ; i++)
    	{
    		setOfCustomersInCategory = haMapPerCategory.get(categories[i]);
    		if(setOfCustomersInCategory == null)
    		{
    			setOfCustomersInCategory = new TreeSet<Customer>(new OrderByAmount());
    			setOfCustomersInNoOfCategories = new TreeSet<>();
    			customerWithID.categories[customerWithID.noOfCategories] = categories[i];
    			if(setOfCustomersInCategory.add(customerWithID))
    			{
    				setOfCustomersInNoOfCategories = haMapPerNoOfCategories.get(customerWithID.noOfCategories);
    				setOfCustomersInNoOfCategories.remove(customerWithID);
    				haMapPerNoOfCategories.put(customerWithID.noOfCategories,setOfCustomersInNoOfCategories );
    				customerWithID.noOfCategories = customerWithID.noOfCategories + 1;
    				noOfNewlyAddedInterestsToTheCustomer++;
    				setOfCustomersInNoOfCategories = haMapPerNoOfCategories.get(customerWithID.noOfCategories);
    				if(setOfCustomersInNoOfCategories == null)
    				{
    				setOfCustomersInNoOfCategories = new TreeSet<>(new OrderByAmount());
    				}
    				setOfCustomersInNoOfCategories.add(customerWithID);
    				haMapPerNoOfCategories.put(customerWithID.noOfCategories,setOfCustomersInNoOfCategories );
    			}
    		}
    		else
    		{
    			if(setOfCustomersInCategory.contains(customerWithID))
    			{
    				continue;
    			}
    			customerWithID.categories[customerWithID.noOfCategories] = categories[i];
    			if(setOfCustomersInCategory.add(customerWithID))
    			{
    				setOfCustomersInNoOfCategories = haMapPerNoOfCategories.get(customerWithID.noOfCategories);
    				setOfCustomersInNoOfCategories.remove(customerWithID);
    				haMapPerNoOfCategories.put(customerWithID.noOfCategories,setOfCustomersInNoOfCategories );
    				customerWithID.noOfCategories = customerWithID.noOfCategories + 1;
    				noOfNewlyAddedInterestsToTheCustomer++;
    				setOfCustomersInNoOfCategories = haMapPerNoOfCategories.get(customerWithID.noOfCategories);
    				if(setOfCustomersInNoOfCategories == null)
    				{
    				setOfCustomersInNoOfCategories = new TreeSet<>(new OrderByAmount());
    				}
    				setOfCustomersInNoOfCategories.add(customerWithID);
    				haMapPerNoOfCategories.put(customerWithID.noOfCategories,setOfCustomersInNoOfCategories );
    			}
    		}
    		haMapPerCategory.put(categories[i], setOfCustomersInCategory);
    		haMap.put(id, customerWithID);
    	}
    	return noOfNewlyAddedInterestsToTheCustomer;
    	}
    /*
     * removeinterests will remove interests from the customer object in haMap and
     * removes the customer object from the set corresponding to the category in
     * haMapPerCategory
     */
    int removeinterests(long id, int[] categories) { 
    	
    	Customer customerWithID = haMap.get(id);
    	if(customerWithID == null)
    	{
    		return -1;
    	}
        TreeSet<Customer> setOfCustomersInCategory;
    	TreeSet<Customer> setOfCustomersInNoOfCategories;
    	for( int i = 0 ; i < categories.length && categories[i] != 0 ; i++)
    	{
    		setOfCustomersInCategory = haMapPerCategory.get(categories[i]);
    		if(setOfCustomersInCategory == null)
    		{
    			continue;
    		}
    		else
    		{
    			setOfCustomersInCategory.remove(customerWithID);
    			setOfCustomersInNoOfCategories = new TreeSet<>(new OrderByAmount());
    			
    			for(int j = 0;j < customerWithID.noOfCategories  ;j++ )
    			{
    				if(customerWithID.categories[j] == categories[i])
    				{    				
    					customerWithID.categories[j] = 0;
    					setOfCustomersInNoOfCategories = haMapPerNoOfCategories.get(customerWithID.noOfCategories);
    					setOfCustomersInNoOfCategories.remove(customerWithID);
    					
    					haMapPerNoOfCategories.put(customerWithID.noOfCategories, setOfCustomersInNoOfCategories);
    					customerWithID.noOfCategories = customerWithID.noOfCategories - 1;
    					setOfCustomersInNoOfCategories = haMapPerNoOfCategories.get(customerWithID.noOfCategories);
    					if(setOfCustomersInNoOfCategories == null)
    					{
    					setOfCustomersInNoOfCategories =  new TreeSet<>(new OrderByAmount());
    					}
    					setOfCustomersInNoOfCategories.add(customerWithID);
    					haMapPerNoOfCategories.put(customerWithID.noOfCategories, setOfCustomersInNoOfCategories);
    					InsertionSort(customerWithID.categories); 
    				}
    			}
    		}
    		haMapPerCategory.put(categories[i], setOfCustomersInCategory);
    		haMap.put(id, customerWithID);
    	}
    	//System.out.println(customerWithID.noOfCategories);
        return customerWithID.noOfCategories;
    	}
    /*
     * addrevenue will add the amount to the customer's existing purchase amount 
     * in both haMap and haMapPerCategory and returns the updated amount
     */
    int addrevenue(long id, double purchase1) { 
    	BigDecimal purchase = BigDecimal.valueOf(purchase1);
    	Customer existingCustomer,updatedCustomerWithPurchase=null;
    	existingCustomer = this.haMap.get(id);
    	if(existingCustomer == null)
    	{    		
    		return -1;
    	}
    	BigDecimal updatedAmount = new BigDecimal("0.0");
    	updatedCustomerWithPurchase = new Customer();
		updatedCustomerWithPurchase.id = existingCustomer.id;
		updatedCustomerWithPurchase.categories = existingCustomer.categories;
		updatedCustomerWithPurchase.amount = (existingCustomer.amount).add(purchase);
		updatedCustomerWithPurchase.noOfCategories = existingCustomer.noOfCategories;
		updatedCustomerWithPurchase.noOfPurchases = existingCustomer.noOfPurchases + 1;
    	TreeSet<Customer> setOfCustomersWithCategory;
    	
    	for( int i = 0;i<existingCustomer.categories.length && existingCustomer.categories[i] != 0;i++)
    	{
    		setOfCustomersWithCategory = this.haMapPerCategory.get(existingCustomer.categories[i]);
     		setOfCustomersWithCategory.remove(existingCustomer);
     	    setOfCustomersWithCategory.add(updatedCustomerWithPurchase);
       	    haMapPerCategory.put(updatedCustomerWithPurchase.categories[i], setOfCustomersWithCategory);
    	}
    	   	updatedAmount = haMap.get(id).amount.add(purchase);
    	   	haMap.remove(id);
    	   	haMap.put(id,updatedCustomerWithPurchase );
    	  	return updatedAmount.intValue();
    	}
    /*
     * range will return the number of customer in the range given as input
     * i.e customers with purchase amounts in between low and high 
     */
    int range(double low,double high)
    {
    	BigDecimal lowValue = BigDecimal.valueOf(low);
    	BigDecimal highValue = BigDecimal.valueOf(high);
    	Collection<Customer> sc = haMap.values();
    	int noOfCustomersInTheRange = 0;
    	for(Customer c : sc)
    	{
    		if(c.amount.compareTo(lowValue) == 1 && c.amount.compareTo(highValue) == -1 || c.amount.compareTo(lowValue) == 0 || c.amount.compareTo(highValue) == 0)
    		{
    			noOfCustomersInTheRange++;
    		}
    		
    	}
    	return noOfCustomersInTheRange;
    }
    
    /*
     * samesame will return the number of customers with the
     * same number of same categories
     */
    int samesame() { 
    	Set<Integer> setOfNoOfCategories =  haMapPerNoOfCategories.keySet();
    	TreeSet<Customer> setOfCustomers;
    	int noOfCustomerWithSameCategories = 0;
    	for(Integer i : setOfNoOfCategories)
    	{
    		if(i >= 5)
    		{
    	   	setOfCustomers = haMapPerNoOfCategories.get(i);
    		if(setOfCustomers.isEmpty())
    		{
    			continue;
    		}
    		for(Customer c : setOfCustomers)
    		{
    			c.included = false;
    		}
    		
    		/*
    		 * converted setOfCustomers to an array for comparison
    		 */
    		Customer[] arr = setOfCustomers.toArray(new Customer[setOfCustomers.size()]);
     		boolean flag = false;
    			for(int k = 0; k < arr.length  ; k++)
    			{
    				if(arr[k].included == true)
    				{
    					continue;
    				}
    			    for(int p = k+1 ; p < arr.length  ;p++)
    					{
    			    	
    			    	if(arr[p].included == true)
    			    	{
    			    		continue;
    			    	}
    						if(same(arr[k].categories,arr[p].categories,i))
    	    				{					
    	    					//count1++;
    							noOfCustomerWithSameCategories++;
    							arr[p].included = true;
    							flag = true;
    	    								    					
    	    				}
    						
    					}
    				if(flag == true)
        			{
        				noOfCustomerWithSameCategories++;
        				flag = false;
        			}
    					
    			}
    		}
    	}
    	System.out.println(noOfCustomerWithSameCategories);
    	return noOfCustomerWithSameCategories;
    	}
    /*
     * same will return true if all the elements in categories1 is same
     * as the elements in categories2. else it will return false
     */
    private boolean same(int[] categories1, int[] categories2,int lengthOfArray)
    {
		for(int i = 0; i < lengthOfArray ; i ++)
    	{
    		if(categories1[i] != categories2[i])
    		{
    			return false;
    		}
    	}
		return true;
	}

    /*
     * numberpurchases will return the number of purchases
     * made by the customer
     */
	int numberpurchases(long id) {
    	Customer customerWithID = haMap.get(id);
    	if(customerWithID == null)
    	{
    		return -1;
    	}
    	return customerWithID.noOfPurchases;
    	}
	
    public static void InsertionSort( int [ ] num)
    {
         int j;                  
         int key;               
         int i;  
         for (j = 1; j < num.length; j++) 
        {
               key = num[ j ];
               for(i = j - 1; (i >= 0) && (num[ i ] < key); i--)
              {
                     num[ i+1 ] = num[ i ];
              }
             num[ i+1 ] = key;
         }
    }
}
