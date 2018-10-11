package com.avior.idan.drawsomething;

/**
 * Created by Idan Avior on 9/24/2017.
 */

/**
 * Drawing class
 */
public class Drawing {
    // The name by which the image associated with the Drawing can be retrieved from the storage
    private String imageReference;

    // The word/sentence other users will have to guess
    private String description;

    public Drawing(String imgRef, String desc){
        imageReference = imgRef;
        description = desc;
    }

    public Drawing(){
        imageReference = "";
        description = "";
    }

    public String getImageReference(){
        return imageReference;
    }

    public String getDescription(){
        return description;
    }
}
