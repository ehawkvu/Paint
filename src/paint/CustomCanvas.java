/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package paint;

import java.util.Stack;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.effect.SepiaTone;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.PixelReader;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import javafx.util.Pair;

/**
 *
 * @author ethan
 */
public class CustomCanvas extends Canvas{
	
	public GraphicsContext gc; //pointer to the graphics context of the canvas

	private Pair<Double,Double> mouseCoord; //Pair for the mouse coordinates

	private Stack<Image> undoStack = new Stack(); 
	private Stack<Image> redoStack = new Stack();
	
	private Image drag_drop_image = null;
	
	public CustomCanvas(){
		super();
		
		this.imgToStack(this.getImage());
		
		this.gc = this.getGraphicsContext2D();
		//this.colorpick.setValue(Color.BLACK);
		this.mouseCoord = new Pair(0, 0);
		
		this.setOnMousePressed(e -> {
			this.mouseCoord = new Pair(e.getX(), e.getY());
			
			if (Paint.getMode() == Paint.EDIT_MODE) {
				if (Paint.edittoolbar.getDrawSelection().equals(
						Paint.edittoolbar.COLOR_GRAB)) {
					
					Paint.colorpick.setValue(this.getImage().getPixelReader().getColor(
						roundDouble(e.getX()),
						roundDouble(e.getY())
					));
				}
			}
			//this.imgToStack(this.getImage());
		});
		
		this.setOnMouseReleased((MouseEvent e) -> {
			this.gc.setFill(Paint.colorpick.getValue());
			this.gc.setStroke(Paint.colorpick.getValue());
			this.gc.setLineWidth(Paint.brushSize);

			if (Paint.getMode() == Paint.EDIT_MODE) {
				switch (Paint.edittoolbar.getDrawSelection()) {
					case EditToolBar.LINE:
						this.gc.strokeLine(
							this.mouseCoord.getKey(),
							this.mouseCoord.getValue(),
							e.getX(),
							e.getY()
						);	Paint.getCurrentTab().imgHasBeenSaved = false;
						break;
					case EditToolBar.CIRCLE:
						double l;
						//Use the larger dimension for drawing the circle
						if (e.getX() >= e.getY()) {
							l = (e.getX() - this.mouseCoord.getKey());
						} else {
							l = (e.getY() - this.mouseCoord.getValue());
						}	
						this.gc.fillOval(
							this.mouseCoord.getKey(),
							this.mouseCoord.getValue(),
							l,
							l
						);	Paint.getCurrentTab().imgHasBeenSaved = false;
						break;
					case EditToolBar.ELLIPSE:
						this.gc.fillOval(
							this.mouseCoord.getKey(),
							this.mouseCoord.getValue(),
							(e.getX() - this.mouseCoord.getKey()),
							(e.getY() - this.mouseCoord.getValue())
						);	Paint.getCurrentTab().imgHasBeenSaved = false;
						break;
					case EditToolBar.RECTANGLE:
						this.gc.fillRect(
							this.mouseCoord.getKey(),
							this.mouseCoord.getValue(),
							(e.getX() - this.mouseCoord.getKey()),
							(e.getY() - this.mouseCoord.getValue())
						);	Paint.getCurrentTab().imgHasBeenSaved = false;
						break;
					case EditToolBar.SQUARE:
						double s;
						//Use the larger dimension for drawing the square
						if (e.getX() >= e.getY()) {
							s = (e.getX() - this.mouseCoord.getKey());
						} else {
							s = (e.getY() - this.mouseCoord.getValue());
						}	
						this.gc.fillRect(
							this.mouseCoord.getKey(),
							this.mouseCoord.getValue(),
							s,
							s
						);	Paint.getCurrentTab().imgHasBeenSaved = false;
						break;
					case EditToolBar.TEXTBOX:
						this.gc.setFont(new Font(Paint.brushSize));
						this.gc.fillText(Paint.edittoolbar.getOptionsField(),
							this.mouseCoord.getKey(),
							this.mouseCoord.getValue()
						);	
						Paint.getCurrentTab().imgHasBeenSaved = false;
						break;
					case EditToolBar.TRIANGLE:
						{
							Pair PolygonPts = getPolygonPoints(
								3,
								this.mouseCoord,
								roundDouble(e.getX())
							);		double[] xp = (double[]) PolygonPts.getKey();
							double[] yp = (double[]) PolygonPts.getValue();
							this.gc.fillPolygon(xp, yp, 3);
							Paint.getCurrentTab().imgHasBeenSaved = false;
							break;
						}
					case EditToolBar.NGON:
						{
							int n = 0;
							try {
								n = Integer.parseInt(Paint.edittoolbar.getOptionsField());
							} catch (Exception ex) {
								System.out.println("Failed to parse options field: " + ex);
								return; // to keep from drawing a shape
							}		Pair PolygonPts = getPolygonPoints(
								n,
								this.mouseCoord,
								roundDouble(e.getX())
							);		
							double[] xp = (double[]) PolygonPts.getKey();
							double[] yp = (double[]) PolygonPts.getValue();
							this.gc.fillPolygon(xp, yp, n);
							Paint.getCurrentTab().imgHasBeenSaved = false;
							break;
						}
					case EditToolBar.CROP:
						{
							//I could use somethign similar to what I've done
							//here for the drag and drop method
							PixelReader r = this.getImage().getPixelReader();
							WritableImage wi = new WritableImage(
								r,
								roundDouble(this.mouseCoord.getKey()),
								roundDouble(this.mouseCoord.getValue()),
								roundDouble(e.getX() - this.mouseCoord.getKey()),
								roundDouble(e.getY() - this.mouseCoord.getValue())
							);		
							Paint.getCurrentTab().setImage(wi);
							Paint.getCurrentTab().imgHasBeenSaved = false;
							break;
						}
					case EditToolBar.DRAGDROP:
						if (this.drag_drop_image == null) {
							//Three steps:
							//1 - get the image
							//2 - make the image globally accessible
							//3 - clear out a rectangle of the same size
							
							//1
							PixelReader r = this.getImage().getPixelReader();
							WritableImage wi = new WritableImage(
								r,
								roundDouble(this.mouseCoord.getKey()),
								roundDouble(this.mouseCoord.getValue()),
								roundDouble(e.getX() - this.mouseCoord.getKey()),
								roundDouble(e.getY() - this.mouseCoord.getValue())
							);
							//2
							this.drag_drop_image = wi;
							//3
							this.gc.clearRect(
								roundDouble(this.mouseCoord.getKey()),
								roundDouble(this.mouseCoord.getValue()),
								roundDouble(e.getX() - this.mouseCoord.getKey()),
								roundDouble(e.getY() - this.mouseCoord.getValue())
							);
							//Exit
							return;
						}	
						this.gc.drawImage(
							this.drag_drop_image,
							e.getX(),
							e.getY()
						);	//set the image back to null
						this.drag_drop_image = null;
						Paint.getCurrentTab().imgHasBeenSaved = false;
						break;
					case EditToolBar.BLUR:
						{
							//INCOMPLETE
							
							//Three steps:
							//1 get image
							//2 apply blur effect to image
							//3 draw the new image
							
							//1
							PixelReader r = this.getImage().getPixelReader();
							WritableImage wi = new WritableImage(
								r,
								roundDouble(this.mouseCoord.getKey()),
								roundDouble(this.mouseCoord.getValue()),
								roundDouble(e.getX() - this.mouseCoord.getKey()),
								roundDouble(e.getY() - this.mouseCoord.getValue())
							);		CustomCanvas t = new CustomCanvas();
							t.updateDimensions(wi); //need to make sure the canvas has dimensions
							t.gc.setEffect(new GaussianBlur());
							t.gc.drawImage(wi, 0, 0);
							//Popup.showImage(t.getImage()); //DEBUG
							this.gc.drawImage(
								t.getImage(),
								this.mouseCoord.getKey(),
								this.mouseCoord.getValue()
							);		
							Paint.getCurrentTab().imgHasBeenSaved = false;
							break;
						}
					case EditToolBar.SEPIA:
						{
							PixelReader r = this.getImage().getPixelReader();
							WritableImage wi = new WritableImage(
								r,
								roundDouble(this.mouseCoord.getKey()),
								roundDouble(this.mouseCoord.getValue()),
								roundDouble(e.getX() - this.mouseCoord.getKey()),
								roundDouble(e.getY() - this.mouseCoord.getValue())
							);		CustomCanvas t = new CustomCanvas();
							t.updateDimensions(wi); //need to make sure the canvas has dimensions
							t.gc.setEffect(new SepiaTone());
							t.gc.drawImage(wi, 0, 0);
							//Popup.showImage(t.getImage()); //DEBUG
							this.gc.drawImage(
								t.getImage(),
								this.mouseCoord.getKey(),
								this.mouseCoord.getValue()
							);		
							Paint.getCurrentTab().imgHasBeenSaved = false;
							break;
						}
					case EditToolBar.ROTATE:
						{
							//INCOMPLETE
							//Three steps:
							//1 - get selection
							//2 - rotate selectin
							//3 - draw rotated selection
							
							//1
							PixelReader r = this.getImage().getPixelReader();
							WritableImage wi = new WritableImage(
								r,
								roundDouble(this.mouseCoord.getKey()),
								roundDouble(this.mouseCoord.getValue()),
								roundDouble(e.getX() - this.mouseCoord.getKey()),
								roundDouble(e.getY() - this.mouseCoord.getValue())
							);		//2
							CustomCanvas t = new CustomCanvas();
							t.updateDimensions(wi);
							t.gc.save();
							t.gc.rotate(Double.parseDouble(Paint.edittoolbar.getOptionsField()));
							t.gc.drawImage(wi, 0, 0);
							t.gc.restore();
							//Popup.showImage(t.getImage()); //DEBUG
							//3
							this.gc.drawImage(
								t.getImage(),
								this.mouseCoord.getKey(),
								this.mouseCoord.getValue()
							);		
							Paint.getCurrentTab().imgHasBeenSaved = false;
							break;
						}
					default:
						break;
				}	
			this.imgToStack(this.getImage());
			}
			
		});
				
		this.setOnMouseDragged(e -> {
			
			double bsize = Paint.brushSize;
			double x = e.getX() - bsize / 2;
			double y = e.getY() - bsize / 2;
			
			//if in edit mode
			if (Paint.getMode() == Paint.EDIT_MODE) {
				if (Paint.edittoolbar.getDrawSelection().equals(
						Paint.edittoolbar.ERASE)) {
					
					this.gc.clearRect(x, y, bsize, bsize);
					
					this.imgToStack(this.getImage());
					Paint.getCurrentTab().imgHasBeenSaved = false;

				} else if (Paint.edittoolbar.getDrawSelection().equals(
						Paint.edittoolbar.PENCIL)) {
					
					this.gc.setFill(Paint.colorpick.getValue());
					this.gc.fillRect(x, y, bsize, bsize);
					
					this.imgToStack(this.getImage());
					Paint.getCurrentTab().imgHasBeenSaved = false;

				} 
				/*
				//Experimental; dont expect to work
				else if (Paint.edittoolbar.getDrawSelection().equals(
						Paint.edittoolbar.BLUR)) {
					
					this.gc.setEffect(new GaussianBlur());
					this.gc.setFill(Color.TRANSPARENT);
					this.gc.fillOval(x, y, bsize, bsize);
					//this.gc.fillRect(x, y, bsize, bsize);
					this.gc.setEffect(null);
				}	
				*/
				//this.imgToStack(this.getImage());
			}
		});
		
	}
	
