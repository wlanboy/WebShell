package com.wlanboy.webshell;

import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@SpringBootTest
public class BCryptTest {

	@Test
	public void encodePassword() {
		PasswordEncoder enc = new BCryptPasswordEncoder();
		
		System.out.println(enc.encode("user"));
		System.out.println(enc.encode("test"));
	}
}
