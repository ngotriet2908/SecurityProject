package Mod5.Mod5.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class PictureByteWithStatus {
    private byte[] picture;
    private RoomStatus roomStatus;

    public byte[] getPicture() {
        return picture;
    }

    public void setPicture(byte[] picture) {
        this.picture = picture;
    }

    public RoomStatus getRoomStatus() {
        return roomStatus;
    }

    public void setRoomStatus(RoomStatus roomStatus) {
        this.roomStatus = roomStatus;
    }

    public PictureByteWithStatus(byte[] picture, RoomStatus roomStatus) {
        this.picture = picture;
        this.roomStatus = roomStatus;
    }
}
