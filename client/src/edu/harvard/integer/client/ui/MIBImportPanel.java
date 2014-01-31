package edu.harvard.integer.client.ui;

import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.FileUpload;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;

/**
 * This class represents a form panel for importing MIB file
 * 
 * @author jhuang
 * 
 */
public class MIBImportPanel extends FormPanel {

	/**
	 * Create a new MibImportPanel.
	 */
	public MIBImportPanel() {
		// Because we're going to add a FileUpload widget, we'll need to set the
		// form to use the POST method, and multipart MIME encoding.
		setEncoding(FormPanel.ENCODING_MULTIPART);
		setMethod(FormPanel.METHOD_POST);

		// Create a grid panel to hold all of the form widgets.
		Grid grid = new Grid(2,2);
		setWidget(grid);

		// Create a CheckBox widget to indicate it is a standard MIB
		grid.setWidget(0, 0, new Label("Standard MIB"));
		final CheckBox checkbox = new CheckBox();
		checkbox.setName("checkboxFormElement");
		grid.setWidget(0, 1, checkbox);
		
		// Create a FileUpload widget
		grid.setWidget(1, 0, new Label("Select MIB File"));
		final FileUpload upload = new FileUpload();

		upload.setName("uploadFormElement");
		grid.setWidget(1, 1, upload);
		
		// You can use the CellFormatter to affect the layout of the grid's cells.
	    grid.getCellFormatter().setWidth(0, 0, "150px");
	    grid.getCellFormatter().setWidth(0, 1, "150px");
	    
	    HTMLTable.CellFormatter formatter = grid.getCellFormatter();
	    formatter.setHorizontalAlignment(0, 1, HasHorizontalAlignment.ALIGN_LEFT);
	    formatter.setVerticalAlignment(0, 1, HasVerticalAlignment.ALIGN_MIDDLE);


		// Add an event handler to the form.
		addSubmitHandler(new FormPanel.SubmitHandler() {
			public void onSubmit(SubmitEvent event) {
				// This event is fired just before the form is submitted. We can
				// take this opportunity to perform validation.
				String filename = upload.getFilename();
	            if (filename.length() == 0) {
	                Window.alert("No File Specified!");
					event.cancel();
				}
			}
		});
		addSubmitCompleteHandler(new FormPanel.SubmitCompleteHandler() {
			public void onSubmitComplete(SubmitCompleteEvent event) {
				// When the form submission is successfully completed, this
				// event is fired. Assuming the service returned a response of
				// type text/html, we can get the result text here (see the
				// FormPanel documentation for further explanation).
				Window.alert(event.getResults());
			}
		});
	}
}