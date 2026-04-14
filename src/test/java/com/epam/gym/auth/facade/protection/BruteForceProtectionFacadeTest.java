package com.epam.gym.auth.facade.protection;

import com.epam.gym.auth.controller.rest.dto.response.BruteForceStatusResponse;
import com.epam.gym.auth.service.protection.IProtectionService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;

@ExtendWith(MockitoExtension.class)
class BruteForceProtectionFacadeTest {

    private static final UUID USER_UID = UUID.randomUUID();
    private static final BruteForceStatusResponse RESPONSE = BruteForceStatusResponse.notBlocked();

    @Mock
    private IProtectionService protectionService;

    @InjectMocks
    private BruteForceProtectionFacade testObject;

    @AfterEach
    void tearDown() {
        verifyNoMoreInteractions(protectionService);
    }

    @Test
    void getStatus_shouldDelegateToService() {
        doReturn(RESPONSE).when(protectionService).getStatus(USER_UID);

        var result = testObject.getStatus(USER_UID);

        assertEquals(RESPONSE, result);
        verify(protectionService).getStatus(USER_UID);
    }

    @Test
    void recordFailedAttempt_shouldDelegateToService() {
        doNothing().when(protectionService).recordFailedAttempt(USER_UID);

        testObject.recordFailedAttempt(USER_UID);

        verify(protectionService).recordFailedAttempt(USER_UID);
    }

    @ParameterizedTest
    @NullSource
    void getStatus_shouldThrowException_whenUidNull(UUID uid) {
        assertThrows(NullPointerException.class, () -> testObject.getStatus(uid));
    }
}
