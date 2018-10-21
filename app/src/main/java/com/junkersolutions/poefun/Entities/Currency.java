package com.junkersolutions.poefun.Entities;

public class Currency implements Cloneable {

    private String description;
    private String groupDescription;
    private String id;
    private String imageURL;
    private boolean selected;

    public Currency(String description,String groupDescription, String id, String imageURL){
        this.description = description;
        this.groupDescription = groupDescription;
        this.id = id;
        this.imageURL = imageURL;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getGroupDescription() {
        return groupDescription;
    }

    public void setGroupDescription(String groupDescription) {
        this.groupDescription = groupDescription;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getImageURL() {
        return imageURL;
    }

    public void setImageURL(String imageURL) {
        this.imageURL = imageURL;
    }

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public Currency getClone() {
        try {
            // call clone in Object.
            return (Currency) super.clone();
        } catch (CloneNotSupportedException e) {
            System.out.println (" Cloning not allowed. " );
            return this;
        }
    }
}
