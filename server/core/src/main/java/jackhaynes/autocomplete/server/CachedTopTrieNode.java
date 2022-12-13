package jackhaynes.autocomplete.server;

import java.io.Serializable;
import java.security.InvalidParameterException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class CachedTopTrieNode implements Serializable {
    private String value;
    private String termLiteral;
    private boolean endsTerm;

    private CachedTopTrieNode parent;
    private Map<String, CachedTopTrieNode> children;
    private List<CachedTopTrieNode> topSuggestions;

    public CachedTopTrieNode(String value, CachedTopTrieNode parent) {
        this.value = value;
        this.parent = parent;
        this.termLiteral = null;
        this.children = new HashMap<String, CachedTopTrieNode>();
        this.topSuggestions = new LinkedList<>();
    }

    public String getTermLiteral() {
        if(termLiteral != null) {
            return termLiteral;
        } else {
            StringBuilder stringBuilder = new StringBuilder();

            CachedTopTrieNode currentNode = this;
            while(currentNode != null) {
                stringBuilder.append(currentNode.value);
                currentNode = this.parent;
            }

            return stringBuilder.reverse().toString();
        }
    }

    public CachedTopTrieNode insertChild(String value) {
        if(!this.children.containsKey(value)) {
            CachedTopTrieNode newNode = new CachedTopTrieNode(value, this);
            this.children.put(value, newNode);

            return newNode;
        } else {
            throw new InvalidParameterException("Node child key already exists.");
        }
    }

    public void addTopSuggestion(CachedTopTrieNode suggestionTerminalNode) {
        this.topSuggestions.add(suggestionTerminalNode);
    }

    public CachedTopTrieNode findChild(String value) {
        return this.children.getOrDefault(value, null);
    }

    public void setEndOfTerm(String term) {
        this.endsTerm = true;
        this.termLiteral = term;
    }

    public boolean hasChildren() {
        return !this.children.isEmpty();
    }

    public boolean isEndOfTerm() { return this.endsTerm; }

    public int topSuggestionsSize() {
        return this.topSuggestions.size();
    }

    public CachedTopTrieNode getParent() {
        return this.parent;
    }

    public List<CachedTopTrieNode> getTopSuggestionNodes() {
        return this.topSuggestions;
    }
}
