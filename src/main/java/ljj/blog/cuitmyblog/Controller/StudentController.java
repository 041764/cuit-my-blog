package ljj.blog.cuitmyblog.Controller;

import ljj.blog.cuitmyblog.Response;
import ljj.blog.cuitmyblog.dto.StudentDTO;
import ljj.blog.cuitmyblog.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
public class StudentController {

    @Autowired
    private StudentService studentService;

    @GetMapping("/students/{id}")
    public Response<StudentDTO> getStudentById(@PathVariable Long id) {
        return Response.newSuccess(studentService.getStudentById(id));
    }
    @GetMapping("/students/count")
    public Response<Long> countStudents() {
        return Response.newSuccess(studentService.countStudents());
    }

    @PostMapping("/students")
    public Response<Long> addNewStudent(@RequestBody StudentDTO studentDTO) throws IllegalAccessException {
        return Response.newSuccess(studentService.addNewStudent(studentDTO));
    }

    @DeleteMapping("/students/{id}")
    public void deleteStudentById(@PathVariable Long id) {
        studentService.deleteStudentById(id);
    }

    @PutMapping("/students/{id}")
    public Response<StudentDTO> updateStudentById(@PathVariable Long id, @RequestBody StudentDTO studentDTO) throws IllegalAccessException {
        return Response.newSuccess(studentService.updateStudentById(id, studentDTO.getName(), studentDTO.getEmail()));
    }
}
