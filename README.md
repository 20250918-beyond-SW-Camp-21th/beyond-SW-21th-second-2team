# 💻 사내 인프라 솔루션, ON:S 💻
### > **휴가, 사내 커뮤니티, 회사 자원 사용 예약까지 한 번에**

</br>

----

</br>

## 목차
### 1. 팀 소개
### 2. 프로젝트 개요
### 3. 유사 서비스
### 4. 개발 환경 및 협업 도구 
### 5. 프로젝트 구조
### 6. 프로젝트 기획
### 7. 컨벤션
### 8. 프로젝트 기능
### 9. 회고

</br>

----

</br>

## 1. 팀 소개
<table width="100%">
  <thead>
    <tr align="center">
      <th width="20%">강윤혜🐼</th>
      <th width="20%">김채우🐾</th>
      <th width="20%">송형욱🦉</th>
      <th width="20%">윤홍석🐿️</th>
      <th width="20%">진희헌🦍</th>
    </tr>
  </thead>
  <tbody>
    <tr align="center">
      <td><img src="https://github.com/user-attachments/assets/826e640b-c388-4dba-9fa7-2414242fa3f1" width="80%" /></td>
      <td><img src="https://github.com/user-attachments/assets/9bbce749-38fc-4497-8cfc-08de00899d67" width="80%" /></td>
      <td><img src="https://github.com/user-attachments/assets/06dfb26a-3f80-4b47-92a8-e428e77dd81f" width="80%" /></td>
      <td><img src="https://github.com/user-attachments/assets/63278d3c-c088-4ae8-a719-2f3a9a6fd832" width="80%" /></td>
      <td><img src="https://github.com/user-attachments/assets/508e961d-a673-4b65-a2bf-11d01702ed09" width="80%" /></td>
    </tr>
  </tbody>
  <tbody>
    <tr align="center">
      <td>연차/근태</td>
      <td>자원 관리</td>
      <td>전자결재</td>
      <td>보안/권한</td>
      <td>게시판/공지사항</td>
    </tr>
  </tbody>
</table>


</br>

----

</br>

## 2. 프로젝트 개요
### 소개
- **ON:S(ON System)** 는 모든 업무가 이 시스템 위에서 켜진다는 의미를 갖습니다. 
- 휴가 상신과 결재, 사내 공지사항, 회사 자원 예약 및 사용, 직원 등록과 해제 등이 이루어집니다. 
### 선정 이유
- 1차, 2차, 최종 결재자 등으로 이루어진 계층 구조를 JPA를 이용하여 구현해 보고자 하였습니다.
- 여러 테이블이 복합적으로 얽혀 있는 상태에서 데이터를 주고받아 사용하는 것을 구현하는 것이 가장 큰 목표였습니다. 
### 주요 기능
- 인증/보안
- 전자 결재
- 출퇴근 기록 및 연차 관리
- 자원 예약
- 사내 게시판 및 공지사항 확인


</br>

----

</br>

## 3. 유사 서비스 
### 하이웍스
- 그룹웨어, 경비지출관리, 근무관리 등의 기능을 지원합니다.
- 기업 메일과 전자결재 등 필수 기능에 집중하여 합리적인 가격에 제공합니다.
- 중소기업 및 스타트업 초기에 비용을 낮추고 핵심 기능만 빠르게 도입하려는 기업을 타겟팅합니다.
### 더존 비즈온
- 회계, 인사, 세무 관리와 결재 시스템을 연동해 제공합니다.
- 자동 전표 처리, 자금 관리, 근태 연동이 핵심 기능입니다.
- 중견기업 및 제조/유통업재무/회계팀의 업무 비중이 높으며 정확한 자금 통제가 필요한 기업을 타겟팅합니다.


</br>

----

</br>

