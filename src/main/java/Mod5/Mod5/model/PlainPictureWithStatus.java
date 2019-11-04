package Mod5.Mod5.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class PlainPictureWithStatus {
    private String picture;
    private RoomStatus roomStatus;

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public RoomStatus getRoomStatus() {
        return roomStatus;
    }

    public void setRoomStatus(RoomStatus roomStatus) {
        this.roomStatus = roomStatus;
    }

    public PlainPictureWithStatus(String picture, RoomStatus roomStatus) {
        this.picture = picture;
        this.roomStatus = roomStatus;
    }
}
