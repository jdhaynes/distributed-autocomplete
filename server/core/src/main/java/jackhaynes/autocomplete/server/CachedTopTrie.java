package jackhaynes.autocomplete.server;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

public class CachedTopTrie implements Serializable {
    private CachedTopTrieNode root;
    private int size;
    private final int topTermsLimit;

    /***
     * Instantiates an empty trie without any search terms.
     * @param topTermsLimit The maximum number of top search terms to cache for each prefix.
     */
    public CachedTopTrie(int topTermsLimit) {
        this.size = 0;
        this.root = new CachedTopTrieNode(null, null);
        this.topTermsLimit = topTermsLimit;
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
        return false;
    }

    /***
     * Checks if a given search term exists in the trie. Strings which
     * @param term The search term to find in the trie.
     * @return True if the given search term exists in the trie.
     */
    public boolean containsTerm(String term) {
        CachedTopTrieNode currentNode = root;
        String termProcessed = preProcessTerm(term);

        for(int i = 0; i < termProcessed.length(); i++) {
            String letter = Character.toString(termProcessed.charAt(i));

            CachedTopTrieNode matchingChild = currentNode.findChild(letter);
            if(matchingChild == null) {
                return false;
            }

            currentNode = matchingChild;
        }

        return currentNode.isEndOfTerm();
    }

    /**
     * Retrieves the highest ranking suggested search terms for a given prefix.
     * @param prefix String representing the prefix to search for.
     * @return Collection of the highest ranking search terms containing the prefix.
     */
    public List<String> getTopTermsForPrefix(String prefix) {
        return new LinkedList<>();
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

        // Create final node of term, so it can be added to top terms of each prefix on the way down the trie.
        String terminalValue =  Character.toString(termProcessed.charAt(term.length() - 1));
        CachedTopTrieNode terminalNode = new CachedTopTrieNode(terminalValue, null);

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
