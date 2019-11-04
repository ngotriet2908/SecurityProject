package Mod5.Mod5.resource;

import Mod5.Mod5.Dao.UserDao;
import Mod5.Mod5.model.Picture;
import Mod5.Mod5.model.PlainPicture;
import org.glassfish.jersey.media.multipart.FormDataBodyPart;
import org.glassfish.jersey.media.multipart.FormDataContentDisposition;
import org.glassfish.jersey.media.multipart.FormDataParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.io.IOException;
import java.io.InputStream;
import java.security.Principal;
import java.util.Base64;

@Path("/room")
public class Room {

    @Context
    UriInfo uriInfo;
    @Context
    Request request;

    @POST
    @Path("/status")
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED, MediaType.MULTIPART_FORM_DATA})
    public void saveStatus(@FormDataParam("people") String status,
                            @FormDataParam("room_id") int room_id,
                            @Context HttpServletResponse servletResponse,
                            @Context HttpServletRequest servletRequest) throws IOException {

//        if (body.getMediaType().toString().equals("image/jpeg") ||
//                body.getMediaType().toString().equals("image/jpg") ||
//                body.getMediaType().toString().equals("image/png")) {
        System.out.println("updating status");
        UserDao.instance.updateStatus(status, room_id);
    }

    @POST
    @Path("/temp")
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED, MediaType.MULTIPART_FORM_DATA})
    public void saveTemp(
                            @FormDataParam("room_id") int room_id,
                            @FormDataParam("temp") String temp,
                            @Context HttpServletResponse servletResponse,
                            @Context HttpServletRequest servletRequest) throws IOException {

//        if (body.getMediaType().toString().equals("image/jpeg") ||
//                body.getMediaType().toString().equals("image/jpg") ||
//                body.getMediaType().toString().equals("image/png")) {
        System.out.println("updating status");
        UserDao.instance.updateTemp(Float.parseFloat(temp),room_id);
    }

    @Path("/{room_id}")
    @GET
    @Produces(MediaType.TEXT_HTML)
    public InputStream showLoginPage(@Context HttpServletResponse response,
                                     @PathParam("room_id") int room_id,
                                     @Context HttpServletRequest request
                                     ) throws IOException {

        ClassLoader classLoader = getClass().getClassLoader();
        InputStream inputStream = classLoader.getResourceAsStream("../../html/Room.html");
        System.out.println("Req received.");

        return inputStream;
    }


    @Path("/{room_id}")
    @GET
    @Produces("image/jpeg")
    public Response getPicture(@PathParam("room_id") int room_id,
                               @Context HttpServletResponse servletResponse,
                               @Context HttpServletRequest servletRequest) throws IOException {

        System.out.println("get latest image of room " + String.valueOf(room_id));
        byte[] imageData = UserDao.instance.getLastestImage(room_id).getPicture();
        return Response.ok(imageData).build();
    }

    @Path("/{room_id}")
    @GET
    @Produces({MediaType.APPLICATION_XML, MediaType.APPLICATION_JSON})
    public PlainPicture getPictureBase64(@PathParam("room_id") int room_id,
                                     @Context HttpServletResponse servletResponse,
                                     @Context HttpServletRequest servletRequest) throws IOException {
        Picture current = UserDao.instance.getLastestImage(room_id);
        byte[] imageData = current.getPicture();

        if (imageData == null) {
            ClassLoader classLoader = getClass().getClassLoader();
            InputStream inputStream = classLoader.getResourceAsStream("../../img/No-profile.jpg");
            imageData = UserDao.toByteArray(inputStream);
        }

        String encoded = Base64.getEncoder().encodeToString(imageData);
        return new PlainPicture(encoded, current.getStatus());
    }

}
