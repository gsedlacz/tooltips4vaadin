# 2.0.0
## API changes
* no longer supports '#removeAllTooltips' - closed and removed (so that they would not reappear afterwards) all Tooltips of the current UI
* added support for '#closeAllTooltips' - closes all Tooltips of the current UI
## Other
* [#32](https://gitlab.com/gsedlacz/tooltips4vaadin/-/issues/32): added support for [@PreserveOnRefresh](https://vaadin.com/docs/v14/flow/advanced/tutorial-preserving-state-on-refresh)
* [#31](https://gitlab.com/gsedlacz/tooltips4vaadin/-/issues/31): removed the need to manage tooltip state manually (including a custom cleanup)
