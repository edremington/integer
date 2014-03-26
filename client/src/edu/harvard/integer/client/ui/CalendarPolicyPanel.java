/*
 * 
 */
package edu.harvard.integer.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.FormPanel;
import com.google.gwt.user.client.ui.Grid;
import com.google.gwt.user.client.ui.HTMLTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TextBox;

import edu.harvard.integer.client.IntegerService;
import edu.harvard.integer.client.IntegerServiceAsync;
import edu.harvard.integer.client.widget.HvListBoxPanel;

/**
 * This class represents a form panel for importing MIB file.
 *
 * @author jhuang
 */
public class CalendarPolicyPanel extends FormPanel {

	/**
	 * Create a remote service proxy to talk to the server-side Greeting
	 * service.
	 */
	private final IntegerServiceAsync integerService = GWT
			.create(IntegerService.class);

	/**
	 * Create a new CalendarPolicyPanel.
	 */
	public CalendarPolicyPanel() {
		// Because we're going to add a FileUpload widget, we'll need to set the
		// form to use the POST method, and multipart MIME encoding.
		setEncoding(FormPanel.ENCODING_MULTIPART);
		setMethod(FormPanel.METHOD_POST);

		// Create a grid panel to hold all of the form widgets.
		Grid grid = new Grid(3, 2);
		setWidget(grid);

		// Create a CheckBox widget to indicate it is a standard MIB
		grid.setWidget(0, 0, new Label("Name"));
		final TextBox nameTextBox = new TextBox();
		grid.setWidget(0, 1, nameTextBox);
		
		grid.setWidget(1, 0, new Label("Description"));
		final TextBox descTextBox = new TextBox();
		grid.setWidget(1, 1, descTextBox);

		grid.setWidget(2, 0, new Label("Calendar List"));
		final HvListBoxPanel calendarListBoxPanel = new HvListBoxPanel(true);
		calendarListBoxPanel.setVisibleItemCount(5);
		calendarListBoxPanel.setName("calendarListBoxPanel");
		calendarListBoxPanel.addItem("06:00 AM Jan 20, 2014", "calendar1Value");
		calendarListBoxPanel.addItem("07:00 AM Feb 21, 2014", "calendar2Value");
		calendarListBoxPanel.addItem("09:00 AM March 22, 2014", "calendar3Value");
		grid.setWidget(2, 1, calendarListBoxPanel.getVisualPanel());

		// You can use the CellFormatter to affect the layout of the grid's
		// cells.
		grid.getCellFormatter().setWidth(0, 0, "150px");
		grid.getCellFormatter().setWidth(0, 1, "250px");

		HTMLTable.CellFormatter formatter = grid.getCellFormatter();
		formatter.setHorizontalAlignment(0, 1,
				HasHorizontalAlignment.ALIGN_LEFT);
		formatter.setVerticalAlignment(0, 1, HasVerticalAlignment.ALIGN_MIDDLE);

		// Add an event handler to the form.
		addSubmitHandler(new FormPanel.SubmitHandler() {
			public void onSubmit(final SubmitEvent submitEvent) {
				Window.alert("Under construction ...");
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