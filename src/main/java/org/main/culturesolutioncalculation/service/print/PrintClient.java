package org.main.culturesolutioncalculation.service.print;

public class PrintClient {
    private Print print;
    public PrintClient(Print print){
        this.print = print;
    }
    public void setStrategy(Print print){
        this.print = print;
    }
    public void setUp(){
        print.setUp();
    }
    public void getPDF(){
        print.getPDF();
    }

}
