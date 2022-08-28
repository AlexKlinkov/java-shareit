package ru.practicum.shareit.serviceTests.Integration;

import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional(propagation = Propagation.REQUIRES_NEW, rollbackFor = Exception.class)
@DirtiesContext
public class RequestTest {
}
