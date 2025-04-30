package in.a1result.mockexam.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.a1result.mockexam.entity.Question;
import in.a1result.mockexam.repositoy.QuestionRepo;

@Service
public class QuestionService {

    @Autowired
    private QuestionRepo questionRepo;

    // 1. Save a new question
    public Question saveQuestion(Question question) {
        return questionRepo.save(question);
    }

    // 2. Get a question by ID
    public Question getQuestionById(Long id) {
        return questionRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Question not found with id: " + id));
    }

    // 3. Get all questions
    public List<Question> getAllQuestions() {
        return questionRepo.findAll();
    }

    // 4. Get all questions belonging to an exam
    public List<Question> getQuestionsByExamId(Long examId) {
        return questionRepo.findByExamId(examId);
    }

    // 6. Update an existing question
    public Question updateQuestion(Long id, Question updatedQuestion) {
        Question existingQuestion = getQuestionById(id);
        
        existingQuestion.setQuestionTitle(updatedQuestion.getQuestionTitle());
        existingQuestion.setOptionA(updatedQuestion.getOptionA());
        existingQuestion.setOptionB(updatedQuestion.getOptionB());
        existingQuestion.setOptionC(updatedQuestion.getOptionC());
        existingQuestion.setOptionD(updatedQuestion.getOptionD());
        existingQuestion.setCorrectAnswer(updatedQuestion.getCorrectAnswer());
        existingQuestion.setExplanation(updatedQuestion.getExplanation());
        
        return questionRepo.save(existingQuestion);
    }

    // 7. Delete a question
    public void deleteQuestion(Long id) {
        questionRepo.deleteById(id);
    }
}
