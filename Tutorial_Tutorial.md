# Tutorial 1 #

When you start up word 2 word, you will see the following screen

![http://wordsimilarity.googlecode.com/svn/wiki/w2w_home.png](http://wordsimilarity.googlecode.com/svn/wiki/w2w_home.png)

The main empty panel is where visualization will be taking place. To the right of that is the comparison management tool. At the top, is the menu. To begin, we need to create a new comparator.

**Select tools->similarity wizard**

You must now select the semantic space model that you are interested in. A small description follows each model.

**Select the beagle model and then next**

The wizard will now lead you through the stages of setting up the semantic model. First you must specify the dimensionality of the model.

**Type 200 and select set, then next**

The next option set is commonly available for most semantic models. Here you can set the color and name of the model (color used to render lines which can be changed later). Then select the [preprocessing](preprocessing.md) technique that you would prefer. In this case,

**Click Default under cleaning tools**

There are more options that can be selected to modify how the BEAGLE model runs but these can be ignored for now. Instead,

**Select next to continue**

This panel will allow you to [train](train.md) the model that you have generated on a corpus of your choice. Data can be pulled from many different sources but we will grab some web pages for this tutorial.

**On the dropdown list for "Learn From Website" select the wikipedia dog entry. Then select learn**

The system will show a progress bar and message update. When it is complete,

**select Finish to complete your similarity metric**

You will be returned to the main visualization panel. On the right hand side, a new box should have appeared to represent your new similarity metric. There are no nodes in the visualization yet because you must first select the words that you are interested in.

**select tools->word manager from the menu**

The [word manager](word_manager.md) allows you to select the words that you wish to visualize. There are many different ways to select words, but we will simply select based on frequency (within the training corpora that were given).

**select the count tab on the right. Then type 20 into the left, minimum box. Select add**

All words that occur 20 or more times should be added to the selected word list as well as into the visualization.

**close the word selection window**

All of the words have now been stacked on top of each other in the visualization. We need to place the nodes on the screen now to get meaning from them. While this can be done by hand (click and drag on a node to move it, or click and drag on the screen to pan), usually you will want to use a [layout manager](layout_manager.md).

**select the layout menu, then select random layout to place the nodes randomly**

There should now be a mass of lines on the screen and it is extremely difficult to see what is going on. This is because the BEAGLE model tends to assign some similarity between any pair of words, even if it is a very small similarity. We are probably only interested in the highly similar nodes, so we will select a visualization cutoff.

**on the right panel, in the box that represents your new similarity metric, take the minimum slider (left side) of the "show connections of strength" and slide it to the right until there are a reasonable number of connections visible**

You now have your first visualization. You can move words around individually to get a better view. The remaining lines indicate strong connections between words. If you hover over a word, it will indicate information about that word as well as highlight the other words that it is connected to.