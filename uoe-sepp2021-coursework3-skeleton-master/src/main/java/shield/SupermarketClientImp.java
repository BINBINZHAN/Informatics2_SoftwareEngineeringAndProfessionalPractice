/**
 *
 */

package shield;

import java.io.IOException;

public class SupermarketClientImp implements SupermarketClient {
  private String endPoint;
  private String name;
  private String postCode;
  private boolean isRegistered;
  public SupermarketClientImp(String endpoint) {
    this.endPoint = endpoint;
    name = "";
    postCode = "";
    isRegistered = false;
  }

  @Override
  public boolean registerSupermarket(String name, String postCode) {
    try{
      String request = endPoint + "/registerSupermarket?business_name=" + name + "&postcode=" + postCode;
      String response = ClientIO.doGETRequest(request);
      if(response.contains("registered new") || response.contains("already registered")){
        this.name = name;
        this.postCode = postCode;
        isRegistered = true;
        return true;
      }else{
        isRegistered = false;
        return false;
      }
    }catch (IOException e){
      e.printStackTrace();
    }

    return false;
  }

  // **UPDATE2** ADDED METHOD
  @Override
  public boolean recordSupermarketOrder(String CHI, int orderNumber) {
    try{
      String request = endPoint + "/recordSupermarketOrder?individual_id=" + CHI + "&order_number=" +
              + orderNumber + "&supermarket_business_name=" + name + "&supermarket_postcode=" + postCode;
      String response = ClientIO.doGETRequest(request);
      if(response.contains("True")){
        return true;
      }else{
        return false;
      }
    }catch (IOException e){
      e.printStackTrace();
    }
    return false;
  }

  // **UPDATE**
  @Override
  public boolean updateOrderStatus(int orderNumber, String status) {
    try{
      String request = endPoint + "/updateSupermarketOrderStatus?order_id=" + orderNumber +"&newStatus=;"+status;
      String response = ClientIO.doGETRequest(request);
      if(response.contains("True")){
        return true;
      }else{
        return false;
      }
    }catch (IOException e){
      e.printStackTrace();
    }
    return false;
  }

  @Override
  public boolean isRegistered() {
    return isRegistered;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getPostCode() {
    return postCode;
  }
}
