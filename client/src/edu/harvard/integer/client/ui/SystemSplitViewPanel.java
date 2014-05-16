package edu.harvard.integer.client.ui;

import com.emitrom.lienzo.client.core.mediator.EventFilter;
import com.emitrom.lienzo.client.core.mediator.MouseWheelZoomMediator;
import com.emitrom.lienzo.client.widget.LienzoPanel;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DockPanel;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Tree;
import com.google.gwt.user.client.ui.TreeItem;
import com.google.gwt.user.client.ui.VerticalPanel;

import edu.harvard.integer.client.MainClient;
import edu.harvard.integer.client.widget.HvDialogBox;
import edu.harvard.integer.client.widget.HvIconButton;
import edu.harvard.integer.common.topology.ServiceElement;

public class SystemSplitViewPanel extends SplitLayoutPanel {
	public static final int SPLITTER_SIZE = 4;
	public static final int CONTENT_WIDTH = 1200;
	public static final int CONTENT_HEIGHT = 600;
	public static final int WESTPANEL_WIDTH = 250;
	
	public static SplitLayoutPanel westPanel = null;
	public static DockPanel eastPanel = null;
	public static SplitLayoutPanel eastSplitPanel = null;
	
	public static final String title = "Device Children";
	public static final String[] headers = {"Name", "Status", "Description"};
	public static ContaineeTreeView containeeTreeView = null;
	
	public static HvIconButton detailsButton = new HvIconButton("Summary");

	public static ServiceElement selectedElement;

	private LienzoPanel networkPanel = new LienzoPanel(CONTENT_WIDTH, CONTENT_HEIGHT);

