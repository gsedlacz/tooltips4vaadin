package dev.mett.vaadin.tooltip;

import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

final class TooltipsUtil {
	private TooltipsUtil() {}

	private final static Logger log = Logger.getLogger(TooltipsUtil.class.getName());

	/**
	 * Cleans the map containing {@link TooltipStateData} entries.
	 *
	 * @param <T> the type of key used for the map (generic as it might change in the future)
	 * @param interval long time in ms until the first cleanup starts and the time between subsequent cleanups executions.
	 * @param tooltipStorage the map containing references to {@link TooltipStateData}
	 */
	public static <T extends Object> void setupCleanup(long interval, Map<T, TooltipStateData> tooltipStorage) {
		new Timer(true).schedule(
				new TimerTask() {
					@Override
					public void run() {
						cleanup(tooltipStorage);
					}
				},
				interval,
				interval);
		System.out.println("Tooltips cleanup got setup.");
	}

	private static <T extends Object> void cleanup(Map<T, TooltipStateData> tooltipStorage) {
		tooltipStorage.forEach((key, value) -> {
			if(value.getComponent().get() == null) {
				tooltipStorage.remove(key);
				System.out.println("Removed \"" + value.getCssClass() + "\" from tooltipStorage.");
			}
		});
		System.out.println("Finished cleaning up tooltipStorage.");
	}
}
