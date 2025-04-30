package in.a1result.mockexam.repositoy;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.a1result.mockexam.entity.Question;

@Repository
public interface QuestionRepo extends JpaRepository<Question,Long>{

    List<Question> findByExamId(Long examId);

}
