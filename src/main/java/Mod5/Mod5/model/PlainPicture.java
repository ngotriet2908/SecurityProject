package Mod5.Mod5.model;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class PlainPicture {
    private String picture;
    private String status;

    public PlainPicture(String picture, String status) {
        this.picture = picture;
        this.status = status;
    }

    public String getPicture() {
        return picture;
    }

    public void setPicture(String picture) {
        this.picture = picture;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
