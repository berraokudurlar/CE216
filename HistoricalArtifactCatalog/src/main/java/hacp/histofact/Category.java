package hacp.histofact;

public enum Category {
    SCULPTURE,
    MANUSCRIPT,
    WEAPON,
    TOOL,
    JEWELRY;

    @Override
    public String toString() {
        return name().toUpperCase();
    }


}
