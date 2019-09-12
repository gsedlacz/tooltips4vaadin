import tippy from 'tippy.js';
import retry from 'retry';

window.tooltips = {
		/* Util */
		getElement: function (classname) {
			return document.querySelector('.' + classname);
		},
		
		setTooltip: function (classname, tooltip){
			const tooltipElement = window.tooltips.getElement(classname);
			if(tooltipElement) tippy(tooltipElement, {
				content: tooltip
			});
			
			const abc = retry.operation();
			
			// this id will be used by tooltips DOM id associated with the tooltipElement
			return tooltipElement._tippy.id;
		},
		
		updateTooltip: function (classname, tooltip){
			const tooltipElement = window.tooltips.getElement(classname);
			if(tooltipElement) tooltipElement._tippy.setContent(tooltip);
		},
		
		removeTooltip: function(classname, tooltipId){
			const tooltipElement = window.tooltips.getElement(classname);
			if(tooltipElement) tooltipElement._tippy.destroy();
			
			/* tippy fails to remove tooltips whose registered component 
			   gets removed during the open animation */
			const lostTooltip = document.getElementById('tippy-' + tooltipId);
			if(lostTooltip) lostTooltip._tippy.destroy();
		},

		/*
			https://www.wolframalpha.com/input/?i=Sum%5B100*x%5Ek%2C+%7Bk%2C+0%2C+4%7D%5D+%3D+5+*+1000
			https://www.wolframalpha.com/input/?i=Sum%5B100*2.3178%5Ek%2C+%7Bk%2C+0%2C+4%7D%5D
		*/
		getRetryOperation: function (){
			return retry.operation({
				retries: 5,
				factor: 2.3178,
				minTimeout: 100,
				maxTimeout: 1000
			  });
		}
}