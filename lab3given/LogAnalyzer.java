import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;


public class LogAnalyzer
{
      //constants to be used when pulling data out of input
      //leave these here and refer to them to pull out values
   private static final String START_TAG = "START";
   private static final int START_NUM_FIELDS = 3;
   private static final int START_SESSION_ID = 1;
   private static final int START_CUSTOMER_ID = 2;
   private static final String BUY_TAG = "BUY";
   private static final int BUY_NUM_FIELDS = 5;
   private static final int BUY_SESSION_ID = 1;
   private static final int BUY_PRODUCT_ID = 2;
   private static final int BUY_PRICE = 3;
   private static final int BUY_QUANTITY = 4;
   private static final String VIEW_TAG = "VIEW";
   private static final int VIEW_NUM_FIELDS = 4;
   private static final int VIEW_SESSION_ID = 1;
   private static final int VIEW_PRODUCT_ID = 2;
   private static final int VIEW_PRICE = 3;
   private static final String END_TAG = "END";
   private static final int END_NUM_FIELDS = 2;
   private static final int END_SESSION_ID = 1;

      //a good example of what you will need to do next
      //creates a map of sessions to customer ids
   private static void processStartEntry(
      final String[] words,
      final Map<String, List<String>> sessionsFromCustomer)
   {
      if (words.length != START_NUM_FIELDS)
      {
         return;
      }

         //check if there already is a list entry in the map
         //for this customer, if not create one
      List<String> sessions = sessionsFromCustomer
         .get(words[START_CUSTOMER_ID]);
      if (sessions == null)
      {
         sessions = new LinkedList<>();
         sessionsFromCustomer.put(words[START_CUSTOMER_ID], sessions);
      }

         //now that we know there is a list, add the current session
      sessions.add(words[START_SESSION_ID]);
   }

      //similar to processStartEntry, should store relevant view
      //data in a map - model on processStartEntry, but store
      //your data to represent a view in the map (not a list of strings)
   private static void processViewEntry(final String[] words,
        final Map<String, List<View>> viewsFromSession)
   {

      if (words.length != VIEW_NUM_FIELDS)
      {
         return;
      }

      //check if there already is a list entry in the map
      //for this customer, if not create one
      List<View> products = viewsFromSession
              .get(words[VIEW_SESSION_ID]);
      if (products == null) {
         products = new LinkedList<>();
         
      }
      String session = words[VIEW_SESSION_ID];
      boolean found = false;
      for (View product: products){
            if (product.productId.equals(words[VIEW_PRODUCT_ID])){
               for (String sesh: product.views){
                  if (sesh.equals(session)){
                     break ;
                  }
               }
               product.views.add(words[VIEW_SESSION_ID]);
               found = true;
               break;
            }
         }
         if (!found) {
            List <String> sesh_viewed = new LinkedList<>();
            List <String> sesh_bought = new LinkedList<>();
            sesh_viewed.add(words[VIEW_SESSION_ID]);
            View new_product = new View(words[VIEW_PRODUCT_ID], Integer.valueOf(words[VIEW_PRICE]));
            products.add(new_product);

         }



   }

      //similar to processStartEntry, should store relevant purchases
      //data in a map - model on processStartEntry, but store
      //your data to represent a purchase in the map (not a list of strings)
   private static void processBuyEntry(
      final String[] words,
        final Map<String, List<Buy>> productsFromSession)
   {
      if (words.length != VIEW_NUM_FIELDS)
      {
         return;
      }

      //check if there already is a list entry in the map
      //for this customer, if not create one
      List<Buy> products = productsFromSession
              .get(words[BUY_SESSION_ID]);
      if (products == null)
      {
         products = new LinkedList<>();
         String current_sesh_id = words[BUY_SESSION_ID];
         productsFromSession.put(current_sesh_id, products);
      }
      else {

         String session = words[BUY_SESSION_ID];
         boolean found = false;
         for (Buy product: products){
            if (product.productId.equals(words[BUY_PRODUCT_ID])){
               product.num_purchased +=1;
               for (String sesh: product.views){
                  if (sesh.equals(session)){
                     break;
                  }
               }
               product.bought.add(words[BUY_SESSION_ID]);
               found = true;
               break;
            }
         }
         if (!found) {
            List <String> num_viewed = new LinkedList<>();
            List <String> num_bought = new LinkedList<>();
            num_bought.add(words[BUY_SESSION_ID]);
            Buy new_product = new Buy(words[BUY_PRODUCT_ID], Integer.valueOf(words[BUY_PRICE]));
            products.add(new_product);
         }

      }


   }

