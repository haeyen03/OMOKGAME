package work;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MainGame extends JFrame {
    private JLabel weatherLabel; // 날씨 정보를 표시할 라벨
    private Weather Weather; // Weather 인스턴스
    private JPanel chatPanel; // 채팅 패널
    private JTextField chatInput; // 채팅 입력 필드
    private JLabel opponentFaceLabel; // 상대방 얼굴 레이블
    private JLabel myFaceLabel; // 내 얼굴 레이블

    public MainGame(String userId) 
    {
        Weather = new Weather(userId);

        setTitle("오목 게임");
        setSize(1000, 900); // 프레임 크기 고정
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null); // 중앙에 위치

        // 전체 패널
        JPanel mainPanel = new JPanel();
        mainPanel.setLayout(new BorderLayout());

        // 오목 게임 패널 (7 비율)
        JPanel gamePanel = new JPanel();
        gamePanel.setPreferredSize(new Dimension(700, 900)); // 게임 패널 크기 조정
        gamePanel.setBackground(Color.LIGHT_GRAY); // 게임 패널 배경색
        mainPanel.add(gamePanel, BorderLayout.CENTER); // 메인 패널에 게임 패널 추가

        // 플레이어 정보 패널 (3 비율)
        JPanel playerPanel = new JPanel();
        playerPanel.setPreferredSize(new Dimension(300, 900));
        playerPanel.setLayout(new BorderLayout());

        // 플레이어 얼굴 패널 (3 비율)
        JPanel playerFacePanel = new JPanel();
        playerFacePanel.setLayout(new GridLayout(1, 2)); // 두 플레이어 얼굴
        playerFacePanel.setBorder(BorderFactory.createTitledBorder("플레이어 얼굴"));

        // 왼쪽: 상대방 얼굴
        opponentFaceLabel = new JLabel(); // 상대방 얼굴 라벨
        opponentFaceLabel.setHorizontalAlignment(SwingConstants.CENTER);
        opponentFaceLabel.setPreferredSize(new Dimension(100, 100)); // 크기 설정
        playerFacePanel.add(opponentFaceLabel);

        // 오른쪽: 나의 얼굴
        myFaceLabel = new JLabel(); // 내 얼굴 라벨
        myFaceLabel.setHorizontalAlignment(SwingConstants.CENTER);
        myFaceLabel.setPreferredSize(new Dimension(100, 100)); // 크기 설정
        playerFacePanel.add(myFaceLabel);

        // 고정된 이미지 경로에서 내 얼굴 이미지 설정
        setMyFace("C:\\angang\\java\\사람\\나르시스.png");

        playerPanel.add(playerFacePanel, BorderLayout.NORTH); // 플레이어 얼굴 패널 추가

        // 채팅 패널 (5 비율)
        chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS)); // 세로 레이아웃 유지
        chatPanel.setBorder(BorderFactory.createTitledBorder("채팅"));

        // 채팅 영역을 위한 스크롤 패널
        JPanel chatAreaPanel = new JPanel();
        chatAreaPanel.setLayout(new BoxLayout(chatAreaPanel, BoxLayout.Y_AXIS)); // 세로로 쌓이도록 변경
        JScrollPane scrollPane = new JScrollPane(chatAreaPanel);
        scrollPane.setPreferredSize(new Dimension(250, 600)); // 스크롤 패널 크기 설정
        chatPanel.add(scrollPane); // 스크롤 패널을 채팅 패널에 추가

        // 채팅 입력 필드
        chatInput = new JTextField(20); // 채팅 입력 필드 크기 설정
        chatInput.setPreferredSize(new Dimension(250, 30));
        chatPanel.add(chatInput); // 입력 필드를 채팅 패널에 추가
        
        // 채팅 전송 버튼
        JButton	chatButton = new JButton("전송");
        chatPanel.add(chatButton);
        
        chatButton.addActionListener(e -> {
        	String message = chatInput.getText();
        	if(!message.trim().isEmpty())
        	{
        		JLabel messageLabel = new JLabel(message);
        		chatAreaPanel.add(messageLabel);
        		chatAreaPanel.revalidate(); // 패널 업데이트
        		chatAreaPanel.repaint(); // 패널 다시 그리기
        		
        		saveMessageToDatabase(message);
        	}
        });

        // 이모티콘 버튼 패널
        JPanel emojiPanel = new JPanel();
        emojiPanel.setLayout(new FlowLayout()); // 흐름 레이아웃 추가

        // 이모티콘 버튼 추가 (12개)
        String[] emojiFiles = {
            "C:\\angang\\java\\표정\\샤방.png", "C:\\angang\\java\\표정\\씨익.png", 
            "C:\\angang\\java\\표정\\와우.png", "C:\\angang\\java\\표정\\이것뭐에요.png", 
            "C:\\angang\\java\\표정\\지름신.png", "C:\\angang\\java\\표정\\초진지.png", 
            "C:\\angang\\java\\표정\\하트쪽.png", "C:\\angang\\java\\표정\\허걱.png", 
            "C:\\angang\\java\\표정\\홍홍.png", "C:\\angang\\java\\표정\\광기.png", 
            "C:\\angang\\java\\표정\\냉랭.png", "C:\\angang\\java\\표정\\눈물.png"
        }; // 이모티콘 이미지 파일 경로

        for (String fileName : emojiFiles) {
            ImageIcon icon = new ImageIcon(fileName); // 이미지 파일 로드
            JButton emojiButton = new JButton(); // 버튼 생성
            emojiButton.setPreferredSize(new Dimension(30, 30)); // 버튼 크기 고정

            // 아이콘 크기를 30px × 30px로 조정
            Image img = icon.getImage();
            Image scaledImg = img.getScaledInstance(30, 30, Image.SCALE_SMOOTH);
            emojiButton.setIcon(new ImageIcon(scaledImg)); // 아이콘을 버튼에 설정

            // 버튼 클릭 시 채팅 영역에 이모티콘 추가
            emojiButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    // 클릭한 이모티콘을 50px × 50px 크기로 리사이즈하면서 비율 유지
                    int targetSize = 50; // 목표 크기
                    int originalWidth = img.getWidth(null);
                    int originalHeight = img.getHeight(null);
                    double aspectRatio = (double) originalWidth / (double) originalHeight; // 비율 계산

                    // 크기 조정
                    int newWidth, newHeight;
                    if (aspectRatio > 1) {
                        newWidth = targetSize;
                        newHeight = (int) (targetSize / aspectRatio); // 비율에 맞춰 높이 계산
                    } else {
                        newHeight = targetSize;
                        newWidth = (int) (targetSize * aspectRatio); // 비율에 맞춰 너비 계산
                    }

                    Image resizedImg = img.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH); // 크기 조정
                    JLabel emojiLabel = new JLabel(new ImageIcon(resizedImg)); // 리사이즈된 이미지로 JLabel 생성
                    emojiLabel.setPreferredSize(new Dimension(50, 50)); // 채팅 영역에 이모티콘 크기 설정
                    emojiLabel.setMaximumSize(new Dimension(50, 50)); // 최대 크기 설정
                    chatAreaPanel.add(emojiLabel); // 채팅 영역에 추가
                    chatAreaPanel.revalidate(); // 레이아웃 갱신
                    chatAreaPanel.repaint(); // 패널 다시 그리기
                    scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum()); // 스크롤 바를 아래로
                }
            });

            emojiPanel.add(emojiButton); // 패널에 버튼 추가
        }

        chatPanel.add(emojiPanel); // 이모티콘 패널 추가

        playerPanel.add(chatPanel, BorderLayout.CENTER); // 채팅 패널 추가

        // 날씨 패널 (2 비율)
        JPanel weatherPanel = new JPanel();
        weatherPanel.setLayout(new BorderLayout());
        weatherPanel.setBorder(BorderFactory.createTitledBorder("날씨 정보"));
        weatherLabel = new JLabel("날씨 정보: " + Weather.getCurrentWeatherInfo()); // 날씨 정보 라벨 초기화
        weatherPanel.add(weatherLabel, BorderLayout.CENTER); // 날씨 정보 추가

        playerPanel.add(weatherPanel, BorderLayout.SOUTH); // 날씨 패널 추가

        // 패널을 메인 패널에 추가
        mainPanel.add(playerPanel, BorderLayout.EAST); // 오른쪽에 플레이어 패널 추가

        // 메인 패널을 프레임에 추가
        add(mainPanel);
        setVisible(true);
    }
    
    private void saveMessageToDatabase(String message)
    {
    	
    }

    // 내 얼굴을 고정된 경로에서 설정하는 메서드
    private void setMyFace(String filePath) {
        ImageIcon myFaceIcon = new ImageIcon(filePath); // 고정된 경로에서 이미지 아이콘 생성
        myFaceLabel.setIcon(myFaceIcon); // 내 얼굴 레이블에 아이콘 설정
    }

    // 상대방 얼굴을 설정하는 메서드
    public void setPlayerFaces(ImageIcon opponentFace) {
        opponentFaceLabel.setIcon(opponentFace);
    }
}