	public SystemSplitViewPanel() {
		super(SPLITTER_SIZE);

        //DragImageWidget dragImageWidget = new DragImageWidget(WIDGET_WIDTH, WIDGET_HEIGHT);
		final DeviceMap deviceMap = new DeviceMap();
		MainClient.integerService.getTopLevelElements(new AsyncCallback<ServiceElement[]>() {

			@Override
			public void onFailure(Throwable caught) {
				// Window.alert("Failed to receive Devices from Integer");
				deviceMap.demo(100);
			}

			@Override
			public void onSuccess(ServiceElement[] result) {
				deviceMap.update(result);
			}
		});
		
        networkPanel.add(deviceMap);
        
        networkPanel.getViewport().pushMediator(new MouseWheelZoomMediator(EventFilter.ANY));
        LienzoPanel.enableWindowMouseWheelScroll(true);
        
        setSize("100%", MainClient.WINDOW_HEIGHT+"px");
		
		FilterView filterView = createFilterView();
		westPanel = new SplitLayoutPanel(SPLITTER_SIZE);
		westPanel.addSouth(filterView, 200);
		westPanel.add(createNetworkTreePanel());
		westPanel.setWidgetToggleDisplayAllowed(filterView, true);

		EventView eventView = createEventView();
		
		eastPanel = new DockPanel();
		eastPanel.setBorderWidth(1);
		eastPanel.setWidth("100%");

		HorizontalPanel mapToolbarPanel = new HorizontalPanel();
		mapToolbarPanel.setStyleName("toolbar");
		
		// Details button
		detailsButton.addClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				selectedElement = deviceMap.getSelectedElement();
				ServiceElementPanel detailsPanel = new ServiceElementPanel();
				HvDialogBox detailsDialog = new HvDialogBox("Service Element: " + selectedElement.getName(),
						detailsPanel);
				detailsDialog.setSize("400px", "150px");
				detailsDialog.center();
				detailsDialog.show();
			}
			
		});
		mapToolbarPanel.add(detailsButton);
		
		eastSplitPanel = new SplitLayoutPanel(SPLITTER_SIZE);
		eastSplitPanel.setSize("100%",  "500px");
		
		containeeTreeView = new ContaineeTreeView(title, headers);
		eastSplitPanel.addEast(containeeTreeView, 300);
		eastSplitPanel.setWidgetHidden(containeeTreeView, true);
		eastSplitPanel.setWidgetToggleDisplayAllowed(containeeTreeView, true);
		eastSplitPanel.add(networkPanel);
		
		eastPanel.add(mapToolbarPanel, DockPanel.NORTH);
		eastPanel.add(eastSplitPanel, DockPanel.CENTER);
		eastPanel.add(eventView, DockPanel.SOUTH);
		
		addWest(westPanel, WESTPANEL_WIDTH);
		add(eastPanel);
	}
	
	private VerticalPanel createNetworkTreePanel() {
		VerticalPanel treePanel = new VerticalPanel();
		Tree staticTree = createStaticTree();
	    staticTree.setAnimationEnabled(true);
	    staticTree.ensureDebugId("cwTree-staticTree");
	    ScrollPanel staticTreeWrapper = new ScrollPanel(staticTree);
	    staticTreeWrapper.ensureDebugId("cwTree-staticTree-Wrapper");
	    staticTreeWrapper.setSize("250px", "500px");
	    
	    treePanel.add(staticTree);
	    
	    return treePanel;
	}
	
	private Tree createStaticTree() {
	    // Create the tree
	    String[] networks = {"Cambridge Campus", "Allston Campus", "Longwood Medical", };
	    String[] subnetworks = {"192.168.1.", "192.168.2.", "192.168.3.", };
	    
	    Tree tree = new Tree();
	    tree.setAnimationEnabled(true);
	    TreeItem root = new TreeItem();
	    root.setText("Physical Network");

	    final TreeItem deviceNode = root.addTextItem("Discovered Devices");
	    SelectionHandler<TreeItem> handler = new SelectionHandler<TreeItem>() {
			@Override
			public void onSelection(SelectionEvent<TreeItem> event) {
				if(event.getSelectedItem() == deviceNode) {
					
		            networkPanel.removeAll();

		            final DeviceMap deviceMap = new DeviceMap();
		            
		            networkPanel.add(deviceMap);
		            networkPanel.getViewport().pushMediator(new MouseWheelZoomMediator(EventFilter.ANY));
		            
		            MainClient.integerService.getTopLevelElements(new AsyncCallback<ServiceElement[]>() {

						@Override
						public void onFailure(Throwable caught) {
							Window.alert("Failed to receive Devices from Integer");
							deviceMap.demo(500);
						}

						@Override
						public void onSuccess(ServiceElement[] result) {
							if (result != null || result.length == 0)
								deviceMap.demo(1000);
							
							deviceMap.update(result);
						}
					});
		        }
			}
	    	
	    };
		tree.addSelectionHandler(handler);
	    
	    int i = 1;
	    for (String network : networks) {
		    TreeItem cambridgeNet = root.addTextItem(network);
		    for (String subnet : subnetworks) {
		    	cambridgeNet.addTextItem(subnet+i++);
			}
	    }

	    root.setState(true);
	    tree.addItem(root);
	    return tree;
	}
	
	private FilterView createFilterView() {
		String title = "Layer 3 Topology";
		String subTitle = "State - Campus Wide";
		final String[] headers = {"Views", "Filters", "Manager"};
		final FilterView filterView = new FilterView(title, subTitle, headers);
		/*integerService.getEvents(new AsyncCallback<List<Object>>() {

			@Override
			public void onSuccess(List<Object> result) {
				filterView.update(result);
			}

			@Override
			public void onFailure(Throwable caught) {
			}
		});*/
		return filterView;
	}
	
	private EventView createEventView() {
		String title = "Events";
		final String[] headers = {"Type", "Severity", "Start Time", "Status", "Description"};
		final EventView eventView = new EventView(title, headers);
		/*integerService.getEvents(new AsyncCallback<List<Object>>() {

			@Override
			public void onSuccess(List<Object> result) {
				eventView.update(result);
			}

			@Override
			public void onFailure(Throwable caught) {
			}
		});*/
		return eventView;
	}

	public static void enablecontaineeTreeView(boolean enable) {
		eastSplitPanel.setWidgetHidden(containeeTreeView, !enable);
	}
	
	public static void showContaineeTreeView(final ServiceElement se) {
		//containeeTreeView.updateTitle(se.getName());
		eastSplitPanel.setWidgetHidden(containeeTreeView, false);
		
		MainClient.integerService.getServiceElementByParentId(se.getID(), new AsyncCallback<ServiceElement[]>() {

			@Override
			public void onFailure(Throwable caught) {
				Window.alert("Error");
			}

			@Override
			public void onSuccess(ServiceElement[] serviceElements) {
				/*if (serviceElements == null || serviceElements.length == 0) {
					serviceElements = new ServiceElement[9];
					for (int i = 0; i < serviceElements.length; i++) {
						serviceElements[i] = new ServiceElement();
						serviceElements[i].setName("fake-se" + (i+1));
					}
				}
				*/
				containeeTreeView.updateTree(se.getName(), serviceElements);
			}
		});
		
		ServiceElement[] serviceElements = new ServiceElement[39];
		for (int i = 0; i < serviceElements.length; i++) {
			serviceElements[i] = new ServiceElement();
			serviceElements[i].setName("fake-se" + (i+1));
		}
	
		containeeTreeView.updateTree(se.getName(), serviceElements);
		
	}
}
