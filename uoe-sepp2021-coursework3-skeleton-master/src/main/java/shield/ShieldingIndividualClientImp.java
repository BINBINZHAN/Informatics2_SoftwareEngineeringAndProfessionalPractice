/**
 * To implement
 */

package shield;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;

import java.io.IOException;
import java.util.*;

public class ShieldingIndividualClientImp implements ShieldingIndividualClient {
  /**
   * Delivery Status:
   * PLACED = 0
   * PACKED = 1
   * DISPATCHED = 2
   * DELIVERED = 3
   * CANCELLED = 4
   */
  private String endPoint;
  private String CHI;
  private String postCode;
  private String name;
  private String surname;
  private String phoneNumber;
  boolean isregistered;
  //store the latest foodBox from server 存放最近一次从服务器请求的
  private HashMap<Integer,JSONObject> foodBox;
  //store order which has been placed key=order_number value = order_content,order_status
  private HashMap<Integer,JSONObject> historyOrder;
  private HashMap<Integer,JSONObject> historyOrderCache;
  //store order which has been picked but have not placed
  private HashMap<String,String> cateringCompany;
  private JSONObject pickedBox;
  private String pickedCateringCompany;
  public ShieldingIndividualClientImp(String endpoint) {
    endPoint = endpoint;
    foodBox = new HashMap<>();
    cateringCompany = new HashMap<>();
    pickedBox = null;
    historyOrder = new HashMap<>();
    historyOrderCache = new HashMap<>();
    name = "";
    surname = "";
    phoneNumber = "";
    pickedCateringCompany = "";
  }

  @Override
  public boolean registerShieldingIndividual(String CHI) {
    try {
      String endpoint = endPoint + "/registerShieldingIndividual?" + "CHI=" + CHI;
      String response = ClientIO.doGETRequest(endpoint);
      if(response.contains("already registered")){
        this.CHI = CHI;
        isregistered = true;
        return true;
      }else{
        this.CHI = CHI;
        String personInfo = response.substring(1,response.length()-1);
        String[] info = personInfo.split(",");
        postCode = info[0].substring(1, info[0].length()-1);
        name = info[1].substring(1, info[1].length()-1);
        surname = info[2].substring(1, info[2].length()-1);
        phoneNumber = info[3].substring(1, info[3].length()-1);
        isregistered = true;
        return true;
      }
    } catch (IOException e) {
      e.printStackTrace();
    }
    return false;
  }

