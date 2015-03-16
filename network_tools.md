In the main menu->tools->network tools. This window helps to run some analysis on the data set selected in word 2 word. There are a number of different analysis packages.

## Size vs Word Similarity ##

This tool allows you to compare the similarity of the semantic neighborhood for a single word between two similarity measures. First, you must select the two similarity measures to compare between using the two drop down lists. Then select the target word that you are interested in.

Select the neighborhood selection type and the type of similarity measurement that you would like to use with the radio buttons. Then use the min and max sliders to set a range for N. You may select logarithmic X to make the X axis logarithmic.

Select run up at the top to perform the visualization.

This will generate a visualization with the X axis as the number of neighbors and the Y axis as the similarity of these neighborhoods. See the following link for more detail

http://inpho.cogs.indiana.edu/datablog/info/info-word2word-similarity-heat-maps/

## Word ##

Type in a word and then select find. You will be prompted to select a similarity measurement. The system will then generate a list of the top 20 most similar words to your target and display the words and their similarities in a list.

## Scatter Plot ##

Select two similarity metrics using the drop-down boxes. Then select calculate. This will generate a scatter plot. For each word pair, it will calculate the similarity according to the first metric and the second metric and then plot it using those as the X and Y axis. This is useful in understanding the correlation between two different similarity measures.

## Distance ##

Calculate the minimum distance between two words. Either select "Find among all active comparators" or select a particular comparator. Then select a from and to word. Finally click calculate. This will calculate the shortest distance between these words as a path through a series of words. This uses AStar to find the minimum spanning distance between two points int he graph.

## Discrepency ##

Find word pairs that are used maximally differently or similarly between two different similarity metrics. Select a number to show and then select calculate. This will generate a list of the top N most similar or dissimilar pairs.

## Statistic ##

Calculate general statistics. For each similarity metric, it will calculate min, max average, variance and percent connected. It will also generate a word frequency histogram.