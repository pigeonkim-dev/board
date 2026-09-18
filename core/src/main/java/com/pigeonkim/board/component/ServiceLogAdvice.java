package com.pigeonkim.board.component;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import java.util.Arrays;

/**
 * 서비스 메서드가 불릴 때마다 인자를 기록한다.
 *
 * 왜 여기서 하는가:
 * 예외를 던지는 자리마다 로그를 쓰면 스무 곳에 로그가 스무 줄 생기고,
 * 나중에 누가 예외만 옮기고 로그는 두고 가면 짝이 어긋난다.
 * 한 곳에 적고 끼워 넣는다.
 *
 * 왜 서비스인가:
 * 호출 한 건이 사용자 행동 한 건이고, 인자가 도메인의 말이라 의도가 보인다.
 * 리포지토리에 걸면 호출이 3~5배 많고 인자가 id·Pageable 이라 의도가 안 보이며,
 * 이미 org.hibernate.SQL 로그와 겹친다.
 */
@Slf4j
@Aspect
@Component
public class ServiceLogAdvice {

    /**
     * execution(...) 이 "어디에 끼울지"를 적은 것이다. 이것을 포인트컷이라 한다.
     *
     *   *                                    반환 타입은 무엇이든
     *   com.pigeonkim.board.service..*        이 패키지와 그 아래 모든 클래스
     *                                         점이 둘(..)이면 하위 패키지까지 포함이다
     *   .*(..)                                메서드 이름은 무엇이든, 인자도 몇 개든
     *
     * @Around 는 "앞뒤를 감싼다"는 뜻이다. 진짜 메서드를 부를지 말지도 여기서 정한다.
     * 그래서 proceed() 를 직접 불러야 한다 — 안 부르면 원래 메서드가 실행되지 않는다.
     */
    @Around("execution(* com.pigeonkim.board.service..*(..))")
    public Object logServiceCall(ProceedingJoinPoint joinPoint) throws Throwable {

        // getSignature() 는 "어느 클래스의 어느 메서드인가"를 담고 있다.
        // toShortString() 이면 PostService.updatePost(..) 정도로 짧게 나온다.
        String method = joinPoint.getSignature().toShortString();

        // getArgs() 가 실제로 넘어온 인자들이다.
        // 배열을 그냥 찍으면 [Ljava.lang.Object;@1a2b3c 같은 게 나오므로 Arrays 로 편다.
        // 각 인자의 toString() 이 불린다 — 그래서 가리는 일은 인자 쪽이 맡는다.
        String args = Arrays.toString(joinPoint.getArgs());

        // 성공한 호출은 개발 중에만 보면 된다. 운영 로그를 채울 이유가 없다.
        log.debug("{} 호출 {}", method, args);

        try {
            // 이 한 줄이 진짜 메서드다. 앞은 여기 오기 전, 뒤는 여기 다녀온 뒤.
            return joinPoint.proceed();

        } catch (Throwable e) {
            // 실패한 호출의 인자는 재현에 반드시 필요하다. 그래서 여기서 다시 찍는다.
            // 예외 객체를 마지막 인자로 넘기면 스택트레이스까지 같이 남는다.
            log.warn("{} 실패 {} : {}", method, args, e.toString());
            throw e;   // 삼키지 않는다. 기록만 하고 그대로 올려보낸다
        }
    }
}
