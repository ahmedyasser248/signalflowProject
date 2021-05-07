package sample;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

public class LineWithArrow extends Path{
    private static final double defaultArrowHeadSize = 12.0;

    public LineWithArrow(int startX, int startY, int endX, int endY,GraphicsContext g, double arrowHeadSize ){
        super();
        strokeProperty().bind(fillProperty());
        setFill(Color.BLACK);

        //Line
        getElements().add(new MoveTo(startX, startY));
        getElements().add(new LineTo(endX, endY));

        //ArrowHead
        double angle = Math.atan2((endY - startY), (endX - startX)) - Math.PI / 2.0;
        double sin = Math.sin(angle);
        double cos = Math.cos(angle);
        //point1
        double x1 = (- 1.0 / 2.0 * cos + Math.sqrt(3) / 2 * sin) * arrowHeadSize + (startX+endX)/2;
        double y1 = (- 1.0 / 2.0 * sin - Math.sqrt(3) / 2 * cos) * arrowHeadSize + (startY+endY)/2;
        //point2
        double x2 = (1.0 / 2.0 * cos + Math.sqrt(3) / 2 * sin) * arrowHeadSize + (startX+endX)/2;
        double y2 = (1.0 / 2.0 * sin - Math.sqrt(3) / 2 * cos) * arrowHeadSize + (startY+endY)/2;
        g.beginPath();
        g.moveTo(startX, startY);
        g.lineTo(endX, endY);
        g.lineTo((startX+endX)/2,(startY+endY)/2);
        g.lineTo(x1, y1);
        g.lineTo(x2, y2);
        g.lineTo((startX+endX)/2,(startY+endY)/2);
        g.stroke();
        /*getElements().add(new LineTo(x1, y1));
        getElements().add(new LineTo(x2, y2));
        getElements().add(new LineTo(endX, endY));*/
    }

    public LineWithArrow(int startX, int startY, int endX, int endY, GraphicsContext g){
        this(startX, startY, endX, endY,g, defaultArrowHeadSize);
    }
}