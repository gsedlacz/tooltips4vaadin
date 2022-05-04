package dev.mett.vaadin.tooltip.example.examples;

import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.data.provider.ListDataProvider;
import dev.mett.vaadin.tooltip.Tooltips;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import lombok.Data;

public class GridExample {

  public Grid<GridData> exampleGrid() {
    Tooltips tt = Tooltips.getCurrent();

    List<GridData> data = new ArrayList<>();
    for (int i = 0; i < 999; i++) {
      int random = new Random().nextInt(i + 1);
      data.add(new GridData("key " + i, String.valueOf(random)));
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

    return grid;
  }

  @Data
  private static class GridData {

    private final String key;
    private final String random;
  }
}
