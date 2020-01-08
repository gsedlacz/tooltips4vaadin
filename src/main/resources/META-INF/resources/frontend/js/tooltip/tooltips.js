import tippy from 'tippy.js';
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
					const err = element == undefined || element == null ? "Could not find element for class: " + classname : null;
					
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
		
		setTooltip: function (classname, tooltip){
			//const tooltipElement = window.tooltips.getElement(classname);
			return this.getElementFaulttolerant(classname)
			.then(tooltipElement => {
				if(tooltipElement) tippy(tooltipElement, {
					content: tooltip
				});
				
				// this id will be used by tooltips DOM id associated with the tooltipElement
				return tooltipElement._tippy.id;
			})
			.catch(err => {
				console.log("setTooltip: " + err);
				return null; //TODO: Java check for null | wont be able to update
			})
		},
		
		updateTooltip: function (classname, tooltip){
			//const tooltipElement = window.tooltips.getElement(classname);
			return this.getElementFaulttolerant(classname)
			.then(tooltipElement => {
				if(tooltipElement) tooltipElement._tippy.setContent(tooltip);
			})
			.catch(err => {
				console.log("updateTooltip: " + err);	
			})
		},
		
		removeTooltip: function(classname, tooltipId){
			/* tippy fails to remove tooltips whose registered component 
			   gets removed during the open animation */
			const lostTooltip = document.getElementById('tippy-' + tooltipId);
			if(lostTooltip) lostTooltip._tippy.destroy();
			
			/* destroy frontend tooltip */
			this.getElementFaulttolerant(classname)
			.then(tooltipElement => {
				if(tooltipElement) tooltipElement._tippy.destroy();
			})
			.catch(err => {
				console.log("removeTooltip: " + err);	
			})
		},
}