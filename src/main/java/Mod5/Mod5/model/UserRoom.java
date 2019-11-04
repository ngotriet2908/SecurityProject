package Mod5.Mod5.model;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
public class UserRoom {
    private List<RoomStatus> roomStatusList;
    private String username;

    public UserRoom(List<RoomStatus> roomStatusList, String username) {
        this.roomStatusList = roomStatusList;
        this.username = username;
    }

    public List<RoomStatus> getRoomStatusList() {
        return roomStatusList;
    }

    public void setRoomStatusList(List<RoomStatus> roomStatusList) {
        this.roomStatusList = roomStatusList;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
