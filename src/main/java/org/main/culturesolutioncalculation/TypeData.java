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

//    public static ObservableList<String> getCropList(String type) {
//        System.out.println("============log test=========");
//        System.out.println("type = " + type);
//        ObservableList<String> cropList = FXCollections.observableArrayList();
//
//        CSVDataReader csvDataReader = new CSVDataReader();
//        ArrayList<String> nutrientSolutionList = csvDataReader.readDir();
//        for (String nutrientSolutionName : nutrientSolutionList) {
//            if (nutrientSolutionName.equals(type)) {
//
//                NutrientSolution nutrientSolution = csvDataReader.readFile(nutrientSolutionName);
//                for (CropNutrientStandard crop : nutrientSolution.getCropList()) {
//                    cropList.add(crop.getCropName());
//                }
//                break;
//            }
//        }
//
//        return cropList;
//    }

    public ObservableList<String> getCropList(String type){
        System.out.println("type = " + type);
        mediumService = new MediumService();

        ObservableList<String> cropList = FXCollections.observableArrayList();
        List<String> crops = mediumService.getCropNameList(type);

        for (String crop : crops) {
            cropList.add(crop);
        }
        return cropList;
    }

//    public static ObservableList<String[]> getCompositionData(String type) {
//        ObservableList<String[]> compositionData = FXCollections.observableArrayList();
//
//        CSVDataReader csvDataReader = new CSVDataReader();
//        NutrientSolution nutrientSolution = csvDataReader.readFile(type);
//        ArrayList<CropNutrientStandard> cropList = nutrientSolution.getCropList();
//
//        // 헤더 추가
//        String[] header = new String[17];
//        header[0] = "비료염";
//        header[1] = "EC(dS • m-1)";
//        header[2] = "NO3(mmol/L)";
//        header[3] = "NH4";
//        header[4] = "H2PO4";
//        header[5] = "K";
//        header[6] = "Ca";
//        header[7] = "Mg";
//        header[8] = "SO4";
//        header[9] = "Fe(µmol/L)";
//        header[10] = "Cu";
//        header[11] = "B";
//        header[12] = "Mn";
//        header[13] = "Zn";
//        header[14] = "Mo";
//        compositionData.add(header);
//
//        // 각 작물에 대한 데이터 추가
//        for (CropNutrientStandard crop : cropList) {
//            String[] rowData = new String[17];
//            rowData[0] = crop.getCropName();
//            rowData[1] = String.valueOf(crop.getEC());
//            rowData[2] = String.valueOf(crop.getNO3());
//            rowData[3] = String.valueOf(crop.getNH4());
//            rowData[4] = String.valueOf(crop.getH2PO4());
//            rowData[5] = String.valueOf(crop.getK());
//            rowData[6] = String.valueOf(crop.getCa());
//            rowData[7] = String.valueOf(crop.getMg());
//            rowData[8] = String.valueOf(crop.getSO4());
//            rowData[9] = String.valueOf(crop.getFe());
//            rowData[10] = String.valueOf(crop.getCu());
//            rowData[11] = String.valueOf(crop.getB());
//            rowData[12] = String.valueOf(crop.getMn());
//            rowData[13] = String.valueOf(crop.getZn());
//            rowData[14] = String.valueOf(crop.getMo());
//            compositionData.add(rowData);
//        }
//
//        return compositionData;
//    }

    //DB 연동으로 추가한거
    public ObservableList<String[]> getMediumTypeData(String type) {
        ObservableList<String[]> compositionData = FXCollections.observableArrayList();
        MediumService mediumService = new MediumService();

        System.out.println("type = " + type);
        List<CropNutrientStandard> cropList = mediumService.getCultureMediumData(type);

        //ArrayList<CropNutrientStandard> cropList = nutrientSolution.getCropList();

        // 헤더 추가
        String[] header = new String[18];
        header[0] = "ID";  // 표시하지 않도록 설정할 예정
        header[1] = "비료염";
        header[2] = "EC(dS • m-1)";
        header[3] = "NO3(mmol/L)";
        header[4] = "NH4";
        header[5] = "H2PO4";
        header[6] = "K";
        header[7] = "Ca";
        header[8] = "Mg";
        header[9] = "SO4";
        header[10] = "Fe(µmol/L)";
        header[11] = "Cu";
        header[12] = "B";
        header[13] = "Mn";
        header[14] = "Zn";
        header[15] = "Mo";
        compositionData.add(header);

        // 각 작물에 대한 데이터 추가 (ID 값 포함)
        for (CropNutrientStandard crop : cropList) {
            String[] rowData = new String[18];
            rowData[0] = String.valueOf(crop.getId());  // 첫 번째로 ID 값을 설정
            rowData[1] = crop.getCropName();
            rowData[2] = String.valueOf(crop.getEC());
            rowData[3] = String.valueOf(crop.getNO3());
            rowData[4] = String.valueOf(crop.getNH4());
            rowData[5] = String.valueOf(crop.getH2PO4());
            rowData[6] = String.valueOf(crop.getK());
            rowData[7] = String.valueOf(crop.getCa());
            rowData[8] = String.valueOf(crop.getMg());
            rowData[9] = String.valueOf(crop.getSO4());
            rowData[10] = String.valueOf(crop.getFe());
            rowData[11] = String.valueOf(crop.getCu());
            rowData[12] = String.valueOf(crop.getB());
            rowData[13] = String.valueOf(crop.getMn());
            rowData[14] = String.valueOf(crop.getZn());
            rowData[15] = String.valueOf(crop.getMo());
            compositionData.add(rowData);
        }

        return compositionData;
    }


}
