package com.youaudio;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.control.ProgressBar;

import java.io.*;
import java.util.List;

public class HojaAudioPlayer extends Application {
    private MediaPlayer mediaPlayer;
    private ProgressBar progressBar;
    private TextField urlField;
    private String ffmpegPath;
    private File currentTempFile;
    private Button downloadButton;
    private Button stopButton;
    private Button pauseButton;
    private Stage primaryStage;  // primaryStage 필드로 선언

    public HojaAudioPlayer() {
        this.ffmpegPath = "/snap/bin/ffmpeg"; // FFmpeg 경로 설정
    }

    @Override
    public void start(final Stage primaryStage) {
        this.primaryStage = primaryStage;  // 필드에 값 할당
        primaryStage.setTitle("유튜브 음악 플레이어 유오디오");

        urlField = new TextField();
        urlField.setPromptText("유튜브 URL을 입력하세요");
        urlField.setPrefWidth(300);

        downloadButton = new Button("다운로드");
        stopButton = new Button("정지");
        pauseButton = new Button("일시정지/재개");

        // 초기 상태에서는 정지 버튼만 비활성화
        stopButton.setDisable(true);
        downloadButton.setOnAction(e -> downloadAndPlayAudio());
        stopButton.setOnAction(e -> stopMusic());
        pauseButton.setOnAction(e -> pauseOrResumeMusic());

        progressBar = new ProgressBar(0.0);
        progressBar.setPrefWidth(500);
        progressBar.setOnMouseClicked(event -> handleProgressBarClick(event));

        HBox topBar = new HBox(10, urlField, downloadButton, stopButton);
        topBar.setStyle("-fx-padding: 5; -fx-spacing: 10; -fx-background-color: #87a556;");

        HBox bottomBar = new HBox(10, pauseButton);
        bottomBar.setStyle("-fx-padding: 5; -fx-spacing: 10; -fx-background-color: #87a556;");

        VBox vbox = new VBox(10, topBar, progressBar, bottomBar);
        Scene scene = new Scene(vbox, 500, 111);
        primaryStage.setScene(scene);
        primaryStage.show();

        showCopyrightWarning();
    }

    private void downloadAndPlayAudio() {
        String url = urlField.getText().trim();
        if (url.isEmpty()) {
            showAlert("유튜브 URL을 입력하세요.");
            return;
        }

        // 다운로드 중에는 버튼을 비활성화하고 텍스트 변경
        downloadButton.setText("다운로드 중...");
        downloadButton.setDisable(true);
        stopButton.setDisable(true);
        pauseButton.setDisable(true);

        new Thread(() -> {
            try {
                File downloadedFile = downloadAudioFromYoutube(url);
                if (downloadedFile != null) {
                    // 다운로드가 완료되면
                    if (!isSupportedAudioFormat(downloadedFile)) {
                        if (isFFmpegInstalled()) {
                            // 변환 중
                            Platform.runLater(() -> downloadButton.setText("변환 중..."));
                            File convertedFile = convertToWav(downloadedFile.getAbsolutePath());
                            if (convertedFile != null) {
                                playConvertedFile(convertedFile);
                                Platform.runLater(() -> downloadButton.setText("다운로드"));
                            }
                        } else {
                            showAlert("이 파일을 재생하려면 FFMPEG를 설치하세요.");
                        }
                    } else {
                        // 다운로드된 파일명을 가져와서 "[" 이전 부분을 제목으로 설정
                        String fileName = downloadedFile.getName();
                        String title = fileName.split("\\[")[0].trim();  // "[" 이전까지 자르기

                        // 제목을 변경하는 부분을 Platform.runLater()로 UI 스레드에서 처리
                        Platform.runLater(() -> primaryStage.setTitle(title));  // 제목 변경

                        playMedia(downloadedFile.toURI().toString());
                    }
                } else {
                    Platform.runLater(() -> showAlert("오디오 다운로드 실패."));
                }
            } catch (IOException | InterruptedException e) {
                Platform.runLater(() -> showAlert("다운로드 중 오류가 발생했습니다."));
                e.printStackTrace();
            }
        }).start();
    }

