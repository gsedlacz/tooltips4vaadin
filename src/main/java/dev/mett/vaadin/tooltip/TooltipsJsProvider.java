package dev.mett.vaadin.tooltip;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;

// NOTE: Vaadin for some reason does not remember 'tippy.js' upon building a project that uses this plugin
//       Therefore it needs to be redeclared in pom.xml
@NpmPackage(value = "tippy.js", version = "5.2.1")
@NpmPackage(value = "retry",    version = "0.12.0")
@JsModule("./js/tooltip/tooltips.js")
@Tag("div")
public class TooltipsJsProvider extends Component {
	private static final long serialVersionUID = 3079421969338830944L;
}