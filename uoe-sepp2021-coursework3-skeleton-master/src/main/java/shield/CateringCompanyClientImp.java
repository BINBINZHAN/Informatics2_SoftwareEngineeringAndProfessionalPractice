/**
 *
 */

package shield;

import java.io.IOException;

public class CateringCompanyClientImp implements CateringCompanyClient {
  private String endpoint;
  private String name;
  private String postcode;
  private boolean isregistered;

  public CateringCompanyClientImp(String endPoint) {
    endpoint = endPoint;
    isregistered = false;
    name = "";
    postcode = "";
  }

  @Override
  public boolean registerCateringCompany(String name, String postCode) {
    try {
      String endPoint = endpoint + "/registerCateringCompany?" + "business_name=" + name + "&postcode="+ postCode;
      String response = ClientIO.doGETRequest(endPoint);
      if(response.contains("registered new") || response.contains("already registered")){
        this.name = name;
        this.postcode = postCode;
        isregistered = true;
        return true;
      }else{
        return false;
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return false;
  }

  @Override
  public boolean updateOrderStatus(int orderNumber, String status) {
    try{
      String endPoint = endpoint + "/updateOrderStatus?" + "order_id=" + orderNumber + "&newStatus=" + status;
      String response = ClientIO.doGETRequest(endPoint);
      if(response.contains("True")){
        return true;
      }else if(response.contains("False")){
        return false;
      }
    }catch (IOException e){
      e.printStackTrace();
    }
    return false;
  }

  @Override
  public boolean isRegistered() {
    return isregistered;
  }

  @Override
  public String getName() {
    return name;
  }

  @Override
  public String getPostCode() {
    return postcode;
  }
}
