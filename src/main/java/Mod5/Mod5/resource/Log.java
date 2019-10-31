package Mod5.Mod5.resource;

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
import Mod5.Mod5.Dao.UserDao;

@Path("/log")
public class Log {

    @Context
    UriInfo uriInfo;
    @Context
    Request request;

    @POST
    @Consumes({MediaType.APPLICATION_FORM_URLENCODED, MediaType.MULTIPART_FORM_DATA})
    public void savePicture(@FormDataParam("picture") InputStream picture,
                            @FormDataParam("picture") FormDataContentDisposition pictureInfo,
                            @FormDataParam("picture") FormDataBodyPart body,
                            @FormDataParam("room_id") int room_id,
                            @FormDataParam("status") String status,

                            @Context HttpServletResponse servletResponse,
                            @Context HttpServletRequest servletRequest) throws IOException {

//        if (body.getMediaType().toString().equals("image/jpeg") ||
//                body.getMediaType().toString().equals("image/jpg") ||
//                body.getMediaType().toString().equals("image/png")) {
            UserDao.instance.addLogFile(picture, status, room_id);
    }

    @Path("/{log_id}")
    @GET
    @Produces("image/jpeg")
    public Response getPicture(@PathParam("log_id") String log_id,
                               @Context HttpServletResponse servletResponse,
                               @Context HttpServletRequest servletRequest) throws IOException {

        byte[] imageData = UserDao.instance.getLogImage(log_id);
        return Response.ok(imageData).build();
    }
}
