package exercise06;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import dictionary.DictEntry;
import dictionary.Dictionary;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.shape.Rectangle;

/*******
 * <p> Title: UserInterface Class. </p>
 * 
 * <p> Description: A JavaFX demonstration application: This controller class describes the user
 * interface for the Exercise04 demonstration application </p>
 * 
 * <p> Copyright: Lynn Robert Carter Â© 2018-08-04 </p>
 * 
 * @author Sanchit
 * 
 * @version 2.03	2018-07-19 Baseline
 * @version 3.00	2018-08-04 An enhancement for Exercise 04
 * 
 */
public class UserInterface {
	
	/**********************************************************************************************

	Class Attributes
	
	**********************************************************************************************/

	// Attributes used to establish the display and control panel within the window provided to us
	private double controlPanelHeight = Exercise06Mainline.WINDOW_HEIGHT - 110;
	private int marginWidth = 20;

	// The User Interface widgets used to control the user interface and start and stop the simulation
	private Label label_FileName = new Label("Enter the data file's name here:");
	private TextField text_FileName = new TextField();
	private Button button_Load = new Button("Load the data");
	private Button button_Append = new Button("Append data");
	private Button button_Analyze = new Button("Analyze");
	private Button button_Stop = new Button("Stop");
	private Button button_Display = new Button("Display the data");

	// The attributes used to specify and assess the validity of the data file that defines the game
	private String str_FileName;			// The string that the user enters for the file name
	private Scanner scanner_Input = null;	// The Scanners used to evaluate whether or not the
											// the specified file holds usable data

	// The attributes used to inform the user if the file name specified exists or not
	private Label message_FileFound = new Label("");
	private Label message_FileNotFound = new Label("");

	// These attributes are used to tell the user, in detail, about errors in the input data
	private String errorMessage_FileContents = "";
	private Label message_ErrorDetails = new Label("");
	
	// This attribute hold a reference to the Pane that roots the user interface's window
	private Pane window;

	// These attributes put a graphical frame around the portion of the window that receives the
	// black squares representing alive cells
	private Rectangle rect_outer =  new Rectangle(0,0,Exercise06Mainline.WINDOW_WIDTH, controlPanelHeight-5);
	private Rectangle rect_middle = new Rectangle(5,5,Exercise06Mainline.WINDOW_WIDTH-10, controlPanelHeight-15);
	private Rectangle rect_inner =  new Rectangle(6,6,Exercise06Mainline.WINDOW_WIDTH-12, controlPanelHeight-17);

	// This attribute holds the text that will be displayed 
	private TextArea blk_Text = new TextArea("");
	
	// The application scans the prospective input files to make sure that the file conforms to the
	// application specification and that makes it difficult for hacker to take control of the
	// computer by feeding the application data that corrupts the operating system.  This is done
	// by reading the file twice, once to verify that the file conforms to basic requirements that
	// could cause harm.  The second is to then read the file for actual use.
	
	// The ratio of the following two are used to determine the value of the progress bar
	private int numberOfLinesInTheInputFile = 0;	// Number of lines set during the first read

	private Timeline loadingTimeline;				// Timeline used to update the progress bar
	
	
	private int [] letterTally = new int[26];		// A data structure to hold the counts the
													// number of occurrences of each letter, a - z
	
	// This flag is used to signal from one thread to another that the second read should stop
	private boolean userRequestedStop = false;
	
	// Boolean flag to let the system know when first read is done and we are now appending
	private boolean firstRead = true;
	
	// This is the attribute that holds the reference to the dictionary
	private Dictionary theDictionary = null;

	
	/**********************************************************************************************

	Constructors
	
	**********************************************************************************************/

