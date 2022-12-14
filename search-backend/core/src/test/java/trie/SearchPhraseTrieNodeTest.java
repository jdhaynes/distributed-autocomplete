package trie;

import jackhaynes.autocomplete.search.backend.trie.CachedTopTrieNode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SearchPhraseTrieNodeTest {
    @Test
    public void WhenCreated_ShouldHaveNoChildren() {
        CachedTopTrieNode node = new CachedTopTrieNode("t", null);
        assertFalse(node.hasChildren());
    }

    @Test
    public void WhenInsertChild_ShouldFindChild() {
        CachedTopTrieNode node = new CachedTopTrieNode("t", null);
        node.insertChild("e");
        assertNotNull(node.findChild("e"));
    }

    @Test
    public void WhenChildDoesntExist_ShouldNotFindChild() {
        CachedTopTrieNode node = new CachedTopTrieNode("t", null);
        node.insertChild("e");
        assertNull(node.findChild("s"));
    }
}
