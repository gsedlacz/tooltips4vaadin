package dev.mett.vaadin.tooltip.service;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import dev.mett.vaadin.tooltip.Tooltips;
import dev.mett.vaadin.tooltip.exception.TooltipsAlreadyInitializedException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class provides an easy way to setup this library.<br> When
 * <code>dev.mett.vaadin.tooltip.service.TooltipsInitService</code>
 * gets added to a VaadinServiceInitListener file this code will execute upon UI initialisation.<br> Take a look at the documentation link
 * below to learn more about services.<br>
 * <br>
 * By default this will be done by this plugin.
 *
 * @author Gerrit Sedlaczek
 * @see <a href="https://vaadin.com/docs/v14/flow/advanced/tutorial-service-init-listener.html">Vaadin documentation
 * 'VaadinServiceInitListener'</a>
 */
public class TooltipsInitService implements VaadinServiceInitListener {

  private static final long serialVersionUID = -3190377936113384605L;
  private final Logger log = Logger.getLogger(TooltipsInitService.class.getName());

  @Override
  public void serviceInit(ServiceInitEvent event) {
    event.getSource().addUIInitListener(uiInit -> {
      final UI ui = uiInit.getUI();
      try {
        if(Tooltips.get(ui) == null) {
          new Tooltips(ui);
        }

      } catch (TooltipsAlreadyInitializedException e) {
        log.log(Level.WARNING, "You have already initialized Tooltips for this UI: " + ui, new Throwable().fillInStackTrace());
      }
    });
  }

}
