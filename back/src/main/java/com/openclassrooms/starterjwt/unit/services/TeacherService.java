package com.openclassrooms.starterjwt.unit.services;

import com.openclassrooms.starterjwt.unit.models.Teacher;
import com.openclassrooms.starterjwt.repository.TeacherRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TeacherService {
    private final TeacherRepository teacherRepository;

    public TeacherService(TeacherRepository teacherRepository) {
        this.teacherRepository = teacherRepository;
    }

    public List<Teacher> findAll() {
        return this.teacherRepository.findAll();
    }

    public Teacher findById(Long id) {
        return this.teacherRepository.findById(id).orElse(null);
    }
}
