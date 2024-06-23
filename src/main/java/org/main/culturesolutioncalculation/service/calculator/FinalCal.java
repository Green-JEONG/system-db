package org.main.culturesolutioncalculation.service.calculator;

public class FinalCal {
    String solution;
    String kor;
    double mass;

    public FinalCal(String solution, double mass, String kor) {
        this.solution = solution;
        this.mass = mass;
        this.kor = kor;
    }

    public String getSolution() {
        return solution;
    }

    public void setSolution(String solution) {
        this.solution = solution;
    }

    public double getMass() {
            return mass;
        }

    public void setMass(double mass) {
            this.mass = mass;
        }

    public String getKor() {
        return kor;
    }

    public void setKor(String kor) {
        this.kor = kor;
    }
}
