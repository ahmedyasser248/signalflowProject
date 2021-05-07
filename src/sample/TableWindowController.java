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
    @FXML
    private TableColumn<SingleRow,String>deltasColumn;


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
        deltasColumn.setCellValueFactory(new PropertyValueFactory<SingleRow,String>("delta"));
        ArrayList<SingleRow> singleRows =new ArrayList<>();
        String[] forwardPaths = solver.getForwardPaths();
        int maxI=0;
        for (int  i =0 ; i<forwardPaths.length;i++){
            SingleRow singleRow = new SingleRow();
            singleRow.setForwardPath(forwardPaths[i]);
            singleRows.add(singleRow);
            maxI++;
        }
        String [] loops =solver.getLoops();
        for (int  i = 0 ; i < loops.length;i++){
            if(i<singleRows.size())
            {
                singleRows.get(i).setLoop(loops[i]);
            }  else{
                SingleRow singleRow = new SingleRow();
                singleRow.setLoop(loops[i]);
                singleRows.add(singleRow);
            }
        }
        Double [] loopsGains = solver.getLoopGains();
        for (int  i = 0 ; i < loopsGains.length;i++){
            if(i<singleRows.size()){
                singleRows.get(i).setLoopGains(loopsGains[i]+"");
            }
            else
            {
                SingleRow singleRow = new SingleRow();
                singleRow.setLoopGains(loopsGains[i]+"");
                singleRows.add(singleRow);
            }
        }
        String [] nonTouchingLoops = solver.getNonTouchingLoops();
        for (int  i = 0 ; i < nonTouchingLoops.length;i++){
            if(i<singleRows.size()){
                singleRows.get(i).setNonTouchingLoops(nonTouchingLoops[i]);
            }else{
                SingleRow singleRow = new SingleRow();
                singleRow.setNonTouchingLoops(nonTouchingLoops[i]);
                singleRows.add(singleRow);
            }
        }
        Double [] nonTouchingLoopsGains = solver.getNonTouchingGains();
        for (int  i = 0 ; i < nonTouchingLoopsGains.length;i++){
            if(i<singleRows.size()){
                singleRows.get(i).setNonTouchingLoopsGains(nonTouchingLoopsGains[i]+"");
            }else{
                SingleRow singleRow = new SingleRow();
                singleRow.setNonTouchingLoopsGains(nonTouchingLoopsGains[i]+"");
                singleRows.add(singleRow);
            }
        }
        Double [] deltas = solver.getDeltas();
        for (int  i = 0 ; i < deltas.length;i++){
            if(i<singleRows.size()){
                singleRows.get(i).setDelta(deltas[i]+"");
            }else{
                SingleRow singleRow = new SingleRow();
                singleRow.setDelta(deltas[i]+"");
                singleRows.add(singleRow);
            }
        }
        ObservableList<SingleRow> data = FXCollections.observableArrayList();
        data.addAll(singleRows);
        tv.setItems(data);
    }
}
