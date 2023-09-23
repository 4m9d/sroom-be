# sroom-be

## 시작하기

**❗️❗️매우 중요❗️❗️**

gitignore된 파일들이 존재합니다.
sroom 개발자에게 문의한 후 schema.sql를 패키지에 포함시켜야 실행됩니다. 단, 파일이 github에 공개되지 않도록 다음과 같이 캐시를 삭제한 후 커밋해주세요.
  ```bash
   git rm -r --cached .
   git add .
   ```
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


