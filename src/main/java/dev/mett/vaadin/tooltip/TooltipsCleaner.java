package dev.mett.vaadin.tooltip;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.flow.component.UI;

/**
 * Removes old {@link TooltipStateData} entries form its {@link Tooltips} instance.
 * 
 * @author Gerrit Sedlaczek
 */
enum TooltipsCleaner{
    INSTANCE;
    
    private final Logger log = Logger.getLogger(TooltipsCleaner.class.getName());
    private final List<WeakReference<UI>> uis = Collections.synchronizedList(new LinkedList<>());
    
    TooltipsCleaner() {
        Thread t = new Thread(() -> {
            while(true) {
                try {
                    Thread.sleep(60000);
                    // cleanup repeats every 60s
                    cleanup();
                
                } catch (InterruptedException e) {
                    log.log(Level.WARNING, "sleep faild in TooltipCleaner", e);
                    Thread.currentThread().interrupt();
                }
            }
            
        }, TooltipsCleaner.class.getName());
        
        t.setDaemon(true);
        t.start();
        
        log.log(Level.FINE, "TooltipsCleaner initialized");
    }
    
    private void cleanup() {
        synchronized (uis) {
            Iterator<WeakReference<UI>> iterator = uis.iterator();
            
            while(iterator.hasNext()) {
                WeakReference<UI> uiRef = iterator.next();
                UI ui = uiRef.get();
                
                if(ui == null || ui.isClosing()) {
                    iterator.remove();
                    continue;
                }
                
                Map<Long, TooltipStateData> tooltipStorage = Tooltips.get(ui).getTooltipStorage();
                
                // debug output
//                log.log(Level.FINEST, () -> "TooltipsCleaner: size pre-clean: " + tooltipStorage.size() + " | UI: " + ui);
                
                tooltipStorage.entrySet()
                              .removeIf(entry -> 
                                  entry.getValue().getComponent().get() == null);
                
                // debug output
//                log.log(Level.FINEST, () -> "TooltipsCleaner: size post-clean: " + tooltipStorage.size() + " | UI: " + ui);
            }
        }
    }
    
    public void register(Tooltips tooltipsInstance) {
        uis.add(new WeakReference<UI>(tooltipsInstance.getUI()));
    }
}
