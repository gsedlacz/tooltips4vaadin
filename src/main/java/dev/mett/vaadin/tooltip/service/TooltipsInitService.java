package dev.mett.vaadin.tooltip.service;

import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;

import dev.mett.vaadin.tooltip.Tooltips;
import dev.mett.vaadin.tooltip.TooltipsAlreadyInitialized;

/**
 * This class provides an easy way to setup this library.<br>
 * Add 
 * <code>dev.mett.vaadin.tooltip.service.TooltipsInitService</code> 
 * to your VaadinServiceInitListener file.<br>
 * Take a look at the documentation link below to learn more about services.
 * 
 * @see <a href="https://vaadin.com/docs/v14/flow/advanced/tutorial-service-init-listener.html">Vaadin documentation 'VaadinServiceInitListener'</a>
 * @author Gerrit Sedlaczek
 */
public class TooltipsInitService implements VaadinServiceInitListener {
	private static final long serialVersionUID = -3190377936113384605L;
	private final Logger log = Logger.getLogger(TooltipsInitService.class.getName());

	@Override
	public void serviceInit(ServiceInitEvent event) {
		event.getSource().addUIInitListener(uiInit -> {
			final UI ui = uiInit.getUI();
			try {
				new Tooltips(ui);
			} catch (TooltipsAlreadyInitialized e) {
				log.log(Level.WARNING, "You have already initialized Tooltips for this UI: " + ui, new Throwable().fillInStackTrace());
			}
		});
	}

}
