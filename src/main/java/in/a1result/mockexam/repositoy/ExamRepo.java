package in.a1result.mockexam.repositoy;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.a1result.mockexam.entity.Exam;

@Repository
public interface ExamRepo extends JpaRepository<Exam, Long> {
    
    Optional<Exam> findByUrl(String url);
    Optional<Exam> findByExamTitle(String examTitle);
}
