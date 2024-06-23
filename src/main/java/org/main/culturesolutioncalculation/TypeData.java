package org.main.culturesolutioncalculation;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.main.culturesolutioncalculation.model.CropNutrientStandard;
import org.main.culturesolutioncalculation.model.NutrientSolution;
import org.main.culturesolutioncalculation.service.CSVDataReader;
import org.main.culturesolutioncalculation.service.database.MediumService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TypeData {

    private MediumService  mediumService;


    public ObservableList<String> getCropList(String type){
        mediumService = new MediumService();

        ObservableList<String> cropList = FXCollections.observableArrayList();
        List<String> crops = mediumService.getCropNameList(type);

        for (String crop : crops) {
            cropList.add(crop);
        }
        return cropList;
    }


    //DB 연동으로 추가한거
    public ObservableList<String[]> getMediumTypeData(String type) {
        ObservableList<String[]> compositionData = FXCollections.observableArrayList();
        MediumService mediumService = new MediumService();

        System.out.println("type = " + type);
        List<CropNutrientStandard> cropList = mediumService.getCultureMediumData(type);

        //ArrayList<CropNutrientStandard> cropList = nutrientSolution.getCropList();

        // 헤더 추가
        String[] header = new String[19];
        header[0] = "ID";  // 표시하지 않도록 설정할 예정
        header[1] = "비료염";
        header[2] = "EC(dS • m-1)";
        header[3] = "NO3(mmol/L)";
        header[4] = "NH4";
        header[5] = "P";
        header[6] = "H2PO4";
        header[7] = "K";
        header[8] = "Ca";
        header[9] = "Mg";
        header[10] = "SO4";
        header[11] = "Fe(µmol/L)";
        header[12] = "Cu";
        header[13] = "B";
        header[14] = "Mn";
        header[15] = "Zn";
        header[16] = "Mo";
        compositionData.add(header);

        // 각 작물에 대한 데이터 추가 (ID 값 포함)
        for (CropNutrientStandard crop : cropList) {
            String[] rowData = new String[19];
            rowData[0] = String.valueOf(crop.getId());  // 첫 번째로 ID 값을 설정
            rowData[1] = crop.getCropName();
            rowData[2] = String.valueOf(crop.getEC());
            rowData[3] = String.valueOf(crop.getNO3());
            rowData[4] = String.valueOf(crop.getNH4());
            rowData[5] = String.valueOf(crop.getP());
            rowData[6] = String.valueOf(crop.getH2PO4());
            rowData[7] = String.valueOf(crop.getK());
            rowData[8] = String.valueOf(crop.getCa());
            rowData[9] = String.valueOf(crop.getMg());
            rowData[10] = String.valueOf(crop.getSO4());
            rowData[11] = String.valueOf(crop.getFe());
            rowData[12] = String.valueOf(crop.getCu());
            rowData[13] = String.valueOf(crop.getB());
            rowData[14] = String.valueOf(crop.getMn());
            rowData[15] = String.valueOf(crop.getZn());
            rowData[16] = String.valueOf(crop.getMo());
            compositionData.add(rowData);
        }

        return compositionData;
    }


}
