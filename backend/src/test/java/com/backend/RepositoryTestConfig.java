package com.backend;

import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

@DataJpaTest
@Import(TestcontainersConfiguration.class)
@Transactional
public abstract class RepositoryTestConfig {
}
