package org.main.culturesolutioncalculation.service.print;

import org.main.culturesolutioncalculation.service.requestHistory.RequestHistory;
import org.main.culturesolutioncalculation.service.users.Users;


import java.sql.Timestamp;
import java.time.Instant;

public class CallPrint {

    RequestHistory requestHistory;
    int requestHistory_id = 1;
    public void testPrint(){
        //Print print = new EmbodyPrint(new Users(), requestHistory_id, requestDate);
        Print print = new AbstractPrint(new Users(), requestHistory);

        PrintClient client = new PrintClient(print);

        client.setUp();

        client.getPDF();
    }
}
