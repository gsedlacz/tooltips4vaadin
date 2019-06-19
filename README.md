# Tooltips4Vaadin
This plugin allows you to define proper tooltips.

## Requirements
Vaadin Flow version: 14+

## Usage
### Setup
Initially call `Tooltips.init(UI)` to initialize this plugin.  
It is recommended to do this using an UIInitListener.
### Set / update a tooltip
`Tooltips.setTooltip(yourComponent, "an important information")`
### Remove a tooltip
`Tooltips.removeTooltip(yourComponent)`  
Note: You dont have to do this manually as detaching components will deregister themselves.

## Credit
This plugin includes these libraries:
1. popper.js (license: MIT, version: 1.15.0, URL: https://github.com/FezVrasta/popper.js)
2. tippy.js (license: MIT, version. 4.3.4, URL: https://github.com/atomiks/tippyjs)

## Copyright and license
Code and documentation copyright 2019 Gerrit Sedlaczek.  
Code and documentation released under Apache 2.0 license.