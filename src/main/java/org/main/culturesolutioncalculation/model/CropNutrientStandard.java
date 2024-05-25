package org.main.culturesolutioncalculation.model;


// 작물 배양액과 기준값
public class CropNutrientStandard {
    private final int id;
    private final  String cropName;
    private final String nutrientSolutionName;
    private double EC;      // 전기전도도 (dS•m-1)
    private double PH; //산도
    private double HCO3; //농도
    private final double NO3;   // 다량 질산 이온 (mmol/L)
    private final double NH4;    // 다량 암모늄 이온 (mmol/L)
    private final double H2PO4;  // 다량 인산 이온 (mmol/L)
    private final double K;     // 다량 칼륨 이온 (mmol/L)
    private final double Ca;    // 다량 칼슘 이온 (mmol/L)
    private final double Mg;     // 다량 마그네슘 이온 (mmol/L)
    private final double SO4;    // 다량 황산 이온 (mmol/L)
    private final double Fe;      // 미량 철 이온 (µmol/L)
    private final double Cu;      // 미량 구리 이온 (µmol/L)
    private final double B;       // 미량 붕소 이온 (µmol/L)
    private final double Mn;      // 미량 망간 이온 (µmol/L)
    private final double Zn;      // 미량 아연 이온 (µmol/L)
    private final double Mo;     // 미량 몰리브덴 이온 (µmol/L)

    private final double P; // 다량 인산 이온

    //db에는 산도, 중탄산 없으므로 이 생성자 사용
    public CropNutrientStandard(int id, String cropName, String nutrientSolutionName, double EC, double NO3, double NH4, double H2PO4, double K, double Ca, double Mg, double SO4, double Fe, double Cu, double B, double Mn, double Zn, double Mo, double P) {
        this.id = id;
        this.cropName = cropName;
        this.nutrientSolutionName = nutrientSolutionName;
        this.EC = EC;
        this.NO3 = NO3;
        this.NH4 = NH4;
        this.H2PO4 = H2PO4;
        this.K = K;
        this.Ca = Ca;
        this.Mg = Mg;
        this.SO4 = SO4;
        this.Fe = Fe;
        this.Cu = Cu;
        this.B = B;
        this.Mn = Mn;
        this.Zn = Zn;
        this.Mo = Mo;
        this.P = P;
    }

    public CropNutrientStandard(int id, String cropName, String nutrientSolutionName, double EC, double PH, double HCO3, double NO3, double NH4, double h2PO4, double k, double ca, double mg, double SO4, double fe, double cu, double b, double mn, double zn, double mo, double p) {
        this.id = id;
        this.cropName = cropName;
        this.nutrientSolutionName = nutrientSolutionName;
        this.EC = EC;
        this.PH = PH;
        this.HCO3 = HCO3;
        this.NO3 = NO3;
        this.NH4 = NH4;
        H2PO4 = h2PO4;
        K = k;
        Ca = ca;
        Mg = mg;
        this.SO4 = SO4;
        Fe = fe;
        Cu = cu;
        B = b;
        Mn = mn;
        Zn = zn;
        Mo = mo;
        P = p;
    }

    public double getPH() {
        return PH;
    }

    public double getHCO3() {
        return HCO3;
    }

    public int getId(){return id;}

    public String getCropName() {
        return cropName;
    }

    public String getNutrientSolutionName() {
        return nutrientSolutionName;
    }

    public double getEC() {
        return EC;
    }

    public double getNO3() {
        return NO3;
    }

    public double getNH4() {
        return NH4;
    }

    public double getH2PO4() {
        return H2PO4;
    }

    public double getK() {
        return K;
    }

    public double getCa() {
        return Ca;
    }

    public double getMg() {
        return Mg;
    }

    public double getSO4() {
        return SO4;
    }

    public double getFe() {
        return Fe;
    }

    public double getCu() {
        return Cu;
    }

    public double getB() {
        return B;
    }

    public double getMn() {
        return Mn;
    }

    public double getZn() {
        return Zn;
    }

    public double getMo() {
        return Mo;
    }

    public double getP() {
        return P;
    }
}
