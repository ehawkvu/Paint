/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package paint;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;

/**
 *
 * @author ethan
 */
public class EditToolBar extends ToolBar{

	private ComboBox drawoptionmenu;
	private TextField brushfld;
	private TextField options_fld;
	private CheckBox fillcb;

	public static final String LINE = "Line";
	public static final String PENCIL = "Pencil";
	public static final String SQUARE = "Square";
	public static final String RECTANGLE = "Rectangle";
	public static final String CIRCLE = "Circle";
	public static final String ELLIPSE = "Ellipse";
	public static final String ERASE = "Erase";
	public static final String COLOR_GRAB = "Color Grab";
	public static final String NONE = "None";
	public static final String TEXTBOX = "Text Box";
	public static final String TRIANGLE = "Triangle";
	public static final String NGON = "N-gon";
	public static final String DRAGDROP = "Drag & Drop";
	//Extras
	public static final String BLUR = "Blur";
	public static final String CROP = "Crop";
	public static final String ROTATE = "Rotate"; //kinda broken
	public static final String SEPIA = "Sepia";
	public static final String FREENGON = "Free N-gon";
	public static final String BUCKETFILL = "Bucket Fill";

	public EditToolBar() {

		super();

		Button undobtn = new Button("Undo");
			undobtn.setOnAction((ActionEvent event)-> {
				try {
					Paint.getCurrentTab().undo();
				} catch (Exception e) {
					System.out.println("EditToolBar.java; Undo Button On-Action Failed:" + e);
				}
			});
			undobtn.setTooltip(new Tooltip("Undo a draw action"));
		Button redobtn = new Button("Redo");
			redobtn.setOnAction((ActionEvent event) -> {
				try {
					Paint.getCurrentTab().redo();
				} catch (Exception e) {
					System.out.println("EditToolBar.java; Redo Button On-Action Failed:" + e);
				}
			});
			redobtn.setTooltip(new Tooltip("Redo an undone action"));
		
		this.fillcb = new CheckBox("Fill?");
		this.fillcb.setSelected(true);
		this.fillcb.setTooltip(new Tooltip("Check if you want solid shapes:"));
			
		Label drawlbl = new Label("Draw:");

		this.drawoptionmenu = new ComboBox();
		//drawoptionmenu.autosize();
		this.drawoptionmenu.getItems().addAll(
				EditToolBar.NONE,
				EditToolBar.LINE,
				EditToolBar.PENCIL,
				EditToolBar.SQUARE,
				EditToolBar.RECTANGLE,
				EditToolBar.CIRCLE,
				EditToolBar.ELLIPSE,
				EditToolBar.TRIANGLE,
				EditToolBar.NGON,
				EditToolBar.COLOR_GRAB,
				EditToolBar.TEXTBOX,
				EditToolBar.CROP,
				EditToolBar.DRAGDROP,
				EditToolBar.ROTATE,
				EditToolBar.BLUR,
				EditToolBar.SEPIA,
				EditToolBar.FREENGON,
				EditToolBar.BUCKETFILL, //testing
				EditToolBar.ERASE
		);
		this.drawoptionmenu.setValue(EditToolBar.NONE); //Set our default value to be NONE
		this.drawoptionmenu.setTooltip(new Tooltip("Select a tool to draw with"));

		Label optionslbl = new Label("Option:");
		this.options_fld = new TextField();
		this.options_fld.setMaxWidth(60); //Set to 60 to try to minimize the amount of wasted space.

		Label brushlbl = new Label("Brush (px):");

		this.brushfld = new TextField(Double.toString(Paint.brushSize));

		this.brushfld.setMaxWidth(60); //change the Max width to something smaller (helps fit more on the first line)
		//set the action for brushsize
		this.brushfld.setOnAction((ActionEvent event) -> {
			try {
				Paint.brushSize = Double.parseDouble(brushfld.getText());
			} catch (NumberFormatException e) {
				System.out.println("EditToolBar.java; Brush Field On-Action Failed:" + e);
			}
		});

		Label colorlbl = new Label("Color:");

		ColorPicker colorpick = Paint.colorpick;
		Button resetbtn = new Button();
		resetbtn.setText("Reset");
		resetbtn.setOnAction((ActionEvent event) -> {
			setDefaults();
			//update the values?
		});
		resetbtn.setTooltip(new Tooltip("Reset draw settings"));

		this.getItems().addAll(
			undobtn,
			redobtn,
			drawlbl,
			this.drawoptionmenu,
			this.fillcb,
			brushlbl,
			this.brushfld,
			colorlbl,
			colorpick,
			optionslbl,
			this.options_fld,
			resetbtn
		);
	}

	/**
	 * This method is gets the string value of the selected option; this method is
	 * used primarily in the CustomCanvas Handling code.
	 *
	 * @return the String of the combobox's selected option
	 */
	public String getDrawSelection(){
		return this.drawoptionmenu.getValue().toString();
	}
	/**
	 * Returns whatever string is present in options_fld.
	 *
	 * @return The String of the options field.
	 */
	public String getOptionsField(){
		return this.options_fld.getText();
	}

	/**
	 * This method sets all of the configurable part of the edit tool bar back to their default values.
	 */
	private void setDefaults(){
		Paint.colorpick.setValue(Color.BLACK);
		this.brushfld.setText("5");
		Paint.brushSize = Double.parseDouble(brushfld.getText());
		this.drawoptionmenu.setValue(EditToolBar.NONE);
		this.options_fld.setText(null);
	}
	
	public boolean getFill() {
		return this.fillcb.isSelected();
	}
	
	public void setTool(String tool) {
		this.drawoptionmenu.setValue(tool);
	}
	public void toggleFillCheckBox() {
		if (getFill()) {
			this.fillcb.setSelected(false);
		} else {
			this.fillcb.setSelected(true);
		}
	}
}
