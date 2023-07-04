package com.m9d.sroom;

import com.m9d.sroom.member.domain.Member;
import com.m9d.sroom.member.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MariaDBContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.assertj.core.api.Assertions.*;

@Testcontainers
@SpringBootTest
public
class SroomApplicationTests {

	@Autowired
	private MemberRepository memberRepository;

	@Container
	static MariaDBContainer<?> mariaDBContainer = new MariaDBContainer<>("mariadb:10.5.8")
			.withDatabaseName("sroom")
			.withUsername("root")
			.withPassword("")
			.withInitScript("schema.sql");

	@DynamicPropertySource
	static void registerPgProperties(DynamicPropertyRegistry registry) {
		registry.add("spring.datasource.url", mariaDBContainer::getJdbcUrl);
		registry.add("spring.datasource.username", mariaDBContainer::getUsername);
		registry.add("spring.datasource.password", mariaDBContainer::getPassword);
	}

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Test
	@DisplayName("db connection test")
	public void testExample() {
		memberRepository.save("memberCode1", "member1");

		Member member = memberRepository.getByMemberCode("memberCode1");

		assertThat(member.getMemberId()).isEqualTo(1);
		assertThat(member.getMemberName()).isEqualTo("member1");
	}

}
