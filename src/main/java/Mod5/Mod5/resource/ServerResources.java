package Mod5.Mod5.resource;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.*;
import Mod5.Mod5.filter.Secured;

@Secured
@Path("/security")
public class ServerResources {
    private static boolean beingInvaded = false;

    @Context
    ServletContext servletContext;

    @GET
    @Path("/hello")
    @Produces(MediaType.TEXT_HTML)
    public String hello() {
        System.out.println("Hello");
        return "Hello";
    }

    @GET
    @Path("/status")
    @Produces(MediaType.APPLICATION_JSON)
    public boolean getStatus() {
        System.out.println(ServerResources.beingInvaded);
        return ServerResources.beingInvaded;
    }

    @POST
    @Path("/update/{status}")
    public void update(@PathParam("status") boolean status) {
        ServerResources.beingInvaded = status;
        System.out.println("UPDATE\nStatus: " + status + "\n" + ServerResources.beingInvaded);
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
        InputStream inputStream = classLoader.getResourceAsStream("../../html/Security.html");

        System.out.println("Req received.");

        return inputStream;
    }
}
