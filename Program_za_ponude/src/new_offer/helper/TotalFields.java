/**
 * Helper class that calculates total amount that should be paid before discount, discount and amount
 * after discount. 
 * 
 * @author Petar
 */
package new_offer.helper;

import javafx.collections.ObservableList;
import javafx.scene.control.TextField;
import new_offer.Treatment;


public class TotalFields {
    
    //  Takes and sums all amounts from lists and puts it in field for total amount
    public static void setTotalFields(ObservableList<Treatment> upperJawList, ObservableList<Treatment> lowerJawList,
        ObservableList<Treatment> otherList, TextField cashDiscountPercentageField, TextField totalBeforeDiscountField,
        TextField totalDiscountField, TextField totalAfterDiscountField){
        
        float newBeforeDiscount = sumTotalInTable(upperJawList) + sumTotalInTable(lowerJawList)
                + sumTotalInTable(otherList);
        totalBeforeDiscountField.setText( String.valueOf(newBeforeDiscount) );
        
        float newDiscount;
        if( cashDiscountPercentageField.getText() == null || cashDiscountPercentageField.getText().equals("")
                || cashDiscountPercentageField.getText().equals("0"))
            newDiscount = 0;
        else{
            int discount = Integer.parseInt( cashDiscountPercentageField.getText() );
            newDiscount = newBeforeDiscount * discount /100;
        }
        totalDiscountField.setText( String.valueOf(newDiscount) );
        
        totalAfterDiscountField.setText( String.valueOf( newBeforeDiscount - newDiscount) );
    }
    
    //  Sums total amounts that should be payed for each treatment and returns their sum
    private static Float sumTotalInTable(ObservableList<Treatment> ol){
        float sumOfTotals = 0f;
        if(!ol.isEmpty()){
            for(Treatment treatment : ol){
                sumOfTotals += treatment.getTotal();
            }
        }
        return sumOfTotals;
    }
}
