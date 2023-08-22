# FileUploadGit
스프링부트 API 학습용 - 파일 업로드



## ★해야할일★


    //1. 파일 특수문자 검증 및 저장된 경로가 맞는지 검증(Directory Traversal공격 방어) 추가o
    //2. 예외처리 라이브러리(ExceptionUtils) - 추가ㅇ
    //3. json 전송을 할때 상태 코드 같이 전송 - 추가ㅇ
    //4. 라이브러리나 기타 코드의 동작 원리 숙지하기.
    //5. Entity파일 데이터 저장 크기 늘리기(255는 너무 적음) - 해결ㅇ
    //6. if문의 조건을 잘 선택해서 else문 제외 고민 -코드 간결화 고민하기
    //other. 추후 코드가 많아지면 어디서 오류가 났는지 모르기에, log를 잘 활용할 방안을 확인해 보기


    // 스케쥴링을 통해서 db와 저장경로의 파일을 대조해서 오류(dbo 폴더x, dbx 폴더o)인 경우 조치를 취하도록 구현
    // 아니면 기타 방법으로 db와 파일목록을 조회해서 오류 찾고 조치를 취하도록 설정
    //  ㄴvalidateDownloadFolderWithDB - AOP에서 구현하기 - 구현 완료
    // 파일 업로드 예외처리 구현하기 - 구현 완료
    // 파일 업로드에서 aop의 문제인지 기타 문제인지 모르겠지만, 파일 업로드시 파일명 끝에 숫자가 붙음 해결하기
    // oleparser의 문제임 해결해야 함~~
    // 이제 OLE 분석 구현하기~~ 

    //팀장님 피드백 - 파일 업로드 부분, db저장 부분을 나눠서, 파일을 우선 temp폴더에 넣고, 검증(문서인지 뭔지),
    //그리고 난 후 c:\files로 이동, 이동에 문제가 없었다면 db에 데이터 저장, 만약 문제가 있었다면, 파일 삭제(db저장 전에 조치)
    //db에 업로드를 하고 저장, 만약 db저장에 문제가 생겼다면, 파일 삭제(과감하게)

    //https://poi.apache.org/components/poifs/how-to.html#reading_event
    //https://poi.apache.org/components/slideshow/how-to-shapes.html#OLE
    //doc파일에서 ole가 1개일때, 데이터 추출까지 성공 -> ppt, xls랑 ole가 2개 이상일때 수행
    //예상안(doc) list에 정규표현식으로 폴더를 찾고, 크기랑 package스트림으로 파일 저장
