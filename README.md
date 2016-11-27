
MultiDimensional Search

Data Structures used  to store data :
1) HashMap<Long, Customer> customerInfo  -> hashmap to store customer id and customer object pairs
2) HashMap<Integer, TreeSet<Customer>> customersInCategory -> hashmap to store category number and treeset of customers
   ordered in the decreasing order of the amount spent in that category
3) HashMap<Integer, TreeSet<Customer>> customersWithNoOfCategories -> hashmap to store number of categories and treeset of
   customers with that number of categories.


How it works?

Methods :

1) int readCategories(Scanner in) : reads categories from the scanner object and stores in the categories array. Returns
    the number of categories read;
2) long driver(Scanner in) : reads the input from the scanner object, based on the input string it cals the respective method.
    and updated the rv value(return value) that is returned by the driver method;
3) timer() : that computes the time taken by the methods. It is used around the method cals to compute the time taken by the
    method;
4) method() : similar to the timer() method, computes the memory used by the method.
5) insert(long id, int[] categories) : inserts a customer into the hashmaps userInfo if the customer doesn't already exist,
    for each of the categories of the new customer if it is present in the customersInCategory map, gets the corresponding TreeSet
    and adds the new customer to it. If the category is not present in the customersInCategory map creates a new TreeSet adds the
    new customer object to it and puts the category, TreeSet pair into the customersInCategory map. Also checks if the key number of categories exists in the mao customersWithNoOfCategories, if it is present gets the TreeSet corresponding to it and adds the new
    customer object to it, else it will create a new TreeSet, adds the customer object to it and puts the no of categories and corresponding treeset into the hashmap.

6) int find(long id) : takes the customer id as the argument and returns the amount spent by the customer as an integer. If customer
    is not present then it returns -1.
7) int delete(long id) : if the customer exists then removes it from all three hashmaps and returns the amount spent by the customer
8) int topThree(int cat) : returns the amount spent by top three customers in the category cat given as argument. Gets the TreeSet of
    customers from the customersInCategory hashmap and iterates over it and adds the amounts spent by the first three customers and
    returns it.
9) int addInterests(long id, int[] categories) : adds the new categories to the customer with the given id and returns the number of
    newly added categories to the customer.
10) int removeInterests(long id, int[] categories) : removes the categories from the customers categories and removes the customer
    object references from the customersInCategories and removes the customer object from the customersInNoOfCategories and adds it
    to the TreeSet corresponding to the new key i.e new number of categories of the customer. Returns the new number of categories 
    of the customer.

11) int addRevenue(long id, double purchase) : adds the purchase amount to the customer object in userInfo and customerInCategory maps
    and returns the updated revenue by the customer.
12) int range(double low, double high) : range will returns the number of customers with purchase amounts in the range low and high. 
    Iterates over the values of the userInfo map and checks if the amounts fall in the range low-high and counts and returns the same.
13) int sameSame() : will return the number of customers with same number of same categories.





