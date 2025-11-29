package ljj.blog.cuitmyblog.service;

import ljj.blog.cuitmyblog.Dao.StudentRepository;
import ljj.blog.cuitmyblog.converter.StudentDTOConverter;
import ljj.blog.cuitmyblog.dto.StudentDTO;
import ljj.blog.cuitmyblog.entity.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

@Service
public class StudentServiceImpl implements StudentService {
    @Autowired
    private StudentRepository studentRepository;

    @Override
    public StudentDTO getStudentById(Long id) {
        Student student = studentRepository.findById(id).orElseThrow(RuntimeException::new);
        return StudentDTOConverter.convertStudentToStudentDTO(student);
    }

    @Override
    public Long addNewStudent(StudentDTO studentDTO) throws IllegalAccessException {
        String email = studentDTO.getEmail();
        List<Student> studentList = studentRepository.findByEmail(email);
        if(!CollectionUtils.isEmpty(studentList)) {
            throw new IllegalAccessException("email" + studentDTO.getEmail() + "has been used");
        }
        Student student = studentRepository.save(StudentDTOConverter.convertStudentDTOToStudent(studentDTO));
        return student.getId();
    }

    @Override
    public void deleteStudentById(Long id) {
        studentRepository.findById(id).orElseThrow(() -> new RuntimeException("student id " + id + " does not exist"));
        studentRepository.deleteById(id);
    }

    @Override
    @Transactional
    public StudentDTO updateStudentById(Long id, String name, String email) {
        Student studentInDB = studentRepository.findById(id).orElseThrow(() -> new RuntimeException("student id " + id + " does not exist"));
        if(StringUtils.hasLength(name) && !studentInDB.getName().equals(name)) {
            studentInDB.setName(name);
        }
        if(StringUtils.hasLength(email) && !studentInDB.getEmail().equals(email)) {
            studentInDB.setEmail(email);
        }
        Student student = studentRepository.save(studentInDB);
        return StudentDTOConverter.convertStudentToStudentDTO(student);
    }

    @Override
    public Long countStudents() {
        return studentRepository.count();
    }
}