	/**
	 * 
	 * This method runs when Images are opened, or when the canvas is resized,
	 * and sets the canvas width to be the proper size.
	 * 
	 */
	public void updateDimensions() {
		//Also potential thought, I may have the image be a proportion of the current window size,
		//so that when the main window is resized, the image resizes with it.
		if (Paint.getCurrentTab().opened_image != null) {
			this.setHeight(Paint.getCurrentTab().opened_image.getHeight());
			this.setWidth(Paint.getCurrentTab().opened_image.getWidth());
		} else {
			this.setHeight(0);
			this.setWidth(0);
		}
	}
	
	public void updateDimensions(Image i) {
		this.setHeight(i.getHeight());
		this.setWidth(i.getWidth());
	}
	
	//this is a really hackyway of doing this, I want to make this much cleaner
	//(ie refactor)
	public void updateDimensions(boolean inc_zoom) {
		if (inc_zoom) {
			//if we want to increase the zoom
			this.setWidth(this.getWidth() * 2);
			this.setHeight(this.getHeight() * 2);
		} else {
			//if we want to decrease the zoom
			this.setWidth(this.getWidth() / 2);
			this.setHeight(this.getHeight() / 2);
		}
	}
	
	/**
	 * This method returns the current canvas as an image.
	 * 
	 * @return An Image Object of the canvas.
	 */
	public Image getImage() {
		WritableImage wi = this.snapshot(null, null);
		ImageView iv = new ImageView(wi);
		return iv.getImage();
	}

