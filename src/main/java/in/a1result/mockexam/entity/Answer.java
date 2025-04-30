package in.a1result.mockexam.entity;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class Answer {

    private String question, correctAnswer, selectedOption;
    private boolean isCorrect;
    private LocalDateTime createdAt = LocalDateTime.now();;

}
