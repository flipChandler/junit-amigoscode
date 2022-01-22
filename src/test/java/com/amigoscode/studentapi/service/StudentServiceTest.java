package com.amigoscode.studentapi.service;

import com.amigoscode.studentapi.enums.Gender;
import com.amigoscode.studentapi.exception.BadRequestException;
import com.amigoscode.studentapi.exception.StudentNotFoundException;
import com.amigoscode.studentapi.model.Student;
import com.amigoscode.studentapi.repository.StudentRepository;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StudentServiceTest {

    @Mock
    private StudentRepository studentRepository;
    private AutoCloseable autoCloseable;
    private StudentService underTest;
    private Long EXISTING_ID = 1L;

    public static final String EMAIL = "jamila@gmail.com";

    @BeforeEach
    void setUp() {
        underTest = new StudentService(studentRepository); // each test, we have a clean service and repository
    }

    @Test
    void canGetAllStudents() {
        // when
        underTest.getAllStudents();
        // then
        verify(studentRepository).findAll();
    }

    @Test
    void canAddStudent() {
        // given
        Student student = new Student("Jamila", EMAIL, Gender.FEMALE);

        //when
        underTest.addStudent(student);

        //then
        ArgumentCaptor<Student> studentArgumentCaptor = ArgumentCaptor.forClass(Student.class);

        verify(studentRepository).save(studentArgumentCaptor.capture());

        Student capturedStudent = studentArgumentCaptor.getValue();

        AssertionsForClassTypes.assertThat(capturedStudent).isEqualTo(student);

    }

    @Test
    void willThrowWhenEmailIsTaken() {
        // given
        Student student = new Student("Jamila", EMAIL, Gender.FEMALE);

        BDDMockito.given(studentRepository.selectExistsEmail(anyString()))
                .willReturn(true);
        //when
        //then
        Assertions.assertThatThrownBy(() -> underTest.addStudent(student))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Email " + student.getEmail() + " taken");
        verify(studentRepository, never()).save(any());
    }

    @Test
    void doNothingWhenStudentDeleted() {
        // given
        long id = 10;
        BDDMockito.given(studentRepository.existsById(id)).willReturn(true);

        // when
        underTest.deleteStudent(id);

        // then
        verify(studentRepository, times(1)).deleteById(id);
    }

    @Test
    void willThrowWhenDeleteStudentNotFound() {
        // given
        long id = 10;
        BDDMockito.given(studentRepository.existsById(id))
                .willReturn(false);
        // when
        // then
        Assertions.assertThatThrownBy(() -> underTest.deleteStudent(id))
                .isInstanceOf(StudentNotFoundException.class)
                .hasMessageContaining("Student with id " + id + " does not exists");

        verify(studentRepository, never()).deleteById(any());
    }
}