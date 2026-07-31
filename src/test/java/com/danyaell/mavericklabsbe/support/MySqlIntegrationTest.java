package com.danyaell.mavericklabsbe.support;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
@SpringBootTest(properties = {
        "spring.jpa.generate-ddl=false",
        "spring.jpa.hibernate.ddl-auto=validate",
        "spring.jpa.properties.hibernate.hbm2ddl.auto=validate",
        "spring.sql.init.mode=never"
})
@ActiveProfiles("test")
@Import(MySqlTestcontainersConfiguration.class)
public @interface MySqlIntegrationTest {
}