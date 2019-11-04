package Mod5.Mod5.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class PictureIDwithStatus {
    private long picture;
    private RoomStatus roomStatus;

    public long getPicture() {
        return picture;
    }

    public void setPicture(long picture) {
        this.picture = picture;
    }

    public RoomStatus getRoomStatus() {
        return roomStatus;
    }

    public void setRoomStatus(RoomStatus roomStatus) {
        this.roomStatus = roomStatus;
    }

    public PictureIDwithStatus(long picture, RoomStatus roomStatus) {
        this.picture = picture;
        this.roomStatus = roomStatus;
    }
}
