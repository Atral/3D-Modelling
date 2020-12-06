import gmaths.*;
import java.nio.*;
import com.jogamp.common.nio.*;
import com.jogamp.opengl.*;

public class StopWatch {

    private long startTime = 0;
    private long stopTime = 0;
    private boolean running = false;
    private long pauseStart = 0;
  
  
    public void start() {
      this.startTime = System.currentTimeMillis();
      this.running = true;
    }
  
  
    public void stop() {
      this.stopTime = System.currentTimeMillis();
      this.running = false;
    }

  /*  public void pause(){
        this.pauseStart = System.currentTimeMillis();
    }

    public double unpause(){

        return pauseStart;
    }*/
  
  
    //elaspsed time in milliseconds
    public long getElapsedTime() {
      long elapsed;
      if (running) {
        elapsed = (System.currentTimeMillis() - startTime);
      } else {
        elapsed = (stopTime - startTime);
      }
      return elapsed;
    }
  
  
    //elaspsed time in seconds
    public long getElapsedTimeSecs() {
      long elapsed;
      if (running) {
        elapsed = ((System.currentTimeMillis() - startTime) / 1000);
      } else {
        elapsed = ((stopTime - startTime) / 1000);
      }
      return elapsed;
    }
  }