package sample;

import javafx.beans.property.SimpleStringProperty;

public class SingleRow {
    private SimpleStringProperty forwardPath=new SimpleStringProperty("");
    private SimpleStringProperty loop =new SimpleStringProperty("");
    private SimpleStringProperty loopGains = new SimpleStringProperty("");
    private SimpleStringProperty nonTouchingLoops = new SimpleStringProperty("");
    private SimpleStringProperty nonTouchingLoopsGains = new SimpleStringProperty("");

    public void setForwardPath(String forwardPath) {
        this.forwardPath.set(forwardPath);
    }

    public void setLoop(String loop) {
        this.loop.set(loop);
    }

    public String getForwardPath() {
        return forwardPath.get();
    }

    public String getLoop() {
        return loop.get();
    }

    public String getLoopGains() {
        return loopGains.get();
    }

    public String getNonTouchingLoops() {
        return nonTouchingLoops.get();
    }

    public String getNonTouchingLoopsGains() {
        return nonTouchingLoopsGains.get();
    }


    public void setLoopGains(String loopGains) {
        this.loopGains.set(loopGains);
    }

    public void setNonTouchingLoops(String nonTouchingLoops) {
        this.nonTouchingLoops.set(nonTouchingLoops);
    }

    public void setNonTouchingLoopsGains(String nonTouchingLoopsGains) {
        this.nonTouchingLoopsGains.set(nonTouchingLoopsGains);
    }
}
