package Mod5.Mod5.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Picture {
    private byte[] picture;
    private String status;

    public Picture(byte[] picture, String status) {
        this.picture = picture;
        this.status = status;
    }

    public byte[] getPicture() {
        return picture;
    }

    public void setPicture(byte[] picture) {
        this.picture = picture;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
