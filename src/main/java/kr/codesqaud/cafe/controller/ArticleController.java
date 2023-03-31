package kr.codesqaud.cafe.controller;

import static kr.codesqaud.cafe.exceptions.ArticleInfoException.*;
import static kr.codesqaud.cafe.utils.SessionUtils.*;
import static kr.codesqaud.cafe.validator.AuthorValidator.*;

import java.time.LocalDateTime;

import javax.servlet.http.HttpSession;

import kr.codesqaud.cafe.exceptions.UserInfoException;
import kr.codesqaud.cafe.model.Reply;
import kr.codesqaud.cafe.repository.ArticleRepository;
import kr.codesqaud.cafe.repository.ReplyRepository;
import kr.codesqaud.cafe.repository.UserRepository;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import kr.codesqaud.cafe.exceptions.ArticleInfoException;
import kr.codesqaud.cafe.model.Article;

@Controller
public class ArticleController {
    private final ArticleRepository articleRepository;
    private final UserRepository userRepository;
    private final ReplyRepository replyRepository;

    public ArticleController(ArticleRepository articleRepository, UserRepository userRepository,
            ReplyRepository replyRepository) {
        this.articleRepository = articleRepository;
        this.userRepository = userRepository;
        this.replyRepository = replyRepository;
    }

    @PostMapping("/qna/create")
    public String articlePost(@RequestParam String title, @RequestParam String contents, HttpSession httpSession) {
        articleRepository.addArticle(
                new Article(getSessionId(httpSession), title, contents, LocalDateTime.now()));

        return "redirect:/";
    }

    @GetMapping("/")
    public String articleList(Model model) {
        model.addAttribute("article", articleRepository.getArticleList());

        return "index";
    }

    @GetMapping(value = "/qna/{id}")
    public String articleDetails(@PathVariable long id, Model model) throws ArticleInfoException {
        model.addAttribute("article", articleRepository.findById(id));
        model.addAttribute("reply", replyRepository.getReplyList(id));
        model.addAttribute("replyCount", replyRepository.getReplyList(id).size());

        return "qna/show";
    }

    @GetMapping(value = "/qna/{id}/form")
    public String articleModificationForm(@PathVariable long id, Model model, HttpSession httpSession)
            throws ArticleInfoException {
        String writer = articleRepository.findById(id).getWriter();
        //Session의 사용자와 글 작성자가 동일해야한다.
        validateAuthorization(getSessionId(httpSession).equals(writer), UNAUTHORIZED_MODIFICATION_MESSAGE,
                WRITER_NOT_MATCHING_CODE);
        model.addAttribute("id", id);

        return "qna/modification_form";
    }

    @PutMapping(value = "/qna/{id}/form")
    public String articleModify(@PathVariable long id, @RequestParam String title, @RequestParam String contents,
            HttpSession httpSession) throws ArticleInfoException {
        String writer = articleRepository.findById(id).getWriter();
        //SessionID와 글의 글쓴이가 같아야한다.
        validateAuthorization(getSessionId(httpSession).equals(writer), UNAUTHORIZED_MODIFICATION_MESSAGE,
                WRITER_NOT_MATCHING_CODE);
        articleRepository.modifyArticle(id, title, contents);

        return "redirect:/";
    }

    @DeleteMapping(value = "/qna/{articleId}")
    public String articleDelete(@PathVariable long articleId, HttpSession httpSession)
            throws ArticleInfoException {
        String writer = articleRepository.findById(articleId).getWriter();
        //Session의 사용자와 댓글 작성자가 동일해야한다.
        validateAuthorization(getSessionId(httpSession).equals(writer), UNAUTHORIZED_MODIFICATION_MESSAGE,
                WRITER_NOT_MATCHING_CODE);
        //댓글이 존재하는 경우 삭제 불가
        validateAuthorization(replyRepository.canDeleteById(articleId, writer), REQUIRE_ON_COMMENT_DELETE_MESSAGE,
                REQUIRE_ON_COMMENT_DELETE_CODE);
        articleRepository.deleteArticle(articleId);
        return "redirect:/";
    }

    /*
    @DeleteMapping(value = "/qna/{articleId}/reply/{replyId}")
    public String deleteReply(@PathVariable long articleId, @PathVariable long replyId, HttpSession httpSession)
            throws ArticleInfoException {
        String replyWriter = replyRepository.findById(replyId, articleId).getWriter();
        //Session의 사용자와 댓글 작성자가 동일해야한다.
        validateAuthorization(getSessionId(httpSession).equals(replyWriter), UNAUTHORIZED_DELETE_MESSAGE,
                WRITER_NOT_MATCHING_CODE);

        replyRepository.deleteReply(articleId, replyId);
        return "redirect:/qna/{articleId}";
    }*/

}
