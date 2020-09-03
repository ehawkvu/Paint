/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package paint;

import java.io.File;
import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 *
 * @author ethan
 */
public class Paint extends Application {
	
	//Constants
	final static String PROGRAM_NAME = "Pain(t)";
	final static String PROGRAM_VER = "0.0.2";
	final static String RELEASE_STR = "When the Pizza Roll is too Hot";
	final static int DEFAULT_MODE = 0;
	final static int EDIT_MODE = 1;
	
	//Some global variables that control some vital parts of the program
	public static CustomCanvas imgcanvas = new CustomCanvas();
	public static CustomMenuBar menub;
	
	public static int mode = 0; //set to 0 for default mode
	
	//pointers
	public static Stage window; //basically primaryStage
	
	// might move these to the canvas code
	public static File opened_file; //whatever file is opened
	public static Image opened_image; 
	
		
	@Override
	public void start(Stage primaryStage) {
	
		
		
	//setup the window pointer
		Paint.window = primaryStage; //have window refer to primaryStage
		
	//menu bar
		Paint.menub = new CustomMenuBar(); //new code
		
	//scroll pane
		ScrollPane scroll = new ScrollPane();
		scroll.setContent(imgcanvas);
	
	//root
		VBox root = new VBox(); //set up how the windows will laid out
		root.getChildren().addAll(menub, scroll);
		//root.getChildren().addAll(imgcanvas, menub);
		//root.setAlignment(menub, Pos.TOP_CENTER); //center the menubar at the top of the screen
		//root.setAlignment(imgv, Pos.BOTTOM_CENTER);
	
	//scene setup
		Scene scene = new Scene(root, 800, 600); //create the scene
		
	//setup the main window
		primaryStage.setTitle(PROGRAM_NAME + " - " + PROGRAM_VER);
		primaryStage.setScene(scene);
		primaryStage.show();
		
		//Maybe have a welcome window? 
	}

	/**
	 * @param args the command line arguments
	 */
	public static void main(String[] args) {
		launch(args);
	}
	

	public static void clearImage(){
		//set the image to be nothing
		Paint.opened_file = null; //set the opened_file to be null, to prevent accidentally saving & thus deleting the image
		Paint.opened_image = null; //Same reasoning as above ^^^
		Paint.setImage(null);
	}
	
	public static void setImage(Image img){
		Paint.opened_image = img; //set the opened_image pointer to image
		Paint.imgcanvas.updateDimensions(); //update the canvas dimensions
		Paint.imgcanvas.gc.drawImage(opened_image, 0, 0);
	}
	
	public static void close() {
		Paint.window.close(); //close the main window/stage
		System.exit(0); //Have a successful exit code.
	}
	
	public static int getMode(){
		return Paint.mode;
	}
	
	
	public static void setMode(int i) {
		if (i == 1) {
			Paint.mode = i;
		} else { //if 0, or something else, return to default.
			Paint.mode = 0;
		}
	}
	
	
}