package com.avior.idan.drawsomething;

/**
 * Created by Idan Avior on 10/1/2017.
 */

/**
 * Invitation class - originally meant to represent a direct invitation from one user to another,
 * it is also used as a means to hold relevant information about a drawing.
 */
public class Invitation{

    // The drawing's reference name (img#) within the creating user's storage file
    private String drawingReferenceName;

    // The user who created the drawing
    private String drawingCreator;

    // The string that the user needs to guess
    private String drawingDescription;

    // Indicates whether or not the logged in user already viewed the drawing.
    // This field is used by the InvitationAdapter to mark the drawing as viewed/unviewed,
    // and its value is relevant only in the context of the logged in user.
    private boolean previouslyViewedByUser;

    public Invitation(String drawingReferenceName, String drawingCreator, String drawingDescription){
        this.drawingReferenceName = drawingReferenceName;
        this.drawingCreator = drawingCreator;
        this.drawingDescription = drawingDescription;
        previouslyViewedByUser = false;
    }

    public Invitation(){
        drawingReferenceName = "";
        drawingCreator = "";
        drawingDescription = "";
        previouslyViewedByUser = false;
    }

    public String getDrawingReferenceName(){
        return drawingReferenceName;
    }

    public String getDrawingCreator(){
        return drawingCreator;
    }

    public String getDrawingDescription(){
        return drawingDescription;
    }

    public boolean hasBeenViewedByUser(){
        return previouslyViewedByUser;
    }

    public void setAsViewedByUser(){
        previouslyViewedByUser = true;
    }

    @Override
    public boolean equals(Object o){
        try {
            Invitation invitation = (Invitation) o;
            return (drawingReferenceName.equals(invitation.drawingReferenceName)
            && drawingCreator.equals(invitation.drawingCreator)
            && drawingDescription.equals(invitation.drawingDescription));
        }
        catch (Exception e){
            return false;
        }
    }
}
