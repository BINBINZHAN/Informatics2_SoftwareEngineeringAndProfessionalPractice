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

public class SupermarketClientImpTest {
  private final static String clientPropsFilename = "client.cfg";

  private Properties clientProps;
  private SupermarketClient client;

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

    client = new SupermarketClientImp(clientProps.getProperty("endpoint"));
  }


  @Test
  public void testSupermarketNewRegistration() {
    Random rand = new Random();
    String name = String.valueOf(rand.nextInt(10000));
    String postCode = String.valueOf(rand.nextInt(10000));

    assertTrue(client.registerSupermarket(name, postCode));
    assertTrue(client.isRegistered());
    assertEquals(client.getName(), name);
  }

  @Test
  public void testRegisterSupermarket(){
    String name ="supermarket1";
    String postCode = "EH1_9AB";
    assertTrue(client.registerSupermarket(name, postCode));
    assertTrue(client.isRegistered());
    assertEquals(client.getName(), name);
  }
  @Test
  public void testRecordSupermarketOrder(){
    client.registerSupermarket("super","EH9_8AB");
    assertTrue(client.recordSupermarketOrder("42",2));
  }
  @Test
  public void testRecordSupermarketOrderFalse1(){
    client.registerSupermarket("super","EH9_8AB");
    // not regisitered shielding individual
    assertFalse(client.recordSupermarketOrder("41",2));
  }

}
