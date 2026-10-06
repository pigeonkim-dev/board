package com.pigeonkim.board.web.security;

import com.pigeonkim.board.domain.MemberRole;
import com.pigeonkim.board.domain.entity.Member;
import com.pigeonkim.board.domain.entity.Profile;
import lombok.Getter;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.core.oidc.OidcIdToken;
import org.springframework.security.oauth2.core.oidc.OidcUserInfo;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;

import java.io.Serializable;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Getter
public class BoardOidcUser implements OidcUser, Serializable {
    private final OidcUser oidcUser;
    private final UUID publicId;
    private final String nickname;
    private final MemberRole role;
    private final boolean registered;

    private BoardOidcUser(OidcUser oidcUser, UUID publicId, String nickname,
                          MemberRole role, boolean registered) {
        this.oidcUser = oidcUser;
        this.publicId = publicId;
        this.nickname = nickname;
        this.role = role;
        this.registered = registered;
    }

    public static BoardOidcUser ofRegistered(OidcUser oidcUser, Member member, Profile profile) {
        return new BoardOidcUser(oidcUser, member.getPublicId(), profile.getNickname(), member.getRole(), true);
    }

    public static BoardOidcUser ofUnregistered(OidcUser oidcUser) {
        UUID publicId = UUID.fromString(oidcUser.getSubject());

        return new BoardOidcUser(oidcUser, publicId, null, null, false);
    }

    @Override
    public Map<String, Object> getClaims() {
        return oidcUser.getClaims();
    }

    @Override
    public @Nullable OidcUserInfo getUserInfo() {
        return oidcUser.getUserInfo();
    }

    @Override
    public OidcIdToken getIdToken() {
        return oidcUser.getIdToken();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return oidcUser.getAttributes();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {

        if (registered) {
            return List.of(new SimpleGrantedAuthority("ROLE_" + role.name()));
        }

        return List.of();
    }

    @Override
    public String getName() {
        return oidcUser.getName();
    }
}
