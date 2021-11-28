import entity.EmailAddress;
import entity.Person;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class Application {

    public static String conURL = "jdbc:mysql://localhost:3306/contacts" +
            "?user=root&password=MySQLpass&serverTimezone=UTC";

    public static void main(String[] args) {
        //System.out.println("Połączenie z bazą danych: "+tryConnect());

        System.out.println(findPersonsByContactedNumber(0,10));
        System.out.println(findEmailByPersonId(1));

    }

    /**
     * Zwraca informację czy udało się połączyć z bazą Contact
     * @return
     */
    private static boolean tryConnect(){

        //jdbc: jdbc:<driver>:<host>/<database-name> username password
        try(Connection connection = DriverManager.getConnection(conURL)){
            boolean isValid = connection.isValid(5);
            return isValid;
        }
        catch (SQLException ex){
            ex.printStackTrace();
        }
        return false;
    }

    private static void printPersonList(){

        String query = "SELECT * FROM person";
        try(Connection connection = DriverManager.getConnection(conURL)){
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(query);
            while (resultSet.next()){
                System.out.print(resultSet.getString("first_name"));
                System.out.print(" | ");
                System.out.print(resultSet.getString("last_name"));
                System.out.print(" | ");
                System.out.print(resultSet.getInt("contacted_number"));
                System.out.print("\n");

            }
        }
        catch (SQLException ex){
            ex.printStackTrace();
        }
    }

    private static List<Person> findPersonsByContactedNumber(int min, int max){
        String query = "SELECT " +
                "id, " +
                "first_name, " +
                "last_name, " +
                "contacted_number, " +
                "date_last_contacted, " +
                "date_added " +
                "FROM person " +
                "WHERE contacted_number BETWEEN ? AND ?";
        List<Person> result = new ArrayList<>();
        try(Connection connection = DriverManager.getConnection(conURL);
            PreparedStatement statement = connection.prepareStatement(query)){

            statement.setInt(1,min);
            statement.setInt(2,max);

            try (ResultSet resultSet = statement.executeQuery()) {

               while (resultSet.next()) {
                   Person person = new Person();
                   person.setId(resultSet.getInt("id"));
                   person.setFirstName(resultSet.getString("first_name"));
                   person.setLastName(resultSet.getString("last_name"));
                   person.setContactedNumber(resultSet.getInt("contacted_number"));
                   person.setLastContactedDate(resultSet.getDate("date_last_contacted"));
                   person.setDateAdded(resultSet.getDate("date_added"));

                   result.add(person);

               }
               return result;
           }
           catch (SQLException ex){
               throw  ex;
           }
        }
        catch (SQLException ex){
            ex.printStackTrace();
        }

        throw new NoSuchElementException("No data found in database");
    }

    private static List<EmailAddress> findEmailByPersonId(int personId){
        String query = "SELECT " +
                "id, " +
                "person_id, " +
                "email_address " +
                "FROM email_address "+
                "WHERE person_id = ?";

        try(Connection connection = DriverManager.getConnection(conURL);
            PreparedStatement statement = connection.prepareStatement(query)){

            statement.setInt(1,personId);

            try (ResultSet resultSet = statement.executeQuery()) {
                List<EmailAddress> result = new ArrayList<>();
                while (resultSet.next()) {
                    EmailAddress emailAddress = new EmailAddress();
                    emailAddress.setId(resultSet.getInt("id"));
                    emailAddress.setPersonId(resultSet.getInt("person_id"));
                    emailAddress.setEmailAddress(resultSet.getString("email_address"));
                    result.add(emailAddress);
                }
                return result;
            }
            catch (SQLException ex){
                throw  ex;
            }
        }
        catch (SQLException ex){
            ex.printStackTrace();
        }

        throw new NoSuchElementException("No data found in database");
    }
}
