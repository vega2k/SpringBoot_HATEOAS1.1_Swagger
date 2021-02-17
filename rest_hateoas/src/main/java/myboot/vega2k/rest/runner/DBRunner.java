package myboot.vega2k.rest.runner;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class DBRunner implements ApplicationRunner {
	@Autowired
	private DataSource ds;
	
	@Override
	public void run(ApplicationArguments args) throws Exception {
		System.out.println(ds.getConnection().getMetaData().getURL());
		
	}
	
}
