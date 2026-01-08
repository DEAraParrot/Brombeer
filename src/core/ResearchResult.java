package core;

public enum ResearchResult {
    FAILURE("Failure"),
    DISCOVERY("Discovery"),
    BREAKTHROUGH("Breakthrough");

    private String displayName;

    ResearchResult(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
