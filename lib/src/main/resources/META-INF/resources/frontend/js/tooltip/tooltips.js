import tippy, {followCursor, hideAll, sticky} from 'tippy.js';
import 'tippy.js/dist/tippy.css';
import retry from 'retry';

window.tooltips = {
  /* ### UTIL ### */

  /*
   * see:
   *  - https://www.wolframalpha.com/input/?i=Sum%5B100*x%5Ek%2C+%7Bk%2C+0%2C+4%7D%5D+%3D+5+*+1000
   *  - https://www.wolframalpha.com/input/?i=Sum%5B100*2.3178%5Ek%2C+%7Bk%2C+0%2C+4%7D%5D
  */
  _getRetryOperation: function () {
    return retry.operation({
      retries: 5,
      factor: 2.3178,
      minTimeout: 100,
      maxTimeout: 1000
    });
  },

  _getElement: function (frontendId) {
    return document.querySelector('[tt4v=' + frontendId + ']');
  },

  _getElementFaulttolerant: async function (frontendId) {
    const operation = this._getRetryOperation();

    return new Promise((resolve, reject) => {
      operation.attempt(async function () {
        const element = window.tooltips._getElement(frontendId);
        const err = element === undefined || element == null
            ? "Could not find element for class: " + frontendId : null;

        if (operation.retry(err)) {
          return;
        }

        if (element) {
          resolve(element);
        } else {
          reject(operation.mainError());
        }
      })
    });
  },

  _setupTippyPlugins: function (config) {
    config.plugins = [];

    // enables required plugins
    if (config.followCursor) {
      config.plugins.push(followCursor);
    }
    if (config.sticky) {
      config.plugins.push(sticky);
    }
  },

  _setupAppendTo: function (config) {
    if (config.appendTo && config.appendTo != 'parent') {
      config.appendTo = Function('reference', `return ${config.appendTo}`);
    }
  },

  _removeTooltipFromElement: function (tooltipElement) {
    if (tooltipElement && tooltipElement._tippy) {
      tooltipElement._tippy.destroy();
    }
  },

  /* ### INTERACTION ### */

  setTooltipToElement: function (tooltipElement, config) {
    if (tooltipElement) {
      if(tooltipElement._tippy){
        this._removeTooltipFromElement(tooltipElement)
      }

      this._setupTippyPlugins(config);
      this._setupAppendTo(config);
      tippy(tooltipElement, config);

      // this id will be used by tooltips DOM id associated with the tooltipElement
      return tooltipElement._tippy.id;
    }
  },

  updateTooltip: function (tooltipElement, config) {
    if (tooltipElement) {
      if (tooltipElement._tippy) {
        this._setupTippyPlugins(config)
        this._setupAppendTo(config);
        tooltipElement._tippy.setProps(config);

      } else {
        // lost its _tippy sub entry for some reason
        return this.setTooltipToElement(tooltipElement, config);
      }
    } else {
      console.warn("No tooltipElement supplied during 'updateTooltip'")
    }
  },

  removeTooltip: function (frontendId, tooltipId) {
    this.closeTooltipForced(tooltipId);

    /* destroy frontend tooltip on the element */
    this._getElementFaulttolerant(frontendId)
    .then(this._removeTooltipFromElement)
    .catch(err => {
      console.warn("removeTooltip: " + err);
    })
  },

  /* cleans up if a tooltip is open */
  closeTooltipForced: function (tooltipId) {
    /* tippy fails to remove tooltips whose registered component
       gets removed during the open animation */
    const tooltipElement = document.getElementById(`tippy-${tooltipId}`);
    this._removeTooltipFromElement(tooltipElement);
  },

  closeAllTooltips: function () {
    hideAll();
  },

  showTooltip: function (tooltipElement) {
    if (tooltipElement && tooltipElement._tippy) {
      tooltipElement._tippy.show();
    }
  },

  hideTooltip: function (tooltipElement) {
    if (tooltipElement && tooltipElement._tippy) {
      tooltipElement._tippy.hide();
    }
  }
}
