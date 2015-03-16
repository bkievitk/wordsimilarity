Located in the main menu tools->word manager. This tool allows you to select the words that you wish to visualize. It is rare that you will be able to display all of the words that have been learned (since there are so many of them). There are a number of techniques for selecting the active word list.

All words, shows all of the words that are known by any of the semantic models that you have learned.

Selected words shows the list of all words that you are currently rendering.

Words can be added and removed individually by selecting them on the relevant list and then choosing the arrows between the list to add or remove. More sophisticated techniques of selecting words can be done using the panel on the right.

  * neighborhood: Select words that have a dense or sparse semantic neighborhood. First select the similarity metric that you are working over, then select the neighborhood metric. Selecting stats will show a histogram of values for each word. You can then select a range using a minimum and maximum. Then select add or remove to choose words that appear in the desired range.
  * near: Select words that are near a target word semantically. First select the semantic measure, then select the number of words that you are interested in (top N) and select your word. Then select add.
  * count: Select words by frequency. Add or remove words that occur within a given band of word frequency. Frequency is a sum, overall across all models, of how many times the word occurred.
  * list: Select words by name. Type or paste in a list. Each word must be on its own line.
  * all: Add or remove all words.
  * relator: Add or remove all words that one specific similarity metric knows about.
  * rand: Add or remove a random collection of N words.
  * word: Add or remove a single word.