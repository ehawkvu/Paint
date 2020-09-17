/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package paint;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.ToolBar;
import javafx.scene.paint.Color;

/**
 *
 * @author ethan
 */
public class EditToolBar extends ToolBar{
	
	private ComboBox drawoptionmenu;
	private TextField brushfld;
	private TextField options_fld;

	public final String LINE = "Line";
	public final String PENCIL = "Pencil";
	public final String SQUARE = "Square";
	public final String RECTANGLE = "Rectangle";
	public final String CIRCLE = "Circle";
	public final String ELLIPSE = "Ellipse";
	public final String ERASE = "Erase";
	public final String COLOR_GRAB = "Color Grab";
	public final String NONE = "None";
	public final String TEXTBOX = "Text Box";

	public final String TRIANGLE = "Triangle";
	
	public EditToolBar() {
		
		super();		
		
		Button undobtn = new Button("Undo");
			undobtn.setOnAction((ActionEvent event)-> {
				Paint.imgcanvas.undo();
			});
		
		Button redobtn = new Button("Redo");
			redobtn.setOnAction((ActionEvent event) -> {
				Paint.imgcanvas.redo();
			});
			
		Label drawlbl = new Label("Draw:");
		
		this.drawoptionmenu = new ComboBox();
		//drawoptionmenu.autosize();
		drawoptionmenu.getItems().addAll(
				this.NONE,
				this.LINE, 
				this.PENCIL, 
				this.SQUARE,
				this.RECTANGLE, 
				this.CIRCLE, 
				this.ELLIPSE,
				this.TRIANGLE,
				this.COLOR_GRAB,
				this.TEXTBOX,
				this.ERASE
		);
		drawoptionmenu.setValue(this.NONE);
		
		
		Label optionslbl = new Label("Option:");
		options_fld = new TextField();
		options_fld.setMaxWidth(60);
		/*
		if (! this.getDrawSelection().equals(this.TEXTBOX)) {
			textbox_textfld.setVisible(false);
		} else {
			textbox_textfld.setVisible(true);
		}
		*/
		Label brushlbl = new Label("Brush (px):");
		brushfld = new TextField(Double.toString(Paint.imgcanvas.brushSize));
		brushfld.setMaxWidth(60); //change the Max width to something smaller (helps fit more on the first line)
		//set the action for brushsize
		brushfld.setOnAction((ActionEvent event) -> {
			Paint.imgcanvas.brushSize = Double.parseDouble(brushfld.getText());
			//init_canvas();
		});
		
		Label colorlbl = new Label("Color:");
		//set the action for hexcolorfield
		ColorPicker colorpick = Paint.imgcanvas.colorpick;

		Button resetbtn = new Button();
		resetbtn.setText("Reset");
		resetbtn.setOnAction((ActionEvent event) -> {
			setDefaults();
			//update the values?
		});

		this.getItems().addAll(
			undobtn, 
			redobtn,
			drawlbl, 
			drawoptionmenu, 
			brushlbl, 
			brushfld, 
			colorlbl, 
			colorpick, 
			optionslbl,
			options_fld,
			resetbtn
		);
	}	
	
	
	public String getDrawSelection(){
		return this.drawoptionmenu.getValue().toString();
	}
	
	public String getOptionsField(){
		return this.options_fld.getText();
	}
	
	private void setDefaults(){
		Paint.imgcanvas.colorpick.setValue(Color.BLACK);
		Paint.imgcanvas.brushSize = Double.parseDouble(brushfld.getText());
		this.brushfld.setText("5");
		this.drawoptionmenu.setValue(this.NONE);
	}
}
