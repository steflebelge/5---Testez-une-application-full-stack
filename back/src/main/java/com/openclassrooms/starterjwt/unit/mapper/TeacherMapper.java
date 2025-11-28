package com.openclassrooms.starterjwt.unit.mapper;

import com.openclassrooms.starterjwt.dto.TeacherDto;
import com.openclassrooms.starterjwt.unit.models.Teacher;
import org.mapstruct.Mapper;
import org.springframework.stereotype.Component;

@Component
@Mapper(componentModel = "spring")
public interface TeacherMapper extends EntityMapper<TeacherDto, Teacher> {
}
