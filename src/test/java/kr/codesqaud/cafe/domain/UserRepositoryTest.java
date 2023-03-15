package kr.codesqaud.cafe.domain;



import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import kr.codesqaud.cafe.user.User;


class UserRepositoryTest {
    private static UserRepository userRepository = new MemoryUserRepository();

    @Test
    @DisplayName("ID가 일치하는 유저를 찾아서 반환한다.")
    void findById() {
        //given

        //when
        userRepository.addUser(new User("crong","123","krong","crong@naver.com"));

        User user = userRepository.findById("crong").get();
        //then
        assertThat(user.getName()).isEqualTo("krong");
        System.out.println(userRepository.findById("poro"));
        System.out.println(userRepository.findById("crong"));
    }

    @Test
    @DisplayName("유저를 추가한다.")
    void addUser() {
        //given

        //when
        userRepository.addUser(new User("poro","123","gwonwoo","ngw7617@naver.com"));
        //then
        assertThat(userRepository.findById("poro").get().getName()).isEqualTo("gwonwoo");
        System.out.println(userRepository.findById("poro"));
        System.out.println(userRepository.findById("crong"));
    }
}
