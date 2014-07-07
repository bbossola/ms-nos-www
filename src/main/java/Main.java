import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.util.resource.ResourceCollection;
import org.eclipse.jetty.webapp.WebAppContext;

public class Main {
    public static void main(String[] args) throws Exception {

        Server server = new Server();
        SelectChannelConnector connector = new SelectChannelConnector();

        int port = 4242;
        try {
            port = Integer.parseInt(args[0]);
        } catch (Exception ignore) {
        }

        connector.setMaxIdleTime(60 * 60 * 1000);
        connector.setSoLingerTime(-1);
        connector.setPort(port);
        server.addConnector(connector);

        WebAppContext webAppContext = new WebAppContext();
        webAppContext.setBaseResource(new ResourceCollection(
                Resource.newResource("src/main/webapp"),
                Resource.newClassPathResource("META-INF/resources")));
        server.setHandler(webAppContext);

        server.start();
        server.join();
    }
}