   private static void processEndEntry(final String[] words)
   {
      if (words.length != END_NUM_FIELDS)
      {
         return;
      }
   }

      //this is called by processFile below - its main purpose is
      //to process the data using the methods you write above
   private static void processLine(
      final String line,
      Map<String, List<String>> sessionsFromCustomer,
      Map<String, List<View>> viewsFromSession,
   Map<String, List<Buy>> buysFromSession)
   {
      final String[] words = line.split("\\h");

      if (words.length == 0)
      {
         return;
      }

      switch (words[0])
      {
         case START_TAG:
            processStartEntry(words, sessionsFromCustomer);
            break;
         case VIEW_TAG:
            processViewEntry(words, viewsFromSession);
            break;
         case BUY_TAG:
            processBuyEntry(words, buysFromSession);
            break;
         case END_TAG:
            processEndEntry(words);
            break;
      }
   }

      //write this after you have figured out how to store your data
      //make sure that you understand the problem
   private static void printViewsNotPurchased(Map<String, List<String>> sessionsFromCustomer, Map<String, List<Buy>> BuyFromSession, Map<String, List<View>> viewsFromSession)
   {
      int no_purchase_sesh = 0;
      int total_views = 0;
      for (String session: BuyFromSession.keySet()) {
         boolean found = false;
         for (Buy bought : BuyFromSession.get(session)) {
            System.out.print(bought.productId);
            found = true;
         }
         for (View viewed : viewsFromSession.get(session)){
            total_views += viewed.num_viewed;
         }
         if (found){
            no_purchase_sesh += 1;
         }
      }

      double result = (double)(total_views/no_purchase_sesh);
      System.out.println("Average Views without Purchase: " + result);
   }

   private static void printSessionPriceDifference(Map<String, List<String>> sessionsFromCustomer, Map<String, List<Buy>> buysFromSession,Map<String, List<View>> viewsFromSession) {
      System.out.println("Price Difference for Purchased Product by Session");
      for (String session : buysFromSession.keySet()) {
         List<Buy> purchased_prods = new LinkedList<>();
         int amount_viewed = 0;
         int cost_bought = 0;
         for (Buy product : buysFromSession.get(session)) {
            cost_bought = product.cost;
            int viewed_cost = 0;
            for (View viewed: viewsFromSession.get(session)){
               amount_viewed +=1;
               viewed_cost += viewed.cost;
            }
            System.out.println(product.productId + ":" + (double)(cost_bought - (viewed_cost/ amount_viewed)));
         }

      /* add printing */
      }
   }

      //write this after you have figured out how to store your data
      //make sure that you understand the problem
   private static void printCustomerItemViewsForPurchase(Map<String, List<String>> sessionsFromCustomer,
    Map<String, List<View>> viewsFromSession, Map<String, List<Buy>> buysFromSession
      /* add parameters as needed */
      )
   {
      System.out.println("Number of Views for Purchased Product by Customer");
      Set<String> customers = sessionsFromCustomer.keySet();
      for (String customer: customers){
         List<Integer> bought_by_cust = new ArrayList<Integer>();
         List<Integer>viewed_by_cust = new ArrayList<Integer>();
         List<String> sessions_per_cust = sessionsFromCustomer.get(customer);
         for (String session: sessions_per_cust){
            List<Buy> bought_per_sesh =  buysFromSession.get(session);
            for (Buy bought : bought_per_sesh) {
               int idAsInt = bought.productIDtoInt();
               int pos = bought_by_cust.get(idAsInt);
               bought_by_cust.set(idAsInt, pos + 1);
            }
            List<View> view_per_sesh = viewsFromSession.get(session);
            for (View viewed : view_per_sesh){
               int idAsInt = viewed.productIDtoInt();
               int pos = viewed_by_cust.get(idAsInt);
               viewed_by_cust.set(idAsInt, pos + 1);

            }
         }
         int count = 0;
         for (int i = 0; i < bought_by_cust.size(); i ++){
            if (bought_by_cust.get(i)>= 1){
               if (viewed_by_cust.get(i)>=1){
                  if (count == 0){
                     System.out.println(customer);
                  }
                  count +=1;
                  System.out.println("product".concat(Integer.toString(viewed_by_cust.get(i))) + " " +viewed_by_cust.get(i));
               }
            }
         }



      }


      /* add printing */
   }

