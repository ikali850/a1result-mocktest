package in.a1result.mockexam.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import in.a1result.mockexam.entity.Question;
import in.a1result.mockexam.service.QuestionService;

@RestController
@RequestMapping("/admin/question")
public class QuestionController {

    @Autowired
    private QuestionService questionService;

    // 1. Create a new question
    @PostMapping
    public Question createQuestion(@RequestBody Question question) {
        return questionService.saveQuestion(question);
    }

    // 2. Get a single question by ID
    @GetMapping("/{id}")
    public Question getQuestionById(@PathVariable Long id) {
        return questionService.getQuestionById(id);
    }

    // 3. Get all questions
    @GetMapping
    public List<Question> getAllQuestions() {
        return questionService.getAllQuestions();
    }

    // 4. Get questions by examId
    @GetMapping("/exam/{examId}")
    public List<Question> getQuestionsByExam(@PathVariable Long examId) {
        return questionService.getQuestionsByExamId(examId);
    }

    // 6. Update a question
    @PutMapping("/{id}")
    public Question updateQuestion(@PathVariable Long id, @RequestBody Question updatedQuestion) {
        return questionService.updateQuestion(id, updatedQuestion);
    }

    // 7. Delete a question
    @DeleteMapping("/{id}")
    public String deleteQuestion(@PathVariable Long id) {
        questionService.deleteQuestion(id);
        return "Question deleted successfully!";
    }

}
