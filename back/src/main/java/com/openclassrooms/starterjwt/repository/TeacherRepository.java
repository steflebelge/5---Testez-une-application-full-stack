package com.openclassrooms.starterjwt.repository;

import com.openclassrooms.starterjwt.unit.models.Teacher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeacherRepository  extends JpaRepository<Teacher, Long> {
}
