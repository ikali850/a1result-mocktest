package in.a1result.mockexam.controller;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

import jakarta.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@RestController
public class TestController {

    @GetMapping("/start/test/{id}")
    public ModelAndView startTest(@PathVariable("id") Long id, HttpSession session) {
        System.out.println("got called");

        ModelAndView mv = new ModelAndView("testPage");

        // Dummy Exam Data
        mv.addObject("examName", "General Knowledge Mock Test");
        mv.addObject("examDescription", "This test will check your basic GK skills.");
        mv.addObject("examDuration", "75 Minutes");
        mv.addObject("totalQuestions", 5);

        // Dummy Questions List
        List<Map<String, String>> questions = new ArrayList<>();

        Map<String, String> q1 = Map.of(
                "id", "101",
                "text", "What is the capital of India?",
                "optionA", "Mumbai",
                "optionB", "New Delhi",
                "optionC", "Chennai",
                "optionD", "Kolkata");
        Map<String, String> q2 = Map.of(
                "id", "102",
                "text", "Which planet is known as the Red Planet?",
                "optionA", "Earth",
                "optionB", "Mars",
                "optionC", "Venus",
                "optionD", "Jupiter");
        Map<String, String> q3 = Map.of(
                "id", "103",
                "text", "Who wrote the Indian National Anthem?",
                "optionA", "Bankim Chandra Chatterjee",
                "optionB", "Rabindranath Tagore",
                "optionC", "Subhash Chandra Bose",
                "optionD", "Mahatma Gandhi");
        Map<String, String> q4 = Map.of(
                "id", "104",
                "text", "Which gas is most abundant in the Earth's atmosphere?",
                "optionA", "Oxygen",
                "optionB", "Nitrogen",
                "optionC", "Carbon Dioxide",
                "optionD", "Helium");
        Map<String, String> q5 = Map.of(
                "id", "105",
                "text", "Which is the largest continent?",
                "optionA", "Africa",
                "optionB", "Asia",
                "optionC", "Europe",
                "optionD", "Australia");

        questions.add(q1);
        questions.add(q2);
        questions.add(q3);
        questions.add(q4);
        questions.add(q5);

        // Save questions in session
        session.setAttribute("questions", questions);

        // Redirect to first question
        return new ModelAndView("redirect:/test/question/0");
    }

    @GetMapping("/test/question/{number}")
    public ModelAndView getQuestion(@PathVariable("number") int number, HttpSession session) {
        ModelAndView mv = new ModelAndView("testPage");

        List<Map<String, String>> questions = (List<Map<String, String>>) session.getAttribute("questions");

        if (questions == null || number < 0 || number >= questions.size()) {
            return new ModelAndView("redirect:/start/test/1"); // restart test
        }

        Map<String, String> currentQuestion = questions.get(number);

        // Add same dummy exam data
        mv.addObject("examName", "General Knowledge Mock Test");
        mv.addObject("examDescription", "This test will check your basic GK skills.");
        mv.addObject("examDuration", "75 Minutes");
        mv.addObject("totalQuestions", questions.size());

        mv.addObject("question", currentQuestion);
        mv.addObject("number", number + 1); // Human-readable numbering
        mv.addObject("currentIndex", number); // Actual index

        return mv;
    }

    @GetMapping("/test/submit")
    public ModelAndView submitTest(HttpSession session) {
        ModelAndView mv = new ModelAndView("resultPage");

        // Fetch questions and user's answers
        List<Map<String, String>> questions = (List<Map<String, String>>) session.getAttribute("questions");
        Map<Integer, String> userAnswers = (Map<Integer, String>) session.getAttribute("userAnswers");

        if (questions == null || userAnswers == null) {
            return new ModelAndView("redirect:/start/test/1");
        }

        int correct = 0;
        int wrong = 0;
        int unattempted = 0;

        List<Map<String, Object>> detailedResults = new ArrayList<>();

        for (int i = 0; i < questions.size(); i++) {
            Map<String, String> question = questions.get(i);
            String correctAnswer = "B"; // **Dummy correct answer B for all** (you can later change it)

            String userAnswer = userAnswers.get(i);
            String status;

            if (userAnswer == null) {
                status = "Unattempted";
                unattempted++;
            } else if (userAnswer.equals(correctAnswer)) {
                status = "Correct";
                correct++;
            } else {
                status = "Wrong";
                wrong++;
            }

            Map<String, Object> result = new HashMap<>();
            result.put("questionText", question.get("text"));
            result.put("userAnswer", userAnswer);
            result.put("correctAnswer", correctAnswer);
            result.put("status", status);
            result.put("options", List.of(
                    question.get("optionA"),
                    question.get("optionB"),
                    question.get("optionC"),
                    question.get("optionD")));

            detailedResults.add(result);
        }

        mv.addObject("examName", "General Knowledge Mock Test");
        mv.addObject("examDescription", "This test will check your basic GK skills.");
        mv.addObject("totalQuestions", questions.size());
        mv.addObject("correct", correct);
        mv.addObject("wrong", wrong);
        mv.addObject("unattempted", unattempted);
        mv.addObject("detailedResults", detailedResults);

        // Calculate Pass/Fail (Dummy: 50% pass criteria)
        String resultStatus = (correct >= (questions.size() / 2)) ? "Pass" : "Fail";
        mv.addObject("resultStatus", resultStatus);

        return mv;
    }

    @GetMapping("/test/answer")
    public String saveAnswer(@RequestParam("index") int index,
            @RequestParam("option") String option,
            HttpSession session) {

        Map<Integer, String> userAnswers = (Map<Integer, String>) session.getAttribute("userAnswers");

        if (userAnswers == null) {
            userAnswers = new HashMap<>();
        }

        userAnswers.put(index, option);

        session.setAttribute("userAnswers", userAnswers);

        return "Saved"; // simple response
    }

}
