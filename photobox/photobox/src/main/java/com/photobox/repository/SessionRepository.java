// repository/SessionRepository.java
package com.photobox.repository;

import com.photobox.model.Session;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SessionRepository extends JpaRepository<Session, String> {
}