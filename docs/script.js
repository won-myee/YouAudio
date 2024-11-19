document.addEventListener('DOMContentLoaded', () => {
    // 브라우저 언어 감지
    const userLang = navigator.language || navigator.userLanguage;
    const isKorean = userLang.startsWith('ko');

    // 텍스트 콘텐츠 정의
    const content = {
        en: {
            title: "YouTube Music Player YouAudio",
            subtitle: "Play Your Favorite YouTube Audio Without Any Ads",
            navAbout: "About",
            navFeatures: "Features",
            navHowTo: "How to Use",
            navDownload: "Download",
            aboutTitle: "About YouAudio",
            aboutDescription: "YouAudio is a simple application that allows you to play YouTube audio without any ads. Powered by yt-dlp and ffmpeg.",
            featuresTitle: "Features",
            featuresList: [
                "Easy YouTube video download",
                "Automatic conversion to audio format",
                "Built-in audio player"
            ],
            howToTitle: "How to Use",
            howToList: [
                "Enter a valid YouTube URL in the input field.",
                "Click the 'Download' button.",
                "Enjoy the audio playback after automatic conversion!"
            ],
            downloadTitle: "Download",
            downloadDescription: "You can download the latest version of YouAudio from the GitHub repository below.\n\n⚠️ Warning: This program is intended for personal use and legitimate purposes only.\n\nDownloading or distributing copyrighted content illegally is strictly prohibited.\n\nBy using this program, you agree to these terms.",
            downloadButton: "Visit GitHub",
            footerText: "© 2024 YouAudio. All rights reserved.",
            warningMessage: `⚠️ Warning: This program is intended for personal use and legitimate purposes only.\n\nDownloading or distributing copyrighted content illegally is strictly prohibited.\n\nBy using this program, you agree to these terms.`
        },
        ko: {
            title: "유튜브 음악 플레이어 유오디오",
            subtitle: "유튜브의 음악을 광고 없이 마음껏 재생하세요!",
            navAbout: "소개",
            navFeatures: "기능",
            navHowTo: "사용법",
            navDownload: "다운로드",
            aboutTitle: "유오디오 소개",
            aboutDescription: "유오디오는 유튜브 음악을 광고 없이 재생할 수 있는 애플리케이션입니다. yt-dlp와 ffmpeg로 구동됩니다.",
            featuresTitle: "주요 기능",
            featuresList: [
                "유튜브 영상 간편 다운로드",
                "자동 오디오 변환",
                "내장 오디오 플레이어"
            ],
            howToTitle: "사용 방법",
            howToList: [
                "유효한 유튜브 URL을 입력합니다.",
                "'다운로드' 버튼을 클릭합니다.",
                "자동 변환 후 재생을 즐기세요!"
            ],
            downloadTitle: "다운로드",
            downloadDescription: "최신 버전의 유오디오는 아래 GitHub 저장소에서 다운로드할 수 있습니다\n\n⚠️ 주의: 이 프로그램은 개인 용도 및 정당한 이용을 위한 목적에만 사용해야 합니다.\n\n저작권법을 위반하여 불법적인 콘텐츠를 다운로드하거나 배포하는 행위는 금지됩니다.\n\n사용자는 프로그램을 사용함으로써 이에 동의한 것으로 간주됩니다.",
            downloadButton: "GitHub 방문",
            footerText: "© 2024 유오디오. 모든 권리 보유.",
            warningMessage: `⚠️ 주의: 이 프로그램은 개인 용도 및 정당한 이용을 위한 목적에만 사용해야 합니다.\n\n저작권법을 위반하여 불법적인 콘텐츠를 다운로드하거나 배포하는 행위는 금지됩니다.\n\n사용자는 프로그램을 사용함으로써 이에 동의한 것으로 간주됩니다.`
        }
    };

    // 현재 언어로 텍스트 업데이트
    const langContent = isKorean ? content.ko : content.en;
    document.getElementById('title').textContent = langContent.title;
    document.getElementById('subtitle').textContent = langContent.subtitle;
    document.getElementById('nav-about').textContent = langContent.navAbout;
    document.getElementById('nav-features').textContent = langContent.navFeatures;
    document.getElementById('nav-how-to').textContent = langContent.navHowTo;
    document.getElementById('nav-download').textContent = langContent.navDownload;
    document.getElementById('about-title').textContent = langContent.aboutTitle;
    document.getElementById('about-description').textContent = langContent.aboutDescription;
    document.getElementById('features-title').textContent = langContent.featuresTitle;
    document.getElementById('features-list').innerHTML = langContent.featuresList.map(item => `<li>${item}</li>`).join('');
    document.getElementById('how-to-title').textContent = langContent.howToTitle;
    document.getElementById('how-to-list').innerHTML = langContent.howToList.map(item => `<li>${item}</li>`).join('');
    document.getElementById('download-title').textContent = langContent.downloadTitle;
    document.getElementById('download-description').textContent = langContent.downloadDescription;
    document.getElementById('download-button').textContent = langContent.downloadButton;
    document.getElementById('footer-text').textContent = langContent.footerText;

    document.addEventListener('DOMContentLoaded', () => {
    // 다운로드 버튼 가져오기
    const downloadButton = document.getElementById('download-button');

    // 버튼이 없을 경우 에러 방지
    if (!downloadButton) {
        console.error("Download button not found!");
        return;
    }

    // 버튼 클릭 이벤트 추가
    downloadButton.addEventListener('click', (e) => {
        e.preventDefault(); // 기본 동작 방지
        console.log("Download button clicked!"); // 디버그 로그

        // 현재 언어에 맞는 경고 메시지
        const langContent = isKorean ? content.ko : content.en;

        // 경고창 표시
        const userAgreed = alert(langContent.warningMessage);

        // 버튼 클릭 이벤트 추가
        downloadButton.addEventListener('click', (e) => {
           e.preventDefault(); // 기본 동작 방지
           console.log("Download button clicked!"); // 디버그 로그

           // 현재 언어에 맞는 경고 메시지
           const langContent = isKorean ? content.ko : content.en;

           // 경고창 표시
           alert(langContent.warningMessage);

           // 경고창이 닫히면 바로 이동 
           window.location.href = "https://github.com/won-myee/YouAudio/releases"; // 실제 GitHub 링크로 변경
       });
    });
  });
});
