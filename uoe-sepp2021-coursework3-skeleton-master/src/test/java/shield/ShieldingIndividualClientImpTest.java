/**
 *
 */

package shield;

import org.junit.jupiter.api.*;

import java.util.Collection;
import java.util.Properties;
import java.time.LocalDateTime;
import java.io.InputStream;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

/**
 *
 */

public class ShieldingIndividualClientImpTest {
  private final static String clientPropsFilename = "client.cfg";

  private Properties clientProps;
  private ShieldingIndividualClient client;

  private Properties loadProperties(String propsFilename) {
    ClassLoader loader = Thread.currentThread().getContextClassLoader();
    Properties props = new Properties();

    try {
      InputStream propsStream = loader.getResourceAsStream(propsFilename);
      props.load(propsStream);
    } catch (Exception e) {
      e.printStackTrace();
    }

    return props;
  }

  @BeforeEach
  public void setup() {
    clientProps = loadProperties(clientPropsFilename);

    client = new ShieldingIndividualClientImp(clientProps.getProperty("endpoint"));
  }


  @Test
  public void testShieldingIndividualNewRegistration() {
    Random rand = new Random();
    String chi = String.valueOf(rand.nextInt(10000));

    assertTrue(client.registerShieldingIndividual(chi));
    assertTrue(client.isRegistered());
    assertEquals(client.getCHI(), chi);
  }
  @Test
  public void testRegisterShieldingIndividual(){
    String CHI ="42";
    assertTrue(client.registerShieldingIndividual(CHI));
    assertEquals(CHI, client.getCHI());
  }
  @Test
  public void testPlaceOrder() throws Exception {
    // placeOrder first
    assertTrue(client.pickFoodBox(1));
    System.out.println("pickFoodBox Succeed");
    assertTrue(client.changeItemQuantityForPickedFoodBox(2, 3));
    System.out.println("Change Succeed");
    assertTrue(client.placeOrder());
    System.out.println("placeOrder succeed");
    Collection<Integer> orderIds = client.getOrderNumbers();
    int orderId = (int) orderIds.toArray()[0];
    // new placed order status should be Placed
    assertEquals("Placed", client.getStatusForOrder(orderId));
    System.out.println("Status correct");
    // check the quantity has been changed
    assertEquals(3, client.getItemQuantityForOrder(2, orderId));
    System.out.println("Item quantity modify succeed");
  }



}
