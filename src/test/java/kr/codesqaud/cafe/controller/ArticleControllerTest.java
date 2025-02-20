package kr.codesqaud.cafe.controller;

import static org.mockito.BDDMockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Collections;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import kr.codesqaud.cafe.model.ArticleDto;
import kr.codesqaud.cafe.repository.ArticleRepository;

@ContextConfiguration(classes = TestConfig.class)
@WebMvcTest(ArticleController.class)
class ArticleControllerTest {
    @Autowired
    private MockMvc mvc;
    @MockBean
    private ArticleRepository articleRepository;

    private MockHttpSession mockHttpSession;

    @BeforeEach
    private void setup() {
        mockHttpSession = new MockHttpSession();
        mockHttpSession.setAttribute("sessionedUser", "poro");
    }

    @Test
    @DisplayName("게시물 게시 요청을 하면 postQna를 호출하고 홈으로 리다이렉트한다.")
    public void articlePostTest() throws
            Exception {
        MultiValueMap<String, String> paramsMap = new LinkedMultiValueMap<>();
        paramsMap.add("title", "제목");
        paramsMap.add("contents", "내용");

        mvc.perform(post("/qna/create").params(paramsMap).session(mockHttpSession))
                .andExpect(status().is3xxRedirection())
                .andExpect(view().name("redirect:/"));
    }

    @Test
    @DisplayName("홈에서는 글 목록을 모델에 저장해서 보여준다.")
    public void articleListTest() throws Exception {
        ArticleDto article = new ArticleDto(1,"poro","제목","내용", "1995-07-18 00:00:00");
        given(articleRepository.getArticleList()).willReturn(Collections.singletonList(article));

        mvc.perform(get("/"))
                .andExpect(model().attributeExists("article"))
                .andExpect(model().attribute("article", Collections.singletonList(article)))
                .andExpect(view().name("index"))
                .andExpect(status().isOk());

    }

    @Test
    @DisplayName("게시글을 상세 조회하면 모델에 ID가 일치하는 Article을 담아 게시글 상세보기 뷰를 표시한다.")
    public void articleDetailsTest() throws Exception {
        ArticleDto article = new ArticleDto(1,"poro","제목","내용", "1995-07-18 00:00:00");
        given(articleRepository.findById(1)).willReturn(article);

        mvc.perform(get("/qna/1").session(mockHttpSession))
                .andExpect(model().attributeExists("article"))
                .andExpect(model().attribute("article", article))
                .andExpect(view().name("qna/show"))
                .andExpect(status().isOk());
    }
}
