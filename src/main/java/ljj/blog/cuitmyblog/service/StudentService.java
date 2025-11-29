package ljj.blog.cuitmyblog.service;
import ljj.blog.cuitmyblog.dto.StudentDTO;

public interface StudentService {
    StudentDTO getStudentById(Long id);

    Long addNewStudent(StudentDTO studentDTO) throws IllegalAccessException;

    void deleteStudentById(Long id);

    StudentDTO updateStudentById(Long id, String name, String email);

    Long countStudents();
}
