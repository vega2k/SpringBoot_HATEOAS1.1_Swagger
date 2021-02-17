package myboot.vega2k.rest.config;

import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;

import myboot.vega2k.rest.account.Account;
import myboot.vega2k.rest.account.AccountRole;
import myboot.vega2k.rest.account.AccountService;
import myboot.vega2k.rest.common.AppProperties;

@Configuration
public class AppConfig {	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}
	
	@Bean
	public ApplicationRunner applicationRunner() {
		return new ApplicationRunner() {			
			@Autowired
			AccountService accountService;
			@Autowired
			AppProperties appProperties;

			
			@Override
			public void run(ApplicationArguments args) throws Exception {
				Account account = Account.builder()
						.email(appProperties.getUserUsername())
						.password(appProperties.getUserPassword())
						.roles(Set.of(AccountRole.ADMIN,AccountRole.USER))
						.build();				
				accountService.saveAccount(account);
			}
		};
	}

}