	/**********
	 * This constructor established the user interface with all of the graphical widgets that are
	 * use to make the user interface work.
	 * 
	 * @param theRoot	This parameter is the Pane that JavaFX expects the application to use when
	 * 					it sets up the GUI elements.
	 */
	public UserInterface(Pane theRoot) {
		window = theRoot;							// Save a reference to theRoot so we can update
													// that Pane during the application's execution
				
		// Set the fill colors for the border frame for the game's output of the simulation
		rect_outer.setFill(Color.LIGHTGRAY);
		rect_middle.setFill(Color.BLACK);
		rect_inner.setFill(Color.WHITE);
		
		// Place a text area into the window and just within the above frame and make it not editable
		setupTextAreaUI(blk_Text, "Monaco", 14, 6, 6, Exercise06Mainline.WINDOW_WIDTH-12, controlPanelHeight-17, false);		


		// Label the text field that is to receive the file name.
		setupLabelUI(label_FileName, "Arial", 18, Exercise06Mainline.WINDOW_WIDTH-20, Pos.BASELINE_LEFT, 
				marginWidth, controlPanelHeight);

		// Establish the text input widget so the user can enter in the name of the file that holds
		// the data about which cells are alive at the start of the simulation
		setupTextUI(text_FileName, "Arial", 18, Exercise06Mainline.WINDOW_WIDTH / 2, Pos.BASELINE_LEFT, 
				marginWidth, controlPanelHeight + 24, true);
		
		// Establish the link between the text input widget and a routine that checks to see if
		// if a file of that name exists and if so, whether or not the data is valid
		text_FileName.textProperty().addListener((observable, oldValue, newValue) -> {checkFileName(); });

		// Establish a GUI button the user presses when the file name have been entered and the
		// code has verified that the data in the file is valid (e.g. it conforms to the requirements)
		setupButtonUI(button_Load, "Arial", 18, 100, Pos.BASELINE_LEFT, Exercise06Mainline.WINDOW_WIDTH - 275,  
				controlPanelHeight + 24);
		
		// Establish the link between the button widget and a routine that loads the data into theData
		// data structure
		button_Load.setOnAction((event) -> { loadTheData(); });

		// Establish a GUI button the user presses when the file name have been entered and the
		// code has verified that the data in the file is valid (e.g. it conforms to the requirements)
		setupButtonUI(button_Append, "Arial", 18, 100, Pos.BASELINE_LEFT, Exercise06Mainline.WINDOW_WIDTH - 275,  
				controlPanelHeight + 24);

		// Establish a GUI button the user presses to display the diction in the window and console
		setupButtonUI(button_Display, "Arial", 18, 120, Pos.BASELINE_LEFT, Exercise06Mainline.WINDOW_WIDTH - 210,  
				controlPanelHeight + 66);
		
		// Establish the link between the button widget and a routine that loads the data into theData
		// data structure
		button_Append.setOnAction((event) -> { appendTheData(); });

		// Establish a GUI button that the user presses to start the analysis of theData and display the result
		setupButtonUI(button_Analyze, "Arial", 18, 50, Pos.BASELINE_LEFT, Exercise06Mainline.WINDOW_WIDTH - 110,  
				controlPanelHeight + 24);
		
		// Link the start button to the routine that does the analysis and displays the result
		button_Analyze.setOnAction((event) -> { startTheAnalysis(); });

		// Establish a GUI button that the user can press once the analysis starts to stop it
		setupButtonUI(button_Stop, "Arial", 18, 50, Pos.BASELINE_LEFT, Exercise06Mainline.WINDOW_WIDTH - 80,  
				controlPanelHeight + 24);
		
		// Link the stop button to the routine that stops the analysis 
		button_Stop.setOnAction((event) -> { stopTheApplication(); });
		
		// Link the Display button to the routine that displays the dictionary
		button_Display.setOnAction((event) -> { displayTheDictionary(); });

		// Disable the buttons (They will appear grayed out)
		button_Load.setDisable(true);				// We want them grayed out when the application starts
		button_Analyze.setDisable(true);			// Only when the state is read for the action should they
		button_Display.setDisable(true);			// be enabled

		// The following set up the control panel messages for messages and information about errors
		setupLabelUI(message_FileFound, "Arial", 18, 150, Pos.BASELINE_LEFT, 350, controlPanelHeight);
		message_FileFound.setStyle("-fx-text-fill: green; -fx-font-size: 18;");

		setupLabelUI(message_FileNotFound, "Arial", 18, 150, Pos.BASELINE_LEFT, 350, controlPanelHeight);
		message_FileNotFound.setStyle("-fx-text-fill: red; -fx-font-size: 18;");

		setupLabelUI(message_ErrorDetails, "Arial", 16, Exercise06Mainline.WINDOW_WIDTH, Pos.BASELINE_LEFT, 20,
				controlPanelHeight);
		
		message_ErrorDetails.setStyle("-fx-text-fill: red; -fx-font-size: 16;");

		// Place all of the just-initialized GUI elements into the pane with the exception of the
		// Stop button.  That widget will replace the Start button, once the Start has been pressed
		theRoot.getChildren().addAll(rect_outer, rect_middle, rect_inner, label_FileName, text_FileName, 
				button_Load, button_Analyze, message_FileFound, message_FileNotFound, message_ErrorDetails,
				blk_Text, button_Display);
	}

	
	/**********************************************************************************************

	Helper methods - Used to set up the JavaFX widgets and simplify the code above
	
	**********************************************************************************************/

