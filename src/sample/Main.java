package sample;


import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.shape.ArcType;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Button;
import javafx.scene.paint.Color;
import javafx.scene.input.MouseEvent;

import javax.swing.tree.TreeNode;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

public class Main extends Application {
    Button NodeButton = new Button("Add a Node");
    TextField Field = new TextField();
    Button joinButton = new Button("Join");
    Button rootButton = new Button("Pick the beginning");
    Button beginButton = new Button("Start Simulation");
    public Shape[] shapes = new Shape[500];  // Contains shapes the user has drawn.
    public int shapeCount = 0; // Number of shapes that the user has drawn.
    public Canvas canvas; // The drawing area where the user draws.
    private Color currentColor = Color.WHITE;  // Color to be used for new shapes.
    public ArrayList<Shape> joinXY = new ArrayList<Shape>();
    public ArrayList<Integer> gains = new ArrayList<Integer>();
    private boolean joinn = false;
    private boolean enterGained = false;
    private boolean selectroot = false;
    private int joinfig = 0;
    public int gainsNo = 0;
    public TreeNode root ;
    public int counter =1;
    boolean sameNode[] = new boolean[10000];
    @Override
    public void start(Stage stage) throws Exception{
        canvas = makeCanvas();
        paintCanvas();
        StackPane canvasHolder = new StackPane(canvas);
        canvasHolder.setStyle("-fx-border-width: 1px; -fx-border-color: #444");
        BorderPane root = new BorderPane(canvasHolder);
        //root.setStyle("-fx-border-width: 1px; -fx-border-color: #000000");
        root.setTop(makeToolPanel());
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Signal Flow Graph");
        stage.setResizable(false);
        stage.show();
        Alert a = new Alert(Alert.AlertType.CONFIRMATION);
        a.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
        a.setContentText("When joining two Nodes >Join them and Enter their gain");
        a.show();
    }
    private Canvas makeCanvas() {
        Canvas canvas = new Canvas(800,600);
        canvas.setOnMousePressed( this::mousePressed );
        canvas.setOnMouseReleased( this::mouseReleased );
        canvas.setOnMouseDragged( this::mouseDragged );
        return canvas;
    }
    public void textf(){
        String pr=Field.getText();
        if(pr.matches("-?\\d+")) {
            enterGained= true;
            gainsNo = Integer.parseInt(pr);
            System.out.println("number "+gainsNo);
            Field.clear();
            gains.add(gainsNo);
            lastTwoIndices();
            if(!this.joinn && enterGained){
                NodeButton.setDisable(false);
                beginButton.setDisable(false);
                rootButton.setDisable(false);
                enterGained = false;
            }
            Field.setDisable(true);
            joinButton.setDisable(false);
        }else{
            Alert a = new Alert(Alert.AlertType.ERROR);
            a.setContentText("Only Numbers are allowed with no spaces");
            a.show();
        }
    }
    public void join(){
        this.joinn=true;
        NodeButton.setDisable(true);
        beginButton.setDisable(true);
        rootButton.setDisable(true);
    }
    public void first(){
        this.selectroot = true;
    }
    public void lastTwoIndices(){
        GraphicsContext g = canvas.getGraphicsContext2D();
        if(joinXY.get(joinXY.size()-1).count!=joinXY.get(joinXY.size()-2).count) {
            int k = this.joinXY. get(joinXY.size() - 2).left + this.joinXY.get(joinXY.size() - 2).width / 2;
            int m = this.joinXY. get(joinXY.size() - 2).top + this.joinXY.get(joinXY.size() - 2).height / 2;
            int k2 = this.joinXY.get(joinXY.size() - 1).left + this.joinXY.get(joinXY.size() - 1).width / 2;
            int m2 = this.joinXY.get(joinXY.size() - 1).top + this.joinXY.get(joinXY.size() - 1).height / 2;
            g.strokeText(gains.get(gains.size() - 1) + "", (k + k2) / 2, (m + m2) / 2);
        }else{
            g.strokeText(gains.get( (joinXY.size()-2) / 2) + "", (joinXY.get(joinXY.size()-2).left+ joinXY.get(joinXY.size()-2).width/2), (joinXY.get(joinXY.size()-2).top+joinXY.get(joinXY.size()-2).height/2)-80);
        }
    }
    public void gett(){
        if (this.joinfig%2==0){
            if(this.joinn){
                Field.setDisable(false);
            }
            this.joinn=false;
            this.shapeBeingjoined = null;

            GraphicsContext g = canvas.getGraphicsContext2D();
            for(int i=0;i<this.joinXY.size()-1;i+=2) {
                if(joinXY.get(i).count!=joinXY.get(i+1).count) {//if it is the same node draw arc only no line to update or stroke text
                    int k = this.joinXY.get(i).left + this.joinXY.get(i).width / 2;
                    int m = this.joinXY.get(i).top + this.joinXY.get(i).height / 2;
                    int k2 = this.joinXY.get(i + 1).left + this.joinXY.get(i + 1).width / 2;
                    int m2 = this.joinXY.get(i + 1).top + this.joinXY.get(i + 1).height / 2;
                    LineWithArrow arrowLine = new LineWithArrow(k, m, k2, m2, g);
                    if (gains.size() > i / 2) {
                        g.strokeText(gains.get(i / 2) + "", (k + k2) / 2, (m + m2) / 2);
                    }
                }else{
                    g.strokeArc((joinXY.get(i).left+ joinXY.get(i).width/2)-15,(joinXY.get(i).top+joinXY.get(i).height/2)-80,25,450,45,90,ArcType.OPEN);
                    if (gains.size() > i / 2) {
                        g.strokeText(gains.get(i / 2) + "", (joinXY.get(i).left+ joinXY.get(i).width/2), (joinXY.get(i).top+joinXY.get(i).height/2)-80);
                    }
                }
            }
        }
    }

