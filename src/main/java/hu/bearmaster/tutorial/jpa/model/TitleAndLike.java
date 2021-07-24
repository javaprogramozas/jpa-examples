package hu.bearmaster.tutorial.jpa.model;

public class TitleAndLike {
    
    private String title;
    
    private int likes;

    public TitleAndLike(String title, int likes) {
        super();
        this.title = title;
        this.likes = likes;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getLikes() {
        return likes;
    }

    public void setLikes(int likes) {
        this.likes = likes;
    }

    @Override
    public String toString() {
        return "TitleAndLike [title=" + title + ", likes=" + likes + "]";
    }

}
