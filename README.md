# Tooltips4Vaadin

This plugin allows you to define proper tooltips.

![Tooltip example image did not load.](https://gitlab.com/gsedlacz/tooltips4vaadin/raw/master/misc/demo.png "Tooltip demo")

## Official Download

[Vaadin Directory](https://vaadin.com/directory/component/tooltips4vaadin)

## Requirements

1. Java 1.8
2. Vaadin Flow version: 14+
3. npm based build

## Breaking Changes

### 1.X to 2.X

* replaced 'Tooltips#removeAllTooltips' with 'Tooltips#closeAllTooltips'
* ~~build target changed from 1.8 to 11~~ requirement got removed in version 2.1.2

## Usage

### Set / update a tooltip

```
Tooltips.getCurrent().setTooltip(yourComponent, "an important information")
```

### Remove a tooltip

```
Tooltips.getCurrent().removeTooltip(yourComponent)
``` 

### Mixin Interface for Components

```
class MyComp extends Component implements HasTooltip {
    MyComp(){
        this.setTooltip("useful hint");
        this.removeTooltip();
    }
}
```

### Display Configuration

```
TooltipConfiguration ttconfig = new TooltipConfiguration("test text");
ttconfig.setDuration(null, 20);
ttconfig.setContent("test \n<br> abc");
ttconfig.setFollowCursor(TC_FOLLOW_CURSOR.HORIZONTAL);
ttconfig.setHideOnClick(TC_HIDE_ON_CLICK.FALSE);
ttconfig.setShowOnCreate(false);

Textfield tf = new TextField();
Tooltips.getCurrent().setTooltip(tf, tconf);
```

### Using a default configuration

```
TooltipConfiguration defaultConf = new TooltipConfiguration();
defaultConf.setArrow(true);
defaultConf.setDelay(2000);

Tooltips.getCurrent().setDefaultTooltipConfiguration(defaultConf);

// both of the approaches below will make use of the default configuration, while directly setting a customized configuration will not
HasTooltip component = ...
component.setTooltip("message");
// OR
Tooltips.getCurrent().setTooltip(component, "message");
```

## Browser Compatibilty

This library should work nicely with any modern browser but only the ones shown on the right side were tested.

## Trouble Shooting

If you receive build errors like:

```
ERROR] Failed to execute goal com.vaadin:vaadin-maven-plugin:14.1.28:build-frontend (default) on project tooltipdemo: Execution default of goal com.vaadin:vaadin-maven-plugin:14.1.28:build-frontend failed:
[ERROR]
[ERROR]   Failed to find the following css files in the `node_modules` or `PATH\tooltipdemo\frontend` directory tree:
[ERROR]       - tippy.js/dist/tippy.css
[ERROR]   Check that they exist or are installed. If you use a custom directory for your resource files instead of the default `frontend` folder then make sure it's correctly configured (e.g. set 'vaadin.frontend.frontend.folder' property)
```

Try loading npm dependencies manually using: `npm i` in your project directory.

## Credit

This plugin makes use of these libraries:

1. tippy.js (license: MIT, version. 6.3.1, URL: https://github.com/atomiks/tippyjs)
2. Project Lombok (license: MIT, version 1.18.12, URL: https://projectlombok.org/)
3. retry (license: MIT, version: 0.12.0, URL: https://www.npmjs.com/package/retry)

## Copyright and license

Code and documentation copyright 2021 Gerrit Sedlaczek.  
Code and documentation released under Apache 2.0 license.