    private HBox makeToolPanel() {
        //maButton.setOnAction( (e) -> replay());
        NodeButton.setOnAction( (e) -> {addShape( new CircleShape(counter) );counter++;
        } );
        joinButton.setOnAction( (e) -> {
            join();
            Alert a = new Alert(Alert.AlertType.CONFIRMATION);
            a.getDialogPane().setMinHeight(Region.USE_PREF_SIZE);
            a.setContentText("Click on the two nodes and then Enter the branch Gain please");
            a.show();
            joinButton.setDisable(true);});
        rootButton.setOnAction( (e) -> first() );
        beginButton.setOnAction( (e) -> convertTo2DArray(counter,joinXY,gains) );
        beginButton.setStyle("-fx-background-color: Green");
        Field.setPromptText("Gain");
        Field.setOnAction( (e) -> textf() );
        Field.setDisable(true);
        HBox tools = new HBox(10);
        tools.getChildren().add(NodeButton);
        tools.getChildren().add(joinButton);
        tools.getChildren().add(rootButton);
        tools.getChildren().add(beginButton);
        tools.getChildren().add(Field);
        tools.setStyle("-fx-border-width: 5px; -fx-border-color: transparent; -fx-background-color: lightgray");
        return tools;
    }
    public void paintCanvas() {
        GraphicsContext g = canvas.getGraphicsContext2D();
        g.setFill(Color.WHITE);
        g.fillRect(0,0,canvas.getWidth(),canvas.getHeight());
        for (int i = 0; i < shapeCount; i++) {
            Shape s = shapes[i];
            s.draw(g);
            gett();
        }
    }

    private void addShape(Shape shape) {
        shape.setColor(currentColor);
        shape.reshape(10,10,35,35);
        shapes[shapeCount] = shape;
        shapeCount++;
        paintCanvas();
    }