## 4. 개발 환경 및 협업 도구
### 개발 환경
- Java <img src="https://img.shields.io/badge/Java-007396?style=for-the-badge&logo=java&logoColor=white"> 
- MariaDB <img src="https://img.shields.io/badge/MariaDB-003545?style=for-the-badge&logo=mariadb&logoColor=white">
- SpringBoot <img src="https://img.shields.io/badge/SpringBoot-6DB33F?style=for-the-badge&logo=springboot&logoColor=white">
- Spring Security <img src="https://img.shields.io/badge/Spring%20Security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white">
- Spring Cloud Gateway <img src="https://img.shields.io/badge/Spring%20Cloud%20Gateway-6DB33F?style=for-the-badge&logo=spring&logoColor=white">
### IDE & Tools
- IntelliJ <img src="https://img.shields.io/badge/IntelliJ%20IDEA-000000?style=for-the-badge&logo=intellijidea&logoColor=white">
- Git <img src="https://img.shields.io/badge/Git-F05032?style=for-the-badge&logo=git&logoColor=white">
### 협업 도구
- Discord <img src="https://img.shields.io/badge/Discord-5865F2?style=for-the-badge&logo=discord&logoColor=white">
- Notion <img src="https://img.shields.io/badge/Notion-000000?style=for-the-badge&logo=notion&logoColor=white">
  - https://www.notion.so/coffit23/2-What-The-Fork-2b6a02b1ffb1812fa667f22ff0f2ef8c
- Github <img src="https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=github&logoColor=white">
- Google Drive <img src="https://img.shields.io/badge/Google%20Drive-4285F4?style=for-the-badge&logo=googledrive&logoColor=white">
- Jira <img src="https://img.shields.io/badge/Jira-0052CC?style=for-the-badge&logo=jira&logoColor=white">
  - https://chaewookim2877.atlassian.net/jira/software/projects/APPR/boards/1/backlog?epics=visible


</br>

----

</br>

## 5. 프로젝트 구조
### MSA 구조도
<img width="871" height="801" alt="msa_architecture drawio" src="https://github.com/user-attachments/assets/53c2fb26-cf9f-446f-a581-065f58bcc05b" />

### ERD
<img width="1040" height="666" alt="스크린샷 2025-12-11 오후 5 10 28" src="https://github.com/user-attachments/assets/7dac025a-8568-4c54-8872-0a7a628b83a1" />

### 패키지 구조
<img width="394" height="620" alt="스크린샷 2025-12-11 오후 5 10 58" src="https://github.com/user-attachments/assets/a2bbce09-af24-4fd1-9c1e-e7ab2770253a" />


</br>

----

</br>

## 6. 프로젝트 기획
### 요구사항 명세서
<img width="2339" height="1653" alt="백엔드 2조 xlsx - 요구사항 정의서-1" src="https://github.com/user-attachments/assets/ceaf6e97-d8c1-4a32-9818-9a8e18d3befc" />
<img width="2339" height="1653" alt="백엔드 2조 xlsx - 요구사항 정의서-2" src="https://github.com/user-attachments/assets/b10a0813-1dee-4f5f-8854-f36e88b3012f" />

### 테이블 정의서

<details>
<summary style = " font-size:1.3em;">User Service</summary>
<div markdown="1">
<img width="960" height="553" alt="13" src="https://github.com/user-attachments/assets/98ad608a-53c7-4ff7-a87c-20426c5b68b2" />
<img width="958" height="605" alt="14" src="https://github.com/user-attachments/assets/b766b410-3529-4049-bb15-d6fc54c0e97c" />
<img width="961" height="432" alt="18" src="https://github.com/user-attachments/assets/35c502af-1f15-4e2c-8f6a-844c93ce3647" />
</div>
</details>

<details>
<summary style = " font-size:1.3em;">Annual Leave</summary>
<div markdown="1">
<img width="962" height="582" alt="2" src="https://github.com/user-attachments/assets/d3fc0a1b-3939-43c5-9e21-f2e57f672c2c" />
<img width="966" height="585" alt="3" src="https://github.com/user-attachments/assets/11eaf04a-d7b3-42ce-bd37-cb6e30c94ed7" />
<img width="957" height="560" alt="8" src="https://github.com/user-attachments/assets/4bac2228-bd88-48e6-affd-b904d36da4ce" />
</div>
</details>

<details>
<summary style = " font-size:1.3em;">Approval System</summary>
<div markdown="1">
<img width="958" height="596" alt="4" src="https://github.com/user-attachments/assets/9aba7026-a3c5-419d-8b8e-d5ec02529b14" />
<img width="958" height="510" alt="5" src="https://github.com/user-attachments/assets/6f6c18aa-d251-4215-9421-cdf5eb20e999" />
<img width="965" height="510" alt="6" src="https://github.com/user-attachments/assets/57579fdc-5b91-4d89-900a-3c0cfd5d58c5" />
<img width="973" height="463" alt="7" src="https://github.com/user-attachments/assets/4c7c28e7-5f8d-41ab-8fa9-314355f85a95" />
</div>
</details>

