package app.dev.ussapp.controller;

import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import app.dev.ussapp.model.Professor;
import app.dev.ussapp.model.Student;
import app.dev.ussapp.model.forms.LoginForm;
import app.dev.ussapp.model.forms.ProfessorAccountForm;
import app.dev.ussapp.model.forms.StudentAccountForm;
import app.dev.ussapp.model.repos.ProfessorRepository;
import app.dev.ussapp.model.repos.StudentRepository;

@Controller
public class IndexController {

    @Autowired
    StudentRepository studentRepo;

    @Autowired
    ProfessorRepository profRepo;

    @RequestMapping("/")
    public String home(HttpSession session) {
        return checkSession(session, "/index");
    }
    
    @RequestMapping("/about")
    public String about() {
        return "about";
    }

    @RequestMapping("/signin")
    public String signIn(HttpSession session) {
        return checkSession(session, "signin");
    }

    @RequestMapping("/login")
    public String login(HttpSession session, @ModelAttribute(name = "msg") String msg, Model model) {
        System.out.println(msg);

        String sessChk = checkSession(session, "login");

        if (sessChk.equals("login")) {
            if (msg != null && !msg.equals("")) {
                model.addAttribute("msg", msg);
            }

            model.addAttribute("form", new LoginForm());

            return "login";
        }

        return sessChk;
    }

    @PostMapping("/login-user")
    public String loginUser(HttpSession session, LoginForm form, RedirectAttributes redirAttr) {
        List<Student> studentResults = studentRepo.findAllByUsername(form.getUsername());
        List<Professor> profResults = profRepo.findAllByUsername(form.getUsername());

        if (!studentResults.isEmpty()) {
            Student student = studentResults.get(0);

            if (student.isValidPassword(form.getPassword())) {
                session.setAttribute("student", student);

                return "redirect:/student-home";
            }
        } else if (!profResults.isEmpty()) {
            Professor professor = profResults.get(0);

            if (professor.isValidPassword(form.getPassword())) {
                session.setAttribute("professor", professor);

                return "redirect:/professor-home";
            }
        }

        redirAttr.addFlashAttribute("msg", "Invalid username/password");

        return "redirect:/login";
    }

    @RequestMapping("/create-account")
    public String studentCreateAccount(HttpSession session, @RequestParam(value = "type") String type) {
        if (type == null || (!type.equals("student") && !type.equals("professor"))) {
            return "redirect:/";
        }

        String sessChk = checkSession(session, "create");

        if (sessChk.equals("create")) {
            if (type.equals("student")) {
                return "redirect:/student-account-form";
            } else {
                return "redirect:/professor-account-form";
            }
        }

        return sessChk;
    }

    @RequestMapping("/student-account-form")
    public String studentAccountForm(Model model) {
        model.addAttribute("form", new StudentAccountForm());

        return "student/create-form";
    }

    @RequestMapping("/professor-account-form")
    public String professorAccountForm(Model model) {
        model.addAttribute("form", new ProfessorAccountForm());

        return "professor/create-form";
    }

    @RequestMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();

        return "redirect:/";
    }

    private String checkSession(HttpSession session, String fallback) {
        if (session.getAttribute("student") != null) {
            return "redirect:/student-home";
        }

        if (session.getAttribute("professor") != null) {
            return "redirect:/professor-home";
        }

        return fallback;
    }

}
