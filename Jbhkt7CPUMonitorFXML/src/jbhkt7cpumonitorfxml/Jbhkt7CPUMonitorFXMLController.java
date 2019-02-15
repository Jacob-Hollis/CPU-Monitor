
package jbhkt7cpumonitorfxml;

import java.io.File;
import java.lang.management.ManagementFactory;
import java.lang.management.OperatingSystemMXBean;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.util.Duration;

/**
 *
 * @author Jacob Hollis
 */
public class Jbhkt7CPUMonitorFXMLController implements Initializable {
    
    @FXML
    private AnchorPane root;
    @FXML
    private Label digitalDisplay;
    @FXML
    private Label recordLabel1;
    @FXML
    private Label recordLabel2;
    @FXML
    private Label recordLabel3;
    @FXML
    private Label recordLabel4;
    @FXML
    private Label recordLabel5;
    @FXML
    private Label diskSpace;
    @FXML
    private Label memoryUsage;
    @FXML
    private ImageView gaugeImage;
    @FXML
    private ImageView handImage;
    @FXML
    private Button startStopButton;
    @FXML
    private Button recordResetButton;
    
    private Timeline digitaltimeline;
    private Timeline gaugetimeline;
    private Timeline memorytimeline;
    private double startangle = 225;
    private int recordlistcounter = 1;
    private int recordcounter = 1;
    private boolean firsttimerun = false;
    
    
    private void updateGaugeDisplay(){
        gaugetimeline = new Timeline(
                new KeyFrame(Duration.millis(1),
                e -> {
                    double cpu = 0;    
                    cpu = Jbhkt7CPUMonitorFXMLController.getCPUUsage();
                    double angle = startangle + (cpu*270);
                    handImage.setRotate(angle);
                }),
        
              new KeyFrame(Duration.millis(1)
        ));
              
        gaugetimeline.setCycleCount(Animation.INDEFINITE);
        gaugetimeline.play();
    }
    
    private void updateDigitalDisplay(){
            digitaltimeline = new Timeline(
                    new KeyFrame(Duration.millis(1), 
                    e -> {
                    digitalDisplay.setText(getFormattedCPUValue() + "%");})
                    
                    
                                        
        );
        
        digitaltimeline.setCycleCount(Animation.INDEFINITE);
        digitaltimeline.play();
    }
    
    private void updateMemoryDisplay(){
        memorytimeline = new Timeline(
                    new KeyFrame(Duration.millis(1), 
                    e -> {
                    memoryUsage.setText(getFormattedMemoryValue() + "%");})

                                        
        );
        
        memorytimeline.setCycleCount(Animation.INDEFINITE);
        memorytimeline.play();
    }
    
    private void getUsedDiskSpace(){ //note: this is getting space for the main C: drive, it does not factor additional drives 
        long totalspace;
        totalspace = new File("/").getTotalSpace();
        long freespace;
        freespace = new File("/").getFreeSpace();
        
        double usedspace = (totalspace-freespace);
        double currentspace = (usedspace/totalspace)*100;
        System.out.println(currentspace);
        
        DecimalFormat decformat = new DecimalFormat("#0.00");
        String formattedspace = decformat.format(currentspace);
        
        diskSpace.setText(formattedspace + "%");
    }
    
    private String getFormattedCPUValue(){
                    double cpu = 0; 
                    cpu = Jbhkt7CPUMonitorFXMLController.getCPUUsage();
                    if(Double.isNaN(cpu)){ //redundant checking to see if value is equal to NaN, if so set the value to 0
                        cpu = 0;
                    }
                    double multiplycpu = cpu*100;
                    DecimalFormat decformat = new DecimalFormat("#0.00");
                    String formattedcpu = decformat.format(multiplycpu);
        
        return formattedcpu;
    }
    
    private String getFormattedMemoryValue(){
                    long freemem = 0; 
                    freemem = Jbhkt7CPUMonitorFXMLController.getFreeMemoryUsage();
                    long totalmem = 0;
                    totalmem = Jbhkt7CPUMonitorFXMLController.getTotalMemory();
                    double usedmem = (totalmem-freemem);
                    double currentmem = (usedmem/totalmem)*100;
                    DecimalFormat decformat = new DecimalFormat("#0.00");
                    String formattedmem = decformat.format(currentmem);
        
        return formattedmem;
    }
    
    private String getTime(){
        DateFormat dateFormat = new SimpleDateFormat("hh:mm:ss a");
        String formattedDate = dateFormat.format(new Date());
        
        return formattedDate;
    }
    
