/*
 * main program
 *
 */

package shield;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;

public class Main {
  public static void main(String[] args) {
    System.out.println("running!");
    /*register catering2*/
    CateringCompanyClientImp companyClientImp = new CateringCompanyClientImp("http://localhost:5000");
    if(companyClientImp.registerCateringCompany("catering2","eh0113")){
      System.out.println("Register success!");
    }else {
      System.out.println("Register failed");
    }

    /*register shielding individual && CHI=45 && dietaryPreference is None*/
    ShieldingIndividualClientImp shieldingIndividualClientImp = new ShieldingIndividualClientImp("http://localhost:5000");
    if(shieldingIndividualClientImp.registerShieldingIndividual("45")){
      System.out.println("Register success!");
    }else {
      System.out.println("Register failed!");
    }
    /*register shielding individual && CHI=45 && dietaryPreference is None*/

    /*acquire foodbox information where dietaryPreference=none*/
    Collection<String> ids = shieldingIndividualClientImp.showFoodBoxes("none");
    ids.forEach((e)->{
      System.out.println("id is "+e);
    });
    /*acquire foodbox information where dietaryPreference=none*/

    /*acquire cateringCompany information*/
    Collection<String> companies = shieldingIndividualClientImp.getCateringCompanies();
    companies.forEach((e)->{
      System.out.println("company is "+e);
    });
    /* acquire cateringCompany information */

    /*pick foodbox which id is 4*/
    if(shieldingIndividualClientImp.pickFoodBox(4)){
      System.out.println("pick succeed");
    }else{
      System.out.println("pick failed");
    }
    /*pick foodbox which id is 4*/


    /*change quantity of food to 0 for ItemID is 9 */
    if(shieldingIndividualClientImp.changeItemQuantityForPickedFoodBox(9,0)){
      System.out.println("change item quantity success");
    }else{
      System.out.println("change item quantity failed");
    }
    /*change quantity of food to 0 for ItemID is 9*/


    /*choose the campany which named catering2 to delivery*/
    if(shieldingIndividualClientImp.pickCateringCompany("catering2")){
      System.out.println("pick succeed");
    }else{
      System.out.println("pick failed");
    }
    /*choose the campany which named catering2 to delivery*/


    /*place order for selected foodbox*/
    if(shieldingIndividualClientImp.placeOrder()){
      System.out.println("place succeed");
    }else{
      System.out.println("place failed");
    }
    /*place order for selected foodbox*/


    /* print order information */
    int first_order;
    Collection<Integer> orderNumbers = shieldingIndividualClientImp.getOrderNumbers();
    first_order = (int)orderNumbers.toArray()[0];
    /*print order information*/

    /*acquire order status */
    if(shieldingIndividualClientImp.requestOrderStatus(first_order)){
      System.out.println("request succeed");
    }else{
      System.out.println("request failed");
    }
    String status = shieldingIndividualClientImp.getStatusForOrder(first_order);
    System.out.println("status is " + status);
    /*acquire order status */

    /* change order information */
    if(shieldingIndividualClientImp.setItemQuantityForOrder(11,first_order,0)){
      System.out.println("set succeed");
    }else{
      System.out.println("set failed");
    }
    if(shieldingIndividualClientImp.editOrder(first_order)){
      System.out.println("edit succeed");
    }else{
      System.out.println("edit failed");
    }
    /*change order information*/

    /*acquire the quantity after changes */
    int num = shieldingIndividualClientImp.getItemQuantityForOrder(11,first_order);
    System.out.println("num is " + num);
    /*acquire the quantity after changes*/

    /* set order status as packed */
    if(companyClientImp.updateOrderStatus(first_order,"packed")){
      System.out.println("update succeed");
    }else{
      System.out.println("update failed");
    }
    /*set order status as packed*/


    /* acquire current order status*/
    if(shieldingIndividualClientImp.requestOrderStatus(first_order)){
      System.out.println("request succeed");
    }else{
      System.out.println("request failed");
    }
    status = shieldingIndividualClientImp.getStatusForOrder(first_order);
    System.out.println("status is " + status);
    /*acquire current order status*/

    /*cancel order */
    if(shieldingIndividualClientImp.cancelOrder(first_order)){
      System.out.println("cancel succeed");
    }else{
      System.out.println("cancel failed");
    }
    /*cancel order*/


    /*acquire current order status*/
    if(shieldingIndividualClientImp.requestOrderStatus(first_order)){
      System.out.println("request succeed");
    }else{
      System.out.println("request failed");
    }
    status = shieldingIndividualClientImp.getStatusForOrder(first_order);
    System.out.println("status is " + status);
    return;
  }
}
