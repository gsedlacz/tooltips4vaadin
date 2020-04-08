# Tooltips4Vaadin
This plugin allows you to define proper tooltips.

![Tooltip example image did not load.](https://gitlab.com/gsedlacz/tooltips4vaadin/raw/master/misc/demo.png "Tooltip demo")


## Official Download
[Vaadin Directory](https://vaadin.com/directory/component/tooltips4vaadin)

## Requirements
1. Java 1.8
2. Vaadin Flow version: 14+
3. npm based build

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

## Browser Compatibilty
This library should work nicely with any modern browser but Chrome and Firefox were used during testing.

## Credit
This plugin makes use of these libraries:
1. tippy.js (license: MIT, version. 6.1.0, URL: https://github.com/atomiks/tippyjs)
2. Project Lombok (license: MIT, version 1.18.8, URL: https://projectlombok.org/)
3. retry (license: MIT, version: 0.12.0, URL: https://www.npmjs.com/package/retry)

## Copyright and license
Code and documentation copyright 2020 Gerrit Sedlaczek.  
Code and documentation released under Apache 2.0 license.