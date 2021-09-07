package dev.mett.vaadin.tooltip.example;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import dev.mett.vaadin.tooltip.Tooltips;
import java.util.concurrent.atomic.AtomicInteger;

@Route("")
public class BasicDemo extends FlexLayout {

  private static final long serialVersionUID = 7591127437515385460L;

  public BasicDemo() {
//    add(new BasicStateExample().getBasicExample(this));

    ButtonWithToolTip target = new ButtonWithToolTip();
    target.setButtonText("TARGET");

    TextField tfTooltipContent = new TextField("tooltip content");

    Button btSetTooltip = new Button("set tooltip", evt -> {
//      Tooltips.getCurrent().setTooltip(target, tfTooltipContent.getValue());
      target.setTooltipContent(tfTooltipContent.getValue());
    });

    AtomicInteger atomicInteger = new AtomicInteger();
    Button btCountTooltip = new Button("counting tooltip", evt -> {
      target.setTooltipContent(atomicInteger.getAndIncrement() + "");
    });

    Button btCountTwiceTooltip = new Button("counting twice tooltip", evt -> {
      target.setTooltipContent(atomicInteger.getAndIncrement() + "");
      target.setTooltipContent(atomicInteger.getAndIncrement() + "");
    });

    Button btSetTooltipByConfig = new Button("tooltip on composite", evt -> {
      Tooltips.getCurrent().setTooltip(target, tfTooltipContent.getValue());
    });

    VerticalLayout layout = new VerticalLayout(btCountTooltip, btCountTwiceTooltip, tfTooltipContent, btSetTooltip, btSetTooltipByConfig);
    layout.setMargin(true);

    add(target, layout);
  }
}
