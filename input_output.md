Word 2 Word has the ability to save and load a number of different types of data files.

# save #

  * binary (.w2w): Uses java serialization to save the full state of the project. This will save options, word positions, colors etc as well as saving the similarity metrics.
  * Comma Separated Matrix File (.csv): This will save the results as a comma separated file in a giant matrix. This will only use active words and will list similarities between words in the matrix.
  * Comma Separated Word Pair File (.csv): This will save similarity pairs as a list of word1, word2, similarity1, similarity2 ... similarityn.
  * Word list (.csv): Just a list of active words, one word per line.
  * Compressed (.cmp): Save with w2w binary but also use compression.

# load #
  * binary (.w2w): Same as the save.
  * Comma Separated Matrix File (.csv): Same as the save)
  * Image Directory (dir): This will load all of the images in the specified directory. The file name for the image will then be used to match up the image with a word in the network. This image will then be used in place of the circle node representing each word.
  * Word Similarity (.csv): This must be a list that contains similarities of a single word to a list of other words.
  * Compressed (.cmp): Same as the save.

# load types #

This allows you to select colors directly for each node. Each node must be defined in a file as belonging to a specific type. This type then matches up to a color. This is designed for a project in InPho and is not really for general use.

# load web #

This queries the CCL Lab server for existing projects. You can then download these projects as compressed files and load them into W2W. Warning, these are usually very large projects.

# screen shot #

Save a screen shot. This will save as a .png and will use maximum quality rendering.

# images #

Select either Google or Flickr. The system will then query the given search engine for the top image for each active word in the visualization. These will all be saved in the specified directory. You can then use load->Image Directory to add these images into the visualization.