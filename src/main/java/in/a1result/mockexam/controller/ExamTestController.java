package in.a1result.mockexam.controller;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import in.a1result.mockexam.entity.Answer;
import in.a1result.mockexam.entity.Exam;
import in.a1result.mockexam.entity.Feedback;
import in.a1result.mockexam.entity.Question;
import in.a1result.mockexam.repositoy.FeedbackFormRepo;
import in.a1result.mockexam.service.ExamService;
import in.a1result.mockexam.service.QuestionService;
import in.a1result.mockexam.utils.Utils;
import jakarta.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
@RequestMapping("/mock/test")
public class ExamTestController {

    @Autowired
    private ExamService examService;
    @Autowired
    private QuestionService questionService;
    @Autowired
    private FeedbackFormRepo feedbackFormRepo;

    @GetMapping("/{url}")
    public ModelAndView startTest(@PathVariable("url") String url, HttpSession session) {
        Exam examData = examService.getExamDataByUrl(url);
        String lang = "en"; // default value

        if (session != null) {
            Object langAttr = session.getAttribute("lang");

            if (langAttr instanceof String) {
                lang = (String) langAttr;
            } else {
                session.setAttribute("lang", lang); // set default
            }
        }

        if (examData == null || examData.getQuestions() == null || examData.getQuestions().isEmpty()) {
            return new ModelAndView("errorPage");
        }

        List<Question> questions = examData.getQuestions();

        // Shuffle the questions randomly
        Collections.shuffle(questions);

        // Limit to 75 questions if more
        if (questions.size() > 100) {
            questions = questions.subList(0, 100);
        }

        // Save only question IDs separately
        List<Long> questionIds = questions.stream()
                .map(Question::getId)
                .collect(Collectors.toList());

        // Answer list for user
        List<Answer> answerList = new ArrayList<>(questions.size());

        // Prepare the first question
        Question firstQuestion = questions.get(0);

        if (lang.equals("hi")) {
            Question hindQuestion = new Question();
            hindQuestion.setQuestionTitle(firstQuestion.getQuestionTitleHi());
            hindQuestion.setCorrectAnswer(firstQuestion.getCorrectAnswerHi());
            hindQuestion.setOptionA(firstQuestion.getOptionAHi());
            hindQuestion.setOptionB(firstQuestion.getOptionBHi());
            hindQuestion.setOptionC(firstQuestion.getOptionCHi());
            hindQuestion.setOptionD(firstQuestion.getOptionDHi());
            hindQuestion.setId(firstQuestion.getId());
            firstQuestion = hindQuestion;
        }

        // Calculate total test time (100 questions * 1 mins)
        int totalTestDurationSeconds = questions.size() * 1 * 60;

        // remove all the question store only question ids
        questions.clear();

        // Set attributes in session
        session.setAttribute("examInfo", examData);
        session.setAttribute("answerList", answerList);
        session.setAttribute("questionIds", questionIds);
        session.setAttribute("currentIndex", 0);
        session.setAttribute("startTime", System.currentTimeMillis());
        session.setMaxInactiveInterval(totalTestDurationSeconds + 300); // expire session after exam

        // Build ModelAndView
        ModelAndView mv = new ModelAndView("testPage");
        mv.addObject("examData", examData);
        mv.addObject("examName", examData.getExamTitle());
        mv.addObject("examDescription", examData.getExamDescription());
        mv.addObject("totalQuestions", questionIds.size());
        mv.addObject("currentIndex", 0);
        mv.addObject("testDuration", totalTestDurationSeconds);
        mv.addObject("question", firstQuestion);
        mv.addObject("examTitle", examData.getUrl());
        return mv;
    }

    @PostMapping("/saveAnswer")
    public ResponseEntity<?> saveUserAnswer(@RequestBody Answer answer, HttpSession session) {
        // get language choice
        String lang = (String) session.getAttribute("lang");

        if (answer.getCorrectAnswer().equals(answer.getSelectedOption())) {
            answer.setCorrect(true);
        } else {
            answer.setCorrect(false);
        }
        // Get current session attributes
        @SuppressWarnings("unchecked")
        List<Answer> answerList = (List<Answer>) session.getAttribute("answerList");
        @SuppressWarnings("unchecked")
        List<Long> questionIds = (List<Long>) session.getAttribute("questionIds");
        int questionIndex = ((Integer) session.getAttribute("currentIndex"));
        // Save the submitted answer
        answerList.add(answer);

        // Increment the question index
        questionIndex++;

        // Check if user has answered all questions
        if (questionIndex >= questionIds.size()) {
            // All questions answered -> redirect to submit
            // Update session with final answers
            session.setAttribute("answerList", answerList);

            Map<String, Object> response = new HashMap<>();
            response.put("redirect", true);
            response.put("redirectUrl", "/mock/test/submit"); // URL to go

            return ResponseEntity.ok(response); // You can just return this if your front end will handle the redirect

        }

        // Otherwise, get next question
        Long nextQuestionId = questionIds.get(questionIndex);
        Question nextQuestion = this.questionService.getQuestionById(nextQuestionId);

        if (lang.equals("hi")) {
            Question hindQuestion = new Question();
            hindQuestion.setQuestionTitle(nextQuestion.getQuestionTitleHi());
            hindQuestion.setCorrectAnswer(nextQuestion.getCorrectAnswerHi());
            hindQuestion.setOptionA(nextQuestion.getOptionAHi());
            hindQuestion.setOptionB(nextQuestion.getOptionBHi());
            hindQuestion.setOptionC(nextQuestion.getOptionCHi());
            hindQuestion.setOptionD(nextQuestion.getOptionDHi());
            hindQuestion.setId(nextQuestion.getId());
            nextQuestion = hindQuestion;
        }

        // Update session with new index and answer list
        session.setAttribute("answerList", answerList);
        session.setAttribute("currentIndex", questionIndex);

        // Return next question
        return ResponseEntity.ok(nextQuestion);
    }

