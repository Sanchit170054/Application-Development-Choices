package exercise05;

import java.util.Scanner;

/**
 * <p> Title: Exercise05Mainline. </p>
 * 
 * <p> Description: Mainline to create the sample propgram according to the requirements</p>
 * 
 * 
 * @author Sanchit
 * 
 * @version 1.00	Initial baseline
 * 
 */


public class Exercise05Mainline {
	
	
	public static void main(String []args) {
		
						Scanner sc = new Scanner(System.in); //Scanner class using the sc for the console input
				
			   System.out.println("Enter a line of text or just press return (enter) to stop the loop:");
				System.out.println("\n\n\n*****************************************************");

				String str = sc.nextLine().trim();    //take the input from the user and then trim the inout using 
				 
				while (str.length() > 0) {  //initiate the whle loop
					
					Scanner sc1 = new Scanner(str); //use to fetch the data
					
					String ls = str.trim().toLowerCase();
					
					System.out.println("\n\nThe input line, trimmed and upper case converted to lower case.");
					System.out.println(ls);

					removeNonAlpha(ls);
					
					// Ask for more input
					System.out.println("\n\n\n*****************************************************");
					System.out.println("\nEnter a line of text or just press return (enter) to stop the loop:");
					str = sc.nextLine().trim();
					sc1.close();
				}
				System.out.println("\n\n\n*****************************************************");
				System.out.println("Empty line detected... the program stops"); //wehn the line is null, program will stop
				sc.close();
				
	}
	/**********

     * Given an input String containing no upper-case letters, this method returns a string with

     * all of the non-alphabetic characters removed.

     *

     * @param ls - the input String with no upper-case alphabet characters in it

     * @return  - the returned String with all non-alphabetic characters removed

     */
	private static void removeNonAlpha(String ls) {
		
		
        String str2="";
          for (int s=0;s<ls.length();s++)
          {
              //Ascci range for a-z A-Z
              if (ls.charAt(s)>64&&ls.charAt(s)<121)
              {
                    str2+=ls.charAt(s);
              }
          }
 
      	 System.out.println("\n\n\n*****************************************************"); 
         System.out.println("\n\nThe input line, trimmed, upper case converted to lower case, and non-alpha removed.");
   	     System.out.println(str2);

   	  System.out.println("\n\n\n*****************************************************"); 
       reverse(str2, ls);
       
	}
	
	 /**********

     * Given a String, this method reverses the string, so that last character is the first

     * character in the returned String and the first character in the input is the last

     * character in the returned String.

     *

     * @param str2,ls - the input String

     * @return  - the reverse of the input String is returned

     */

 
	private static void reverse(String str2, String ls) {
		
		
        String reverse = "";
        
        
        for(int s = str2.length() - 1; s >= 0; s--)
        {
            reverse = reverse + str2.charAt(s);
        }
        
        System.out.println("\n\nThe input line, trimmed, upper case converted to lower case, non-alpha removed, and reversed.");
        System.out.println(reverse);
        
        if (reverse.equals(ls)) {
        	
        	System.out.println(str2);
        }
      
        
	}
}
