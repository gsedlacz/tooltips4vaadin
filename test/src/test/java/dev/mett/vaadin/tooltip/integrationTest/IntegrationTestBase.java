package dev.mett.vaadin.tooltip.integrationTest;//package dev.mett.vaadin.tooltip.integrationTest;
//
//import com.vaadin.testbench.TestBenchTestCase;
//import io.github.bonigarcia.wdm.WebDriverManager;
//import org.junit.After;
//import org.junit.Before;
//import org.junit.BeforeClass;
//import org.openqa.selenium.chrome.ChromeDriver;
//
//public abstract class IntegrationTestBase extends TestBenchTestCase {
//
//  protected String applicationPath = "http://localhost:8080/";
//
//  @BeforeClass
//  public static void setupClass() {
//    WebDriverManager.chromedriver().setup();
//  }
//
//  @Before
//  public void setup() throws Exception {
//    // Create a new browser instance
//    setDriver(new ChromeDriver());
//  }
//
//
//  @After
//  public void tearDown() throws Exception {
//    // close the browser instance when all tests are done
//    getDriver().quit();
//  }
//
//}
