import tippy, {followCursor, sticky, hideAll} from 'tippy.js';
import 'tippy.js/dist/tippy.css';
import retry from 'retry';

window.tooltips = {
		/* ### UTIL ### */
		
		/*
		 * see:
		 *  - https://www.wolframalpha.com/input/?i=Sum%5B100*x%5Ek%2C+%7Bk%2C+0%2C+4%7D%5D+%3D+5+*+1000
		 *  - https://www.wolframalpha.com/input/?i=Sum%5B100*2.3178%5Ek%2C+%7Bk%2C+0%2C+4%7D%5D
		*/
		getRetryOperation: function (){
			return retry.operation({
				retries: 5,
				factor: 2.3178,
				minTimeout: 100,
				maxTimeout: 1000
			});
		},
		
		getElement: function (classname) {
			return document.querySelector('.' + classname);
		},
		
		getElementFaulttolerant: async function (classname) {
			const operation = this.getRetryOperation();
			
			return new Promise((resolve, reject) => {
				operation.attempt(async function () {
					const element = window.tooltips.getElement(classname);
					const err = element === undefined || element == null ? "Could not find element for class: " + classname : null;
					
					if(operation.retry(err)){
						return;
					}
					
					if(element){
						resolve(element);
					} else {
						reject(operation.mainError());
					}
				})
			});
		},
		
		/* ### INTERACTION ### */
		
		setTooltip: function (classname, config){
			return this.getElementFaulttolerant(classname)
			.then(tooltipElement => {
				return window.tooltips.setTooltipToElement(tooltipElement, config);
			})
			.catch(err => {
				console.debug("setTooltip: " + err);
				return null;
			})
		},
		
		setTooltipToElement: function(tooltipElement, config){
			if(tooltipElement) {
				config.plugins = [];
				
				// enables required plugins
				if(config.followCursor) {
					config.plugins.push(followCursor);
				}
				if(config.sticky) {
					config.plugins.push(sticky);
				}
				
				tippy(tooltipElement, config);
			
				// this id will be used by tooltips DOM id associated with the tooltipElement
				return tooltipElement._tippy.id;
			}
			console.debug(tooltipElement)
		},
		
		updateTooltip: function (classname, config){
			return this.getElementFaulttolerant(classname)
			.then(tooltipElement => {
				if(tooltipElement) {
					if(tooltipElement._tippy){
						tooltipElement._tippy.setProps(config);
					
					} else {
						// lost its _tippy sub entry for some reason
						return window.tooltips.setTooltipToElement(tooltipElement, config);
					}
				}
			})
			.catch(err => {
				console.debug("updateTooltip: " + err);	
			})
		},
		
		removeTooltip: function(classname, tooltipId){
			window.tooltips.closeTooltipForced(tooltipId);
			
			/* destroy frontend tooltip on the element */
			this.getElementFaulttolerant(classname)
			.then(tooltipElement => {
				if(tooltipElement) tooltipElement._tippy.destroy();
			})
			.catch(err => {
				console.debug("removeTooltip: " + err);	
			})
		},
		
		/* cleans up if a tooltip is open */
		closeTooltipForced: function(tooltipId) {
			/* tippy fails to remove tooltips whose registered component 
			   gets removed during the open animation */
			const lostTooltip = document.getElementById('tippy-' + tooltipId);
			if(lostTooltip) {
				lostTooltip._tippy.destroy();
			}
		},

		closeAllTooltips: function() {
			hideAll();
		}
}