# ClaudeTest

- 사용자에게 APK 다운로드 링크를 줄 때는 항상 Release 페이지 링크만 준다:
  https://github.com/stockYandere/claudeTest/releases/latest
  (`/releases/latest/download/...` 직접 다운로드 링크는 쓰지 않는다.)
- 빌드는 GitHub Actions(`.github/workflows/build.yml`)에서 돌고, 푸시마다 새 Release가 만들어진다.
- 빌드가 끝나면 폰 푸시 알림으로 알려준다.
