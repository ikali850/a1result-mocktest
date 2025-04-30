package in.a1result.mockexam.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.a1result.mockexam.entity.Exam;
import in.a1result.mockexam.service.ExamService;

@RestController
public class ExamController {

    @Autowired
    private ExamService examService;

    // 1. Create a new exam
    @PostMapping
    public Exam createExam(@RequestBody Exam exam) {
        return examService.saveExam(exam);
    }

    // 2. Get exam by ID
    @GetMapping("/{id}")
    public Exam getExamById(@PathVariable Long id) {
        return examService.getExamData(id);
    }

    // 3. Get all exams
    @GetMapping("/mock/exams")
    public List<Exam> getAllExams() {
        return examService.getExams();
    }

    // 4. Update exam by ID
    @PutMapping("/{id}")
    public Exam updateExam(@PathVariable Long id, @RequestBody Exam updatedExam) {
        return examService.updateExam(id, updatedExam);
    }

    // 5. Delete exam by ID
    @DeleteMapping("/{id}")
    public String deleteExam(@PathVariable Long id) {
        examService.deleteExam(id);
        return "Exam deleted successfully!";
    }

}
