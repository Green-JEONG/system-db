package org.main.culturesolutioncalculation.service.print;

import org.main.culturesolutioncalculation.service.users.Users;
import org.w3c.dom.views.AbstractView;

public class test {

    public void testPrint(){
        Print print = new EmbodyPrint(new Users());

        PrintClient client = new PrintClient(print);

        client.setUp();

        client.getPDF();
    }
}
