package Mod5.Mod5.model;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@XmlRootElement
public class UserRoom {
    private List<RoomStatus> roomStatusList;
    private String username;
    private int totalRoom;
    private int totalRoomWithPeople;
    private int getTotalRoomWithFire;

    public UserRoom(List<RoomStatus> roomStatusList, String username, int totalRoom, int totalRoomWithPeople, int getTotalRoomWithFire) {
        this.roomStatusList = roomStatusList;
        this.username = username;
        this.totalRoom = totalRoom;
        this.totalRoomWithPeople = totalRoomWithPeople;
        this.getTotalRoomWithFire = getTotalRoomWithFire;
    }

    public int getTotalRoom() {
        return totalRoom;
    }

    public void setTotalRoom(int totalRoom) {
        this.totalRoom = totalRoom;
    }

    public int getTotalRoomWithPeople() {
        return totalRoomWithPeople;
    }

    public void setTotalRoomWithPeople(int totalRoomWithPeople) {
        this.totalRoomWithPeople = totalRoomWithPeople;
    }

    public int getGetTotalRoomWithFire() {
        return getTotalRoomWithFire;
    }

    public void setGetTotalRoomWithFire(int getTotalRoomWithFire) {
        this.getTotalRoomWithFire = getTotalRoomWithFire;
    }

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