      //write this after you have figured out how to store your data
      //make sure that you understand the problem
   private static void printStatistics(Map<String, List<String>> sessionsFromCustomer,
                                       Map<String, List<View>> viewsFromSession, Map<String, List<Buy>> buysFromSession)
      /* add parameters as needed */

   {
      //printSessionPriceDifference( /*add arguments as needed */);
      //printCustomerItemViewsForPurchase( /*add arguments as needed */);

      /* This is commented out as it will not work until you read
         in your data to appropriate data structures, but is included
         to help guide your work - it is an example of printing the
         data once propogated
         */
         printOutExample(sessionsFromCustomer, viewsFromSession, buysFromSession);

   }

   /* provided as an example of a method that might traverse your
      collections of data once they are written 
      commented out as the classes do not exist yet - write them! */

   private static void printOutExample(
      final Map<String, List<String>> sessionsFromCustomer,
      final Map<String, List<View>> viewsFromSession,
      final Map<String, List<Buy>> buysFromSession) 
   {
      //for each customer, get their sessions
      //for each session compute views
      for(Map.Entry<String, List<String>> entry: 
         sessionsFromCustomer.entrySet()) 
      {
         System.out.println(entry.getKey());
         List<String> sessions = entry.getValue();
         for(String sessionID : sessions)
         {
            System.out.println("\tin " + sessionID);
            List<View> theViews = viewsFromSession.get(sessionID);
            for (View thisView: theViews)
            {
               System.out.println("\t\tviewed " + thisView.getProduct());
            }
         }
      }
   }


      //called in populateDataStructures
   private static void processFile(
      final Scanner input,
      final Map<String, List<String>> sessionsFromCustomer,Map<String, List<View>> viewsFromSession, Map<String, List<Buy>> buysFromSession
      )
   {
      while (input.hasNextLine())
      {
         processLine(input.nextLine(), sessionsFromCustomer, viewsFromSession, buysFromSession);
      }
   }

      //called from main - mostly just pass through important data structures
   private static void populateDataStructures(
      final String filename,
      final Map<String, List<String>> sessionsFromCustomer,
      Map<String, List<View>> viewsFromSession, Map<String, List<Buy>> buysFromSession)
      throws FileNotFoundException
   {
      try (Scanner input = new Scanner(new File(filename)))
      {
         processFile(input, sessionsFromCustomer, viewsFromSession, buysFromSession);
         //System.out.println(99999);
      }
   }

   private static String getFilename(String[] args)
   {
      if (args.length < 1)
      {
         System.err.println("Log file not specified.");
         System.exit(1);
      }

      return args[0];
   }

   public static void main(String[] args)
   {
      /* Map from a customer id to a list of session ids associated with
       * that customer.
       */

      final Map<String, List<String>>  sessionsFromCustomer = new HashMap<>();
      final Map<String, List<View>> viewsFromSession = new HashMap<>();
      final Map<String, List<Buy>> buysFromSession = new HashMap<>();


      /* create additional data structures to hold relevant information */
      /* they will most likely be maps to important data in the logs */

      final String filename = "sample.log";

      try
      {
         populateDataStructures(filename, sessionsFromCustomer,viewsFromSession, buysFromSession);
         printStatistics(sessionsFromCustomer, viewsFromSession,buysFromSession);
      }
      catch (FileNotFoundException e)
      {
         System.err.println(e.getMessage());
      }
   }
}
