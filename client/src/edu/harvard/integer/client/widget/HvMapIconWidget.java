package edu.harvard.integer.client.widget;

import java.util.ArrayList;
import java.util.List;

import com.emitrom.lienzo.client.core.event.NodeDragEndEvent;
import com.emitrom.lienzo.client.core.event.NodeDragEndHandler;
import com.emitrom.lienzo.client.core.event.NodeDragMoveEvent;
import com.emitrom.lienzo.client.core.event.NodeDragMoveHandler;
import com.emitrom.lienzo.client.core.event.NodeDragStartEvent;
import com.emitrom.lienzo.client.core.event.NodeDragStartHandler;
import com.emitrom.lienzo.client.core.event.NodeMouseEnterEvent;
import com.emitrom.lienzo.client.core.event.NodeMouseEnterHandler;
import com.emitrom.lienzo.client.core.event.NodeMouseExitEvent;
import com.emitrom.lienzo.client.core.event.NodeMouseExitHandler;
import com.emitrom.lienzo.client.core.image.PictureLoadedHandler;
import com.emitrom.lienzo.client.core.shape.Group;
import com.emitrom.lienzo.client.core.shape.Line;
import com.emitrom.lienzo.client.core.shape.Picture;
import com.emitrom.lienzo.client.core.shape.Text;
import com.emitrom.lienzo.client.core.types.Point2D;
import com.emitrom.lienzo.client.core.types.Point2DArray;
import com.emitrom.lienzo.shared.core.types.Color;
import com.emitrom.lienzo.shared.core.types.ColorName;
import com.emitrom.lienzo.shared.core.types.TextAlign;

import edu.harvard.integer.client.utils.Coordinate;
import edu.harvard.integer.client.utils.LinePoints;

public class HvMapIconWidget extends Group implements NodeDragStartHandler, NodeDragMoveHandler, NodeDragEndHandler {

	private Picture picture;
	private int center_x;
	private int center_y;
	private String title;
	private List<LinePoints> lineConnectorList = new ArrayList<LinePoints>(); 
	private List<LinePoints> dragLineConnectorList = new ArrayList<LinePoints>();
	
	public HvMapIconWidget(Picture picture, String title) {
		this.picture = picture;
		this.title = title;
		
		setDraggable(true);
		setListening(true);

		final HvMapIconPopup popup = new HvMapIconPopup(title, "Ok", "Cambridge");
		final int dw = (int)picture.getClippedImageDestinationWidth()*3/2;
		final int dh = (int)picture.getClippedImageDestinationHeight()/2;
		
		
		picture.addNodeMouseEnterHandler(new NodeMouseEnterHandler() {  
            
			@Override
			public void onNodeMouseEnter(NodeMouseEnterEvent event) {
				popup.setPopupPosition(event.getX()+dw, event.getY()+dh);
				popup.show();
			}  
        });  
  
		picture.addNodeMouseExitHandler(new NodeMouseExitHandler() {

			@Override
			public void onNodeMouseExit(NodeMouseExitEvent event) {
				popup.hide();
			}  
            
        });  
		
		addNodeDragStartHandler(this);  
        addNodeDragMoveHandler(this);  
        addNodeDragEndHandler(this); 
	}
	
	public void draw(int x, int y) {
		center_x = x + DragImageWidget.IMAGE_WIDTH/2;
		center_y = y + DragImageWidget.IMAGE_HEIGHT/2;
		
		picture.setX(x).setY(y).onLoad(new PictureLoadedHandler() {

			@Override
			public void onPictureLoaded(Picture picture) {
				add(picture);
				HvMapIconWidget.this.getLayer().draw();
			}
			
		});
		
		Text text = new Text(title, "oblique normal bold", 24);
		text.setX(x).setY(y+110).setTextAlign(TextAlign.LEFT).setFillColor(ColorName.DARKBLUE.getValue()).setScale(0.5);
		add(text);
	}
	
	public void addLineConnector(LinePoints lc) {
		lineConnectorList.add(lc);
	}

	@Override
	public void onNodeDragEnd(NodeDragEndEvent event) {
		updateLines(event.getX(), event.getY(), true);
		// removeTempLines();
	}

	@Override
	public void onNodeDragMove(NodeDragMoveEvent event) {
		updateLines(event.getX(), event.getY(),false);
		// moveTempLines();
	}

	@Override
	public void onNodeDragStart(NodeDragStartEvent event) {
		updateLines(event.getX(), event.getY(), false);
		addDragLines(event.getX(), event.getY());
	}

	private void updateLines(int cur_x, int cur_y, boolean draw) {
		Point2D cur_point = new Point2D(cur_x, cur_y);
		
		for (LinePoints lineConnector : lineConnectorList) {
			Coordinate otherPoint = lineConnector.getEndPoint();
			Point2D endPoint = new Point2D(otherPoint.getX(), otherPoint.getY());
			Line line = lineConnector.getLine();
			line.setVisible(false);
			
			line.setPoints(new Point2DArray(cur_point, endPoint));
			
			if (draw) {
				line.setVisible(true);
				line.getScene().draw();
			}
		}
	}
	
	private void addDragLines(int cur_x, int cur_y) {
		Coordinate curPoint = new Coordinate(cur_x, cur_y);
		//Point2D cur_point = new Point2D(cur_x, cur_y);
		
		for (LinePoints lineConnector : lineConnectorList) {
			Coordinate otherPoint = lineConnector.getEndPoint();
			// Point2D other_point = new Point2D(otherPoint.getX(), otherPoint.getY());
			Line line = lineConnector.getLine();
			line.setVisible(false);
			
			Line newLine = new Line(cur_x, cur_y, otherPoint.getX(), otherPoint.getY());
			newLine.setStrokeColor(Color.getRandomHexColor()).setStrokeWidth(2).setFillColor(Color.getRandomHexColor());  	
			
			if (newLine.getParent() == null) {  
                getViewport().getDraglayer().add(newLine);  
                newLine.moveToBottom();  
            } 
			newLine.setVisible(true);
			
			// save lines being dragged
			LinePoints newLineConnector = new LinePoints(newLine, curPoint, otherPoint);
			dragLineConnectorList.add(newLineConnector);
		}
	}

}
