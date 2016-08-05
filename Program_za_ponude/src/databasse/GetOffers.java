/**
 * GetOffers class is helper class that provides methods for retrieving data (offers basic information) from database
 * based on searched word/number, and adding them to ObservableList. Other classes can retrieve ObservableList through
 * method getTableList()
 * 
 * @author Petar Deveric
 */
package databasse;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;


public class GetOffers {
    
    private final Connection conn;
    //  Searched word/number
    private final String filteringString;
    private final ObservableList<OfferBasicInfo> tableList;
    
    public GetOffers(Connection conn, String filteringString){
        this.conn = conn;
        this.filteringString = filteringString;
        tableList = FXCollections.observableArrayList();
    }
    
    //  Get all offers and customers for case when filtering string is empty
    public void getAllOffersAndCustomers(){
        String getOffersQuery = "SELECT DISTINCT o.OfferID, o.CustomerID, c.Name, o.Date FROM Offers AS o JOIN "
                + "Customers AS c ON o.CustomerID = c.CustomerID ORDER BY o.Date DESC, o.OfferID DESC";
            try(PreparedStatement ps = conn.prepareStatement(getOffersQuery);
                ResultSet rs = ps.executeQuery();){
                    while( rs.next() ){
                        tableList.add( new OfferBasicInfo(rs.getInt(1), rs.getInt(2), rs.getString(3), rs.getString(4) ) );
                    }
            } catch (SQLException e){
                e.printStackTrace(System.out);
            }
            //  Find and add customers without offer
            String getCustomersQuery = "SELECT CustomerID, Name FROM Customers ORDER BY Name ASC";
            getCustomers(getCustomersQuery);
    }
    
    //  Get all offers for searched number
    public void getAllOffersForMatchingNumber(){
        String getOffersQuery = "SELECT DISTINCT o.OfferID, o.CustomerID, c.Name, o.Date FROM Offers AS o JOIN "
                + "Customers AS c ON o.CustomerID = c.CustomerID ORDER BY o.OfferID DESC";
            try(PreparedStatement ps = conn.prepareStatement(getOffersQuery);){
                try(ResultSet rs = ps.executeQuery();){
                    while( rs.next() ){
                        int offerID = rs.getInt(1);
                        if(String.valueOf(offerID).contains(filteringString) )
                            tableList.add( new OfferBasicInfo(offerID, rs.getInt(2), rs.getString(3), rs.getString(4) ) );
                    }
                }
            } catch (SQLException e){
                e.printStackTrace(System.out);
            }
    }
    
    //  Get all offers for searched name
    public void getAllOffersForMatchingCustomer(){
        String getOffersQuery = "SELECT DISTINCT o.OfferID, o.CustomerID, c.Name, o.Date FROM Offers AS o JOIN "
                + "Customers AS c ON o.CustomerID = c.CustomerID WHERE LOWER(c.Name) LIKE ? ORDER BY o.OfferID DESC";
            try(PreparedStatement ps = conn.prepareStatement(getOffersQuery);){
                ps.setString(1, "%" + filteringString + "%" );
                try(ResultSet rs = ps.executeQuery();){
                    while( rs.next() ){
                        tableList.add( new OfferBasicInfo(rs.getInt(1), rs.getInt(2), rs.getString(3), rs.getString(4) ) );
                    }
                }
            } catch (SQLException e){
                e.printStackTrace(System.out);
            }
            //  Find and add customers without offer
            String getCustomersQuery = "SELECT CustomerID, Name FROM Customers WHERE LOWER(Name) LIKE ? ORDER BY "
                + "Name ASC";
            getCustomers(getCustomersQuery);
    }
    
    //  Find and add all customers from database for whom offer has not been issued yet
    private void getCustomers(String getCustomersQuery){
        try(PreparedStatement ps = conn.prepareStatement(getCustomersQuery);){
                if(filteringString.length() >= 1)    ps.setString(1, "%" + filteringString + "%" );
                try(ResultSet rs = ps.executeQuery();){
                    while( rs.next() ){
                        boolean ifAlreadyInList = false;
                        int currentCustomerID = rs.getInt(1);
                        for(OfferBasicInfo obi : tableList){
                            if(obi.getCustomerID() == currentCustomerID )
                                ifAlreadyInList = true;
                        }
                        if ( !ifAlreadyInList )
                            tableList.add( new OfferBasicInfo(0, currentCustomerID, rs.getString(2), "") );
                    }
                }
            } catch (SQLException e){
                e.printStackTrace(System.out);
            }
    }
    
    //  Returns ObservableList
    public ObservableList<OfferBasicInfo> getTableList(){
        return tableList;
    }
}