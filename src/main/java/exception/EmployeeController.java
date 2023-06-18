package exception;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.mvc.WebMvcLinkBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

@RestController
@RequestMapping("/students")
public class EmployeeController {

    @Autowired
    private EmployeeRepo studentRepository;

    @GetMapping()
    public List<Employee> retrieveAllStudents() {
        return studentRepository.findAll();
    }

    @GetMapping("{id}")
    public EntityModel<Employee> retrieveStudent(@PathVariable long id) {
        Optional<Employee> student = studentRepository.findById(id);

        if (student.isEmpty())
            throw new EmployeeNotFoundException("id-" + id);

        EntityModel<Employee> resource = EntityModel.of(student.get());

        WebMvcLinkBuilder linkTo = linkTo(methodOn(this.getClass()).retrieveAllStudents());

        resource.add(linkTo.withRel("all-students"));

        return resource;
    }

    @DeleteMapping("{id}")
    public void deleteStudent(@PathVariable long id) {
        studentRepository.deleteById(id);
    }

    @PostMapping()
    public ResponseEntity<Object> createStudent(@RequestBody Employee student) {
        Employee savedStudent = studentRepository.save(student);

        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(savedStudent.getId())
                .toUri();

        return ResponseEntity.created(location)
                .build();

    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> updateStudent(@RequestBody Employee student, @PathVariable long id) {

        Optional<Employee> studentOptional = studentRepository.findById(id);

        if (studentOptional.isEmpty())
            return ResponseEntity.notFound().build();

        student.setId(id);

        studentRepository.save(student);

        return ResponseEntity.noContent().build();
    }
}
