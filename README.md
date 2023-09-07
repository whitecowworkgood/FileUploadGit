# FileUploadGit
스프링부트 API 학습용 - 파일 업로드



## ★해야할일★


    //09-01이슈
    //엔트리를 가져와 새로운 파일에 덮어쓰는 코드를 구현했지만, 원본과 너무 다를뿐더러, 원본이 ole를 가지고 있는 문서이면,
    //기존 알고있는 ole구조가 틀어져, 추출된 문서에 연결된 ole를 불러올 수가 없음. 
    //이를 통해 97-03 - 97-03버전 문제와, hwp doc 문제를 해결할 수 없음.

    //9월 3일 발견한 이슈~~ 매우 중요!!
    //한컴 문서에서 연결된 docx의 문서의 경우, 0XFF로 대부분이 채워지는 현상이 있었음,
    //확인해 보니, 한컴은 zlib를 통해 압축을 해왔음,
    //복호화를 시도했지만, 잘 안됬음 -> 집에서 한컴이랑 docx를 새로 만들어서 테스트를 해보니, 잘 됬음...?
    //두 hwp의 차이점이라고는, docx의 연결프로그램이 word냐 한컴워드이냐인데, 한컴워드이면 Package엔트리가 생성이 안됬음....
    //결론: 자회사 제품의 ole연결에 한해서 매우 짜증나는 현상,,,
    //hwp에서 docx를 추출하는데 결국 성공함!~


    //9월 5일자 이슈!!
    //DOC나 DOCX에서 XLSX와 같은 스프레드 시트를 연결 후, 추출하면 시트가 안보이는 현상이 있었음
    //확인해 보니, 내가 생성했던 파일을 그대로 넣으면, 시트가 없어지고
    //객체 새로생성으로 넣으면 시트가 잘보임 ㅋㅋㅋㅋ

    //97-03버전에 한컴연결은, 97-03에 97-03을 연결하는것과 같다

    /*
    ※ 공통사항 -워드프로세서 문서는 전부 시도 안함(이거는 데이터 자체가 없음)
    만약 ole이름이 같으면, 추가 조치 진행하기

    doc 파일 개선사항 - hwp추출이 안됨(97-03, 97-03이슈랑 같음), 파일명-완벽
    docx 파일 개선사항 - hwp이름만 수정하면 될 듯
    hwp 파일 개선사항 - 97-03버전의 이름만 찾으면 될 듯 -> 해결 완료
    ppt 파일 개선사항 - hwp추출이 안됨(97-03, 97-03이슈랑 같음), 파일명-완벽
    pptx파일 개선사항 - hwp이름만 수정하면 될 듯
    xls파일 개선사항 - hwp추출이 안됨(97-03, 97-03이슈랑 같음), 파일 확장자 null오류 발견!
                    -> xls의 compobj에 확장자를 식별할 문자열이 없어서 파일명으로 구별
    xlsx파일 개선사항 - hwp이름만 수정하면 될 듯


    --번외--
    자동 입력시
    doc 파일 개선사항 - hwp추출이 안됨(97-03, 97-03이슈랑 같음) -> 추출 성공 -> 97-03들도 적용하기 및 ole기타 엔트리는 지우기,
                    ->  자동입력된 docx파일은 CompObj엔트리가 없어서 오버라이드 시켜야 할 듯 함->해결함
    docx 파일 개선사항 - hwp이름만 구하면 될 듯함.
    hwp 파일 개선사항 - 자동입력된 docx파일은 CompObj엔트리가 없어서 오버라이드 시켜야 할 듯 함, 97-03버전의 이름 찾으면 됨 -> 해결 완료
    ppt 파일 개선사항 - hwp추출이 안됨(97-03, 97-03이슈랑 같음), docx를 추출하지 못함.->해결함
    pptx 파일 개선사항 - 97-03문서들이 추출 안됨 -> .bin으로 들어감 -> 각 파일별로 특정 엔트리로 구별하는 코드 추가 -> 이름도 추출하는 코드 추가
    xls 파일 개선사항 -  hwp추출이 안됨(97-03, 97-03이슈랑 같음), (특이사항: 엑셀에 엑셀 자동입력이 없음)
    xlsx 파일 개선사항 - hwp이름만 구하면 될 듯함.
    */

    //9월 6일 이슈
    //이제 해야할 것: hwp 파일 이름, 워드프로세서 문서는 전부 시도 안함(이거는 데이터 자체가 없음)
    만약 ole이름이 같으면, 추가 조치 진행하기


    //9월 7일 이슈
    발표할 때, 되는거, 안되는거, 진행중인거 정리해서 발표하기
    close()는 finally로 - try-with-resources도 있음.
    파일 이름 가져오는거(db에 랜덤 이름과 실제 파일명)

    21일 연구소 발표 전 최종 발표연습
    26일 연구소 분들 앞에서 성과 발표