	//need to preserve the image modifications
	public void zoomIn(){
		this.updateDimensions(true); // zoom in
		this.gc.drawImage(this.getImage(), 0, 0, this.getWidth(), this.getHeight());
		Paint.getCurrentTab().setScrollPrefSize(this.getWidth(), this.getHeight());
		this.imgToStack(this.getImage());

	}
	public void zoomOut(){
		this.updateDimensions(false); // zoom out
		this.gc.drawImage(this.getImage(), 0, 0, this.getWidth(), this.getHeight());
		Paint.getCurrentTab().setScrollPrefSize(this.getWidth(), this.getHeight());
		this.imgToStack(this.getImage());

	}
	/**
	 * Add an Image to the undo Stack 
	 * 
	 * @param i The image to add to the stack
	 */
	private void imgToStack(Image i) {
		this.undoStack.push(i);
		this.redoStack = null; // reset the redo stack (should only be able to redo what you've undone)
		System.out.println("Added Image to undo Stack");
	}
	
	/**
	 * Rounds Any Double values to integers, may be removed in favor of type casting.
	 * 
	 * @param d
	 * @return An Integer rounded via the Math Library.
	 */
	private int roundDouble(double d) {
		return (int) Math.round(d);
	}
	
	/**
	 * 
	 * This Method is responsible for undoing actions that are taken by the 
	 * user, by pop-ing them off of the undo stack, and setting the canvas 
	 * to be the next image in line, so to speak.
	 * 
	 */
	public void undo() {
		if (! undoStack.empty()) { //if the image stack is not empty
			redoStack.add(undoStack.pop());
			if (! undoStack.empty()) {
				Paint.getCurrentTab().setImage(undoStack.pop());
				System.out.println("Undo was Successful!");
			}
		}
	}
	/**
	 * This Method is responsible for redo-ing actions that have been undone,
	 * by setting the image to be the last image to be whatever pops off the 
	 * redo stack, and adding that image back onto the undo stack.
	 */
	public void redo() {
		if (! redoStack.empty()) {
			Image lastimg = redoStack.pop(); //get the last image
			Paint.getCurrentTab().setImage(lastimg);
			undoStack.add(lastimg);
		}
	}
	
	/**
	 * 
	 * This method is a helper method for drawing polygons on the canvas, and handles calculating the proper points.
	 * 
	 * @param n An integer for the number of sides the polygon should have.
	 * @param initMouseCoord The initial mouse coordinates 
	 * @param cx The current X value 
	 * @return A Pair of double Arrays, with the key corresponding to the X points, and the value corresponding to the Y points.
	 */
	private Pair<double[],double[]> getPolygonPoints(int n, Pair initMouseCoord, int cx){
		double ix = (double) initMouseCoord.getKey();
                double iy = (double) initMouseCoord.getValue();
                double radius = cx - ix;

		double[] xp = new double[n];
		double[] yp = new double[n];

		for (int i = 0; i < n; i++) {
			xp[i] = (ix + (radius * Math.cos(2 * Math.PI * i / n)));
			yp[i] = (iy + (radius * Math.sin(2 * Math.PI * i / n)));
		}

		return new Pair(xp, yp);
	}
	
}