    private File downloadAudioFromYoutube(String url) throws IOException, InterruptedException {
        String downloadCommand = "yt-dlp -x " + url;
        if(HojaCheckOS.isWindows()) {
            ProcessBuilder processBuilder = new ProcessBuilder("cmd", "/c", downloadCommand);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            String downloadedFilePath = null;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                // 다운로드한 파일의 경로를 찾기 (yt-dlp 출력에서)
                if (line.contains("Destination:")) {
                    downloadedFilePath = line.split(":")[1].trim();
                }
            }
            process.waitFor();

            if (downloadedFilePath != null) {
                File downloadedFile = new File(downloadedFilePath);
                return downloadedFile.exists() ? downloadedFile : null;
            }
            return null;
        } else if(HojaCheckOS.isMacOS()) {
            ProcessBuilder processBuilder = new ProcessBuilder("zsh", "-c", downloadCommand);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            String downloadedFilePath = null;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                // 다운로드한 파일의 경로를 찾기 (yt-dlp 출력에서)
                if (line.contains("Destination:")) {
                    downloadedFilePath = line.split(":")[1].trim();
                }
            }
            process.waitFor();

            if (downloadedFilePath != null) {
                File downloadedFile = new File(downloadedFilePath);
                return downloadedFile.exists() ? downloadedFile : null;
            }
            return null;
        } else if(HojaCheckOS.isLinux()) {
            ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", downloadCommand);
            processBuilder.redirectErrorStream(true);
            Process process = processBuilder.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            String downloadedFilePath = null;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
                // 다운로드한 파일의 경로를 찾기 (yt-dlp 출력에서)
                if (line.contains("Destination:")) {
                    downloadedFilePath = line.split(":")[1].trim();
                }
            }
            process.waitFor();

            if (downloadedFilePath != null) {
                File downloadedFile = new File(downloadedFilePath);
                return downloadedFile.exists() ? downloadedFile : null;
            }
            return null;
        } else {
            showAlert("지원하지 않는 운영체제입니다.");
        }
        return null;
    }

    private boolean isSupportedAudioFormat(File file) {
        String fileName = file.getName().toLowerCase();
        return fileName.endsWith(".mp3") || fileName.endsWith(".wav") ||
                fileName.endsWith(".aac") || fileName.endsWith(".flac") ||
                fileName.endsWith(".m4a") || fileName.endsWith(".ogg") ||
                fileName.endsWith(".wma");
    }

    private boolean isFFmpegInstalled() {
        ProcessBuilder processBuilder = new ProcessBuilder("ffmpeg", "-version");
        try {
            Process process = processBuilder.start();
            return process.waitFor() == 0;
        } catch (Exception e) {
            return false;
        }
    }

    private File convertToWav(String inputFilePath) {
        try {
            File inputFile = new File(inputFilePath);
            String dirPath = inputFile.getParent();
            String tempWavFilePath = dirPath + File.separator + ".temp.wav";
            String ffmpegCommand = String.format("ffmpeg -i \"%s\" -acodec pcm_s16le -ar 44100 -y \"%s\"", inputFilePath, tempWavFilePath);
            if(HojaCheckOS.isWindows()) {
                ProcessBuilder processBuilder = new ProcessBuilder("cmd", "/c", ffmpegCommand);
                processBuilder.redirectErrorStream(true);
                Process process = processBuilder.start();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println(line);
                    }
                }
                process.waitFor();
                if (process.exitValue() == 0) {
                    return new File(tempWavFilePath);
                }
                System.err.println("FFmpeg 변환 실패");
                return null;
            } else if(HojaCheckOS.isMacOS()) {
                ProcessBuilder processBuilder = new ProcessBuilder("zsh", "-c", ffmpegCommand);
                processBuilder.redirectErrorStream(true);
                Process process = processBuilder.start();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println(line);
                    }
                }
                process.waitFor();
                if (process.exitValue() == 0) {
                    return new File(tempWavFilePath);
                }
                System.err.println("FFmpeg 변환 실패");
                return null;
            } else if(HojaCheckOS.isLinux()) {
                ProcessBuilder processBuilder = new ProcessBuilder("bash", "-c", ffmpegCommand);
                processBuilder.redirectErrorStream(true);
                Process process = processBuilder.start();
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.out.println(line);
                    }
                }
                process.waitFor();
                if (process.exitValue() == 0) {
                    return new File(tempWavFilePath);
                }
                System.err.println("FFmpeg 변환 실패");
                return null;
            } else {
                showAlert("지원하지 않는 운영체제입니다.");
            }
            return null;
        } catch (IOException | InterruptedException ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private void playMedia(String filePath) {
        System.out.println("[Musica] 파일: " + filePath);
        Media media = new Media(filePath);
        mediaPlayer = new MediaPlayer(media);
        setMediaPlayerListeners();
        mediaPlayer.play();
        downloadButton.setDisable(true);
        stopButton.setDisable(false);
        pauseButton.setDisable(false);
    }

    private void playConvertedFile(File tempFile) {
        Media media = new Media(tempFile.toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        setMediaPlayerListeners();
        mediaPlayer.play();
        downloadButton.setDisable(true);
        stopButton.setDisable(false);
        pauseButton.setDisable(false);
    }

    private void setMediaPlayerListeners() {
        mediaPlayer.setOnEndOfMedia(() -> {
            progressBar.setProgress(0.0);
            System.out.println("재생 완료");
            if (currentTempFile != null) {
                currentTempFile.delete(); // 임시 파일 삭제
            }
            // 정지 후 버튼 상태 변경
            Platform.runLater(() -> {
                downloadButton.setDisable(false);
                downloadButton.setText("다운로드");
                stopButton.setDisable(true);
            });
        });

        mediaPlayer.currentTimeProperty().addListener((obs, oldTime, newTime) -> Platform.runLater(() -> {
            double totalDuration = mediaPlayer.getTotalDuration().toSeconds();
            if (totalDuration > 0.0) {
                progressBar.setProgress(newTime.toSeconds() / totalDuration);
            }
        }));

        mediaPlayer.setOnPlaying(() -> System.out.println("재생 중..."));
    }

    private void stopMusic() {
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            if (currentTempFile != null) {
                currentTempFile.delete(); // 임시 파일 삭제
            }
            // 정지 후 버튼 상태 변경
            downloadButton.setDisable(false);
            downloadButton.setText("다운로드");
            stopButton.setDisable(true);
            pauseButton.setDisable(true);
        }
    }

    private void pauseOrResumeMusic() {
        if (mediaPlayer != null) {
            if (mediaPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                mediaPlayer.pause();
            } else if (mediaPlayer.getStatus() == MediaPlayer.Status.PAUSED) {
                mediaPlayer.play();
            }
        }
    }

    private void handleProgressBarClick(javafx.scene.input.MouseEvent event) {
        double x = event.getX();
        double width = progressBar.getWidth();
        double progress = x / width;
        if (mediaPlayer != null && mediaPlayer.getTotalDuration().toSeconds() > 0.0) {
            mediaPlayer.seek(mediaPlayer.getTotalDuration().multiply(progress));
        }
    }

    private void showAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("경고");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void showCopyrightWarning() {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle("저작권 안내");
        alert.setHeaderText("이 프로그램을 사용하기 전 필수 확인 사항");

        // 긴 메시지를 담을 TextArea 생성
        TextArea textArea = new TextArea();
        textArea.setEditable(false);  // 텍스트 수정 불가능
        textArea.setWrapText(true);   // 텍스트 자동 줄바꿈
        textArea.setText(
                "이 프로그램은 개인 용도 및 정당한 이용을 위한 목적에만 사용해야 합니다.\n"
                        + "저작권법을 위반하여 불법적인 콘텐츠를 다운로드하거나 배포하는 행위는 금지됩니다.\n"
                        + "사용자는 프로그램을 사용함으로써 이에 동의한 것으로 간주됩니다."
        );

        // Alert에 TextArea를 설정
        alert.getDialogPane().setContent(textArea);

        // 알림 창 표시
        alert.showAndWait();
    }

}
