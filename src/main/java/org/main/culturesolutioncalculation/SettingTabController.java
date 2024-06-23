package org.main.culturesolutioncalculation;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.util.HashMap;
import java.util.Map;

public class SettingTabController {

    private SettingInfo settingInfo = MainController.getSettingInfo();

    private static MainController mainController;

    @FXML
    private Tab settingTab;
    @FXML
    private RadioButton macroPpm, macroMm, macroMe; //다량 원소 단위
    @FXML
    private RadioButton microPpm, microUm;
    @FXML
    private RadioButton calciumNitrate4, calciumNitrate10; //질산칼슘 4수염과 10수염

    @FXML
    private RadioButton sodiumMolybdateDihydrate, ammoniumHeptamolybdateTetrahydrate; //Na₂MoO₄·2H₂O 와 (NH₄)₂Mo₇O₂₄·4H₂O
    @FXML
    private CheckBox ph, ec, no3n, nh4n, p, k, ca, mg, s, cl, na, fe, b, mn, zn, cu, mo, hco3; // 원수 고려할 때의 옵션들
    @FXML
    private RadioButton considerFalse; // 원수 고려 X
    @FXML
    private RadioButton consideredUnitPpm, consideredUnitMm;

    private ToggleGroup macroUnitGroup = new ToggleGroup();
    private ToggleGroup microUnitGroup = new ToggleGroup();
    private ToggleGroup calciumNitrateGroup = new ToggleGroup();
    private ToggleGroup considerGroup = new ToggleGroup();
    private ToggleGroup molybdenumGroup = new ToggleGroup();
    private ToggleGroup consideredUnitGroup = new ToggleGroup();

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }


    @FXML
    private void initialize() {

        // 원수 고려 X 라디오 버튼 설정
        considerFalse.setToggleGroup(considerGroup);

        // 원수 고려 X가 선택될 때 고려 원수 입력 단위 비활성화
        considerFalse.selectedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) {
                disableConsideredUnitRadios(true);
                clearConsideredUnitCheckboxes(); // 체크박스 해제
            }
        });

        // 각 원수 고려 O 체크박스의 상태 변경 시 고려 원수 입력 단위 활성화 & 원수 고려 X 선택 가능해야 함
        ph.selectedProperty().addListener((observable, oldValue, newValue) -> checkAndEnableConsideredUnitRadios());
        ec.selectedProperty().addListener((observable, oldValue, newValue) -> checkAndEnableConsideredUnitRadios());
        no3n.selectedProperty().addListener((observable, oldValue, newValue) -> checkAndEnableConsideredUnitRadios());
        nh4n.selectedProperty().addListener((observable, oldValue, newValue) -> checkAndEnableConsideredUnitRadios());
        p.selectedProperty().addListener((observable, oldValue, newValue) -> checkAndEnableConsideredUnitRadios());
        k.selectedProperty().addListener((observable, oldValue, newValue) -> checkAndEnableConsideredUnitRadios());
        ca.selectedProperty().addListener((observable, oldValue, newValue) -> checkAndEnableConsideredUnitRadios());
        mg.selectedProperty().addListener((observable, oldValue, newValue) -> checkAndEnableConsideredUnitRadios());
        s.selectedProperty().addListener((observable, oldValue, newValue) -> checkAndEnableConsideredUnitRadios());
        cl.selectedProperty().addListener((observable, oldValue, newValue) -> checkAndEnableConsideredUnitRadios());
        na.selectedProperty().addListener((observable, oldValue, newValue) -> checkAndEnableConsideredUnitRadios());
        fe.selectedProperty().addListener((observable, oldValue, newValue) -> checkAndEnableConsideredUnitRadios());
        b.selectedProperty().addListener((observable, oldValue, newValue) -> checkAndEnableConsideredUnitRadios());
        mn.selectedProperty().addListener((observable, oldValue, newValue) -> checkAndEnableConsideredUnitRadios());
        zn.selectedProperty().addListener((observable, oldValue, newValue) -> checkAndEnableConsideredUnitRadios());
        cu.selectedProperty().addListener((observable, oldValue, newValue) -> checkAndEnableConsideredUnitRadios());
        mo.selectedProperty().addListener((observable, oldValue, newValue) -> checkAndEnableConsideredUnitRadios());
        hco3.selectedProperty().addListener((observable, oldValue, newValue) -> checkAndEnableConsideredUnitRadios());


        // 다량 원수 단위 선택
        macroPpm.setToggleGroup(macroUnitGroup);
        macroMe.setToggleGroup(macroUnitGroup);
        macroMm.setToggleGroup(macroUnitGroup);

        // 미량 원수 단위 선택
        microPpm.setToggleGroup(microUnitGroup);
        microUm.setToggleGroup(microUnitGroup);

        // 질산 칼슘 비료 선택
        calciumNitrate4.setToggleGroup(calciumNitrateGroup);
        calciumNitrate10.setToggleGroup(calciumNitrateGroup);

        // 몰리브뎀 비료 선택
        sodiumMolybdateDihydrate.setToggleGroup(molybdenumGroup);
        ammoniumHeptamolybdateTetrahydrate.setToggleGroup(molybdenumGroup);

        // 고려 원수 입려 단위 선택
        consideredUnitPpm.setToggleGroup(consideredUnitGroup);
        consideredUnitMm.setToggleGroup(consideredUnitGroup);
    }

    private void clearConsideredUnitCheckboxes() {
        ph.setSelected(false);
        ec.setSelected(false);
        no3n.setSelected(false);
        nh4n.setSelected(false);
        p.setSelected(false);
        k.setSelected(false);
        ca.setSelected(false);
        mg.setSelected(false);
        s.setSelected(false);
        cl.setSelected(false);
        na.setSelected(false);
        fe.setSelected(false);
        b.setSelected(false);
        mn.setSelected(false);
        zn.setSelected(false);
        cu.setSelected(false);
        mo.setSelected(false);
        hco3.setSelected(false);
    }

    private void checkAndEnableConsideredUnitRadios() {
        boolean anySelected = ph.isSelected() || ec.isSelected() || no3n.isSelected() || nh4n.isSelected() ||
                p.isSelected() || k.isSelected() || ca.isSelected() || mg.isSelected() ||
                s.isSelected() || cl.isSelected() || na.isSelected() || fe.isSelected() ||
                b.isSelected() || mn.isSelected() || zn.isSelected() || cu.isSelected() ||
                mo.isSelected() || hco3.isSelected();

        if (anySelected) {
            considerFalse.setSelected(false); // 원수 고려 O 체크박스가 선택된 경우 -> 원수 고려 X 선택 해제
        }
        disableConsideredUnitRadios(!anySelected);
    }

    private void disableConsideredUnitRadios(boolean disable) {
        consideredUnitPpm.setDisable(disable);
        consideredUnitMm.setDisable(disable);
        if (disable) {
            Toggle selectedToggle = consideredUnitGroup.getSelectedToggle();
            if(selectedToggle!=null)
                selectedToggle.setSelected(false); // 선택 해제
        }
    }

    private Map<String, String> getSelectedValues() {
        Map<String, String> selectedValues = new HashMap<>();

        // 각 라디오 버튼에서 선택된 값을 가져와서 맵에 추가
        selectedValues.put("질산칼슘 비료", getSelectedText(calciumNitrateGroup));
        selectedValues.put("몰리브뎀 비료", getSelectedText(molybdenumGroup));

        selectedValues.put("설정 다량원소 단위", getSelectedUnitText(macroUnitGroup));
        selectedValues.put("설정 미량원소 단위", getSelectedUnitText(microUnitGroup));
        selectedValues.put("고려 원수 입력 단위", getSelectedUnitText(consideredUnitGroup)); //고려 원수 입려 단위 밀리몰이랑 마이크로몰 왜 묶여있지? -> TODO 확인하기

        boolean isConsiderFalseSelected = considerFalse.isSelected();

        selectedValues.put("원수 고려 유무", isConsiderFalseSelected ? "X" : "O");

        if (isConsiderFalseSelected) { // 원수 고려 안하는 경우
            //selectedValues.put("원수 고려", "0");
        } else { // 원수 고려하는 경우
            addCheckBoxToMap(selectedValues, ph);
            addCheckBoxToMap(selectedValues, ec);
            addCheckBoxToMap(selectedValues, no3n);
            addCheckBoxToMap(selectedValues, nh4n);
            addCheckBoxToMap(selectedValues, p);
            addCheckBoxToMap(selectedValues, k);
            addCheckBoxToMap(selectedValues, ca);
            addCheckBoxToMap(selectedValues, mg);
            addCheckBoxToMap(selectedValues, s);
            addCheckBoxToMap(selectedValues, cl);
            addCheckBoxToMap(selectedValues, na);
            addCheckBoxToMap(selectedValues, fe);
            addCheckBoxToMap(selectedValues, b);
            addCheckBoxToMap(selectedValues, mn);
            addCheckBoxToMap(selectedValues, zn);
            addCheckBoxToMap(selectedValues, cu);
            addCheckBoxToMap(selectedValues, mo);
            addCheckBoxToMap(selectedValues, hco3);
        }

        for (String key : selectedValues.keySet()) {
            System.out.println("key = " + key);
            System.out.println("value = " + selectedValues.get(key));
        }

        return selectedValues;
    }

    private void addCheckBoxToMap(Map<String, String> map, CheckBox checkBox) {
        map.put(checkBox.getText(), checkBox.isSelected() ? "1" : "0");
    }

    private String getSelectedUnitText(ToggleGroup group){
        RadioButton selectedRadioButton = (RadioButton) group.getSelectedToggle();
        if (selectedRadioButton != null) {
            if(selectedRadioButton.getText().equals("ppm(피피엠)")) return "ppm";
            else if(selectedRadioButton.getText().equals("mM(밀리몰)")) return "mM";
            else if(selectedRadioButton.getText().equals("µM(마이크로몰)")) return "µM";
            else if(selectedRadioButton.getText().equals("me(밀리당량)")) return "me";
            return selectedRadioButton.getText();
        } else {
            return null; // 선택된 값이 없을 경우
        }
    }

    private String getSelectedText(ToggleGroup group) {
        RadioButton selectedRadioButton = (RadioButton) group.getSelectedToggle();
        if (selectedRadioButton != null) {
            return selectedRadioButton.getText();
        } else {
            return null; // 선택된 값이 없을 경우
        }
    }

    @FXML
    public void prevButton() {
        mainController.moveToUserInfoTab();
    }

    @FXML
    public void saveSettingInfo() {
        Map<String, String> selectedValues = getSelectedValues();
        settingInfo.setTotalSetting(selectedValues);


        mainController.moveToMacroTab();

    }



}