package jackhaynes.autocomplete.server;

import javax.print.DocFlavor;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class CachedTopTrie implements Serializable {
    private CachedTopTrieNode root;
    private int size;
    private final int topSuggestionsLimit;

    /***
     * Instantiates an empty trie without any search terms.
     * @param topSuggestionsLimit The maximum number of top search terms to cache for each prefix.
     */
    public CachedTopTrie(int topSuggestionsLimit) {
        this.size = 0;
        this.root = new CachedTopTrieNode(null, null);
        this.topSuggestionsLimit = topSuggestionsLimit;
    }

    /**
     * Returns the number of search phrases loaded into the trie.
     * @return The number of search phrases loaded into the trie.
     */
    public int size() {
        return this.size;
    }

    /***
     * Checks if the trie has any search terms loaded.
     * @return True if the trie contains search terms.
     */
    public boolean isEmpty() {
        return !this.root.hasChildren();
    }

    /**
     * Checks if a given string exists as a prefix of any search phrase loaded in the trie.
     * @param prefix The prefix to search the trie for.
     * @return True if given string exists as a prefix of any search phrase loaded in the trie.
     */
    public boolean containsPrefix(String prefix) {
        CachedTopTrieNode node = findPrefixTerminalNode(prefix);
        return node != null;
    }

    /***
     * Checks if a given search term exists in the trie. Strings which
     * @param term The search term to find in the trie.
     * @return True if the given search term exists in the trie.
     */
    public boolean containsTerm(String term) {
        CachedTopTrieNode node = findPrefixTerminalNode(term);
        return node != null && node.isEndOfTerm();
    }

    /**
     * Retrieves the highest ranking suggested search terms for a given prefix.
     * @param prefix String representing the prefix to search for.
     * @return Collection of the highest ranking search terms containing the prefix.
     */
    public List<String> getTopSuggestionsForPrefix(String prefix) {
        LinkedList<String> topSuggestions = new LinkedList<>();

        CachedTopTrieNode prefixNode = findPrefixTerminalNode(prefix);
        for(CachedTopTrieNode suggestion : prefixNode.getTopSuggestionNodes()) {
            topSuggestions.add(suggestion.getTermLiteral());
        }

        return topSuggestions;
    }

    private CachedTopTrieNode findPrefixTerminalNode(String prefix) {
        CachedTopTrieNode currentNode = this.root;
        String termProcessed = preProcessTerm(prefix);

        for(int i = 0; i < termProcessed.length(); i++) {
            String letter = Character.toString(termProcessed.charAt(i));

            CachedTopTrieNode matchingChild = currentNode.findChild(letter);
            if(matchingChild == null) {
                return null;
            }

            currentNode = matchingChild;
        }

        return currentNode;
    }

    /**
     * Inserts a search phrase into the trie.
     * @param term String literal of the search term.
     * @param score Score representing the ranking of the search term.
     */
    public void insertTerm(String term, int score) {
        CachedTopTrieNode currentNode = root;
        String termProcessed = preProcessTerm(term);
        boolean hasInserted = false;

        for(int i = 0; i < term.length(); i++) {
            String letter = Character.toString(termProcessed.charAt(i));

            CachedTopTrieNode matchingChild = currentNode.findChild(letter);
            if(matchingChild != null) {
                currentNode = matchingChild;
            } else {
                CachedTopTrieNode insertedNode = currentNode.insertChild(letter);

                // For last node, set end of the term and add full term info.
                if (i == termProcessed.length() - 1) {
                    insertedNode.setEndOfTerm(termProcessed);
                }

                currentNode = insertedNode;
                hasInserted = true;
            }
        }

        if(hasInserted) {
            size++;

            // Go back up to the root and add terminal top as top suggestion to each prefix.
            // Naive algorithm - make this more efficient in the future.
            CachedTopTrieNode terminalNode = currentNode;
            while(currentNode.getParent() != null) {
                currentNode = currentNode.getParent();
                if(currentNode.topSuggestionsSize() < this.topSuggestionsLimit) {
                    currentNode.addTopSuggestion(terminalNode);
                }
            }
        }
    }

    /**
     * Modifies term strings so searches are not case-sensitive and ignores whitespace.
     * @param term Search term string.
     * @return Modified string without padding and uppercase characters.
     */
    private String preProcessTerm(String term) {
        return term.toLowerCase().strip();
    }
}
