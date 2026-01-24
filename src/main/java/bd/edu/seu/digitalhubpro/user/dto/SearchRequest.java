package bd.edu.seu.digitalhubpro.user.dto;

public record SearchRequest(String searchText) {
    public SearchRequest(String searchText) {
        this.searchText = searchText;
    }

    public String searchText() {
        return this.searchText;
    }
}
