package dev.mett.vaadin.tooltip;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.vaadin.flow.component.UI;

/**
 * Removes old {@link TooltipStateData} entries form its {@link Tooltips}
 * instance.
 *
 * @author Gerrit Sedlaczek
 */
enum TooltipsCleaner {
    INSTANCE;

    private final Logger log = Logger.getLogger(TooltipsCleaner.class.getName());
    private final List<WeakReference<UI>> uis = Collections.synchronizedList(new LinkedList<>());

    private final ScheduledThreadPoolExecutor scheduledPool = initScheduledPool();

    private ScheduledThreadPoolExecutor initScheduledPool() {
        ScheduledThreadPoolExecutor pool = new ScheduledThreadPoolExecutor(1);
        pool.setExecuteExistingDelayedTasksAfterShutdownPolicy(false);
        pool.setContinueExistingPeriodicTasksAfterShutdownPolicy(true);
        return pool;
    }

    TooltipsCleaner() {
        scheduledPool.scheduleWithFixedDelay(this::cleanup, 60, 60, TimeUnit.SECONDS);

        log.log(Level.FINE, "TooltipsCleaner initialized");
    }

    private void cleanup() {
        synchronized (uis) {
            Iterator<WeakReference<UI>> iterator = uis.iterator();

//            log.log(Level.INFO, () -> "Processing all known UIs: " + uis.size());

            while (iterator.hasNext()) {
                WeakReference<UI> uiRef = iterator.next();
                UI ui = uiRef.get();

                if (ui == null || ui.isClosing()) {
//                    log.log(Level.INFO, () -> "##### TooltipsCleaner Removing UI: " + ui);
                    iterator.remove();
                    continue;
                }

                Map<Long, TooltipStateData> tooltipStorage = Tooltips.get(ui).getTooltipStorage();

//                log.log(Level.INFO, () -> "TooltipsCleaner: size pre-clean: " + tooltipStorage.size() + " | UI: " + ui);

                tooltipStorage.entrySet()
                        .removeIf(entry -> entry.getValue().getComponent().get() == null);

//                log.log(Level.INFO, () -> "TooltipsCleaner: size post-clean: " + tooltipStorage.size() + " | UI: " + ui);
            }
        }
    }

    public void register(Tooltips tooltipsInstance) {
        uis.add(new WeakReference<>(tooltipsInstance.getUI()));
    }
}
