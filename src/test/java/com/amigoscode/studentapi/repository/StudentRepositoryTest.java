package com.amigoscode.studentapi.repository;

import com.amigoscode.studentapi.enums.Gender;
import com.amigoscode.studentapi.model.Student;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class StudentRepositoryTest {

    public static final String EMAIL = "jamila@gmail.com";

    @Autowired
    private StudentRepository studentRepository;

    @AfterEach // each test we have a clean state
    void tearDown() {
        studentRepository.deleteAll();
    }

    @Test
    void itShouldCheckWhenStudentEmailExists() {
        // given
        Student student = new Student("Jamila", EMAIL, Gender.FEMALE);
        studentRepository.save(student);

        // when
        boolean expected = studentRepository.selectExistsEmail(EMAIL);

        //then
        assertThat(expected).isTrue();
    }

    @Test
    void itShouldCheckWhenStudentEmailDoesNotExist() {
        // given
        String email = EMAIL;

        // when
        boolean expected = studentRepository.selectExistsEmail(email);

        //then
        assertThat(expected).isFalse();
    }
}