package org.main.culturesolutioncalculation;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.main.culturesolutioncalculation.service.print.SangJuPrint;

import java.util.Map;

public class PrintTabController {
    MainController mainController = new MainController();
    UserInfo userInfo = mainController.getUserInfo();
    SettingInfo settingInfo = mainController.getSettingInfo();

    @FXML
    private Label customerNameLabel;
    @FXML
    private Label addressLabel;
    @FXML
    private Label contactLabel;
    @FXML
    private Label processingDateLabel;
    @FXML
    private Label scaleLabel;
    @FXML
    private Label selectedCultureLabel;
    @FXML
    private Label selectedCropLabel;

    @FXML
    private Label settingsLabel;

    @FXML
    public void load() {
        try {
            customerNameLabel.setText(userInfo.getCustomerName());
            addressLabel.setText(userInfo.getAddress());
            contactLabel.setText(userInfo.getContact());
            processingDateLabel.setText(userInfo.getProcessingDate().toString());
            scaleLabel.setText(userInfo.getScale());
            selectedCultureLabel.setText(userInfo.getSelectedCulture());
            selectedCropLabel.setText(userInfo.getSelectedCrop());

            for (Map.Entry<String, Integer> entry : settingInfo.getTotalSetting().entrySet()) {
                String settingText = entry.getKey() + ": " + entry.getValue().toString();
                settingsLabel.setText(settingsLabel.getText() + settingText + "\n");
            }

            // Call generatePDF method to create and open the PDF
            SangJuPrint sangJuPrint = new SangJuPrint();
            sangJuPrint.generatePDF();

        } catch (NullPointerException e) {
            System.out.println("값이 입력되지 않았습니다.");
        }
    }

}

