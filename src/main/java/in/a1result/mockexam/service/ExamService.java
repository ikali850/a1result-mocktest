package in.a1result.mockexam.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import in.a1result.mockexam.entity.Exam;
import in.a1result.mockexam.repositoy.ExamRepo;

@Service
public class ExamService {

    @Autowired
    private ExamRepo examRepository;

    public Exam getExamData(long id) {
        return this.examRepository.findById(id).get();
    }

    public Exam getExamDataByUrl(String url) {
        return this.examRepository.findByUrl(url).get();
    }

    public List<Exam> getExams() {
        return this.examRepository.findAll();
    }

    public Exam saveExam(Exam exam) {
        return examRepository.save(exam);
    }

    public Exam updateExam(Long id, Exam updatedExam) {
        Exam existingExam = examRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Exam not found"));

        existingExam.setExamTitle(updatedExam.getExamTitle());
        existingExam.setExamDescription(updatedExam.getExamDescription());
        existingExam.setDurationMinutes(updatedExam.getDurationMinutes());
        existingExam.setActive(updatedExam.isActive());

        return examRepository.save(existingExam);
    }

    public void deleteExam(Long id) {
        examRepository.deleteById(id);
    }

}