

package exercise06;

import exercise06.UserInterface;
import javafx.application.Application;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Screen;
import javafx.stage.Stage;

/*******
* <p> Title: Exercise04Main Class. </p>
* 
* <p> Description: A JavaFX demonstration application: This controller class is the entry point
* for this JavaFX application.</p>
* 
* <p> Copyright: Lynn Robert Carter Â© 2018-08-04 </p>
* 
* @author Sanchit
* 
* @version 1.00	2018-07-18 Baseline
* @version 2.00	2018-08-04	 Enhance the baseline to read definitions into an array of definitions
* 
*/


public class Exercise06Mainline extends Application {
	
	public UserInterface theGUI;
	
	public static double WINDOW_WIDTH;
	public static double WINDOW_HEIGHT;
	
	@Override
	/**********
	 * This method is the root of the application from it, the foundations of the application are
	 * establish, the GUI is linked to the methods of various classes and the setup is performed.
	 * 
	 * This method queries the environment to determine the size of the window that is at the heart
	 * of the Graphical User Interface (GUI). The method is called with a single parameter that 
	 * specified the Stage object that JavaFX applications use.
	 * 
	 * The method starts by creating a Pane object, calls the GUI to instantiate the GUI widgets 
	 * using that Pane, creates a Scene using that Pane as a window of a size that will fit the
	 * specifics of the system running the application. Once the Scene is set, it is shown to the
	 * user, and at that moment the application changes from a programmed sequence of actions set
	 * by the programmer into a set of actions determined by the user by means of the various GUI
	 * elements the user selects and uses. 
	 * 
	 * @param theStage is a Stage object that is passed in to the methods and is used to set up the
	 * the controlling object for the application's user interface
	 */
	public void start(Stage theStage) throws Exception {
		
		// Determine the actual visual bounds for this display
		Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();

		//set Stage boundaries to the visual bounds so the window does not totally fill the screen 
		WINDOW_WIDTH = primaryScreenBounds.getWidth() - primaryScreenBounds.getMinX() - 100;
		if (WINDOW_WIDTH > 1000) WINDOW_WIDTH = 1000;
		WINDOW_HEIGHT = primaryScreenBounds.getHeight() - primaryScreenBounds.getMinY() - 100;
		if (WINDOW_HEIGHT > 600) WINDOW_HEIGHT = 600;
			
		theStage.setTitle("Exercise 06");						// Label the stage's window
		
		Pane theRoot = new Pane();								// Create a pane within the window
		
		theGUI = new UserInterface(theRoot);					// Create the Graphical User Interface
																// and populate that Pane with widgets
		
		Scene theScene = new Scene(theRoot, WINDOW_WIDTH, WINDOW_HEIGHT);	// Create the scene using
																// the GUI window and the size that 
																// was computed earlier
		
		theStage.setScene(theScene);							// Set the scene on the stage and
		
		theStage.show();										// show the stage to the user
		
		// When the stage is shown to the user, the pane within the window is now visible.  This means 
		// that the labels, fields, and buttons of the Graphical User Interface (GUI) are visible and
		// it is now possible for the user to select input fields and enter values into them, click on 
		// buttons, and read the labels, the results, and the error messages.
	}
	


	/*******************************************************************************************************/

	/*******************************************************************************************************
	 * This is the method that launches the JavaFX application
	 * 
	 * @param args are the program parameters and they are not used by this program.
	 * 
	 */
	public static void main(String[] args) {					// This method may not be required
		launch(args);											// for all JavaFX applications using
	}															// other IDEs.


	
}
