import java.sql.*;
import java.util.Collections;
import java.util.List;

// If you are looking for Java data structures, these are highly useful.
// Remember that an important part of your mark is for doing as much in SQL (not Java) as you can.
// Solutions that use only or mostly Java will not receive a high mark.
import java.util.ArrayList;
//import java.util.Map;
//import java.util.HashMap;
//import java.util.Set;
//import java.util.HashSet;
public class Assignment2 extends JDBCSubmission {

    public Assignment2() throws ClassNotFoundException {

        Class.forName("org.postgresql.Driver");
    }

    @Override
    public boolean connectDB(String url, String username, String password) {
        //set the search path
        //get the connection based on the credentials
        try {
            this.connection = DriverManager.getConnection(url, username, password);
            connection.setSchema("parlgov");
        }
        catch ( SQLException err ) {
            System.out.println("Cannot connect to the database");
            return false;
        }
        return true;
    }

    @Override
    public boolean disconnectDB() {
        try {
            this.connection.close();
        }
        catch ( SQLException err ) {
            System.out.println("Could not close the database");
            return false;
        }
        return true;
    }

    @Override
    public ElectionCabinetResult electionSequence(String countryName) {
        try {

//            PreparedStatement query = connection.prepareStatement("SELECT " +
//                    "e1.id, " +
//                    "cabinet.id " +
//            "FROM " +
//            "election e1 "+
//            "inner join cabinet on e1.id = cabinet.election_id " +
//            "inner join country on cabinet.country_id = country.id " +
//            "WHERE country.name = " + countryName + " AND ((" +
//            "cabinet.start_date > e1.e_date AND " +
//            "EXISTS ( " +
//                            "SELECT " +
//                            "e2.id " +
//                            "FROM " +
//                            "election e2 " +
//                            "WHERE " +
//                            "e1.id = e2.previous_parliament_election_id AND cabinet.start_date < e2.e_date AND e1.e_type = e2.e_type" +
//                    "))" +
//                    " OR (cabinet.start_date > e1.e_date AND NOT EXISTS (SELECT " +
//                    "e4.id " +
//                    "FROM " +
//                    "election e4 " +
//                    "WHERE "  +
//                    "e1.id = e4.previous_parliament_election_id)) " +
//            " OR (EXISTS (" +
//                    "SELECT " +
//                    "e3.id " +
//                    "FROM " +
//                    "election e3 " +
//                    "WHERE " +
//                    "e1.id = e3.previous_ep_election_id AND cabinet.start_date < e3.e_date AND e1.e_type = e3.e_type) " +
//                    "AND cabinet.start_date > e1.e_date))");



            PreparedStatement query = connection.prepareStatement("SELECT election.id, cabinet.id " +
                    "FROM cabinet, election, country " +
                    "WHERE country.name = ? AND cabinet.election_id = election.id AND cabinet.country_id = country.id" +
                    " ORDER BY election.e_date DESC");

            query.setString(1, countryName);

            ResultSet set = query.executeQuery();

            //loop through the result set and return a 2d array which is an ElectionCabinetResult
            //create to arrays then feed into ElectionCabinetResult
            ArrayList<Integer> cabinet = new ArrayList<>();
            ArrayList<Integer> election = new ArrayList<>();

            // only put corresponding e_type into the array
           while (set.next()) {
                election.add(set.getInt(1));
                cabinet.add(set.getInt(2));
           }
            return new ElectionCabinetResult(election, cabinet);
        } catch (SQLException e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public List<Integer> findSimilarPoliticians(Integer politicianId, Float threshold) {
        ArrayList<Integer> output = new ArrayList<>();
        try {
            PreparedStatement query = connection.prepareStatement("SELECT p2.id, CONCAT(p1.description, ' ', p1.comment) " +
                    "as compare1, CONCAT(p2.description, ' ', p2.comment) as compare2 " +
                    "FROM politician_president p1, politician_president p2 " +
                    "WHERE p1.id = " + politicianId + " AND p1.id <> p2.id");

            ResultSet set = query.executeQuery();

            //if the description and comments pass the threshold, then retain the id of the second politician
            while (set.next()) {
                if (similarity(set.getString(2), set.getString(3)) > threshold){
                    output.add(set.getInt(1));
                }
            }
            return output;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void main(String[] args) {
        // You can put testing code in here. It will not affect our autotester.
        System.out.println("Hello");
       //Assignment2 a2 = new Assignment2();
       //a2.connectDB("jdbc:postgresql://localhost:5432/csc343h-caumecul", "caumecul", "");
       //a2.electionSequence("Canada");
       //a2.disconnectDB();


    }

}