	/**********
	 * Private local method to initialize the standard fields for a label
	 */
	private void setupLabelUI(Label l, String ff, double f, double w, Pos p, double x, double y){
		l.setFont(Font.font(ff, f));
		l.setMinWidth(w);
		l.setAlignment(p);
		l.setLayoutX(x);
		l.setLayoutY(y);
	}

	/**********
	 * Private local method to initialize the standard fields for a text field
	 */
	private void setupTextUI(TextField t, String ff, double f, double w, Pos p, double x, double y, boolean e){
		t.setFont(Font.font(ff, f));
		t.setMinWidth(w);
		t.setMaxWidth(w);
		t.setAlignment(p);
		t.setLayoutX(x);
		t.setLayoutY(y);		
		t.setEditable(e);
	}
	

	/**********
	 * Private local method to initialize the standard fields for a text field
	 */
	private void setupTextAreaUI(TextArea t, String ff, double f, double x, double y, double w, double h, boolean e){
		t.setFont(Font.font(ff, f));
		t.setPrefWidth(w);
		t.setPrefHeight(h);	
		t.setLayoutX(x);
		t.setLayoutY(y);	
		t.setEditable(e);
		t.setWrapText(true);
	}


	/**********
	 * Private local method to initialize the standard fields for a button
	 */
	private void setupButtonUI(Button b, String ff, double f, double w, Pos p, double x, double y){
		b.setFont(Font.font(ff, f));
		b.setMinWidth(w);
		b.setAlignment(p);
		b.setLayoutX(x);
		b.setLayoutY(y);		
	}
	
	/**********************************************************************************************

	Action methods - Used cause things to happen with the set up or during the simulation
	
	**********************************************************************************************/

	/**********
	 * This routine checks, after each character is typed, to see if the game of life file is there
	 * and if so, sets up a scanner to it and enables a button to read it and verify that it is ready
	 * to be used by the application.
	 * 
	 * If a file of that name is found, it checks to see if the contents conforms to the specification.
	 * 		If it does, the Load button is enabled and a green message is displayed
	 * 		If it does not, the Load button is disabled and a red error message is displayed
	 * If a file is not found, a warning message is displayed and the button is disabled.
	 * If the input is empty, all the related messages are removed and the Load button is disabled.
	 */
	void checkFileName(){
		str_FileName = text_FileName.getText();			// Whenever the text area for the file name is changed
		if (str_FileName.length()<=0){					// this routine is called to see if it is a valid filename.
			message_FileFound.setText("");				// Reset the messages to empty
			message_FileNotFound.setText("");
			scanner_Input = null;
		} else 											// If there is something in the file name text area
			try {										// this routine tries to open it and establish a scanner.
				scanner_Input = new Scanner(new File(str_FileName));

				// There is a readable file there... this code checks the data to see if it is valid 
				// for this application (Basic user input errors are GUI issues, not analysis issues.)
				if (fileContentsAreValid()) {
					message_FileFound.setText("File found and the contents are valid!");
					message_ErrorDetails.setText("");
					message_FileNotFound.setText("");
					button_Load.setDisable(false);		// Enable the Load button
					button_Append.setDisable(false);	// and the Append button
					button_Analyze.setDisable(true);	// Disable the Analyze button			
				}
				
				// If the methods returns false, it means there is a problem with input file
				else {	// and the method has set up a String to explain what the issue is
					message_FileFound.setText("");
					message_FileNotFound.setText("File found, but the contents are not valid!");
					message_ErrorDetails.setText(errorMessage_FileContents);
					button_Load.setDisable(true);		// Set the buttons to disabled
					button_Append.setDisable(true);		// as well as the Append button
					button_Analyze.setDisable(true);
				}

			} catch (FileNotFoundException e) {			// If an exception is thrown, the file name
				message_FileFound.setText("");			// that the button to run the analysis is
				message_FileNotFound.setText("File not found!");	// not enabled.
				message_ErrorDetails.setText("");
				scanner_Input = null;								
				button_Load.setDisable(true);			// Set the buttons to disabled
				button_Append.setDisable(true);
				button_Analyze.setDisable(true);
			}
	}
	
