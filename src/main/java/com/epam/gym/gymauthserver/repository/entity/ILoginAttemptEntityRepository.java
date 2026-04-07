package com.epam.gym.gymauthserver.repository.entity;

import lombok.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ILoginAttemptEntityRepository extends JpaRepository<@NonNull LoginAttemptEntity, @NonNull UUID> {

}
