import javax.servlet.ServletContext;
import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

@Path("")
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
    @Path("/index")
    @Produces(MediaType.TEXT_HTML)
    public InputStream getIndex() {
        String base = servletContext.getRealPath("/");
        String path = "/index.html";
        File f = new File(base + path);
        FileInputStream res = null;
        try {
            res = new FileInputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return res;
    }
}
