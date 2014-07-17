package edu.harvard.integer.client.ui;

import com.emitrom.lienzo.client.core.event.NodeMouseClickEvent;
import com.emitrom.lienzo.client.core.event.NodeMouseClickHandler;
import com.emitrom.lienzo.client.core.event.NodeMouseEnterEvent;
import com.emitrom.lienzo.client.core.event.NodeMouseEnterHandler;
import com.emitrom.lienzo.client.core.event.NodeMouseExitEvent;
import com.emitrom.lienzo.client.core.event.NodeMouseExitHandler;
import com.emitrom.lienzo.client.core.shape.Line;
import com.emitrom.lienzo.client.core.shape.Picture;
import com.emitrom.lienzo.shared.core.types.ColorName;
import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.touch.client.Point;

import edu.harvard.integer.client.resources.Resources;
import edu.harvard.integer.client.widget.HvDialogBox;
import edu.harvard.integer.client.widget.HvMapIconPopup;
import edu.harvard.integer.common.BaseEntity;
import edu.harvard.integer.common.ID;
import edu.harvard.integer.common.topology.InterNetworkLink;
import edu.harvard.integer.common.topology.Network;
import edu.harvard.integer.common.topology.NetworkInformation;

/**
 * The Class NetworkMap represents a map object of Integer.
 * This is a subclass class extended from IntegerMap.
 * It is able to display any number of service element by calculating the individual widget size.
 * 
 * @author  Joel Huang
 * @version 1.0, May 2014
 */
public class NetworkMap extends IntegerMap {
	
	/**
	 * Update network information including network lkist and link list
	 *
	 * @param networkkInfo the networkk info
	 */
	public void updateNetworkInformation(NetworkInformation networkkInfo) {
		update(networkkInfo.getNetworks());
		drawLinks(networkkInfo.getLinks());
	}

	/**
	 * Update method will refresh the panel with the given list of ServiceElement objects.
	 *
	 * @param result the result
	 */
	private void update(BaseEntity[] result) {
		entityMap.clear();
		removeAll();
		init_layout(result.length);
		
		// === testing only first 4 points ==
		int N = 10;
		init_layout(N);
		// ==================================
		
		int i = 0;
		ImageResource image = Resources.IMAGES.network();
		
		for (final BaseEntity entity : result) {
			if (!(entity instanceof Network))
				continue;
			
			// === test only first 4 points ====
			if (i >= N)
				break;
			
			Point point = calculatePoint(N, i++);
			// Point point = calculatePoint(result.length, i++);
			entityMap.put(entity.getID(), point);
			pointList.add(point);
			
			image = Resources.IMAGES.network();
			Network network = (Network) entity;
			network.getInterDeviceLinks();
			network.getServiceElements();
			
        	Picture picture = new Picture(image, icon_width, icon_height, true, null);
        	NodeMouseClickHandler mouseClickHandler = new NodeMouseClickHandler() {

        		@Override
        		public void onNodeMouseClick(NodeMouseClickEvent event) {
        			selectedEntity = entity;
        			selectedTimestamp = System.currentTimeMillis();
        		} 		
        	};
        	ServiceElementWidget icon = new ServiceElementWidget(picture, entity, mouseClickHandler);
        	icon.draw((int)point.getX(), (int)point.getY());
        	
        	add(icon);
		}
	}
	
	/**
	 * Draw links.
	 * @param links 
	 */
	private void drawLinks(InterNetworkLink[] links) {
		for (final InterNetworkLink link : links) {
			ID id1 = link.getSourceNetworkId();
			ID id2 = link.getDestinationNetworkId();
			Point p1 = entityMap.get(id1);
			Point p2 = entityMap.get(id2);
			
			if (p1 == null || p2 == null)
				continue;
			
			// draw line between p0 and p
			drawLink(link, p1, p2);
		}
	}
	
	private void drawLink(final InterNetworkLink link, Point p1, Point p2) {
		final HvMapIconPopup tooltip = new HvMapIconPopup();
		
		double x1 = p1.getX() + icon_width/2;
		double y1 = p1.getY() + icon_height/2;
		double x2 = p2.getX() + icon_width/2;
		double y2 = p2.getY() + icon_height/2;
		
		Line line = new Line(x1, y1, x2, y2);
		ColorName colorName = ColorName.BLUE;
		if (link.getName().equalsIgnoreCase("link-2-4"))
			colorName = ColorName.RED;
		else
			colorName = ColorName.GREEN;
//		if (linkStatus == null)
//			colorName = ColorName.GREY;
//		else if (linkStatus.equalsIgnoreCase("up"))
//			colorName = ColorName.GREEN;
//		else if (linkStatus.equalsIgnoreCase("down"));
//			colorName = ColorName.RED;
			
        line.setStrokeColor(ColorName.GREEN).setStrokeWidth(3).setFillColor(colorName);
        
        line.addNodeMouseEnterHandler(new NodeMouseEnterHandler() {  
            
			@Override
			public void onNodeMouseEnter(NodeMouseEnterEvent event) {
				int x = SystemSplitViewPanel.WESTPANEL_WIDTH + event.getX() + 15;
				int y = 100 + event.getY() - 15;

				tooltip.setPopupPosition(x, y);
				tooltip.update(link.getName(), "Not availabel");
				tooltip.show();
			}  
        });  
  
		line.addNodeMouseExitHandler(new NodeMouseExitHandler() {

			@Override
			public void onNodeMouseExit(NodeMouseExitEvent event) {
				tooltip.hide();
			}  
            
        });
		
		line.addNodeMouseClickHandler(new NodeMouseClickHandler() {

			@Override
			public void onNodeMouseClick(NodeMouseClickEvent event) {
				LinkDetailsPanel detailsPanel = new LinkDetailsPanel(link);
				HvDialogBox detailsDialog = new HvDialogBox("Link Details", detailsPanel);
				detailsDialog.enableOkButton(false);
				detailsDialog.setSize("400px", "150px");
				detailsDialog.center();
				detailsDialog.show();
			}
			
		});
        
        add(line);
	}

}