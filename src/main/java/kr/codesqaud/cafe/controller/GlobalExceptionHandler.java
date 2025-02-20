package kr.codesqaud.cafe.controller;

import javax.servlet.http.HttpSession;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import kr.codesqaud.cafe.exceptions.ArticleInfoException;
import kr.codesqaud.cafe.exceptions.UserInfoException;
import kr.codesqaud.cafe.utils.SessionUtils;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = UserInfoException.class)
    public String wrongPasswordException(UserInfoException e, Model model, HttpSession httpSession,
            RedirectAttributes redirectAttributes) {

        if (e.getErrorCode() == UserInfoException.WRONG_MODIFICATION_PASSWORD_CODE) {
            String id = SessionUtils.getSessionId(httpSession);

            redirectAttributes.addFlashAttribute(id);
            redirectAttributes.addFlashAttribute(e.getMessage());
            return "redirect:/users/{id}/form";
        }
        model.addAttribute("error", e.getMessage());
        return "user/login_failed";
    }

    @ExceptionHandler(value = ArticleInfoException.class)
    public String idNotMatchingException(ArticleInfoException e, Model model) {
        model.addAttribute("error", e.getMessage());

        return "exception/error";
    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    public String noneMatchingException(IllegalArgumentException e, Model model) {
        model.addAttribute("error", e.getMessage());

        return "exception/error";
    }
}