<details>
<summary style = " font-size:1.3em;">Communication</summary>
<div markdown="1">
<img width="962" height="578" alt="10" src="https://github.com/user-attachments/assets/e92b3cae-30f6-416b-a26b-260474841c3d" />
<img width="957" height="575" alt="16" src="https://github.com/user-attachments/assets/304267a5-f55e-4a55-ba6a-c3457876f99b" />
<img width="967" height="472" alt="17" src="https://github.com/user-attachments/assets/a1803911-5c42-4f4c-baa7-b10a8629d2a8" />
</div>
</details>

<details>
<summary style = " font-size:1.3em;">Resource Reservation</summary>
<div markdown="1">
<img width="960" height="461" alt="9" src="https://github.com/user-attachments/assets/5e2af3c3-2568-4b4b-9cb6-f3ca29241448" />
<img width="966" height="460" alt="11" src="https://github.com/user-attachments/assets/ca8c83fe-311e-4664-b533-5c46a97e31d9" />
<img width="963" height="475" alt="12" src="https://github.com/user-attachments/assets/e80d90e7-4e63-4269-9be7-20b4131e2dc9" />
<img width="967" height="578" alt="19" src="https://github.com/user-attachments/assets/233c33c0-9825-4c34-aa05-01147cbf580a" />
<img width="963" height="457" alt="20" src="https://github.com/user-attachments/assets/3facc15c-f964-483c-8c4b-3e20d9e82559" />
</div>
</details>

### API 설계서

<details>
<summary style = " font-size:1.3em;">User Service</summary>
<div markdown="1">
<img width="1443" height="713" alt="스크린샷 2025-12-11 오후 2 31 07" src="https://github.com/user-attachments/assets/650ffb80-6729-4cd2-903e-441fb0b103ab" />
</div>
</details>

<details>
<summary style = " font-size:1.3em;">Annual Leave</summary>
<div markdown="1">
<img width="1445" height="661" alt="스크린샷 2025-12-11 오후 2 30 28" src="https://github.com/user-attachments/assets/84f46498-cca4-4010-b182-919baa5120e9" />
</div>
</details>

<details>
<summary style = " font-size:1.3em;">Approval System</summary>
<div markdown="1">
<img width="1455" height="905" alt="스크린샷 2025-12-11 오후 2 30 18" src="https://github.com/user-attachments/assets/2273f579-f2a8-4479-8ca8-edb699653957" />
</div>
</details>

<details>
<summary style = " font-size:1.3em;">Communication</summary>
<div markdown="1">
<img width="1450" height="631" alt="스크린샷 2025-12-11 오후 2 30 37" src="https://github.com/user-attachments/assets/4c9e0356-c9f3-4f21-a459-0571b512cd65" />
</div>
</details>

<details>
<summary style = " font-size:1.3em;">Resource Reservation</summary>
<div markdown="1">
<img width="1329" height="904" alt="스크린샷 2025-12-11 오후 3 20 24" src="https://github.com/user-attachments/assets/b252d46b-3b60-472a-acc6-9110cf480a46" />
<img width="1325" height="743" alt="스크린샷 2025-12-11 오후 3 20 16" src="https://github.com/user-attachments/assets/cb5c0d49-55a5-4cdd-99f3-21e20de727fa" />
</div>
</details>

### WBS
<img width="2339" height="1653" alt="백엔드 2조 xlsx - WBS-1" src="https://github.com/user-attachments/assets/74bc66c8-0100-4347-a81e-ab6f1b1e5cbd" />
<img width="2339" height="1653" alt="백엔드 2조 xlsx - WBS-2" src="https://github.com/user-attachments/assets/9fdc579f-2c48-4f61-b20f-64c1834ab3e2" />


</br>

----

</br>

## 7. 컨벤션
## Back-end
### 공통 사항
- 단위 테스트 작성(service 메소드 별로) : Junit 사용
- 다른 사람이 알아보기 쉽도록 주석처리해야 합니다.
    - javadoc 형식 https://jake-seo-dev.tistory.com/59
