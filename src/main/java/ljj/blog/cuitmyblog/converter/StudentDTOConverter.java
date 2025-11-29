package ljj.blog.cuitmyblog.converter;

import ljj.blog.cuitmyblog.dto.StudentDTO;
import ljj.blog.cuitmyblog.entity.Student;

public class StudentDTOConverter {
    public static StudentDTO convertStudentToStudentDTO(Student student) {
        StudentDTO studentDTO = new StudentDTO();
        studentDTO.setId(student.getId());
        studentDTO.setName(student.getName());
        studentDTO.setEmail(student.getEmail());
        return studentDTO;
    }

    public static Student convertStudentDTOToStudent(StudentDTO studentDTO) {
        Student student = new Student();
        student.setId(studentDTO.getId());
        student.setName(studentDTO.getName());
        student.setEmail(studentDTO.getEmail());
        return student;
    }
}
