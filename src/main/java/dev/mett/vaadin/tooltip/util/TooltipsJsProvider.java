package dev.mett.vaadin.tooltip.util;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;

// NOTE: Vaadin for some reason does not remember 'tippy.js' upon building a project that uses this plugin
//       Therefore it needs to be redeclared in pom.xml
@NpmPackage(value = "tippy.js", version = "6.3.1")
@NpmPackage(value = "retry", version = "0.12.0")
@JsModule("./js/tooltip/tooltips.js")
@CssImport("tippy.js/dist/tippy.css")
@Tag("div")
/**
 * Responsible for including this plugins JS and CSS resources when deploying.
 *
 * @author Gerrit Sedlaczek
 */
public class TooltipsJsProvider extends Component {

  private static final long serialVersionUID = 3079421969338830944L;
}