- 지라 티켓 생성하고 작업 시작합시다.
- 사용 내역 같은 로그 확인할 수 있도록 잘 남겨야 합니다.


<br>

### 개발규칙

**⭐ Code Convention**



<details>
<summary style = " font-size:1.3em;">Naming</summary>
<div markdown="1">

- 패키지 : 언더스코어(`_`)나 대문자를 섞지 않고 소문자를 사용하여 작성합니다.
- 클래스 : 클래스 이름은 명사나 명사절로 지으며, 대문자 카멜표기법(Upper camel case)을 사용합니다.
- 메서드 : 메서드 이름은 동사/전치사로 시작하며, 소문자 카멜표기법(Lower camel case)를 사용합니다. 의도가 전달되도록 최대한 간결하게 표현합니다.
- 변수 : 소문자 카멜표기법(Lower camel case)를 사용합니다.
- ENUM, 상수 : 상태를 가지지 않는 자료형이면서 `static final`로 선언되어 있는 필드일 때를 상수로 간주하며, 대문자와 언더스코어(Upper_snake_case)로 구성합니다.
- DB 테이블: 소문자와 언더스코어로(lower_snake_case) 구성합니다.
- 컬렉션(Collection): **복수형**을 사용하거나 **컬렉션을 명시합니다**. (Ex. userList, users, userMap)
- LocalDateTime: 접미사에 **Date**를 붙입니다.


</div>
</details>
<details>
<summary style = " font-size:1.3em;">Comment</summary>
<div markdown="1">

**1. 한줄 주석은 // 를 사용한다.**

```java
// 하이~
```

**2. Bracket 사용 시 내부에 주석을 작성한다.**

```java
/*
   하이~!
*/
```

**3. 주요 함수에 대한 주석**

```java
/*
 * 입력 : 인덱스:Long
 * 기능 : 유저 인덱스로 db에 접근해 유저 객체를 반환한다
 * 출력 : 유저:User
 */
public User getUser(Long idx)
```

</div>
</details>
<details>
<summary style = " font-size:1.3em;">Import</summary>
<div markdown="1">

**1. 소스파일당 1개의 탑레벨 클래스를 담기**

