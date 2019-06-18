window.tooltips = {
		setTooltip: function (classname, tooltip){
				let tooltipElement = document.querySelector('.' + classname);
				if(tooltipElement) tippy(tooltipElement, {
					content: tooltip
				});
			},
		removeTooltips: function(){console.log("not implemented yet")}
}