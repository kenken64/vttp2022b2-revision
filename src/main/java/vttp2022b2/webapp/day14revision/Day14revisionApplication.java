package vttp2022b2.webapp.day14revision;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.DefaultApplicationArguments;

import static vttp2022b2.webapp.day14revision.util.IOUtil.*;

@SpringBootApplication
public class Day14revisionApplication {
	private static final Logger logger = LoggerFactory.getLogger(Day14revisionApplication.class);

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(Day14revisionApplication.class);
		DefaultApplicationArguments appArgs = new DefaultApplicationArguments(args);
		List<String> opsVal = appArgs.getOptionValues("dataDir");
		System.out.println(opsVal);
		if (opsVal != null) {
			logger.info("" + (String) opsVal.get(0));
			createDir((String) opsVal.get(0));
		} else {
			logger.warn("No data directory was provided");
			System.exit(1);
		}
		app.run(args);
	}

}