	/**********
	 * This private method reads the data from the data file and places it into a data structure
	 * for later processing, should the user decide to do that.  (Recall that the input has already
	 * been scanned by the function fileContentsAreValid(), so redundant checks are not needed.)
	 * 
	 * If during this process, the user should press the Stop button, the reading will stop.
	 * 
	 * @param in	The parameter is a Scanner object that is set up to read the input file
	 */
	private void readTheData(Scanner in) {
		// If this is the first reading of data, we need to establish the dictionary
		if (firstRead)
			theDictionary = new Dictionary();
		
		// Save the start time so we can compute the execution time
		Long start = System.nanoTime();
		
		// Read in the dictionary and store what is read in the Dictionary object
		theDictionary.defineDictionary(in);
		
		// Save the stop time so we can compute the execution time
		Long stop = System.nanoTime();
		
		// Compute the execution time and convert it from nanoseconds to milliseconds
		Double executionTime = ((double)stop - start) / 1000000.0;
		
	
		// Output the execution time on the console display
		System.out.println("Loading execution time: " + executionTime + " ms.");
		
		
		// If the Timeline is still running (it should be), stop it
		if (loadingTimeline != null) loadingTimeline.stop();
		
		// Wait for this thread to finish doing I/O and then update the display with how many lines
		// were actually read in
		Platform.runLater(() -> 
			{button_Analyze.setDisable(false);				// Since the loading is done, we can analyze
			 button_Display.setDisable(false);				// and we can display the dictionary
			 window.getChildren().remove(button_Stop);		// Remove the stop button
			 window.getChildren().add(button_Analyze);		// Add in the start button
				
			 // After an initial load, subsequent input operations are appends, not loads so we 
			 // need to switch to the Append Data button from the Load Data button.
			 button_Append.setDisable(true);				// Disable the Append until the analysis is done
				
			 if (firstRead) {								// If this is the first read, remove the
				window.getChildren().remove(button_Load);	// load button and replace it with
				window.getChildren().add(button_Append);	// the Append button
			 }
			});
	}

	/**********
	 * This private method is called when the Load button is pressed. It tries to load the data into
	 * theData data structure for future analysis, if the user wishes to do that.  The method also
	 * manages the change of state of the various buttons associated with the user interface during
	 * the process.
	 * 
	 * To properly enable the concurrent activities with the user interface, this method uses a
	 * different thread to actually read in the data, leaving this thread available to deal with
	 * any user commands and to update the user interface as the reading takes place (e.g. this
	 * allows the progress bar to be updated *while* the reading is taking place.)
	 */
	private void loadTheData() {
		// Set up the user interface buttons give the user has pressed the Load button
		button_Load.setDisable(true);					// Disable the Load button, since it was pushed
		button_Analyze.setDisable(true);				// and wait for start until the load is done
		button_Display.setDisable(true);				// Also disable the Display button until done
		window.getChildren().remove(button_Analyze);	// Remove the start button
		window.getChildren().add(button_Stop);			// Add in the stop button and
		button_Stop.setDisable(false);					// Enable it

		// Since we have already verified the input, the try should never fail, but Java does not
		// know that and besides hardware and software failures do occur, even though it is rare.
		try {
			final Scanner dataReader = new Scanner(new File(str_FileName));	// Set up scanner
			new Thread(() -> {readTheData(dataReader);}).start();			// Use it on another
													// on another thread, running concurrently
		}
		catch (FileNotFoundException e)  {
			// Since we have already done this check, this exception should never happen
			System.out.println("***Error*** A truly unexpected error occured.");
		}
		
		// Reset the file name field to empty
		text_FileName.setText("");
	};

