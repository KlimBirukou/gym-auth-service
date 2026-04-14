package com.epam.gym.auth.repository.mapper;

import com.epam.gym.auth.domain.LoginAttempt;
import com.epam.gym.auth.repository.entity.LoginAttemptEntity;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.mapstruct.factory.Mappers;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

class ILoginAttemptEntityToLoginAttemptMapperTest {

    private static final UUID USER_UID = UUID.randomUUID();
    private static final int ATTEMPTS = 5;
    private static final LocalDateTime LAST_FAILED = LocalDateTime.of(2026, 4, 4, 10, 0);

    private final ILoginAttemptEntityToLoginAttemptMapper testObject
        = Mappers.getMapper(ILoginAttemptEntityToLoginAttemptMapper.class);

    private static Stream<Arguments> provideMappingData() {
        return Stream.of(
            Arguments.of(USER_UID, ATTEMPTS, LAST_FAILED),
            Arguments.of(UUID.randomUUID(), 0, null)
        );
    }

    @ParameterizedTest
    @MethodSource("provideMappingData")
    void convert_shouldMapEntityToDomain(UUID uid, int count, LocalDateTime time) {
        var entity = LoginAttemptEntity.builder()
            .userUid(uid)
            .failedAttempts(count)
            .lastFailedAt(time)
            .build();

        var result = testObject.convert(entity);

        assertNotNull(result);
        assertEquals(uid, result.getUserUid());
    }

    @ParameterizedTest
    @NullSource
    void convert_shouldReturnNull_whenEntityNull(LoginAttemptEntity entity) {
        assertNull(testObject.convert(entity));
    }

    @ParameterizedTest
    @NullSource
    void convert_shouldReturnNull_whenDomainNull(LoginAttempt domain) {
        assertNull(testObject.convert(domain));
    }
}
