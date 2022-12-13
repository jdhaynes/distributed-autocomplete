package jackhaynes.autocomplete.server;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SearchPhraseTrieTest {
    @Test
    public void WhenTrieCreated_ShouldBeEmpty() {
        CachedTopTrie trie = new CachedTopTrie(3);
        assertTrue(trie.isEmpty());
    }

    @Test
    public void WhenTrieCreated_ShouldReturnZeroSize() {
        CachedTopTrie trie = new CachedTopTrie(3);
        assertEquals(0, trie.size());
    }

    @Test
    public void WhenInsert_ShouldNotBeEmpty() {
        CachedTopTrie trie = new CachedTopTrie(3);
        trie.insertTerm("test", 0);
        assertFalse(trie.isEmpty());
    }

    @Test
    public void WhenInsert_ShouldReturnCorrectSize() {
        CachedTopTrie trie = new CachedTopTrie(3);
        trie.insertTerm("test1", 0);
        trie.insertTerm("test2", 0);
        assertEquals(2, trie.size());
    }

    @Test
    public void WhenInsertSamePhrase_ShouldReturnCorrectSize() {
        CachedTopTrie trie = new CachedTopTrie(3);
        trie.insertTerm("test1", 0);
        trie.insertTerm("test2", 0);
        trie.insertTerm("test2", 0);
        trie.insertTerm("test3", 0);
        assertEquals(3, trie.size());
    }

    @Test
    public void WhenNewTrie_ShouldNotFindPhrase() {
        CachedTopTrie trie = new CachedTopTrie(3);
        assertFalse(trie.containsTerm("test"));
    }

    @Test
    public void WhenInsertingSinglePhrase_ShouldFindPhrase() {
        CachedTopTrie trie = new CachedTopTrie(3);
        trie.insertTerm("test", 0);
        assertTrue(trie.containsTerm("test"));
    }

    @Test
    public void WhenInsertingMultiplePhrases_ShouldFindPhrases() {
        CachedTopTrie trie = new CachedTopTrie(3);
        trie.insertTerm("test", 0);
        trie.insertTerm("temper", 0);
        trie.insertTerm("java", 0);
        trie.insertTerm("javac", 0);

        assertTrue(trie.containsTerm("test"));
        assertTrue(trie.containsTerm("temper"));
        assertTrue(trie.containsTerm("java"));
        assertTrue(trie.containsTerm("javac"));
    }

    @Test
    public void WhenInsertingPhrase_ShouldFindPhrasePrefixes() {
        CachedTopTrie trie = new CachedTopTrie(3);
        trie.insertTerm("search", 0);

        assertTrue(trie.containsPrefix("search"));
        assertTrue(trie.containsPrefix("searc"));
        assertTrue(trie.containsPrefix("sear"));
        assertTrue(trie.containsPrefix("sea"));
        assertTrue(trie.containsPrefix("se"));
        assertTrue(trie.containsPrefix("s"));
    }

    @Test
    public void WhenInsertingPhrase_ShouldNotFindWithSuffix() {
        CachedTopTrie trie = new CachedTopTrie(3);
        trie.insertTerm("search", 0);

        assertFalse(trie.containsTerm("searching"));
    }

    @Test
    public void WhenSearchingPrefix_ShouldNotReturnBottomScored() {
        CachedTopTrie trie = new CachedTopTrie(3);
        trie.insertTerm("mapped", 5);
        trie.insertTerm("mapping", 8);
        trie.insertTerm("maps", 15);
        trie.insertTerm("mapmaker", 1);

        List<String> results = trie.getTopTermsForPrefix("ma");
        assertAll(
                () -> assertTrue(results.contains("mapped")),
                () -> assertTrue(results.contains("mapping")),
                () -> assertTrue(results.contains("maps")),
                () -> assertFalse(results.contains("mapmaker"))
        );
    }

}