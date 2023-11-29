# FileUploadGit
스프링부트 API 학습용 - 파일 업로드



## ★해야할일★
    
    //리팩토링 첫날(11월 24일)
    //dao 부분 멀티스레드 안정성 확보(sqlSessionTemplate로 처리)
    //컨트롤러 json반환 코드 변경(filedtoFactory 생성, ResultMessage클래스 싱글톤으로 구현)
    //jwt에서 인증 인가 관련 코드 변경 (TokenProvider -> TokenFactory, TokenValidate로 분할 구현)
    //-> 나머지 들도 진행해야 함.

    //리팩토링 미 대상 클래스
    //config클래스, enum클래스, 정규식 클래스, 이벤트 리스너(부트 업로드 시)클래스

    //리팩토링 중에 약간 수정을 할 수 있는 클래스
    //DTO, VO클래스, Anno, Dao클래스, AOP클래스

    //핵심 리팩토링 대상 클래스
    //컨트롤러, 서비스, 서비스 구현체, 파일 추출과 관련된 클래스들