	/**********
	 * This private method is called when the Load button is pressed. It tries to load the data into
	 * theData data structure for future analysis, if the user wishes to do that.  The method also
	 * manages the change of state of the various buttons associated with the user interface during
	 * the process.
	 * 
	 * To properly enable the concurrent activities with the user interface, this method uses a
	 * different thread to actually read in the data, leaving this thread available to deal with
	 * any user commands and to update the user interface as the reading takes place (e.g. this
	 * allows the progress bar to be updated *while* the reading is taking place.)
	 */
	private void appendTheData() {
		// Set up the user interface buttons give the user has pressed the Load button
		button_Append.setDisable(true);					// Disable the Load button, since it was pushed
		button_Analyze.setDisable(true);				// and wait for start until the load is done
		button_Display.setDisable(true);				// Also disable the Display button until done
		window.getChildren().remove(button_Analyze);	// Remove the start button
		window.getChildren().add(button_Stop);			// Add in the stop button and
		button_Stop.setDisable(false);					// enable it
		
		// Signal that the first reading operation has been done and we are now appending
		firstRead = false;
		

		// Since we have already verified the input, the try should never fail, but Java does not
		// know that and besides hardware and software failures do occur, even though it is rare.
		try {
			final Scanner dataReader = new Scanner(new File(str_FileName));	// Set up scanner
			new Thread(() -> {readTheData(dataReader);}).start();			// Use it on another
													// on another thread, running concurrently
		}
		catch (FileNotFoundException e)  {
			// Since we have already done this check, this exception should never happen
			System.out.println("***Error*** A truly unexpected error occured.");
		}
		
		// Reset the file name field to empty
		text_FileName.setText("");
	};

	/**********
	 * This private method sets up the system to do the analysis. It begins by removing the start 
	 * button, setting up the stop button, removing the progress bar (in the future, maybe a new 
	 * progress bar should be used), performing the analysis, and then displaying the results of
	 * the analysis.
	 */
	private void startTheAnalysis() {
		window.getChildren().remove(button_Analyze);	// Remove the start button
		window.getChildren().add(button_Stop);		// Add in the stop button

		// Save the start time so we can compute the execution time
		Long start = System.nanoTime();
		
		// Analysis the data stored in theData data structure
		doTheAnalysis();
		
		// Save the stop time so we can compute the execution time
		Long stop = System.nanoTime();
		
		// Compute the execution time and convert it from nanoseconds to milliseconds
		Double executionTime = ((double)stop - start) / 1000000.0;
		
		// Output the execution time on the console display
		System.out.println("Analysis execution time: " + executionTime + " ms.");
		
		// Disable the Stop button
		button_Stop.setDisable(true);
		
		// Reset the input test field
		window.getChildren().remove(button_Stop);
		button_Analyze.setDisable(true);
		window.getChildren().add(button_Analyze);

		
		// This displays the results of the analysis on the main window of the user interface and the console
		String result = "The number of entries in the dictionary: " + theDictionary.getNumEntries() + "\n" +
				"The number of each letter in the alphabet found in the dictionary.\n";
		for (int ndx = 0; ndx < 26; ndx++)
			result += (char)(ndx + 'a') + " " + letterTally[ndx] + "\n";
		System.out.println(result);					// Display result on the console
		
		findTheLargest(result); //calling method
		
	};
	
	/**********
	 * This private methods actually performed the details of the analysis by going over each line
	 * in the data structure.  If the user should press the stop button while this loop is working,
	 * a message is sent to the console and the loop breaks.
	 * 
	 * For each line in the data structure, each character of each line is examined after being
	 * converted to lower case. If a character is a lower case alphabet character, the corresponding
	 * tally in an array of tallies is incremented.
	 * 
	 * When the end of the data structure is reached the Scanner used to parse the data structure
	 * is closed.
	 */
	private void doTheAnalysis() {
		
		// Get the number of entries in the dictionary from theDictionary object
		int numberOfEntries = theDictionary.getNumEntries();
		DictEntry aDictEntry = null;
		letterTally = new int[26];
		
		// Loop through all of the entries in the dictionary and analyze each
		for (int ndx = 0; ndx < numberOfEntries; ndx++) {
			// Check to see if the user has pressed the Stop button.  If so, say so and break 
			if (userRequestedStop) {
				System.out.println("*** STOP *** The user has requested stop during analysis.");
				break;
			}
			aDictEntry = theDictionary.getDictEntry(ndx);
			String theDefinition = aDictEntry.getDefinition().toLowerCase();
			for (int letterIndex = 0; letterIndex < theDefinition.length(); letterIndex++)
				if (theDefinition.charAt(letterIndex) >= 'a' && theDefinition.charAt(letterIndex) <= 'z') 
					letterTally[theDefinition.charAt(letterIndex) - 'a']++;			
		}
		
	}
	
	
	/**********
	 * This private method is invoked when the user presses the Stop button.  The method signals
	 * that event to the rest of the threads by means of the global variable userRequestedStop.
	 * The method then disables the button and changes its text to Done.
	 */
	private void stopTheApplication() {
		userRequestedStop = true;
		button_Stop.setDisable(true);
		button_Stop.setText("Done");
	}