    @GetMapping("/submit")
    public ModelAndView submitTest(HttpSession session) {
        Long endTime = System.currentTimeMillis();
        Long startTime = (Long) session.getAttribute("startTime");
        Exam examData = (Exam) session.getAttribute("examInfo");
        List<Answer> answersList = (List<Answer>) session.getAttribute("answerList");
        List<Long> questionIds = (List<Long>) session.getAttribute("questionIds");

        int totalQuestion = questionIds.size();
        int attemptedQuestionSize = answersList.size();
        int skippedQuestions = totalQuestion - attemptedQuestionSize;

        Map<String, Integer> result = Utils.countCorrectAndWrong(answersList);
        int correctAnswer = result.get("correct");
        int incorrectAnswer = result.get("wrong");

        int totalAnswers = correctAnswer + incorrectAnswer;
        int percentage = (totalAnswers > 0) ? (correctAnswer * 100) / totalAnswers : 0;
        String passFailResult = (percentage >= 50) ? "Pass" : "Fail";

        Long totalExamTime = Utils.getDifferenceInMinutes(startTime, endTime);
        String examTitle = examData.getExamTitle();
        String examDesc = examData.getExamDescription();
        String examUrl = examData.getUrl();

        ModelAndView mv = new ModelAndView("resultPage");

        mv.addObject("totalQuestions", totalQuestion);
        mv.addObject("attemptedQuestions", attemptedQuestionSize);
        mv.addObject("skippedQuestion", skippedQuestions);
        mv.addObject("correctAnswers", correctAnswer);
        mv.addObject("incorrectAnswers", incorrectAnswer);
        mv.addObject("percentage", percentage);
        mv.addObject("resultStatus", passFailResult);
        mv.addObject("totalExamTime", totalExamTime);
        mv.addObject("examTitle", examTitle);
        mv.addObject("examDescription", examDesc);
        mv.addObject("examUrl", examUrl);
        mv.addObject("answerList", answersList);
        mv.addObject("startTimeFormatted", Utils.getFormattedTime(startTime));
        mv.addObject("endTimeFormatted", Utils.getFormattedTime(endTime));

        session.invalidate();
        return mv;
    }

    @GetMapping("/prevQuestion")
    public Question prevQuestion(HttpSession session) {
        String lang = (String) session.getAttribute("lang");
        List<Answer> answerList = (List<Answer>) session.getAttribute("answerList");
        List<Long> questionIds = (List<Long>) session.getAttribute("questionIds");
        int questionIndex = ((Integer) session.getAttribute("currentIndex"));
        answerList.remove(questionIndex - 1);
        // prepare question
        Long prevQuestionId = questionIds.get(questionIndex - 1);
        Question prevQuestion = this.questionService.getQuestionById(prevQuestionId);

        if (lang.equals("hi")) {
            Question hindQuestion = new Question();
            hindQuestion.setQuestionTitle(prevQuestion.getQuestionTitleHi());
            hindQuestion.setCorrectAnswer(prevQuestion.getCorrectAnswerHi());
            hindQuestion.setOptionA(prevQuestion.getOptionAHi());
            hindQuestion.setOptionB(prevQuestion.getOptionBHi());
            hindQuestion.setOptionC(prevQuestion.getOptionCHi());
            hindQuestion.setOptionD(prevQuestion.getOptionDHi());
            hindQuestion.setId(prevQuestion.getId());
            prevQuestion = hindQuestion;
        }
        questionIndex--;
        // Update session with new index and answer list
        session.setAttribute("answerList", answerList);
        session.setAttribute("currentIndex", questionIndex);
        return prevQuestion;

    }

    @GetMapping("/changeLanguage")
    public RedirectView changeLanguage(HttpSession session, @RequestParam("examName") String examName) {
        String currentLang = (String) session.getAttribute("lang");
        session.setAttribute("lang", "hi".equals(currentLang) ? "en" : "hi");
        return new RedirectView("/mock/test/" + examName);
    }

    @PostMapping("/feedback")
    public ModelAndView submitFeedback(@ModelAttribute Feedback feedback) {
        feedbackFormRepo.save(feedback);
        Exam examData = this.examService.getExamByName(feedback.getExamName());
        ModelAndView mv = new ModelAndView("thankyou");
        mv.addObject("examUrl", examData.getUrl());
        mv.addObject("examName", examData.getExamTitle());
        return mv;
    }

    @GetMapping("/reset")
    public RedirectView resetSession(@RequestParam("examName") String examTitle, HttpSession session) {
        session.invalidate();
        String redirectUrl = "/mock/test/" + examTitle;
        return new RedirectView(redirectUrl);
    }

}
