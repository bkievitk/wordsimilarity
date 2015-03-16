Once words have been added to the visualization, you need to select how those words are laid out on the screen. The layout managers (found under the layout tab in the main menu) will help with this. There are a number of different ways to lay out different words.

  * Random Layout: Each word is independently placed in a random location on the screen.
  * Grid Layout: Attempt to evenly distribute nodes on the screen. Order is random but nodes placed in a grid pattern.
  * Word Centered Layout: When it starts it will ask for a similarity measurement and then a word. It will then place the given word in the center and all other words at a distance relative to the similarity with the word in the center. The angle is selected randomly.
  * MDS Layout: Will ask for similarity measure. Will then perform multidimensional scaling on the nodes. This will result in words which are more similar, being closer together.
  * Procrustes Layout: Will ask for two similarity measures and a translation. This will perform MDS on both similarity measures. It will then use the translation to attempt to rotate, scale and translate the two data sets until the words that are the same between the two models (according to the translation) are as close together as possible.
  * tSNE Layout: Will ask for similarity measure. Similar to MDS, this is another scaling layout.
  * Fit Screen: This will scale in the x and y axis such that the full screen is used. This will not respect relative scale in the x and y axis.
  * Sided layout: Will ask for two similarity measures. Layout such that words on the left have a denser semantic neighborhood according to the first similarity metric than the second. Words on the right have denser semantic neighborhood according to the second metric.
  * Split layout: Will ask for a translation. This will layout the nodes such that the first language is across the top. The second language lays out below it such that all instances of the second language that translate to the same word in the first are aligned in a column.
  * 3D: Same layouts as their 2D counterparts but in 3D space.