	/**********
	 * This method reads in the contents of the data file and discards it as quickly as it reads it
	 * in order to verify that the data meets the input data specifications and helps reduce the 
	 * change that invalid input data can lead to some kind of hacking.
	 * 
	 * @return	true - 	when the input file *is* valid
	 * 					when the input file data is *not* valid - The method also sets a string with
	 * 						details about what is wrong with the input data so the user can fix it
	 */
	private boolean fileContentsAreValid() {
		
		// Declare and initialize data variables used to control the method
		numberOfLinesInTheInputFile = 0;
		String firstLine = "";
		
		// Read in the first line and verify that it has the proper header
		if (scanner_Input.hasNextLine()) {
			firstLine = scanner_Input.nextLine().trim();		// Fetch the first line from the file
			if (firstLine.equalsIgnoreCase("Demonstration"))	// See if it is what is expected
				numberOfLinesInTheInputFile = 1;				// If so, count it as one line
			else {												// If not, issue an error message
				System.out.println("\n***Error*** The first line does not consist of the word \"Demonstration\"" +
						" as required by the specification.");
				return false;									// and return false
			}
		} else {
		// If the execution comes here, there was no first line in the file
			System.out.println("\n***Error*** The file appears to be empty.");
			return false;
		}
		
		// Process each and every subsequent line in the input to make sure that none are too long
		while (scanner_Input.hasNextLine()) {
			numberOfLinesInTheInputFile++;						// Count the number of input lines
			
			// Read in the line 
			String inputLine = scanner_Input.nextLine();
			
			// Verify that the input line is not larger than 250 characters...
			if (inputLine.length() > 250) {
				// If it is larger than 250 characters, display an error message on the console
				System.out.println("\n***Error*** Line " + numberOfLinesInTheInputFile + " contains " + 
						inputLine.length() + " characters, which is greater than the limit of 250.");
				
				// Stop reading the input and tell the user this data file has a problem
				return false;
			}
		}
		
		// Should the execution reach here, the input file appears to be valid
		errorMessage_FileContents = "";							// Clear any messages
		return true;											// End of file - data is valid
	}
	
	private void displayTheDictionary() {
		// Display the Display button until more is read in or the analysis in performed.
		button_Display.setDisable(true);
		
		// Save the start time so we can compute the execution time
		Long start = System.nanoTime();
		
		// This displays the results of the analysis on the main window of the user interface and the console
		String result = "The following are the " + theDictionary.getNumEntries() + " dictionary entries read in so far.\n";
		result += theDictionary.listAll();			// Append the contents of the dictionary
		blk_Text.setText(result);					// Display the result on the user interface
		System.out.println(result);					// Display result on the console
		
		// Save the stop time so we can compute the execution time
		Long stop = System.nanoTime();
		
		// Compute the execution time and convert it from nanoseconds to milliseconds
		Double executionTime = ((double)stop - start) / 1000000.0;
		
		
		// Output the execution time on the console display
		System.out.println("Display execution time: " + executionTime + " ms.");
	}
	
	// Create new method "findTheLargest"
	
	public void findTheLargest(String result)
	{
		char char_name = 0; //Initiation for finding the maximum occur letter
		int occurence = 0; //initiation for finding the number of occurance
				
		for(int s = 1; s <26; s++)
				{
					if(letterTally[s] > occurence)
					{
						char_name = (char) ('a' + s);
						occurence = letterTally[s];
					}
				}
				
				System.out.println("");
				
				// print the value, most occurred letter and the number of occurrence
				String fr = "The letter "+ char_name +" occured the most times, a total of " + occurence +" times";
				
				// write the result in the console
				System.out.println(fr);
				result = result + fr;
				
				blk_Text.setText(result);// Display the result in the UI
			
			}
}
