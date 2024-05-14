import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.main.culturesolutioncalculation.service.database.DatabaseConnector;
import org.main.culturesolutioncalculation.service.requestHistory.RequestHistory;
import org.main.culturesolutioncalculation.service.users.Users;

import java.sql.*;
import java.util.LinkedList;
import java.util.List;

public class RequestHistoryServiceTest {

    private final String url = "jdbc:mysql://localhost:3306/CultureSolutionCalculation?useSSL=false";
    private final String user = "root";
    private final String password = "root";

    private DatabaseConnector conn;

    @BeforeEach
    public void setUp() {
        this.conn = DatabaseConnector.getInstance(url, user, password);
    }

    private Users users;

    @Test
    public void findByUser(){
        List<RequestHistory> histories = new LinkedList<>();
        users.setId(1);
        users.setAddress("jeju-si, jeju-do, South Korea");
        users.setContact("010-1234-5678");

        String query = "select * from requestHistory where " +
                "user_id = ?";
        try(Connection connection = conn.getConnection();
            PreparedStatement pstmt = connection.prepareStatement(query)
        ){
            pstmt.setInt(1, users.getId());
            try(ResultSet resultSet = pstmt.executeQuery()){
                while(resultSet.next()){

                    System.out.println("resultSet.getTimestamp(\"request_date\") = " + resultSet.getTimestamp("request_date"));
                    histories.add(new RequestHistory(
                            resultSet.getInt("id"),
                            resultSet.getInt("user_id"),
                            resultSet.getTimestamp("request_date"),
                            resultSet.getInt("culture_medium_id"),
                            resultSet.getInt("cultivation_scale"),
                            resultSet.getString("sample_type"),
                            resultSet.getString("variety_name"),
                            resultSet.getString("substrate_type"),
                            resultSet.getString("report_issuance_method")
                    ));
                }
                for (RequestHistory history : histories) {
                    System.out.println("history = " + history);
                }

            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
}
