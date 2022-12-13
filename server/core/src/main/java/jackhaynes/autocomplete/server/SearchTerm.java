package jackhaynes.autocomplete.server;

public class SearchTerm {
    private String term;
    private int score;

    public SearchTerm(String term, int score) {
        this.term = term;
        this.score = score;
    }
}
