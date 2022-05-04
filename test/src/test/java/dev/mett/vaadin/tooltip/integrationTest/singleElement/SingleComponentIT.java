package dev.mett.vaadin.tooltip.integrationTest.singleElement;//package dev.mett.vaadin.tooltip.integrationTest.singleElement;
//
//import static dev.mett.vaadin.tooltip.example.examples.forUiAndIT.singleElement.LayoutWithActionButtonsConstants.ID_TEXTFIELD;
//import static dev.mett.vaadin.tooltip.integrationTest.singleElement.SingleComponentITConstants.TEST_PATH_INITIALLY_VISIBLE;
//
//import com.vaadin.flow.component.textfield.testbench.TextFieldElement;
//import dev.mett.vaadin.tooltip.integrationTest.IntegrationTestBase;
//import org.junit.Before;
//import org.junit.Test;
//
//public class SingleComponentIT extends IntegrationTestBase {
//
//  @Before
//  public void setup() throws Exception {
//    super.setup();
//
//    // Open the application
//    getDriver().get(applicationPath + TEST_PATH_INITIALLY_VISIBLE);
//  }
//
//  @Test
//  public void hasInitialTooltip() {
//    TextFieldElement textField = $(TextFieldElement.class).id(ID_TEXTFIELD);
//
//    Object returnValue = executeScript("return document.getElementById('" + ID_TEXTFIELD + "')._tippy");
//
//    System.out.println(returnValue);
//
//    // TODO: see https://vaadin.com/docs/v14/tools/testbench/testbench-maintainable-tests-using-page-objects
//  }
//}