    private static double getCPUUsage() {
        OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
        double value = 0;
        for (Method method : operatingSystemMXBean.getClass().getDeclaredMethods()) {
            method.setAccessible(true);
            if (method.getName().startsWith("getSystemCpuLoad")
                    && Modifier.isPublic(method.getModifiers())) {
                try {
                    value = (double) method.invoke(operatingSystemMXBean);
                    if (Double.isNaN(value)){ //checking to see if value is equal to NaN, if so set the value to 0
                        value = 0;
                    }
                } catch (Exception e) {
                    value = 0;
                }
                return value;
            }
        }
        return value;
    }
    
    private static long getFreeMemoryUsage(){
        OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
        long value = 0;
        for (Method method : operatingSystemMXBean.getClass().getDeclaredMethods()) {
            method.setAccessible(true);
            if (method.getName().startsWith("getFreePhysicalMemorySize")
                    && Modifier.isPublic(method.getModifiers())) {
                try {
                    value = (long) method.invoke(operatingSystemMXBean);
                } catch (Exception e) {
                    value = 0;
                }
                return value;
            }
        }
        return value;
  
    }
    
    private static long getTotalMemory(){
        OperatingSystemMXBean operatingSystemMXBean = ManagementFactory.getOperatingSystemMXBean();
        long value = 0;
        for (Method method : operatingSystemMXBean.getClass().getDeclaredMethods()) {
            method.setAccessible(true);
            if (method.getName().startsWith("getTotalPhysicalMemorySize")
                    && Modifier.isPublic(method.getModifiers())) {
                try {
                    value = (long) method.invoke(operatingSystemMXBean);
                } catch (Exception e) {
                    value = 0;
                }
                return value;
            }
        }
        return value;
  
    }
    
    
    @FXML
    private void handleStartStop(ActionEvent event){
            if(isRunning() == false){
                updateDigitalDisplay();
                updateGaugeDisplay();
                updateMemoryDisplay();
                getUsedDiskSpace();
                startStopButton.setText("Stop");
                startStopButton.setStyle("-fx-background-color: red");
            }
            else{
                digitaltimeline.pause();
                gaugetimeline.pause();
                memorytimeline.pause();
                startStopButton.setText("Start");
                recordResetButton.setText("Reset");
                startStopButton.setStyle("-fx-background-color: green");
            }
    }
    
    @FXML
    private void handleRecordReset(){
        
      if(isRunning() == false){
          recordResetButton.setText("Record");
          handImage.setRotate(startangle);
          digitalDisplay.setText("--.--%");
          recordLabel1.setText("--.--%");
          recordLabel2.setText("--.--%");
          recordLabel3.setText("--.--%");
          recordLabel4.setText("--.--%");
          recordLabel5.setText("--.--%");
          memoryUsage.setText("--.--%");
          diskSpace.setText("--.--%");
          recordcounter = 1;
          recordlistcounter = 1;
          firsttimerun = false;
  
      }  
      else if(firsttimerun = true){ 
          
        if(recordlistcounter == 1){
        firsttimerun = true;
        recordLabel1.setText("Record " + recordcounter + ": " + getFormattedCPUValue() + "% at " + getTime());
        recordcounter++;
        recordlistcounter++;
        }
        else if(recordlistcounter == 2){
        recordLabel2.setText("Record " + recordcounter + ": " + getFormattedCPUValue() + "% at " + getTime());
        recordcounter++;
        recordlistcounter++;
        }
        else if(recordlistcounter == 3){
        recordLabel3.setText("Record " + recordcounter + ": " + getFormattedCPUValue() + "% at " + getTime());
        recordcounter++;
        recordlistcounter++;
        }
        else if(recordlistcounter == 4){
        recordLabel4.setText("Record " + recordcounter + ": " + getFormattedCPUValue() + "% at " + getTime());
        recordcounter++;
        recordlistcounter++;
        }
        else if(recordlistcounter == 5){
        recordLabel5.setText("Record " + recordcounter + ": " + getFormattedCPUValue() + "% at " + getTime());
        recordcounter++;
        recordlistcounter = 1;
        }
      } 
    }
    
    public boolean isRunning(){
        if(digitaltimeline != null){
            if(digitaltimeline.getStatus() == Animation.Status.RUNNING){
                return true;
            }
        }
        return false;
    }
    
     
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        handImage.setRotate(startangle); //initial angle of the gauge
    }    
    
}
