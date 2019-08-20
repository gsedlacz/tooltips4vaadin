# Tooltips4Vaadin
This plugin allows you to define proper tooltips.

![Tooltip demo did not load.](https://gitlab.com/gsedlacz/tooltips4vaadin/raw/master/misc/demo.png "Tooltip demo")

## Requirements
Vaadin Flow version: 14.0.1+

## Usage
### Setup
Initially call 
```java
new Tooltips(UI)
``` 
to initialize this plugin.
There can only be a single instance per `UI`!
It is recommended to do this using an `UIInitListener`.
### Set / update a tooltip
```java
Tooltips.getCurrent().setTooltip(yourComponent, "an important information")
```
### Remove a tooltip
```java
Tooltips.getCurrent().removeTooltip(yourComponent)
```  
Note: You dont have to do this manually as detaching components will deregister themselves.

## Sample Code
```java
@Route("demo")
public class DemoLayout extends FlexLayout {
    private final AtomicLong atomicLong = new AtomicLong();

    public DemoLayout(){
        Tooltips tooltips;
		try {
            // creates a new instance
			tooltips = new Tooltips(UI.getCurrent());
		} catch (TooltipsAlreadyInitialized e) {
            // or uses an existing one
			tooltips = Tooltips.getCurrent();
		}

        EmailField emailField = new EmailField();
        // changes the tooltip by using a direct reference
        tooltips.setTooltip(emailField, "initial Value");

        Button btChangeTooltip = new Button("change tooltip", evt -> {
            // or by calling static getter
        	Tooltips.getCurrent().setTooltip(emailField, "tooltip-" + atomicLong.getAndIncrement());
        });

        add(emailField, btChangeTooltip);
    }
}
```

## Credit
This plugin makes use of these libraries:
1. tippy.js (license: MIT, version. 4.3.4, URL: https://github.com/atomiks/tippyjs)
2. Project Lombok (license: MIT, version 1.18.8, URL: https://projectlombok.org/)

## Copyright and license
Code and documentation copyright 2019 Gerrit Sedlaczek.  
Code and documentation released under Apache 2.0 license.