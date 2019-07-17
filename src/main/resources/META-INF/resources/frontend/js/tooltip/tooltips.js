import tippy from 'tippy.js'

window.tooltips = {
		/* Util */
		getElement: function(classname) {
			return document.querySelector('.' + classname);
		},
		
		setTooltip: function (classname, tooltip){
			let tooltipElement = window.tooltips.getElement(classname);
			if(tooltipElement) tippy(tooltipElement, {
				content: tooltip
			});
		},
		
		updateTooltip: function(classname, tooltip){
			let tooltipElement = window.tooltips.getElement(classname);
			if(tooltipElement) tooltipElement._tippy.setContent(tooltip);
		},
		
		removeTooltip: function(classname){
			let tooltipElement = window.tooltips.getElement(classname);
			if(tooltipElement) tooltipElement._tippy.destroy();
		}
}