package com.example.springgame;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class WordController {
    @Autowired
    private WordRepository wordRepository;
    @Autowired
    private UserService userService;

    @GetMapping("/game")
    public String showForm(Model model) {
        model.addAttribute("levels", new String[]{"Easy", "Medium", "Hard"});
        model.addAttribute("selectedLevel", "");
        return "wordForm";
    }

    @PostMapping("/word")
    public String getWord(@ModelAttribute("selectedLevel") String selectedLevel,
                          Model model, HttpSession session) {
        Word wordObj = wordRepository.findRandomWordByLevel(selectedLevel);
        model.addAttribute("word", wordObj);
        session.setAttribute("word", wordObj);
        session.setAttribute("chancesLeft", 5); // Set initial chances
        return "redirect:/showWord";
    }

    @GetMapping("/showWord")
    public String showWord(HttpSession session, Model model) {
        Word wordarray = (Word) session.getAttribute("word");
        model.addAttribute("GivenHints", wordarray.getHints());
        return "wordInput";
    }

    @PostMapping("/getWord")
    public String login(@RequestParam String word, HttpSession session, Model model) {
        Word wordObj = (Word) session.getAttribute("word");
        int tries = (int) session.getAttribute("chancesLeft");
        model.addAttribute("GivenHints", wordObj.getHints());

        if (word != null && wordObj.getWord().equalsIgnoreCase(word)) {
            // Update the score when the user wins
            User user = (User) session.getAttribute("user");
            if (user != null) {
                int currentScore = user.getScore();
                user.setScore(currentScore + 1);
                userService.updateScore(user.getId(), user.getScore());
            }

            model.addAttribute("message", "Congratulations! You win");
            session.removeAttribute("chancesLeft");
            return "wordInput";
        } else {
            tries--;
            if (tries == 0) {
                model.addAttribute("message", "Sorry! You Lose.");
                session.removeAttribute("chancesLeft");
            } else {
                model.addAttribute("message", "Please Try again. Left tries: " + tries);
                session.setAttribute("chancesLeft", tries);
            }
            return "wordInput";
        }
    }

    @GetMapping("/Score/{userId}/{newScore}")
    @ResponseBody()
    public String updateScore(@PathVariable Long userId, @PathVariable int newScore) {
        userService.updateScore(userId, newScore);
        return "Score updated successfully";
    }

    @PostMapping("/changeWord")
    public String changeWord(@RequestParam String word,
                             @RequestParam String hints,
                             @RequestParam String level,
                             Model model) {

        // Validate inputs (add more validation as needed)
        if (word.isEmpty() || hints.isEmpty() || level.isEmpty()) {
            model.addAttribute("error", "All fields must be filled");
            return "changeWord"; // Change this to the appropriate view for the change page
        }

        // Perform the update in the database
        Word existingWord = wordRepository.findByWord(word);
        if (existingWord != null) {
            existingWord.setHints(hints);
            existingWord.setLevel(level);
            wordRepository.save(existingWord);
            return "redirect:/game"; // Redirect to the game page or a success page
        } else {
            model.addAttribute("error", "Word not found");
            return "changeWord"; // Change this to the appropriate view for the change page
        }
    }
}
