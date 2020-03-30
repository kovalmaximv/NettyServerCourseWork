package NettyServerCourseWork;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class CommandLineAppStartupRunner implements CommandLineRunner {
    private final Server server;

    @Autowired
    public CommandLineAppStartupRunner(Server server) {
        this.server = server;
    }

    @Override
    public void run(String...args) throws Exception {
        server.run();
    }
}