> 탑레벨 클래스(Top level class)는 소스 파일에 1개만 존재해야 한다. ( 탑레벨 클래스 선언의 컴파일타임 에러 체크에 대해서는 [Java Language Specification 7.6](http://docs.oracle.com/javase/specs/jls/se7/html/jls-7.html#jls-7.6) 참조 )

**2. static import에만 와일드 카드 허용**

> 클래스를 import할때는 와일드카드(`*`) 없이 모든 클래스명을 다 쓴다. static import에서는 와일드카드를 허용한다.

**3. 애너테이션 선언 후 새줄 사용**

> 클래스, 인터페이스, 메서드, 생성자에 붙는 애너테이션은 선언 후 새줄을 사용한다. 이 위치에서도 파라미터가 없는 애너테이션 1개는 같은 줄에 선언할 수 있다.

**4. 배열에서 대괄호는 타입 뒤에 선언**

> 배열 선언에 오는 대괄호(`[]`)는 타입의 바로 뒤에 붙인다. 변수명 뒤에 붙이지 않는다.

**5. `long`형 값의 마지막에 `L`붙이기**

> long형의 숫자에는 마지막에 대문자 'L’을 붙인다. 소문자 'l’보다 숫자 '1’과의 차이가 커서 가독성이 높아진다.

</div>
</details>
<details>
<summary style = " font-size:1.3em;">URL</summary>
<div markdown="1">

**URL**

URL은 RESTful API 설계 가이드에 따라 작성합니다.

- HTTP Method로 구분할 수 있는 get, put 등의 행위는 url에 표현하지 않습니다.
- 마지막에 `/` 를 포함하지 않습니다.
- `_` 대신 `-`를 사용합니다.
- 소문자를 사용합니다.
- 확장자는 포함하지 않습니다.


</div>
</details>

<br>

**☀️ Commit Convention**


<details>
<summary style = " font-size:1.3em;">Rules</summary>
<div markdown="1">

**1. Git Flow**

작업 시작 시 선행되어야 할 작업은 다음과 같습니다.


> 1. issue를 생성합니다.
> 2. feature branch를 생성합니다.
> 3. add → commit → push → pull request 를 진행합니다.
> 4. pull request를 develop branch로 merge 합니다.
> 5. 이전에 merge된 작업이 있을 경우 다른 branch에서 진행하던 작업에 merge된 작업을 pull 받아옵니다.
> 6. 종료된 issue와 pull request의 label을 관리합니다.

**2. IntelliJ**

IntelliJ로 작업을 진행하는 경우, 작업 시작 시 선행되어야 할 작업은 다음과 같습니다.

> 1. 깃허브 프로젝트 저장소에서 issue를 생성합니다.
> 2. IntelliJ의 git 탭 → local develop branch 우클릭 → update 를 진행합니다.
> 3. IntelliJ의 git 탭 → local develop branch 우클릭 → new branch from ‘develop’ 을 진행합니다.
> 4. 생성한 issue 번호에 맞는 feature branch를 생성함과 동시에 feature branch로 checkout 합니다.
> 5. feature branch에서 issue 단위 작업을 진행합니다.
> 6. 작업 완료 후, add → commit을 진행합니다.
> 7. push 하기 전, remote develop branch의 변경 사항을 확인하기 위해 2번 과정을 다시 수행합니다.
> 8. IntelliJ의 git 탭 → local develop branch 우클릭 → merge ‘develop’ into ‘4번 과정에서 생성한 feature branch’ 를 진행합니다.
> 9. 만약 코드 충돌이 발생하였다면, IntelliJ에서 코드 충돌을 해결하고 add → commit을 진행합니다.
> 10. push → pull request (feature branch → develop branch) 를 진행합니다.
> 11. pull request가 작성되면 작성자 이외의 다른 팀원이 code review를 진행합니다.
> 12. 최소 한 명 이상의 팀원에게 code review와 approve를 받은 경우 pull request 생성자가 merge를 진행합니다.
> 13. 종료된 issue와 pull request의 label과 milestone을 관리합니다.


**3. Etc**

준수해야 할 규칙은 다음과 같습니다.

> 1. develop branch에서의 작업은 원칙적으로 금지합니다. 단, README 작성은 develop branch에서 수행합니다.
> 2. commit, push, merge, pull request 등 모든 작업은 오류 없이 정상적으로 실행되는 지 확인 후 수행합니다.

</div>
</details>

<details>
<summary style = " font-size:1.3em;">Branch</summary>
<div markdown="1">

**1. Branch**

branch는 작업 단위 & 기능 단위로 생성하며 이는 issue를 기반으로 합니다.

**2. Branch Naming Rule**

branch를 생성하기 전 issue를 먼저 작성합니다. issue 작성 후 생성되는 번호와 domain 명을 조합하여 branch의 이름을 결정합니다. `<Prefix>/<JiraEpicNumber>-<Domain>` 의 양식을 준수합니다.

**3. Prefix**

- `main` : 개발이 완료된 산출물이 저장될 공간입니다.
- `develop`: feature branch에서 구현된 기능들이 merge될 default branch 입니다.
- `feature`: 기능을 개발하는 branch 입니다. 이슈 별 & 작업 별로 branch를 생성 후 기능을 개발하며 naming은 소문자를 사용합니다.

**4. Domain**

- `approval`, `attendance`, `auth`, `security`, `comm`, `alarm`, `resource`, `error`, `config` 


**5. Etc**

- `feature/APPR-2-approval`, `feature/APPR-1-config`


</div>
</details>

<details>
<summary style = " font-size:1.3em;">Issue</summary>
<div markdown="1">

**1. Issue**

작업 시작 전 issue 생성이 선행되어야 합니다. issue 는 작업 단위 & 기능 단위로 생성하며 생성 후 표시되는 issue number 를 참조하여 branch 이름과 commit message를 작성합니다.

issue 제목에는 기능의 대표적인 설명을 적고 내용에는 세부적인 내용 및 작업 진행 상황을 작성합니다.

issue 생성 시 github 오른편의 assignee, label을 적용합니다. assignee는 해당 issue 담당자, label은 작업 내용을 추가합니다.

**2. Issue Naming Rule**

`[<Prefix>] <Description>` 의 양식을 준수하되, prefix는 commit message convention을 따릅니다.

**3. Etc**

<aside>
[feat] 약속 잡기 API 구현
<br/>[chore] spring data JPA 의존성 추가

</aside>

---

</div>
</details>

<details>
<summary style = " font-size:1.3em;">Commit</summary>
<div markdown="1">

**1. Commit Message Convention**

`[<Prefix>] #<Issue_Number> <Description>` 의 양식을 준수합니다.

- **feat** : 새로운 기능 구현 `[feat] #11 구글 로그인 API 기능 구현`
- **fix** : 코드 오류 수정 `[fix] #10 회원가입 비즈니스 로직 오류 수정`
- **del** : 쓸모없는 코드 삭제 `[del] #12 불필요한 import 제거`
- **docs** : README나 wiki 등의 문서 개정 `[docs] #14 리드미 수정`
- **refactor** : 내부 로직은 변경 하지 않고 기존의 코드를 개선하는 리팩터링 `[refactor] #15 코드 로직 개선`
- **chore** : 의존성 추가, yml 추가와 수정, 패키지 구조 변경, 파일 이동 `[chore] #21 yml 수정`, `[chore] #22 lombok 의존성 추가`
- **test**: 테스트 코드 작성, 수정 `[test] #20 로그인 API 테스트 코드 작성`
- **style** : 코드에 관련 없는 주석 달기, 줄바꿈

</div>
</details>

<details>
<summary style = " font-size:1.3em;">Pull Request</summary>
<div markdown="1">

**1. Pull Request**

develop & main branch로 merge할 때에는 pull request가 필요합니다. pull request의 내용에는 변경된 사항에 대한 설명을 명시합니다.

**2. Pull Request Naming Rule**

`[<Prefix>] <Description>` 의 양식을 준수하되, prefix는 commit message convention을 따릅니다.

**3. Etc**

[feat] 약속 잡기 API 구현
<br/>[chore] spring data JPA 의존성 추가

</div>
</details>

</br>

----

</br>

## 8. 프로젝트 기능
### 보안 및 권한
- 직원 등록 및 해제
- 로그인, 로그아웃, 토큰 리프레시
- JWT 발급으로 API 접근 제어
- 직급별 접근 권한 부여
### 근태/연차 관리
- 출퇴근 기록
- 연차 차감
- 날짜/시간 계산 로직으로 연차 계산
### 전자결재
- 휴가 기안서 작성/상신
- 순차 승인으로 결재 라인 관리
- 동시성 제어로 동시 승인 및 수정 방지
### 자원(회의실/법인차량/비품) 예약
- 자원 관리
- 중복 예약 방지
- 기간 중복 방지
- 예약 충돌 방지
### 게시판/공지사항
- 공지사항 게시
- 게시판
- 댓글/대댓글

</br>

----

</br>

## 9. 회고

<details>
<summary style = " font-size:1.3em;">강윤혜</summary>
<div markdown="1">
근태관리와 연차관리를 맡아 프로젝트에 참여하였습니다. 처음 역할을 맡았을 때 근태관리는 간단한 로직이라고 생각하였습니다. 하지만 코드를 작성하고 테스트케이스를 작성하며 간단한 로직 속에서도 다양한 예외상황이 존재하고 개발자가 책임져야하는 에러범위는 상당히 넓다는 것을 배웠습니다. 다소 아쉬운 것은 MSA 방식에 익숙하지는데에 시간을 많이 소비하여 스케줄러를 통한 연차자동부여를 구현하여 처음 목표였던 연차에 대한 자동화를 완성하지 못한 것이 아쉽게 느껴집니다. 그래도 MSA 방식에 많은 시간을 소비하며 한 서비스만을 완성시키는 것을 넘어 다른 서비스와의 통신 방식, 장애 상황까지 함께 고민하는 방법을 배울 수 있었던 것 같습니다.
</div>
</details>

<details>
<summary style = " font-size:1.3em;">김채우</summary>
<div markdown="1">
들인 시간과 노력에 비해 생각보다 뛰어난 결과가 나오지 않아 너무나도 아쉬웠습니다. 정말 기초적인 CRUD만을 구현했고 다른 마이크로 서비스와 통신도 하지 않았기 때문에 사실 MSA를 완전히 맛본 것 같지 않습니다. 그렇다고 다른 기술을 도입해 적용해 본 것도 아니기 때문에 아쉬운 감정이 더욱 큽니다. 그리고 적절한 일정 분배가 얼마나 중요한지도 깨달았습니다. 요구사항 정의에 시간을 꽤나 많이 쏟았기 때문에 그나마 마지막 일정에 맞춰 끝낼 수 있었습니다. 다음 프로젝트부터는 조금 더 시간과 노력을 들어 눈에 보이게 성장하고자 합니다.
</div>
</details>

<details>
<summary style = " font-size:1.3em;">윤홍석</summary>
<div markdown="1">
이번 단위 프로젝트는 자신의 부족한 점을 되짚어 볼 수 있는 기회였습니다. 개념적인 이해와 코드에 대한 이해도는 별개이기에, 알고 있다고 생각했던 게 몰랐던 경우가 잦았습니다. 덕분에 필요한 부분들을 복습하고 추가로 무엇을 더 연습해야 할지 알 수 있었습니다. 또한 이번에는 보안을 담당했는데, 복잡하고 새로 배우는 개념이지만 중요한 부분이기에 공부할 기회가 있어서 좋았습니다.
</div>
</details>

<details>
<summary style = " font-size:1.3em;">송형욱</summary>
<div markdown="1">
'전자결재'라는 나름 코어 부분 파트를 맡아 책임감이 막중해서 며칠 밤을 새서 완성을 시켰던 것 같습니다. 그럼에도 불구하고 물리적인 시간이 다소 부족하여서 하고자 하는 기능을 제대로 못 구현한 게 아쉽습니다. minIO를 이용해서 결재 문서 pdf를 등록하고 다운로드 받는 서비스를 추가로 구현했으면 좋았다는 아쉬움이 남습니다. 그래도 낙관적 락을 이용해서 3명의 결재자가 차례로 결재를 승인 및 반려하는 로직을 짜는 과정에서 저 스스로 많이 성장했다고 느낍니다. 그리고 이번에 MSA를 처음 시도해봤는데, 인증 및 인가가 Gateway에서 처리되는 로직의 이해가 조금 오래걸렸지만, 이 부분도 잘만 적응하면 강력한 중앙관리가 될 수 있다는 것을 깨달았습니다. Feign 클라이언트도 설정하여 다른 모듈과의 전송 및 응답을 부딪히며 경험해봤는데 앞으로 MSA를 진행함에 있어 좋은 밑거름이 될 것 같습니다. DB 프로젝트 때도 느꼈지만 초기 아키텍쳐 설계와 테이블 엔티티 구성의 골자가 잘 잡혀있어야 함을 깨달았습니다. 이번 프로젝트에서 느꼈던 내가 잘했던 점과 부족했던 점 모두 반영하여 다음 프로젝트에 그대로 가져가 더 나은 개발자가 되기 위한 역량을 갖춰야겠다는 생각이 들었습니다.
</div>
</details>

<details>
<summary style = " font-size:1.3em;">진희헌</summary>
<div markdown="1">
게시판과 댓글 기능은 CRUD의 가장 기본이 되는 대표적인 도메인입니다. 사실 저는 이전 프로젝트에서도 댓글 기능을 맡은 경험이 있습니다. 이번에는 단순히 기능을 구현하는것에 중점을 두기보다, 이전보다 더 나은 코드 구조를 만드는 것을 목표로 삼았습니다. 예를 들어, CommentService 구현에서는JPA 쿼리 메서드를 적극적으로 활용하여 불필요한 로직을 줄이고 의도를 명확히 드러내는 코드 작성을 시도했습니다. 구조적인 측면에서는 DDD 패턴을 부분적으로 도입하며 설계를 개선하고자 했습니다. 도메인 계층이 JPA와 같은 구체적인 기술에 직접 의존하지 않도록 의존성 역전 원칙(DIP)을 적용하려는 시도를 했습니다. CRUD라는 가장 기본적인 기능일수록 오히려 객체지향 설계와 책임 분리에 대한 고민이 더욱 중요하다는 것이었습니다.
</div>
</details>

