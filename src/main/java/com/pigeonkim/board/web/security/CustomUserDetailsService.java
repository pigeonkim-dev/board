package com.pigeonkim.board.web.security;

import com.pigeonkim.board.component.ProfileFinder;
import com.pigeonkim.board.domain.entity.Member;
import com.pigeonkim.board.domain.entity.Profile;
import com.pigeonkim.board.repository.MemberRepository;
import com.pigeonkim.board.repository.ProfileRepository;
import lombok.AllArgsConstructor;
import org.hibernate.query.criteria.JpaRoot;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {
    private final MemberRepository memberRepository;
    private final ProfileFinder profileFinder;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("존재하지 않는 이메일입니다."));

        Profile profile = profileFinder.findByMemberEmail(email);

        return new CustomUserDetails(member, profile);  // ← User.builder() 대신 CustomUserDetails
    }
}
