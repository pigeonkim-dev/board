package com.pigeonkim.board.web.security;

import com.pigeonkim.board.component.ProfileFinder;
import com.pigeonkim.board.domain.entity.Member;
import com.pigeonkim.board.domain.entity.Profile;
import com.pigeonkim.board.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BoardOidcUserService extends OidcUserService {
    private final MemberRepository memberRepository;
    private final ProfileFinder profileFinder;

    public BoardOidcUser reload(BoardOidcUser current){
        UUID publicId = current.getPublicId();
        Optional<Member> member = memberRepository.findByPublicId(publicId);

        if (member.isEmpty()){
            return BoardOidcUser.ofUnregistered(current);
        }

        Profile profile = profileFinder.findByMemberPublicId(publicId);

        return BoardOidcUser.ofRegistered(current, member.get(), profile);
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(userRequest);

        UUID publicId = UUID.fromString(oidcUser.getSubject());

        Optional<Member> member = memberRepository.findByPublicId(publicId);

        if (member.isEmpty()){
            return BoardOidcUser.ofUnregistered(oidcUser);
        }

        Profile profile = profileFinder.findByMemberPublicId(publicId);

        return BoardOidcUser.ofRegistered(oidcUser, member.get(), profile);
    }
}
