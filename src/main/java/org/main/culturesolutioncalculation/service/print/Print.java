package org.main.culturesolutioncalculation.service.print;

import org.main.culturesolutioncalculation.service.requestHistory.RequestHistory;

public interface Print {


    public void setMacroMolecularMass();
    public void setMicroMolecularMass();

    public void setUp();
    public  String getUserInfo();
    public String getAllHtmlStr();

    public void getPDF();
    
}
