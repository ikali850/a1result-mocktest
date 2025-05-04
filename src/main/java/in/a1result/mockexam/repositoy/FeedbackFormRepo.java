package in.a1result.mockexam.repositoy;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import in.a1result.mockexam.entity.Feedback;

@Repository
public interface FeedbackFormRepo extends JpaRepository<Feedback, Long> {

}
