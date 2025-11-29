package ljj.blog.cuitmyblog.entity;


import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Data
@Entity
@Table(name = "t_student")
public class Student{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;
    @Column(name = "name",nullable = false)
    private String name;
    @Column(name = "email",nullable = false)
    private String email;
//    @Column(name="create_time", nullable = false, updatable = false)
//    private Date createTime = new Date();
}
