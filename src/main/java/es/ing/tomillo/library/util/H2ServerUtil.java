package es.ing.tomillo.library.util;

import org.h2.tools.Server;
import java.sql.SQLException;

public class H2ServerUtil {
    private static Server server;

    public static void startServer() throws SQLException {
        if (server == null || !server.isRunning(false)) {
            server = Server.createTcpServer("-tcpAllowOthers", "-tcpPort", "9092").start();
            System.out.println("✅ H2 server started at " + server.getURL());
        }
    }

    public static void stopServer() {
        if (server != null) {
            server.stop();
            System.out.println("🛑 H2 server stopped.");
        }
    }
}
