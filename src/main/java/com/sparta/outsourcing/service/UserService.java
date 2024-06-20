package com.sparta.outsourcing.service;


import com.sparta.outsourcing.dto.ProfileDto;
import com.sparta.outsourcing.dto.UserDto;
import com.sparta.outsourcing.entity.User;
import com.sparta.outsourcing.enums.UserRoleEnum;
import com.sparta.outsourcing.repository.UserRepository;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class UserService {
    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final UserRepository userRepository;
    //주문 리포지토리
    public ResponseEntity<String> signUp(UserDto userDto, UserRoleEnum userRoleEnum) {
        User user = new User(
                userDto.getUsername(),
                bCryptPasswordEncoder.encode(userDto.getPassword()),
                userDto.getNickname(),
                userDto.getUserinfo(),
                userRoleEnum
        );
        if(userRepository.findByUsername(userDto.getUsername()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("중복된 아이디 입니다.");
        }
        
        userRepository.save(user);
        return ResponseEntity.status(HttpStatus.OK).body("가입 완료");
    }


    public ResponseEntity<ProfileDto> getProfile(Long userId) {
        Optional<User> user = userRepository.findById(userId);
        ProfileDto profileDto = new ProfileDto(
                user.get().getNickname(),
                user.get().getUserinfo()
        );
        return ResponseEntity.status(HttpStatus.OK).body(profileDto);
    }


    @Transactional
    public ResponseEntity<String> updateProfile(Long userId, User user, ProfileDto profileDto) {
        if(!validateUser(userId,user.getId())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("다른 유저를 수정할 수 없습니다.");
        }
        if(validatePassword(user,profileDto.getPassword())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("최근 3번안에 사용한 비밀번호는 사용할 수 없습니다.");
        }
        if(bCryptPasswordEncoder.matches(user.getPassword(),profileDto.getPassword())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("현제 같은 비밀번호는 변경할수 없습니다.");
        }
        ProfileDto newProfileDto = new ProfileDto(
                profileDto.getNickname(), profileDto.getUserinfo(), bCryptPasswordEncoder.encode(profileDto.getPassword())
        );
        Optional<User> originUser = userRepository.findById(userId);
        originUser.get().updateProfile(newProfileDto);
        return ResponseEntity.status(HttpStatus.OK).body("수정완료");
    }


    public ResponseEntity<String> signOut(Long userId, User user) {
        if(!validateUser(userId,user.getId())){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("다른 유저를 탈퇴할 수 없습니다.");
        }
        userRepository.findById(userId).get().deleteUser();
        return ResponseEntity.status(HttpStatus.OK).body("탈퇴 완료");
    }

    // 아이디 일치하는지 검증
    private boolean validateUser(Long userId, Long originId) {
        if (!Objects.equals(userId, originId)) {
            return false;
        }
        return true;
    }
    //이전 변경한 비밀번호 검증
    private  boolean validatePassword(User user, String password) {
        List<String> userPassword = user.getDeniedPassword();

        for(int i=0; i<userPassword.size(); i++){
            if(bCryptPasswordEncoder.matches(userPassword.get(i),password)){
                return true;
            }
        }
        return false;
    }
}
