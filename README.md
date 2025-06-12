# SPRING ADVANCED

## 1. [문제 인식 및 정의 -1]
   AOP - Interceptor를 우회하거나 필터에서 누락하여 authUser가 null인 경우, 이에 대한 로그 기록 부족.

## 2. [해결 방안]
   ### 2-1. [의사결정 과정]
   안전성을 위해 기존의 null인 경우 UNKNOWN으로 처리하는 코드도 함께 사용.
   ### 2-2. [해결 과정]
   authUser가 null인 경우 예외처리 진행. 로그 작성.

## 3. [해결 완료]
   ### 3-1. [회고]
      authUser가 null인 경우 로그가 남도록 변경됨.
   ### 3-2. [전후 데이터 비교]
   ```
   String userId = authUser != null ? String.valueOf(authUser.getId()) : "UNKNOWN";
   ->
   if (authUser == null) {
   logger.warn("Admin API Request, authUser not found in request attribute. Check filter chain or Interceptor.");
   }

   String userId = authUser != null ? String.valueOf(authUser.getId()) : "UNKNOWN";
   ```
----------------------------------------------------------------------------------------------------

## 1. [문제 인식 및 정의 -2]
   WenConfig - AuthUserArgumentResolver를 new를 사용하여 직접 생성하는 방식을 사용.

## 2. [해결 방안]
   ### 2-1. [의사결정 과정]
   결합도가 높고 유지보수성, 테스트 용이성에서 안좋은 성능.
   ### 2-2. [해결 과정]
   AuthUserArgumentResolver를 Component 어노테이션 처리 후, 생성자 DI를 통해 사용.

## 3. [해결 완료]
   ### 3-1. [회고]
   결합도가 낮아지고 유지보수성, 테스트 용이성에서 더 좋은 성능.
   구현체 변경 시 WebConfig 수정 필요 → 필요 없음(빈만 교체하면 됨)
   ### 3-2. [전후 데이터 비교]
   ```
   @Override
   public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
   resolvers.add(new AuthUserArgumentResolver());
   }
   ->
   private final AuthUserArgumentResolver authUserArgumentResolver;

   @Override
   public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
   resolvers.add(authUserArgumentResolver);
   }
   ```