package edu.harvard.integer.client.widget;

import java.util.Comparator;
import java.util.List;

import com.google.gwt.cell.client.CheckboxCell;
import com.google.gwt.cell.client.TextCell;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.Column;
import com.google.gwt.user.cellview.client.ColumnSortEvent.ListHandler;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.view.client.DefaultSelectionEventManager;
import com.google.gwt.view.client.ListDataProvider;
import com.google.gwt.view.client.MultiSelectionModel;
import com.google.gwt.view.client.ProvidesKey;
import com.google.gwt.view.client.SelectionModel;

import edu.harvard.integer.common.BaseEntity;
import edu.harvard.integer.common.ID;

/**
 * The Class HvCheckListPanel represents a panel showing a list of CheckBox.
 * This is a subclass class extended from com.google.gwt.user.client.ui.SimplePanel
 * 
 * It takes any generic data type for object represneted by CheckBox.
 * 
 * @author  Joel Huang
 * @version 1.0, May 2014
 *
 * @param <D> the generic type
 */
public class HvCheckListPanel<D> extends SimplePanel {
	
	/** The key provider. */
	public final ProvidesKey<D> KEY_PROVIDER = new ProvidesKey<D>() {
		@Override
		public Object getKey(D d) {
			Object key = null;
			
			if (d != null && d instanceof ID)
				key = ((ID)d).getIdentifier();
			else if (d != null && d instanceof Enum)
				key = ((Enum<?>)d).name();
			else if (d instanceof BaseEntity)
				key = ((BaseEntity) d).getName();
				
			return key;
		}
	};

	/**
	 * Creates a new HvCheckListPanel instance.
	 *
	 * @param dataProvider the data provider
	 * @param list the list
	 */
	public HvCheckListPanel(ListDataProvider<D> dataProvider, List<D> list) {
		CellTable<D> cellTable = new CellTable<D>(KEY_PROVIDER);

		if (list != null)
		for (D id : list) {
			dataProvider.getList().add(id);
		}

		cellTable.setWidth("100%", true);
		cellTable.setAutoHeaderRefreshDisabled(true);
		cellTable.setAutoFooterRefreshDisabled(true);

		// Attach a column handler to the ListDataProvider to sort the list
		ListHandler<D> sortHandler = new ListHandler<D>(list);
		cellTable.addColumnSortHandler(sortHandler);

		final SelectionModel<D> selectionModel = new MultiSelectionModel<D>(KEY_PROVIDER);
		cellTable.setSelectionModel(selectionModel,DefaultSelectionEventManager.<D> createCheckboxManager());

		// Initialize the columns.
		initTableColumns(cellTable, selectionModel, sortHandler);
		
		// Add the CellList to the adapter in the database.
		dataProvider.addDataDisplay(cellTable);
	
		setWidget(cellTable);
	}
	
	/**
	 * Initializes the table columns.
	 *
	 * @param cellTable the cell table
	 * @param selectionModel the selection model
	 * @param sortHandler the sort handler
	 */
	private void initTableColumns(CellTable<D> cellTable, final SelectionModel<D> selectionModel,
			ListHandler<D> sortHandler) {
		// Checkbox column. This table will uses a checkbox column for selection.
		Column<D, Boolean> checkColumn = new Column<D, Boolean>(
				new CheckboxCell(true, false)) {
			@Override
			public Boolean getValue(D id) {
				return selectionModel.isSelected(id);
			}
		};
		cellTable.addColumn(checkColumn, SafeHtmlUtils.fromSafeConstant("<br/>"));
		cellTable.setColumnWidth(checkColumn, 10, Unit.PX);

		Column<D, String> nameColumn = new Column<D, String>(new TextCell()) {
			@Override
			public String getValue(D d) {
				String value = null;
				if (d instanceof ID)
					value = ((ID)d).getName();
				else if (d instanceof Enum)
					value = ((Enum<?>)d).name();
				else if (d instanceof BaseEntity)
					value = ((BaseEntity) d).getName();
				
				return value;
			}
		};
		nameColumn.setSortable(true);
		sortHandler.setComparator(nameColumn, new Comparator<D>() {
			@Override
			public int compare(D d1, D d2) {
				if (d1 instanceof ID && d2 instanceof ID)
					return ((ID)d1).getName().compareTo(((ID)d2).getName());
				else if (d1 instanceof Enum && d2 instanceof Enum)
					return ((Enum<?>)d1).name().compareTo(((Enum<?>)d2).name());
				else if (d1 instanceof BaseEntity)
					return ((BaseEntity) d1).getName().compareTo(((BaseEntity) d2).getName());
				else
					return 1;
			}
		});
		cellTable.addColumn(nameColumn, "Name");
		cellTable.setColumnWidth(nameColumn, 30, Unit.PX);
	}
}
