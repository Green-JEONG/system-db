package org.main.culturesolutioncalculation;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.main.culturesolutioncalculation.service.database.DatabaseConnector;

import java.io.FileNotFoundException;
import java.net.URL;

public class Main extends Application {

    private final String url = "jdbc:mysql://localhost:3306/CultureSolutionCalculation?useSSL=false";
    private final String user = "root";
    private final String password = "root";

    @Override
    public void start(Stage stage) {
        try {
            initStage(stage);
            // DatabaseConnector 인스턴스를 초기화
            DatabaseConnector connector = DatabaseConnector.getInstance(url, user, password);

            // 종료 시 커넥션 풀도 함께 종료
            stage.setOnCloseRequest(e -> DatabaseConnector.close());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch();
    }

    public static void reload(Stage stage) {
        try {
            initStage(stage);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void initStage(Stage stage) throws Exception {
        //resources/org/main/culturesolutioncalculation/Main.fxml
        //Parent root = FXMLLoader.load(Main.class.getResource("Main.fxml"));

        URL url = Main.class.getResource("/org/main/culturesolutioncalculation/Main.fxml");
        if (url == null) {
            throw new RuntimeException("Resource not found: Main.fxml");
        }
        FXMLLoader loader = new FXMLLoader(url);
        Parent root = loader.load();

        if (root == null) {
            throw new FileNotFoundException("FXML file not found");
        }
        Scene scene = new Scene(root, 950, 750);

        stage.setTitle("배양액 계산 프로그램");
        stage.setMinWidth(950);
        stage.setMinHeight(750);

        stage.setScene(scene);
        stage.show();
    }
}