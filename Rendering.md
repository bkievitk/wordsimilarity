There are three types of rendering that are performed by Word 2 Word.

Full rendering with aliasing (makes it look nicest)
Full rendering without aliasing (all connections shown)
Node rendering (only render the locations but not the connections)

W2W will automatically select the level of rendering that it prefers based on the number of nodes that you have in the visualization (more nodes means lower quality). You can override the automatic selection using the Render menu. There are two options here.

Render once will render full with aliasing one time. Any changes and the system will revert to the previous rendering type.

Render always will always use a full rendering. This option will turn into render smart which can be selected to revert to automatic selection.

Large renderings may take some time. If this is the case, a progress bar will appear to count down the time until rendering is complete.

You should not change the visualization while it is rendering a long render, as it will finish the render and immediately discard it for the new render of the modifications.