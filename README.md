# sroom-be

## 들어가기

1. **`java` 설치하기**

   ```bash
    brew install adoptopenjdk11
    ```

2. **intellij 설치하기**
   ```bash
    brew install --cask intellij
    ```

<br>

## 시작하기

**❗️❗️매우 중요❗️❗️**

db 접근정보 등 민감정보를 보호하기 위해 gitignore된 파일들이 존재합니다. main 브랜치를 clone 받은 후 파일 모두 삭제, sroom 개발자에게 공개된 스프링부트 전체 파일을 덮어씌운 후 캐시를 지우고 다시 git add 해주시기 바랍니다.
  ```bash
   git rm -r --cached .
   git add .
   ```
이떄, git status 를 했을 때 변경된 내용이 없어야 합니다.
<br>

**active profiles 변경**

1. 로컬, 배포 빌드를 다르게 하기 위해 다음과 같이 설정파일이 나뉘어져 있습니다.<br>
   <img width="205" alt="image" src="https://github.com/4m9d/sroom-be/assets/96522218/5b92fbac-a2af-4614-8752-d62700504c00">
2. local 설정파일로 빌드하기 위한 과정입니다. 다음과 같이 Run/Debug Configurations를 클릭해주세요. <br>
   <img width="394" alt="image" src="https://github.com/4m9d/sroom-be/assets/96522218/1c6dae1a-365a-46bb-a063-bafc4a69f5bf">
3. 다음과 같이 add new configuration -> spring Boot를 클릭해주세요. <br>
   <img width="347" alt="image" src="https://github.com/4m9d/sroom-be/assets/96522218/7b09906d-d2ff-4f91-804d-d1713c9a7c07">
4. 다음과 같이 작성해주세요. 이때 active profiles에 local을 써주셔야 application-local.yml 파일로 적용됩니다.
   <img width="722" alt="image" src="https://github.com/4m9d/sroom-be/assets/96522218/e77dc25c-effa-481b-8397-2c2fed2201a3">
5. 그리고 빌드해주세요. 이때 local설정파일로 되었는지 확인하고 싶다면 application-local.yml파일의 port 번호를 임의로(예를들어 9000번)바꿔 다시 빌드해 실행해 보세요. localhost:8080 이 접속되지 않고 localhost:9000 번으로 실행된다면 성공입니다.
6. 콘솔창에 다음과 같이 뜨면 성공입니다. 하단에 에러메세지와 함께 중지된다면 실패입니다.
   <img width="1216" alt="image" src="https://github.com/4m9d/sroom-be/assets/96522218/ff8029bb-578b-47d1-bf92-91c18e7e3892">


