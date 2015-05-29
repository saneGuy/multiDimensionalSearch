
/** 
 *  @author Viswanadha Pratap
 *  NetID : vxk147730
 */
import java.util.Comparator;


public class OrderByAmount implements Comparator<Customer> {
	
	public int compare(Customer c1,Customer c2)
	{
		
		int flag = c1.amount.compareTo(c2.amount);
		if(flag == 1)
		{
			return 1;
		}
		else if (flag == -1)
		{
			return -1;
		}
		else 
			{ if(c1.equals(c2) )
				return 0;
			  if (c1.id > c2.id)
				return 1;
			  else
				  return -1;
			
			}
	}
	
	

}
