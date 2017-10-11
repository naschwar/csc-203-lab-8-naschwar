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
                                        final Map<String, List<View>> viewsFromSession) {

      if (words.length != VIEW_NUM_FIELDS) {
         return;
      }

      //check if there already is a list entry in the map
      //for this customer, if not create one
      List<View> products = viewsFromSession
              .get(words[VIEW_SESSION_ID]);
      if (products == null) {
         products = new LinkedList<>();
         String current_sesh_id = words[VIEW_SESSION_ID];
         View new_product = new View(words[VIEW_PRODUCT_ID], Integer.valueOf(words[VIEW_PRICE]));
         products.add(new_product);
         viewsFromSession.put(current_sesh_id, products);
      }

      else{
         View new_product = new View(words[VIEW_PRODUCT_ID], Integer.valueOf(words[VIEW_PRICE]));
         products.add(new_product);


      }
   }
   //similar to processStartEntry, should store relevant purchases
   //data in a map - model on processStartEntry, but store
   //your data to represent a purchase in the map (not a list of strings)
   private static void processBuyEntry(
           final String[] words,
           final Map<String, List<Buy>> buysFromSession) {
      if (words.length != BUY_NUM_FIELDS) {
         return;
      }

      //check if there already is a list entry in the map
      //for this customer, if not create one
      List<Buy> products = buysFromSession
              .get(words[BUY_SESSION_ID]);
      if (products == null) {
         products = new LinkedList<>();
         String current_sesh_id = words[BUY_SESSION_ID];
         Buy new_product = new Buy(words[BUY_PRODUCT_ID], Integer.valueOf(words[BUY_PRICE]));
         products.add(new_product);
         buysFromSession.put(current_sesh_id, products);
      } else {

         Buy new_product = new Buy(words[BUY_PRODUCT_ID], Integer.valueOf(words[BUY_PRICE]));
         products.add(new_product);


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
   private static void printViewsNotPurchased(Map<String, List<String>> sessionsFromCustomer, Map<String, List<Buy>> BuyFromSession, Map<String, List<View>> viewsFromSession) {
      int no_purchase_sesh = 0;
      int total_views = 0;
      for (String customer : sessionsFromCustomer.keySet()) {
         List<String> sessions = sessionsFromCustomer.get(customer);
         for (String session : sessions) {
            List<Buy> bought_per_sesh = BuyFromSession.get(session);
            if (bought_per_sesh == null) {
               no_purchase_sesh += 1;
               List<View> viewed_sesh = viewsFromSession.get(session);
               if (viewed_sesh != null) {
                  total_views += viewed_sesh.size();
               }
            }
         }
      }
      double result = (double) (total_views / no_purchase_sesh);
      System.out.println("Average Views without Purchase: " + result);
      System.out.println("");
   }

   private static void printSessionPriceDifference(Map<String, List<Buy>> buysFromSession,Map<String, List<View>> viewsFromSession) {
      System.out.println("Price Difference for Purchased Product by Session");
      for (String session : buysFromSession.keySet()) {
         List<Buy> purchased_prods = new LinkedList<>();
         int cost_bought = 0;
         int count = 0;
         boolean first = true;
         for (Buy product : buysFromSession.get(session)) {
            int amount_viewed = 0;
            cost_bought = product.cost;
            int viewed_cost = 0;
            if (viewsFromSession.get(session) != null) {
               count += 1;
               if (count == 1){
            }
               for (View viewed : viewsFromSession.get(session)) {
                  amount_viewed += 1;
                  viewed_cost += viewed.cost;

               }
               if (first){
                  System.out.println(session);
                  first = false;
               }
               int average = viewed_cost/amount_viewed;
               System.out.println("   " + product.productId + " " + (double) (cost_bought - (viewed_cost / amount_viewed)));
            }
            }
      /* add printing */
      }
      System.out.println(""); 
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
      for (String customer: customers) {
         List<String> bought_per_cust = new ArrayList<String>();
         Map<String, Set<String>> viewed_per_cust = new HashMap<>();
         Set<String> viewed_keys = new HashSet<>();
         List<String> sessions_per_cust = sessionsFromCustomer.get(customer);
         for(String session: sessions_per_cust){
            if (buysFromSession.containsKey(session)) {
               List<Buy> bought_per_session = buysFromSession.get(session);
               for (Buy bought : bought_per_session) {
                  bought_per_cust.add(bought.productId);
               }
            }
            if (viewsFromSession.containsKey(session)) {
               List<View> viewed_per_session = viewsFromSession.get(session);
               for (View viewed : viewed_per_session) {
                  if (viewed_keys.contains(viewed.productId)) {
                     Set<String> thisviewed = viewed_per_cust.get(viewed.productId);
                     thisviewed.add(session);
                     viewed_per_cust.put(viewed.productId, thisviewed);
                  } else {
                     Set<String> newViewSesh = new HashSet<>();
                     newViewSesh.add(session);
                     viewed_per_cust.put(viewed.productId, newViewSesh);
                  }
                  viewed_keys.add(viewed.productId);
               }
            }
         }

         for (String view_key : viewed_per_cust.keySet()){
            viewed_keys.add(view_key);
         }
         int count = 0;
         for (String keyView : viewed_keys){
            if (bought_per_cust.contains(keyView)){
               int num_sessions = viewed_per_cust.get(keyView).size();
               if (count == 0){
                  System.out.println(customer);
               }
               count +=1;
               System.out.println("   " + keyView + " " + num_sessions);
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
      printViewsNotPurchased(sessionsFromCustomer, buysFromSession, viewsFromSession);
      printSessionPriceDifference(buysFromSession, viewsFromSession);
      printCustomerItemViewsForPurchase(sessionsFromCustomer, viewsFromSession, buysFromSession);

      /* This is commented out as it will not work until you read
         in your data to appropriate data structures, but is included
         to help guide your work - it is an example of printing the
         data once propogated
         */
     // printOutExample(sessionsFromCustomer, viewsFromSession, buysFromSession);

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
      final Map<String, List<String>>  sessionsFromCustomer = new HashMap<>();
      final Map<String, List<View>> viewsFromSession = new HashMap<>();
      final Map<String, List<Buy>> buysFromSession = new HashMap<>();



      final String filename = getFilename(args);;

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