    private Shape shapeBeingDragged = null;  // This is null unless a shape is being dragged.
    private Shape shapeBeingjoined = null;
    // A non-null value is used as a signal that dragging
    // is in progress, as well as indicating which shape
    // is being dragged.

    private int prevDragX;  // During dragging, these record the x and y coordinates of the
    private int prevDragY;

    private void mousePressed(MouseEvent evt) {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        int x = (int)evt.getX();  // x-coordinate of point where mouse was clicked
        int y = (int)evt.getY();  // y-coordinate of point
        for ( int i = shapeCount - 1; i >= 0; i-- ) {  // check shapes from front to back
            Shape s = shapes[i];
            if (s.containsPoint(x,y)) {
                if(this.selectroot==true){
                    //this.root = new TreeNode(s.hashCode(), s);
                    this.selectroot = false;
                }else{
                    if(this.joinn){
                        this.joinfig++;
                        if(this.joinXY.size()==0) {
                            this.joinXY.add(s);
                        }else{
                            Shape temp = this.joinXY.get(this.joinXY.size()-1);
                            if(this.joinXY.size()%2!=0){
                                if(temp.hashCode() == s.hashCode()){
                                   // this.joinXY.remove(this.joinXY.size()-1);

                                    gc.strokeArc((s.left+ s.width/2)-15,(s.top+s.height/2)-80,25,450,45,90,ArcType.OPEN);
                                    //NodeButton.setDisable(false);
                                    //beginButton.setDisable(false);
                                    //rootButton.setDisable(false);
                                    //enterGained = false;
                                   // this.joinn=false;
                                    //Field.setDisable(true);
                                }

                            }

                            this.joinXY.add(s);
                        }
                    }else {
                        shapeBeingDragged = s;
                    }}
                prevDragX = x;
                prevDragY = y;
                if (evt.isShiftDown()) { // s should be moved on top of all the other shapes
                    for (int j = i; j < shapeCount-1; j++) {
                        // move the shapes following s down in the list
                        shapes[j] = shapes[j+1];
                    }
                    shapes[shapeCount-1] = s;  // put s at the end of the list
                    paintCanvas();  // repaint canvas to show s in front of other shapes
                }
                gett();
                return;
            }
        }
    }

    private void mouseDragged(MouseEvent evt) {
        // User has moved the mouse.  Move the dragged shape by the same amount.
        int x = (int)evt.getX();
        int y = (int)evt.getY();
        if (shapeBeingDragged != null) {
            shapeBeingDragged.moveBy(x - prevDragX, y - prevDragY);
            prevDragX = x;
            prevDragY = y;
            paintCanvas();      // redraw canvas to show shape in new position
        }
    }

    private void mouseReleased(MouseEvent evt) {
        shapeBeingDragged = null;


    }
    public double[][] convertTo2DArray(int numberOfNodes , ArrayList<Shape> edges , ArrayList<Integer>gains){
      double adjacentMatrix[][]=new double[numberOfNodes-1][numberOfNodes-1];
      for (int  i = 0 ; i < edges.size()-1;i+=2){
          adjacentMatrix[edges.get(i).count-1][edges.get(i+1).count-1]=gains.get(i/2);
      }


      for (int i = 0 ; i < numberOfNodes-1 ; i++){
          System.out.println();
          for (int  j = 0 ; j < numberOfNodes-1;j++){
              System.out.print(adjacentMatrix[i][j]+" ");
          }
      }
        try {
            TableWindowController.setGraph(adjacentMatrix);
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(getClass().getResource("tableWindow.fxml"));
            /*
             * if "fx:controller" is not set in fxml
             * fxmlLoader.setController(NewWindowController);
             */
            Scene scene = new Scene(fxmlLoader.load(), 800, 800);
            Stage stage = new Stage();
            stage.setTitle("New Window");
            stage.setScene(scene);
            stage.show();


        } catch (IOException e) {
            e.printStackTrace();
        }
        return adjacentMatrix;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
