package com.epam.gym.auth.repository.domain;

import com.epam.gym.auth.domain.LoginAttempt;
import com.epam.gym.auth.repository.entity.ILoginAttemptEntityRepository;
import com.epam.gym.auth.repository.entity.LoginAttemptEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.ConversionService;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class JpaLoginAttemptRepositoryTest {

    private static final UUID USER_UID = UUID.randomUUID();
    private static final LoginAttempt DOMAIN = new LoginAttempt();
    private static final LoginAttemptEntity ENTITY = new LoginAttemptEntity();

    @Mock
    private ILoginAttemptEntityRepository repository;
    @Mock
    private ConversionService conversionService;

    @InjectMocks
    private JpaLoginAttemptRepository testObject;

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(repository, conversionService);
    }

    @Test
    void findByUserUid_shouldReturnConvertedOptional() {
        doReturn(Optional.of(ENTITY)).when(repository).findById(USER_UID);
        doReturn(DOMAIN).when(conversionService).convert(ENTITY, LoginAttempt.class);

        var result = testObject.findByUserUid(USER_UID);

        assertTrue(result.isPresent());
        assertEquals(DOMAIN, result.get());
        verify(repository).findById(USER_UID);
        verify(conversionService).convert(ENTITY, LoginAttempt.class);
    }

    @ParameterizedTest
    @NullSource
    void findByUserUid_shouldThrowException_whenArgumentNull(UUID userUid) {
        assertThrows(NullPointerException.class, () -> testObject.findByUserUid(userUid));
    }

    @Test
    void save_shouldConvertAndSave() {
        doReturn(ENTITY).when(conversionService).convert(DOMAIN, LoginAttemptEntity.class);

        testObject.save(DOMAIN);

        verify(conversionService).convert(DOMAIN, LoginAttemptEntity.class);
        verify(repository).save(ENTITY);
    }

    @ParameterizedTest
    @NullSource
    void save_shouldThrowException_whenArgumentNull(LoginAttempt attempt) {
        assertThrows(NullPointerException.class, () -> testObject.save(attempt));
    }

    @Test
    void deleteByUserUid_shouldDelete() {
        testObject.deleteByUserUid(USER_UID);

        verify(repository).deleteById(USER_UID);
    }

    @ParameterizedTest
    @NullSource
    void deleteByUserUid_shouldThrowException_whenArgumentNull(UUID userUid) {
        assertThrows(NullPointerException.class, () -> testObject.deleteByUserUid(userUid));
    }
}
