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

    //팀장님 피드백 - 파일 업로드 부분, db저장 부분을 나눠서, 파일을 우선 temp폴더에 넣고, 검증(문서인지 뭔지),
    //그리고 난 후 c:\files로 이동, 이동에 문제가 없었다면 db에 데이터 저장, 만약 문제가 있었다면, 파일 삭제(db저장 전에 조치)
    //db에 업로드를 하고 저장, 만약 db저장에 문제가 생겼다면, 파일 삭제(과감하게)

    //https://poi.apache.org/components/poifs/how-to.html#reading_event
    //https://poi.apache.org/components/slideshow/how-to-shapes.html#OLE


    //doc파일에 한해서, pptx, xlsx, docx문서를 비롯한, png, jpg, pdf를 추출하는데 성공 -> ppt, doc, xls를 추출하는 코드 추가하기
    //특이사항 -> xlsx는 추출을 하면, 데이터가 없어짐...? 시트도 없어짐 -> 저장된 데이터를 전부 가져오기에, 저장방식의 문제일 수 있음
    // -> 구현후, 공통 기능이 있을 수 있으므로, 모듈화 진행하기
    // ppt, xls, doc의 경우 저장할때 헤더의 정보를 가져올 수 없음. -> HSLFSlideShow로 테스트를 해도 데이터를 가져올 방법이 없음

    //ppt -> HSLFSlideShow를 통해 바이너리를 뽑아네는데 성공함, 해당 바이너리를 가지고, carving하면 끝날듯
    // ppt가 성공하면, xls, doc에서 package, ole10Native를 찾는 과정을 생략하고 추출 가능
    //대신에 pptx, docx, xlsx를 carving하기 위해, 헤더 푸터를 적용해줘야함.
    //☆ ppt파일에서 pdf, png, jpg, docx, pptx, xlsx추출 성공☆

    //xls ->  MBD[A-Z0-9]{8}/Package에 ppt, doc, xls가 저장됨(doc처럼 ole문서크기만큼 저장이 아니라, 추가 데이터가 들어있음 주의)
    //JPG, PNG, PDF의 경우는 MBD[A-Z0-9]{8}/Ole10Native에 존재함(doc와 같은 코드를 사용해도 될 듯(엔트리 위치 지정 코드 추가?)
    //-> 구현완료

    //docx
    //pptx
    //xlsx
    // 우선 embeddings라는 문자열이 있는지 검증 -> 그 다음 embedding이라는 폴더에 데이터들을 전부 다운로드(mordernFormat파일의 경우 헤더 푸터,
    //97-03파일들과 jpg, png와 같은 파일들은 97-03과 같은 포맷)
    //-> 똑같이 데이터 헤더와 푸터를 가지고 carving

    //확장자 저장은 열거형 상수로, 만약 문자열을 통해 ole디렉토리 체크시, db를 두어 가져와서 검증하는 방법도 있음
    //나중에 업로드된 파일명으로 따로 폴더를 생성하고, 거기에, ole추출코드 추가하기
    //이제 HWP를 사용해서 DOC구현하기

    //최근 이슈: 용량이 어느정도(10mb이상) 나가는 파일이면서 데이터 영역에 %%EOF이 여러개가 존재하는 PDF의 경우, 데이터가 잘리는 현상이 존재