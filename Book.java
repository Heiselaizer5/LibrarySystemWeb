public class Book {
    private final String title;
    private final String author;
    private final String isbn;
    private final String type;
    private final String level;
    private final String className;
    private String content;

    public Book(String title, String author, String isbn, String type, String level, String className, String content) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.type = type;
        this.level = level;
        this.className = className;
        this.content = content;
    }

    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getIsbn() { return isbn; }
    public String getType() { return type; }
    public String getLevel() { return level; }
    public String getClassName() { return className; }
    public String getContent() { return content; }

    public static Book load(String title, String author, String isbn, String type, String level, String className, String defaultContent) {
        try {
            java.nio.file.Path p = java.nio.file.Paths.get("books", isbn + ".txt");
            if (java.nio.file.Files.exists(p))
                defaultContent = new String(java.nio.file.Files.readAllBytes(p), "UTF-8");
        } catch (Exception e) {}
        return new Book(title, author, isbn, type, level, className, defaultContent);
    }
}
