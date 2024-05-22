package org.main.culturesolutioncalculation;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import org.main.culturesolutioncalculation.service.database.MediumService;
import org.main.culturesolutioncalculation.service.print.SangJuPrint;

import java.util.Map;

public class PrintTabController {
    MainController mainController = new MainController();
    UserInfo userInfo = MainController.getUserInfo();

    RequestHistoryInfo requestHistoryInfo = MainController.getRequestHistoryInfo();
    SettingInfo settingInfo = mainController.getSettingInfo();

    private MediumService mediumService;

    @FXML
    private Label customerNameLabel;
    @FXML
    private Label addressLabel;
    @FXML
    private Label contactLabel;
    @FXML
    private Label emailLabel;

    @FXML
    private Label processingDateLabel;
    @FXML
    private Label selectedCultureLabel;
    @FXML
    private Label selectedCropLabel;

    @FXML
    private Label settingsLabel;

    public PrintTabController() {
        this.mediumService = new MediumService();
    }

    @FXML
    public void load() {
        try {
            customerNameLabel.setText(userInfo.getName());
            addressLabel.setText(userInfo.getAddress());
            contactLabel.setText(userInfo.getContact());
            emailLabel.setText(userInfo.getEmail());
            processingDateLabel.setText(requestHistoryInfo.getRequestDate().toString());
            selectedCultureLabel.setText(mediumService.getMediumTypeName(requestHistoryInfo.getMediumTypeId()));
            selectedCropLabel.setText(requestHistoryInfo.getSelectedCropName());

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

