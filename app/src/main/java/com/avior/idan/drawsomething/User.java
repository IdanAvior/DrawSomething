package com.avior.idan.drawsomething;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Idan Avior on 9/24/2017.
 */

/**
 * User class
 */
public class User {
    private String email;

    private List<Drawing> drawings;

    private List<Invitation> invitations;

    private List<Invitation> previouslyViewedDrawings;

    public User(String email){
        this.email = email;
        drawings = new ArrayList();
        invitations = new ArrayList<>();
        previouslyViewedDrawings = new ArrayList<>();
    }

    public User(){
        email = "";
        drawings = new ArrayList<>();
        invitations = new ArrayList<>();
        previouslyViewedDrawings = new ArrayList<>();
    }

    public String getEmail(){
        return email;
    }

    public List<Drawing> getDrawings(){
        return drawings;
    }

    public void addDrawing(Drawing drawing){
        drawings.add(drawing);
    }

    public List<Invitation> getInvitations(){
        return invitations;
    }

    public void addInvitation(Invitation invitation){
        invitations.add(invitation);
    }

    public void addPreviouslyViewedDrawing(Invitation invitation) {
        if (!hasBeenViewed(invitation))
            previouslyViewedDrawings.add(invitation);
    }

    public void removeInvitation(Invitation invitation){
        Iterator<Invitation> iterator = invitations.iterator();

        while (iterator.hasNext()){
            Invitation invitation1 = iterator.next();
            if (invitation1.equals(invitation))
                iterator.remove();
        }

    }

    public List<Invitation> getPreviouslyViewedDrawings(){
        return previouslyViewedDrawings;
    }

    private boolean hasBeenViewed(Invitation invitation){
        for (Invitation i : previouslyViewedDrawings)
            if (invitation.equals(i))
                return true;
        return false;
    }

}
