package com.epam.gym.auth.repository.domain;

import com.epam.gym.auth.domain.LoginAttempt;
import lombok.NonNull;

import java.util.Optional;
import java.util.UUID;

public interface ILoginAttemptRepository {

    Optional<LoginAttempt> findByUserUid(@NonNull UUID userUid);

    void save(@NonNull LoginAttempt loginAttempt);

    void deleteByUserUid(@NonNull UUID userUid);
}
