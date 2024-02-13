package com.example.springgame;

import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @PostMapping("/login")
    public String login(@RequestParam String email,
                        @RequestParam String password,
                        HttpSession session,
                        Model model) {
        if (email.isEmpty() && password.isEmpty()) {
            model.addAttribute("error", "Please enter Email and Password");
            return "index";
        } else if (email.isEmpty()) {
            model.addAttribute("error", "Please enter Email");
            return "index";
        } else if (password.isEmpty()) {
            model.addAttribute("error", "Please enter Password");
            return "index";
        }

        User user = userRepository.findByEmail(email);
        if (user != null && user.getPassword().equals(password)) {
            session.setAttribute("user", user);
            return "redirect:/dashboard";
        } else {
            model.addAttribute("error", "Invalid email or password");
            return "index";
        }
    }



    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User user = (User) session.getAttribute("user");
        if (user != null) {
            model.addAttribute("user", user);
            return "dashboard";
        } else {
            return "redirect:/";
        }
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.removeAttribute("user");
        return "redirect:/";
    }

    @PostMapping("/signup")
    public String signup(@RequestParam String name,
                         @RequestParam String email,
                         @RequestParam String password,
                         Model model) {

        // Validate inputs (add more validation as needed)
        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            model.addAttribute("error", "All fields must be filled");
            return "signup";
        }

        // Check if the email is already registered
        if (userRepository.findByEmail(email) != null) {
            model.addAttribute("error", "Email is already registered");
            return "signup";
        }

        // Create a new user
        User newUser = new User();
        newUser.setname(name);
        newUser.setEmail(email);
        newUser.setPassword(password);

        // Save the user to the database
        userRepository.save(newUser);

        return "redirect:/"; // Redirect to the home page or login page
    }
}
