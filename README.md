# 실습을 위한 개발 환경 세팅
* https://github.com/slipp/web-application-server 프로젝트를 자신의 계정으로 Fork한다. Github 우측 상단의 Fork 버튼을 클릭하면 자신의 계정으로 Fork된다.
* Fork한 프로젝트를 eclipse 또는 터미널에서 clone 한다.
* Fork한 프로젝트를 eclipse로 import한 후에 Maven 빌드 도구를 활용해 eclipse 프로젝트로 변환한다.(mvn eclipse:clean eclipse:eclipse)
* 빌드가 성공하면 반드시 refresh(fn + f5)를 실행해야 한다.

# 웹 서버 시작 및 테스트
* webserver.WebServer 는 사용자의 요청을 받아 RequestHandler에 작업을 위임하는 클래스이다.
* 사용자 요청에 대한 모든 처리는 RequestHandler 클래스의 run() 메서드가 담당한다.
* WebServer를 실행한 후 브라우저에서 http://localhost:8080으로 접속해 "Hello World" 메시지가 출력되는지 확인한다.

# 각 요구사항별 학습 내용 정리
* 구현 단계에서는 각 요구사항을 구현하는데 집중한다. 
* 구현을 완료한 후 구현 과정에서 새롭게 알게된 내용, 궁금한 내용을 기록한다.
* 각 요구사항을 구현하는 것이 중요한 것이 아니라 구현 과정을 통해 학습한 내용을 인식하는 것이 배움에 중요하다. 

### 요구사항 1 - http://localhost:8080/index.html로 접속시 응답
#### 콘솔에 출력되는 결과를 통해 알 수 있는 내용
- 각 요청마다 클라이언트의 포트는 **서로 다른** 포트로 연결한다.
- 서버는 각 요청에 대해 순차적으로 실행하는 것이 아니라, 동시에 각 요청에 대응하는 스레드
(ex) Thread-0, Thread-1)를 생성해 **동시에 실행**한다. 
- 각 요청에 대한 첫 번째 라인은 "GET /index.html HTTP/1.1"과 같은 형태로 구성되어 있다.
- 각 요청의 마지막은 빈 문자열`("")`로 구성되어 있다.
    
#### HTTP 요청과 응답 메시지의 구조
#####  요청 데이터
 - 모든 HTTP 요청에 대해 요청 라인, 요청 헤더 빈 공백 문자열은 필수이고,
    요청 본문은 필수가 아니다.
    - 첫 번째 라인: `요청 라인`
        - `HTTP -메소드 URI HTTP-버전`으로 구성
    - 두 번째 라인 ~ 빈 공백 문자열: `요청 헤더`
    - 빈 공백 문자열 다음: `요청 본문 데이터`   

##### 응답 데이터
- 첫 번째 라인: `상태 라인`
    - 요청 라인과 상태 라인은 형식이 다르다.
    - `HTTP-버전 상태코드 응답구문`
- 두 번째 라인 ~ 빈 공백 문자열: `응답 헤더`
- 빈 공백 문자열 다음: `응답 본문 데이터`


#### /index.html 요청을 한 번 보냈는데 여러 개의 추가 요청이 발생하는 이유
- 서버가 웹 페이지를 구성하는 모든 자원(HTML, CSS, JavaScript, Image)을 한 번에 응답으로 보내지 않기 때문이다.
- 웹 서버는 첫 번째 응답으로 /index.html에 대한 HTML만 보낸다.
- 응답을 받은 브라우저는 HTML 내용을 분석해 CSS, JavaScript, Image 등의 자원이 포함되어 있으면
서버에 해당 자원을 다시 요청하게 된다.
> 결론: 하나의 웹 페이지를 사용자에게 정상적으로 서비스하려면,
> 클라이언트와 서버 간에 한 번의 요청이 아닌, 여러 번의 요청과 응답을 주고 받게 된다.
> 웹 클라이언트와 웹 서버 간에 주고 받는 이 같은 구조를 이해하고 있어야 추후 성능을 개선할 때
> 개선할 방법을 찾을 수 있다. 
---

### 요구사항 2 - get 방식으로 회원가입
#### GET 메소드 방식으로 요청을 보낼 경우
사용자가 입력한 값을 물음표 뒤에 `매개변수명1=값1&매개변수명2=값2`형식으로 전송한다.
    - 경로(path): `/user/create`
    - 쿼리 스트링(query string): 물음표 뒤에 전달되는 매개변수

#### 물음표(?)를 기준으로 경로와 쿼리 스트링으로 분리하는 방법
물음표를 기준으로 
1. split()
2. 정규표현식 사용
3. 물음표가 위치하는 위치값(index)를 사용

#### 회원가입에 처리를 끝낸 후 응답을 보내야 한다. 
데이터가 서버에 정상적으로 전달되었는지는 확인할 수 있는데 브라우저에서 
응답을 보내지 않을 경우, "수신된 데이터 없음"과 같은 형태로 에러 메시지가 출력된다.

#### GET 방식으로 사용자가 입력한 데이터를 전달하는 것의 문제점
- 사용자가 입력한 데이터가 브라우저 URL 입력창에 표시된다.
(ex) 회원가입을 하는 경우 비밀번호까지 URL에 노출되기 때문에 보안 측면에서도 좋지 않다.)
- 요청 라인의 길이에 제한이 있다.
    -> GET 방식으로 사용할 수 있는 데이터의 크기에도 제한이 있다.
> 결론: GET 방식은 (회원가입, 블로그 글쓰기, 질문하기 등과 같이) 사용자가 입력한 데이터를
> 서버에 전송해 데이터를 추가할 때는 적합하지 않다.  
---

### 요구사항 3 - post 방식으로 회원가입
* 

### 요구사항 4 - redirect 방식으로 이동
* 

### 요구사항 5 - cookie
* 

### 요구사항 6 - stylesheet 적용
* 

### heroku 서버에 배포 후
* 