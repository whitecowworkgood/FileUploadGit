# FileUploadGit
스프링부트 API 학습용 - 파일 업로드



## ★해야할일★
    
    //리팩토링 첫날(11월 24일)
    //dao 부분 멀티스레드 안정성 확보(sqlSessionTemplate로 처리)
    //컨트롤러 json반환 코드 변경(fileDtoFactory 생성, ResultMessage클래스 싱글톤으로 구현)
    //jwt에서 인증 인가 관련 코드 변경 (TokenProvider -> TokenFactory, TokenValidate로 분할 구현)

    //리팩토링 미 대상 클래스
    //config클래스, enum클래스, 정규식 클래스, 이벤트 리스너(부트 업로드 시)클래스

    //리팩토링 중에 약간 수정을 할 수 있는 클래스
    //DTO, VO클래스, Anno, Dao클래스, AOP클래스

    //핵심 리팩토링 대상 클래스
    //컨트롤러, 서비스, 서비스 구현체, 파일 추출과 관련된 클래스들
    // 파일추출, 암호화관련 로직을 중점으로 변경 후, 서비스랑을 수정

    //12월 6일 리팩토링 진행상황
    //rsa와 aes, encryptService 리팩토링
    //RSA와 AES는 코드 단순화 과정을 거침.
    //encryptService는 메서드 분할 과정을 거침

    //12월 7일 리팩토링 진행상황
    //기본 중의 기본 switch문에 break를 안넣는 실수를 이제서야 발견함! -> doc문서 기준 500~600ms가 걸렸던게 200~300ms로 줄어들었음
    //parserOleNativeEntry의 코드를 바이트 찾아서 연산하고 하는 과정에서 라이브러리를 통해 간단하고 빠르게 구현함

    //12월 8일
    //CompObj라는 클래스를 만들어서, 파싱 및 확장자를 가져오도록 구현함. -> 아직 적용하진 않았음
    //기존 추출 팩토리 패턴에서 객체가 계속해서 생성되는 문제를 해결함.