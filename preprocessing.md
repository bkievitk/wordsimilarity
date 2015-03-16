The preprocessing panel is available in the similarity wizard for most semantic space models. This allows you to select how the text is modified before it can be learned. The sentence cleaning tool allows you to stack a series of cleaners on top of each other. They will be performed in FIFO order. To select your list of cleaning tools, choose one of interest and select "add". This will add it to the top of the queue. More can then be added on top, or removed. Selecting the default button, will choose a reasonable set of cleaners for general purpose word learning.

  * To lower case: All English characters will be turned lowercase.
  * Alpha Numeric only: This will remove all characters that are not of the language of the specified [active language](active_language.md)
  * Remove web tags: Apply the regular expression <[^>]+> to remove web format tags (better html parsers for specific web pages need to be written manually)
  * Remove excess white space: All whitespace that is joined together becomes one space.
  * Remove stoplisted words: Uses a stoplist from the [active language](active_language.md) removes high frequency, low content words.
  * Apply stemmer: Uses a stemmer from the [active language](active_language.md) to remove prefixes and suffixes from words.