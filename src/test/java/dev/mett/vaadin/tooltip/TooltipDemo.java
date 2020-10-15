package dev.mett.vaadin.tooltip;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.data.provider.ListDataProvider;
import dev.mett.vaadin.tooltip.config.TC_PLACEMENT;
import dev.mett.vaadin.tooltip.config.TooltipConfiguration;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.router.Route;
import dev.mett.vaadin.tooltip.mixin.HasTooltip;
import lombok.Data;
import lombok.var;

@Route("")
public class TooltipDemo extends FlexLayout {

    private static final long serialVersionUID = 7591127437515385460L;
    private final AtomicLong atomicLong = new AtomicLong();

    public TooltipDemo() {
        extendedConfig();
    }

    private void extendedConfig() {
        TooltipEmailField field = new TooltipEmailField();
        field.setTooltip(new TooltipConfigurationExt("test", TC_PLACEMENT.BOTTOM_END));
        add(field);
    }

    private class TooltipConfigurationExt extends TooltipConfiguration {
        public TooltipConfigurationExt(String content, TC_PLACEMENT placement) {
            super();

            setContent(content);
            setPlacement(placement);
        }
    }

    private void demoDefaultConfig() {
        var defaultConf = new TooltipConfiguration();
        defaultConf.setArrow(true);
        defaultConf.setDelay(2000);
        Tooltips.getCurrent().setDefaultTooltipConfiguration(defaultConf);

        var field = new TooltipEmailField();
        field.setTooltip("test");

        add(field);
    }

    private void demoField() {
        var emailField = new TooltipEmailField();
        emailField.setId("abc");
        emailField.setTooltip("initial Value");

        var btChangeTooltip = new Button("change tooltip",
            evt -> emailField.setTooltip("value-" + atomicLong.getAndIncrement()));

        var btRemoveTooltip = new Button("remove tooltip",
            evt -> emailField.removeTooltip());

        var btShowTooltip = new Button("show",
            evt -> emailField.show());

        var btHideTooltip = new Button("hide",
            evt -> emailField.hide());

        var btDetachAttachField = new Button("detach/attach field", evt -> {
            if (getChildren().anyMatch(c -> emailField == c)) {
                remove(emailField);
            } else {
                getElement().insertChild(0, emailField.getElement());
            }
        });

        var btCloseAll = new Button("close all",
            evt -> Tooltips.getCurrent().removeAllTooltips());

        add(emailField,
            new VerticalLayout(btChangeTooltip, btRemoveTooltip, btShowTooltip, btHideTooltip, btDetachAttachField, btCloseAll));
    }

    private static class TooltipEmailField extends EmailField implements HasTooltip {}

    private void demoGrid() {
        Tooltips tt = Tooltips.getCurrent();

        List<GridData> data = new ArrayList<>();
        for(int i=0; i<999; i++) {
            int ranom = new Random().nextInt(i+1);
            data.add(new GridData("key " + i, String.valueOf(ranom)));
        }

        ListDataProvider<GridData> dataProvider = new ListDataProvider<>(data);

        Grid<GridData> grid = new Grid<>();
        grid.removeAllColumns();
        grid.addComponentColumn(entry -> {
            Span key = new Span(entry.getKey());
            tt.setTooltip(key, "Tooltip of " + key.getText());
            return key;
        }).setHeader("Key");
        grid.addColumn(GridData::getRandom).setHeader("Value");
        grid.setDataProvider(dataProvider);

        add(grid);
    }

    @Data
    private static class GridData {
        private final String key;
        private final String random;
    }
}