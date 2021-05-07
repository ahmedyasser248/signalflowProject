package sample;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class TableWindowController implements Initializable {
    @FXML
    TableView tv ;
    @FXML
    private TableColumn<SingleRow,String>forwardPathColumns;
    @FXML
    private TableColumn<SingleRow,String>loopsColumn;
    @FXML
    private TableColumn<SingleRow,String>gainsColumn;
    @FXML
    private TableColumn<SingleRow,String>nonTouchingLoopsColumn;
    @FXML
    private TableColumn<SingleRow,String>nonTouchingLoopsGains;


    static double[][] graph;
    static Solver solver ;
    static public void setGraph(double[][] graphx){
        graph=graphx;
        System.out.println("reach here");

    }

    public double[][] getGraph() {
        return graph;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
      /*  ArrayList<String> forwardPaths = solver.getForwardPaths();
        String forwardPathsString="" ;
        for (int  i = 0 ; i < forwardPaths.size();i++){
            forwardPathsString+=forwardPaths.get(i)+"\n";
        }
        ArrayList<String>loops =solver.getLoops();
        String loopsString ="";
        for (int  i = 0 ; i < loops.size();i++){
            loopsString+=loops.get(i)+"\n";
        }
        ArrayList<Double> loopsGains = solver.getLoopGains();
        for (int  i = 0 ; i < loopsGains.size();i++){

        }*/
        solver = new Solver(graph);
        solver.solve();
        forwardPathColumns.setCellValueFactory(new PropertyValueFactory<SingleRow,String>("forwardPath"));
        loopsColumn.setCellValueFactory(new PropertyValueFactory<SingleRow,String>("loop"));
        gainsColumn.setCellValueFactory(new PropertyValueFactory<SingleRow,String>("loopGains"));
        nonTouchingLoopsColumn.setCellValueFactory(new PropertyValueFactory<SingleRow,String>("nonTouchingLoops"));
        nonTouchingLoopsGains.setCellValueFactory(new PropertyValueFactory<SingleRow,String>("nonTouchingLoopsGains"));
        ArrayList<SingleRow> singleRows =new ArrayList<>();
        ArrayList<String> forwardPaths = solver.getForwardPaths();
        int maxI=0;
        for (int  i =0 ; i<forwardPaths.size();i++){
            SingleRow singleRow = new SingleRow();
            singleRow.setForwardPath(forwardPaths.get(i));
            singleRows.add(singleRow);
            maxI++;
        }
        ArrayList<String>loops =solver.getLoops();
        for (int  i = 0 ; i < loops.size();i++){
            if(i<singleRows.size())
            {
                singleRows.get(i).setLoop(loops.get(i));
            }  else{
                SingleRow singleRow = new SingleRow();
                singleRow.setLoop(loops.get(i));
                singleRows.add(singleRow);
            }
        }
        ArrayList<Double> loopsGains = solver.getLoopGains();
        for (int  i = 0 ; i < loopsGains.size();i++){
            if(i<singleRows.size()){
                singleRows.get(i).setLoopGains(loopsGains.get(i)+"");
            }
            else
            {
                SingleRow singleRow = new SingleRow();
                singleRow.setLoopGains(loopsGains.get(i)+"");
                singleRows.add(singleRow);
            }
        }
        ArrayList<String> nonTouchingLoops = solver.getNonTouchingLoops();
        for (int  i = 0 ; i < nonTouchingLoops.size();i++){
            if(i<singleRows.size()){
                singleRows.get(i).setNonTouchingLoops(nonTouchingLoops.get(i));
            }else{
                SingleRow singleRow = new SingleRow();
                singleRow.setNonTouchingLoops(nonTouchingLoops.get(i));
                singleRows.add(singleRow);
            }
        }
        System.out.println(nonTouchingLoops.size());
        ArrayList<Double>nonTouchingLoopsGains = solver.getNonTouchingGains();
        for (int  i = 0 ; i < nonTouchingLoopsGains.size();i++){
            if(i<singleRows.size()){
                singleRows.get(i).setNonTouchingLoopsGains(nonTouchingLoopsGains.get(i)+"");
            }else{
                SingleRow singleRow = new SingleRow();
                singleRow.setNonTouchingLoopsGains(nonTouchingLoopsGains.get(i)+"");
                singleRows.add(singleRow);
            }
        }
        ObservableList<SingleRow> data = FXCollections.observableArrayList();
        data.addAll(singleRows);
        tv.setItems(data);
    }
}
