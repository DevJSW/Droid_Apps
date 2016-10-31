package org.heartraise.heartraise;

/**
 * Created by John on 14-Sep-16.
 */
public class heartraise {

    private String title, story, image, username;


    public heartraise () {

    }

    public heartraise(String title, String story, String image, String username) {
        this.title = title;
        this.story = story;
        this.image = image;
        this.username = username;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getStory() {
        return story;
    }

    public void setStory(String story) {
        this.story = story;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
