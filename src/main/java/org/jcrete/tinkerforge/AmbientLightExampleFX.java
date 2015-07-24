package org.jcrete.tinkerforge;

import com.tinkerforge.AlreadyConnectedException;
import com.tinkerforge.BrickletAmbientLight;
import com.tinkerforge.IPConnection;
import com.tinkerforge.NotConnectedException;
import com.tinkerforge.TimeoutException;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 *
 * @author sven
 */
public class AmbientLightExampleFX extends Application {

    private static final String HOST = "localhost";
    private static final int PORT = 4223;

    private IPConnection ipcon;

    @Override
    public void start(Stage primaryStage) throws Exception {
        ipcon = new IPConnection(); // Create IP connection
        ipcon.connect(HOST, PORT);

        // Connect to brickd
        // Don't use device before ipcon is connected
        // Set Period for illuminance callback to 1s (1000ms)
        // Note: The illuminance callback is only called every second if the 
        //       illuminance has changed since the last call!
        BorderPane borderPane = new BorderPane();

        Label label = new Label("Ambient Light Brick Id");
        final TextField idField = new TextField();
        Button run = new Button("Run");

        XYChart.Series<Number, Number> data = new XYChart.Series<>();

        run.setOnAction((ActionEvent event) -> {
            BrickletAmbientLight al = new BrickletAmbientLight(idField.getText(), ipcon);
            Platform.runLater(() -> data.getData().clear());
            long offset = System.currentTimeMillis();
            try {
                al.setIlluminanceCallbackPeriod(1000);
            } catch (TimeoutException ex) {
                Logger.getLogger(AmbientLightExampleFX.class.getName()).log(Level.SEVERE, null, ex);
            } catch (NotConnectedException ex) {
                Logger.getLogger(AmbientLightExampleFX.class.getName()).log(Level.SEVERE, null, ex);
            }
            al.addIlluminanceListener(new BrickletAmbientLight.IlluminanceListener() {
                public void illuminance(int illuminance) {
                    Platform.runLater(()
                            -> data.getData().add(new XYChart.Data<>((System.currentTimeMillis() - offset) / 1000, illuminance / 10.0)));
                }
            });
        });
        HBox hbox = new HBox(label, idField, run);

        TreeView<Brick> brickView = new TreeView<>();

        Map<String, TreeItem<Brick>> map = new HashMap<>();
        
        LineChart<Number, Number> illuminanceChart = new LineChart<Number, Number>(new NumberAxis(), new NumberAxis());

        ipcon.addEnumerateListener((String uid, String connectedUid, char position,
                short[] hardwareVersion, short[] firmwareVersion,
                int deviceIdentifier, short enumerationType) -> {
            System.out.println("UID:               " + uid);
            System.out.println("Enumeration Type:  " + enumerationType);

            if (enumerationType == IPConnection.ENUMERATION_TYPE_DISCONNECTED) {
                System.out.println("");
                return;
            }
            System.out.println("Connected UID:     " + connectedUid);
            System.out.println("Position:          " + position);
            System.out.println("Hardware Version:  " + hardwareVersion[0] + "."
                    + hardwareVersion[1] + "."
                    + hardwareVersion[2]);
            System.out.println("Firmware Version:  " + firmwareVersion[0] + "."
                    + firmwareVersion[1] + "."
                    + firmwareVersion[2]);
            System.out.println("Device Identifier: " + deviceIdentifier);
            System.out.println("Device Identifier: " + BrickType.getType(deviceIdentifier).getName());
            System.out.println("");

            Brick brick = new Brick(uid, connectedUid, position,
                    hardwareVersion, firmwareVersion,
                    deviceIdentifier, enumerationType);
            TreeItem<Brick> item = new TreeItem<>(brick);
            map.put(uid, item);
            if (connectedUid.equals("0")) {
                brickView.setRoot(item);
            } else {
                Platform.runLater(() -> map.get(connectedUid).getChildren().add(item));
            }
        });

        ipcon.enumerate();

        illuminanceChart.getData().add(data);

        borderPane.setTop(hbox);

        borderPane.setCenter(illuminanceChart);

        borderPane.setLeft(brickView);

        Scene scene = new Scene(borderPane, 600, 600);

        primaryStage.setScene(scene);
        primaryStage.initStyle(StageStyle.DECORATED);

        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        ipcon.disconnect();
    }

    public static void main(String[] args) {
        launch(args);
    }

}