  @Override
  public Collection<String> showFoodBoxes(String dietaryPreference) {
    try {
      String request = endPoint +  "/showFoodBox?dietaryPreference=" + dietaryPreference;
      String response = ClientIO.doGETRequest(request);
      JSONArray array = JSON.parseArray(response);
      Collection<String> boxID = new ArrayList<>();
      foodBox.clear();//clean the  last time received information   before request
      for(int i = 0; i < array.size(); i++){
        String id = array.getJSONObject(i).get("id").toString();
        boxID.add(id);
        foodBox.put(Integer.parseInt(id),array.getJSONObject(i));
      }
      return boxID;
    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }

  // **UPDATE2** REMOVED PARAMETER
  @Override
  public boolean placeOrder() {
    if(pickedBox == null || pickedCateringCompany.isEmpty()) return false;
    try{
      String postCode= cateringCompany.get(pickedCateringCompany);
      if(postCode == null)  return false;
      String request = endPoint + "/placeOrder?individual_id=" + CHI + "&catering_business_name="
                     + pickedCateringCompany +"&catering_postcode=" + postCode;
      if(!pickedBox.containsKey("contents"))  return false;
      JSONObject data = new JSONObject();
      data.put("contents",pickedBox.getJSONArray("contents"));
      String response = ClientIO.doPOSTRequest(request,data.toString());
      if(response.contains("must provide individual_id")){
        return false;
      }
      int order_id = Integer.parseInt(response);
      if(order_id == -1)  return false;
      else{
        /*put current order in to historical order list */
        pickedBox.put("status",DeliveryStatus.PLACED.getStatus());//set current order status as placed
        historyOrder.put(order_id,pickedBox);
        pickedBox = null;
        pickedCateringCompany = "";
        return true;
      }

    }catch (IOException e){
      e.printStackTrace();
    }
    return false;
  }

  @Override
  public boolean editOrder(int orderNumber) {
    if(!historyOrder.containsKey(orderNumber))  return false;
    else{
      JSONObject orderCache = historyOrderCache.get(orderNumber);
      if(orderCache == null || !orderCache.containsKey("contents"))  return false;
      JSONObject new_oder = new JSONObject();
      new_oder.put("contents",orderCache.get("contents"));
      String request = endPoint + "/editOrder?order_id=" + orderNumber;
      try{
        String response = ClientIO.doPOSTRequest(request,new_oder.toString());
        if(response.contains("True")) {
          JSONObject order = historyOrder.get(orderNumber);
          order.put("contents",orderCache.get("contents"));
          historyOrderCache.remove(orderNumber);
          return true;
        }
        else return false;
      }catch (IOException e) {
        e.printStackTrace();
      }
    }
    return false;
  }

  @Override
  public boolean cancelOrder(int orderNumber) {
    try {
      String request = endPoint + "/cancelOrder?order_id=" + orderNumber;
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
  public boolean requestOrderStatus(int orderNumber) {
    try {
      String request = endPoint + "/requestStatus?order_id=" + orderNumber;
      String response = ClientIO.doGETRequest(request);
      int status = Integer.parseInt(response);
      if(status == -1){
        return false;
      }else if(status >= 0 && status <= 4){//store the required result into historyOrder
        JSONObject order = historyOrder.get(orderNumber);
        if(order == null || !order.containsKey("status")) return false;
        order.put("status",status);
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
  public Collection<String> getCateringCompanies() {
    try {
      ArrayList<String> cateringCompanies = new ArrayList<>();
      String request = endPoint + "/getCaterers";
      String response = ClientIO.doGETRequest(request);
      response = response.substring(2,response.length()-2);
      String[] caters = response.split("\",\"");
      cateringCompanies.addAll(Arrays.asList(caters));
      cateringCompanies.remove(0);
      cateringCompany.clear();
      cateringCompanies.forEach(e->{
        String[] info = e.split(",");
        cateringCompany.put(info[1],info[2]);
      });
      return cateringCompanies;
    }catch (IOException e){
      e.printStackTrace();
    }
    return null;
  }

  // **UPDATE**
  @Override
  public float getDistance(String postCode1, String postCode2) {
    try {
      String request = endPoint + "/distance?postcode1=" + postCode1 + "&postcode2=" + postCode2;
      String response = ClientIO.doGETRequest(request);
      return Float.parseFloat(response);
    }catch (IOException e){
      e.printStackTrace();
    }
    return 0;
  }

  @Override
  public boolean isRegistered() {
    return isregistered;
  }

  @Override
  public String getCHI() {
    return CHI;
  }

  @Override
  public int getFoodBoxNumber() {
    return foodBox.size();
  }

  @Override
  public String getDietaryPreferenceForFoodBox(int foodBoxId) {
    JSONObject box = foodBox.get(foodBoxId);
    if(box == null || !box.containsKey("diet"))  return null;
    else{
      return (String)box.get("diet");
    }
  }

  @Override
  public int getItemsNumberForFoodBox(int foodBoxId) {
    JSONObject box = foodBox.get(foodBoxId);
    if(box == null || !box.containsKey("contents"))  return 0;
    JSONArray contents = (JSONArray) box.get("contents");
    if(contents == null)  return 0;
    return contents.size();
  }

  @Override
  public Collection<Integer> getItemIdsForFoodBox(int foodboxId) {
    if(!foodBox.containsKey(foodboxId)) return null;
    JSONObject box = foodBox.get(foodboxId);
    if(box == null) return null;
    JSONArray contents = (JSONArray)box.get("contents");
    if(contents == null)  return null;
    Collection<Integer> ItemIds = new ArrayList<>();
    for (Object obj:contents) {
      JSONObject content = (JSONObject) obj;
      ItemIds.add((int)content.get("id"));
    }
    return ItemIds;
  }

  @Override
  public String getItemNameForFoodBox(int itemId, int foodBoxId) {
    if(!foodBox.containsKey(foodBoxId)) return null;
    JSONObject box = foodBox.get(foodBoxId);
    if(box == null) return null;
    JSONArray contents = (JSONArray)box.get("contents");
    if(contents == null)  return null;
    for (Object obj:contents) {
        JSONObject content = (JSONObject) obj;
        if((int)content.get("id") == itemId){
            return (String)content.get("name");
        }
    }
    return null;
  }

  @Override
  public int getItemQuantityForFoodBox(int itemId, int foodBoxId) {
    if(!foodBox.containsKey(foodBoxId))  return 0;
     JSONObject box = foodBox.get(foodBoxId);
     JSONArray contents = (JSONArray) box.get("contents");
     for (Object obj:contents) {
        JSONObject content = (JSONObject)obj;
        if((int)content.get("id") == itemId){
          return (int)content.get("quantity");
        }
     }
     return 0;
  }

  @Override
  public boolean pickFoodBox(int foodBoxId) {
    if(!foodBox.containsKey(foodBoxId)) return false;
    JSONObject foodbox = foodBox.get(foodBoxId);
    pickedBox = foodbox;
    return true;
  }

  @Override
  public boolean changeItemQuantityForPickedFoodBox(int itemId, int quantity) {
    if(pickedBox == null)  return false;
    JSONArray contents = (JSONArray) pickedBox.get("contents");
    for(Object obj:contents){
      JSONObject content = (JSONObject) obj;
      if((int)content.get("id") == itemId){
          content.put("quantity",quantity);
          return true;
        }
    }
    return false;
  }

  @Override
  public Collection<Integer> getOrderNumbers() {
    if(historyOrder.isEmpty())  return null;
    else
      return historyOrder.keySet();
  }

  @Override
  public String getStatusForOrder(int orderNumber) {
    if(historyOrder.isEmpty())  return null;
    else{
      JSONObject order = historyOrder.get(orderNumber);
      if(!order.containsKey("status"))  return null;
      else{
        int status = (int)order.get("status");
        return DeliveryStatus.getStatus(status);
      }
    }
  }

  @Override
  public Collection<Integer> getItemIdsForOrder(int orderNumber) {
    if(historyOrder.isEmpty())  return null;
    else{
      JSONObject order = historyOrder.get(orderNumber);
      if(!order.containsKey("contents")) return null;
      else{
        JSONArray contents = (JSONArray)order.get("contents");
        Collection<Integer> itemIds = new ArrayList<>();
        for(Object obj:contents){
          JSONObject content = (JSONObject) obj;
          itemIds.add((int)content.get("id"));
        }
        return itemIds;
      }
    }
  }

  @Override
  public String getItemNameForOrder(int itemId, int orderNumber) {
    if(historyOrder.isEmpty())  return null;
    else{
      JSONObject order = historyOrder.get(orderNumber);
      if(!order.containsKey("contents")) return null;
      else{
        JSONArray contents = (JSONArray)order.get("contents");
        for(Object obj:contents){
          JSONObject content = (JSONObject) obj;
          if((int)content.get("id") == itemId){
            return (String)content.get("name");
          }
        }
        return null;
      }
    }
  }

  @Override
  public int getItemQuantityForOrder(int itemId, int orderNumber) {
    if(historyOrder.isEmpty())  return 0;
    else{
      JSONObject order = historyOrder.get(orderNumber);
      if(order == null || !order.containsKey("contents")) return 0;
      JSONArray contents = (JSONArray)order.get("contents");
      if(contents == null)  return 0;
      for(Object obj:contents){
        JSONObject content = (JSONObject) obj;
        if((int)content.get("id") == itemId){
          return (int)content.get("quantity");
        }
      }
      return 0;
    }
  }

  @Override
  public boolean setItemQuantityForOrder(int itemId, int orderNumber, int quantity) {
    if(historyOrder.isEmpty())  return false;
    else{
      JSONObject orderCache = null;
      /*check whether been modified locally*/
      if(historyOrderCache.containsKey(orderNumber)){
        orderCache = historyOrderCache.get(orderNumber);
      }else{
        JSONObject order = historyOrder.get(orderNumber);
        orderCache = JSONObject.parseObject(order.toJSONString());
      }
      if(orderCache == null || !orderCache.containsKey("contents")) return false;
      JSONArray contents = (JSONArray) orderCache.get("contents");
      if(contents == null)  return false;
      for(Object obj:contents) {
        JSONObject content = (JSONObject) obj;
        if (content.containsKey("id") && (int) content.get("id") == itemId) {
          content.put("quantity",quantity);
          historyOrderCache.put(orderNumber,orderCache);
          return true;
        }
      }
    }
    return false;
  }

  // **UPDATE2** REMOVED METHOD getDeliveryTimeForOrder

  // **UPDATE**
  @Override
  public String getClosestCateringCompany() {
    String name = "";
    float min_distance = -1;
    Collection<String> cateringCompanies = getCateringCompanies();
    for (String cateringCompany:cateringCompanies) {
      String[] companyInfo = cateringCompany.split(",");
      float distance = getDistance(companyInfo[2],postCode);
      if(min_distance == -1 || min_distance > distance) {
        name = companyInfo[1];
        min_distance = distance;
      }
    }
    return name;
  }

  public boolean pickCateringCompany(String name){
    if(cateringCompany.isEmpty()) return false;
    if(cateringCompany.containsKey(name)){
      pickedCateringCompany = name;
      return true;
    }else{
      return false;
    }
  }
}
