package Mod5.Mod5.resource;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import Mod5.Mod5.Dao.UserDao;
import Mod5.Mod5.filter.Secured;
import Mod5.Mod5.model.RoomStatus;
import Mod5.Mod5.model.UserRoom;

@Secured
@Path("/security")
public class ServerResources {
    private static boolean beingInvaded = false;

    @Context
    ServletContext servletContext;


    @GET
    @Path("/profile/{username}/status")
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED, MediaType.MULTIPART_FORM_DATA})
    public UserRoom getRoomStatus(@Context HttpServletResponse response,
                                          @Context HttpServletRequest request,
                                          @PathParam("username") String username) throws IOException {
//        if (error != null) {
//            if (error.equals("not_authorized") ||
//                    error.equals("not_activated") ||
//                    error.equals("reset_token_invalid")) {
//                response.addHeader("error", error);
//            }
//        }
//
//        if (message != null) {
//            if (message.equals("reset_success") ||
//                    message.equals("reset_request_success") ||
//                    message.equals("registration_success")) {
//                response.addHeader("message", message);
//            }
//        }
        List<RoomStatus> roomStatusList = UserDao.instance.getRoomsStatus(username);
        int totalRoomWithPeople = 0;
        int totalRoomWithFire = 0;
        for(int i = 0; i < roomStatusList.size(); i++) {
            if (roomStatusList.get(i).getTemp() > 25) {
                totalRoomWithFire++;
            }
            if (roomStatusList.get(i).getPeople().equals("True")) {
                totalRoomWithPeople++;
            }
        }
        System.out.println("getting rooms status");
        return new UserRoom(roomStatusList, username,roomStatusList.size(),totalRoomWithPeople,totalRoomWithFire);
    }

    @GET
    @Path("/profile/{username}")
    @Produces(MediaType.TEXT_HTML)
    public InputStream showLoginPage(@Context HttpServletResponse response, @Context HttpServletRequest request) throws IOException {
//        if (error != null) {
//            if (error.equals("not_authorized") ||
//                    error.equals("not_activated") ||
//                    error.equals("reset_token_invalid")) {
//                response.addHeader("error", error);
//            }
//        }
//
//        if (message != null) {
//            if (message.equals("reset_success") ||
//                    message.equals("reset_request_success") ||
//                    message.equals("registration_success")) {
//                response.addHeader("message", message);
//            }
//        }

        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream("../../html/profile.html");

        System.out.println("Req received.");

        return inputStream;
    }
}
