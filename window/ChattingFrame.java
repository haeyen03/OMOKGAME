package gui.KakaoTalk;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Vector;

import dataBase.DataBase;
import gameClient.ClientLobby;
import protocolData.ChatData;
import protocolData.Protocol;
import gameClient.ClientInterface;
import java.sql.PreparedStatement;

// StyledDocument
import javax.swing.text.StyledDocument;
import javax.swing.text.SimpleAttributeSet;
import javax.swing.text.StyleConstants;

// 마우스
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;


public class ChattingFrame extends JFrame {
    private String myName;
    private String targetName;
    private String chatRoomName;

    private DataBase mySql;
    private ClientInterface client;

    private JTextPane chatLog; // JTextPane으로 변경
    private JTextField inputField;
    private JButton sendButton;
    private JButton emojiButton;
    private JButton fileButton;
    private JTextField searchField;
    JButton colorPaletteButton;
    
    private static Font customFont;


 // 텍스트 색상 변수
    private Color myMessageColor = Color.BLACK; // 기본 색상

    private static final String DOWNLOADS_FOLDER = "downloads/";

    private int roomID; // DB에서 로드한 채팅방 ID
    


    // 생성자
    public ChattingFrame(ClientInterface client, String myName, String targetName, DataBase mySql) {
        this.myName = myName;
        this.targetName = targetName;
        this.mySql = mySql;
        this.chatRoomName = targetName + "과의 채팅방";
        this.client = client;
        
        setGlobalFont("src/image/Galmuri11-Bold.ttf", 14);
        


        // 채팅 리스너 등록
        ((ClientLobby) client).setChattingListener(data -> {
            if (data.getState() == ChatData.SEND_MULTICAST) {
                appendChat(data.getName(), data.getMessage(), getCurrentTime());
            } else if (data.getState() == ChatData.SEND_EMOJI) {
                appendChat(data.getName(), data.getEmoji(), getCurrentTime());
            } else if (data.getState() == ChatData.SEND_FILE) {
                handleIncomingFile(data);
            }
        });

        setTitle(chatRoomName);
        setSize(400, 600);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setVisible(true);

        BatchGui();
        loadChatRoomDB(); // DB에서 기존 채팅 기록 로드
    }

    /** GUI 초기화 */
    private void BatchGui() {
        // 채팅 로그
        chatLog = new JTextPane();
        chatLog.setEditable(false);
        chatLog.setMargin(new Insets(5, 5, 5, 5));
        try {
            Font customFont = Font.createFont(Font.TRUETYPE_FONT, new File("src/image/Galmuri11-Bold.ttf")).deriveFont(14f);
            chatLog.setFont(customFont);
            System.out.println("폰트됨");
        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            chatLog.setFont(new Font("SansSerif", Font.PLAIN, 14)); // 기본 폰트로 설정
            System.out.println("폰트안됨");
            }

        
        JScrollPane chatScrollPane = new JScrollPane(chatLog);

        // 입력 필드
        inputField = new JTextField();

        // 전송 버튼
        sendButton = new JButton("보내기");
        sendButton.addActionListener(e -> {
            String message = inputField.getText();
            if (!message.trim().isEmpty()) {
                ChatData data = new ChatData(myName, message, ChatData.SEND_MULTICAST);
                Vector<String> v = new Vector<>();
                v.add(targetName);
                data.setUserList(v);
                client.sendMessage((Protocol) data); // 서버에 메시지 전송
                appendChat(myName, message, getCurrentTime());
                
                // 메시지 데이터베이스에 저장
                saveChatDB(message, getCurrentTime());
                
                inputField.setText("");
            }
        });


        // 이모티콘 버튼
        emojiButton = new JButton("😀");
        emojiButton.addActionListener(e -> showEmojiPopup());

        // 파일 전송 버튼
        fileButton = new JButton("📎");
        fileButton.addActionListener(e -> sendFile());

        // 검색 필드
        searchField = new JTextField();
        searchField.addActionListener(e -> {
            String searchText = searchField.getText().trim();
            if (!searchText.isEmpty()) {
                highlightText(searchText);
            }
        });
     // 팔레트 버튼 추가
        colorPaletteButton = new JButton("🎨 텍스트 색상"); // 폰트 적용
        colorPaletteButton.addActionListener(e -> {
            Color selectedColor = JColorChooser.showDialog(null, "Select a Text Color", myMessageColor);
            if (selectedColor != null) {
                myMessageColor = selectedColor;
                chatLog.setForeground(myMessageColor); // 채팅 로그 색상 변경
                inputField.setForeground(myMessageColor); // 입력 필드 색상 변경
            }
        });



        // 버튼 패널 생성 (이모티콘, 파일 버튼 묶기)
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2)); // 이모티콘 버튼과 파일 버튼을 가로로 나열
        buttonPanel.add(emojiButton);
        buttonPanel.add(fileButton);
        
        // 하단 패널
        JPanel inputPanel = new JPanel(new BorderLayout());
        inputPanel.add(buttonPanel, BorderLayout.WEST);
        inputPanel.add(inputField, BorderLayout.CENTER);
        inputPanel.add(sendButton, BorderLayout.EAST);

        // 상단 검색창 패널
        JPanel searchPanel = new JPanel(new BorderLayout());
        searchPanel.add(new JLabel("검색: "), BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(colorPaletteButton, BorderLayout.EAST);

        // 전체 레이아웃 구성
        setLayout(new BorderLayout());
        add(chatScrollPane, BorderLayout.CENTER);
        add(inputPanel, BorderLayout.SOUTH);
        add(searchPanel, BorderLayout.NORTH);
    }
    
    public static void setGlobalFont(String fontPath, int fontSize) {
        try {
            // TTF 폰트 로드
            Font globalFont = Font.createFont(Font.TRUETYPE_FONT, new File(fontPath)).deriveFont((float) fontSize);

            // UIManager를 사용해 기본 폰트 설정
            UIManager.put("Label.font", globalFont);
            UIManager.put("Button.font", globalFont);
            UIManager.put("TextField.font", globalFont);
            UIManager.put("TextArea.font", globalFont);
            UIManager.put("CheckBox.font", globalFont);
            UIManager.put("ComboBox.font", globalFont);
            // 필요한 컴포넌트를 추가적으로 설정 가능

        } catch (FontFormatException | IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, "글로벌 폰트 설정에 실패했습니다.");
        }
    }


    
    

    /** DB에서 채팅 기록 로드 */
    /** DB로부터 채팅방 정보를 불러옵니다. 
     * @notice - 과거 대화 기록을 불러오기 위함으로 창 생성 시 한 번만 호출합니다. 
     */
     private void loadChatRoomDB()
     {   
       // myName과 targetName이 속한 채팅방 번호 가져오기
       String query = String.format("SELECT room_id FROM ChatRoomTable WHERE user_id IN ('%s', '%s') "
             + "GROUP BY room_id HAVING COUNT(DISTINCT user_id) = 2", myName, targetName);

       try
       {
          Connection conn = mySql.getConnection();
          Statement stmt = conn.createStatement();

          ResultSet rset = stmt.executeQuery(query);
          /** @error ResultSet을 getString 하기 전에 rset.next()가 가능한지 확인해주세요. */
          // 만약 해당 채팅방이 없으면 생성하기
          if(!rset.next())
          {
             createChatRoom();
          }
          else
          {
             roomID = rset.getInt("room_id");
             printChatHistory(roomID);
          }
       }
       catch(SQLException ex){
          JOptionPane.showMessageDialog(this, "LoginPanel - checkIsCorrectLoginInfo() 오류", "로그인 실패", 1);
          System.out.println("SQLException" + ex);
       }      
     }

     /** DB에 채팅방 정보를 추가합니다. */
     private void createChatRoom()
     {      
        try
        {
           Connection conn = mySql.getConnection();
           Statement stmt = conn.createStatement();
        
           roomID = getNewRoomID(conn);
           
             // 2. 새로운 채팅방에 사용자 추가하기
             String insertRoomQuery = "INSERT INTO ChatRoomTable (room_id, user_id, user_room_name) VALUES (?, ?, ?)";
             PreparedStatement pstmt = conn.prepareStatement(insertRoomQuery);

             
             // myName 추가
             pstmt.setInt(1, roomID);
             pstmt.setString(2, myName);
             pstmt.setString(3, targetName + "과의 대화"); // myName이 설정한 채팅방 이름
             pstmt.executeUpdate(); // 쿼리문 실행
             
             if(!myName.equals(targetName))
             {
                 // targetName 추가
                 pstmt.setInt(1, roomID);
                 pstmt.setString(2, targetName);
                 pstmt.setString(3, myName + "과의 대화"); // targetName이 설정한 채팅방 이름          
                 pstmt.executeUpdate(); // 쿼리문 실행
             }
  
             
        } catch(SQLException ex) {
           System.out.println("SQLException" + ex);
        }

     }

     private int getNewRoomID(Connection conn) throws SQLException {
         String query = "SELECT IFNULL(MAX(room_id), 0) + 1 AS new_room_id FROM ChatRoomTable";
         try (Statement stmt = conn.createStatement();
              ResultSet rs = stmt.executeQuery(query)) {
             if (rs.next()) {
                 return rs.getInt("new_room_id");
             }
         }
         throw new SQLException("Failed to generate new room ID.");
     }

     private void saveChatDB(String message, String timestamp)
     {      
        try
        {
           Connection conn = mySql.getConnection();
           Statement stmt = conn.createStatement();
          
             // 2. 새로운 채팅방에 사용자 추가하기
             String query = "INSERT INTO chatLogTable (room_id, sender, message, timestamp) VALUES (?, ?, ?, ?)";
             PreparedStatement pstmt = conn.prepareStatement(query);
             
             pstmt.setInt(1, roomID);
             pstmt.setString(2, myName);
             pstmt.setString(3, message);
             pstmt.setString(4, timestamp);
             pstmt.executeUpdate(); // 쿼리문 실행

             
        } catch(SQLException ex) {
           System.out.println("SQLException" + ex);
        }

     }
     

     /** 과거 대화 기록을 출력합니다. */
     private void printChatHistory(int room_id)
     {
        String query = String.format("SELECT sender, message, timestamp FROM ChatLogTable WHERE room_id = '%d' ORDER BY timestamp", room_id);

         try 
         {
            Connection conn = mySql.getConnection();
            Statement stmt = conn.createStatement();
             ResultSet rset = stmt.executeQuery(query);

             // 결과 출력
             while (rset.next()) 
             {
                 String sender = rset.getString("sender");
                 String message = rset.getString("message");
                 String timestamp = rset.getString("timestamp");

                 appendChat(sender, message, timestamp);
             }

         } catch (SQLException ex) {
             System.out.println("SQLException: " + ex.getMessage());
         }
     }

     
    /** 채팅 기록 로드 */
    private void loadChatHistory(int roomID) {
        try {
            Connection conn = mySql.getConnection();
            String query = String.format("SELECT sender, message, timestamp FROM ChatLogTable WHERE room_id = '%d' ORDER BY timestamp", roomID);
            Statement stmt = conn.createStatement();
            ResultSet rset = stmt.executeQuery(query);

            while (rset.next()) {
                String sender = rset.getString("sender");
                String message = rset.getString("message");
                String timestamp = rset.getString("timestamp");
                appendChat(sender, message, timestamp);
            }
        } catch (SQLException ex) {
            ex.printStackTrace();
            JOptionPane.showMessageDialog(this, "채팅 기록 로드 중 오류 발생", "오류", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** 채팅방 생성 */
    private void createChatRoom(Connection conn) throws SQLException {
        String query = "INSERT INTO ChatRoomTable (user_id, user_room_name) VALUES (?, ?)";
        PreparedStatement pstmt = conn.prepareStatement(query);
        pstmt.setString(1, myName);
        pstmt.setString(2, targetName + "과의 대화");
        pstmt.executeUpdate();
    }




    /** @feature - 이모티콘 선택 팝업 창 */
    /** 이모티콘 선택 팝업 창 */
    private void showEmojiPopup() {
        JDialog emojiDialog = new JDialog(this, "이모티콘 선택", true);
        emojiDialog.setSize(300, 200);
        emojiDialog.setLayout(new GridLayout(3, 3, 10, 10)); // 이모티콘을 3x3 그리드로 배치

        String[] emojis = {"😀", "😂", "😍", "😎", "😢", "😡", "👍", "🙏", "🎉"};
        for (String emoji : emojis) {
            JButton emojiButton = new JButton(emoji);
            
            emojiButton.addActionListener(e -> {
                sendEmoji(emoji); // 이모티콘 전송
                emojiDialog.dispose(); // 창 닫기
            });
            emojiDialog.add(emojiButton);
        }

        emojiDialog.setLocationRelativeTo(this); // 부모 창 중앙에 표시
        emojiDialog.setVisible(true);
    }

    
    /** 이모티콘 전송 */
    private void sendEmoji(String emoji) {
        // ChatData 객체 생성
        ChatData emojiData = new ChatData(myName, emoji, ChatData.SEND_EMOJI);
        Vector<String> v = new Vector<String>();
        v.add(targetName);
        emojiData.setUserList(v);
        client.sendMessage((Protocol)emojiData); // 자신의 소켓 스트림(out)은 clientLobby에 있으므로 함수를 통해 그곳에서 out.writeObject 수행
        //saveChatDB(emojiData, getCurrentTime()); // DB에 대화내용 저장

        // 내 화면에 이모티콘 표시
        appendChat(myName, emoji, getCurrentTime());
    }


    /** 파일 전송 */
    private void sendFile() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            try {
                File file = fileChooser.getSelectedFile();
                String fileName = file.getName();
                byte[] fileData = Files.readAllBytes(file.toPath());

                ChatData fileDataPacket = new ChatData(myName, fileName, fileData, ChatData.SEND_FILE);
                Vector<String> v = new Vector<>();
                v.add(targetName);
                fileDataPacket.setUserList(v);

                client.sendMessage((Protocol) fileDataPacket);
                appendFileInfo(myName, file);

            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "파일 전송 중 오류가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
    /** 채팅 로그에 메시지 추가 (왼쪽: 상대방, 오른쪽: 나) */
    private void appendChat(String sender, String message, String timestamp) {
        StyledDocument doc = chatLog.getStyledDocument();
        SimpleAttributeSet attributes = new SimpleAttributeSet();

        try {
            // 메시지 정렬 설정
            if (sender.equals(myName)) {
                // 내가 보낸 메시지는 오른쪽 정렬
                StyleConstants.setAlignment(attributes, StyleConstants.ALIGN_RIGHT);
                StyleConstants.setForeground(attributes, myMessageColor); // 내 메시지 색상
            } else {
                // 상대방 메시지는 왼쪽 정렬
                StyleConstants.setAlignment(attributes, StyleConstants.ALIGN_LEFT);
                StyleConstants.setForeground(attributes, Color.BLUE); // 상대방 메시지 색상
            }

            // 메시지 추가
            String chatText = String.format("[%s] %s: %s\n", timestamp, sender, message);

            // 문서에 텍스트 삽입
            int start = doc.getLength();
            doc.insertString(start, chatText, attributes);
            
            // 문서의 해당 영역에 정렬 속성 적용
            doc.setParagraphAttributes(start, chatText.length(), attributes, false);

            // 스크롤을 가장 아래로 이동
            chatLog.setCaretPosition(doc.getLength());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    

    


    /** 파일 수신 처리 */
    private void handleIncomingFile(ChatData data) {
        try {
            File downloadDir = new File(DOWNLOADS_FOLDER);
            if (!downloadDir.exists()) {
                downloadDir.mkdir(); // 다운로드 폴더 생성
            }

            File file = new File(downloadDir, data.getFileName());
            Files.write(file.toPath(), data.getFileData()); // 파일 저장

            // 디버그 로그
            System.out.println("파일 수신 완료: " + file.getName());

            // 파일 정보 표시
            appendFileInfo(data.getName(), file);
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "파일 저장 중 오류가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
        }
    }


    /** 파일 정보 표시
     * JTextPane의 HTML을 직접 업데이트 하는 방식
     */
    /** 파일 정보 표시 */
    private void appendFileInfo(String sender, File file) {
        try {
            StyledDocument doc = chatLog.getStyledDocument();

            // 파일 정보 텍스트 생성
            String fileInfo = String.format("%s: [파일] %s\n", sender, file.getName());

            // 현재 텍스트 길이를 저장 (이후 하이퍼링크 범위 지정)
            int startOffset = doc.getLength();

            // 텍스트 추가
            doc.insertString(doc.getLength(), fileInfo, null);

            // 하이퍼링크 스타일 설정
            SimpleAttributeSet linkAttributes = new SimpleAttributeSet();
            StyleConstants.setForeground(linkAttributes, Color.BLUE); // 파란색
            StyleConstants.setUnderline(linkAttributes, true); // 밑줄

            // 하이퍼링크 범위에 스타일 적용
            doc.setCharacterAttributes(startOffset + sender.length() + 3, file.getName().length() + 8, linkAttributes, false);

            // 클릭 이벤트 추가
            chatLog.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    try {
                        int pos = chatLog.viewToModel2D(e.getPoint());
                        if (pos >= startOffset && pos <= startOffset + file.getName().length() + 8) {
                            downloadFile(file);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            });

            // 스크롤을 가장 아래로 이동
            chatLog.setCaretPosition(doc.getLength());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** 파일 다운로드 */
    private void downloadFile(File file) {
        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setSelectedFile(new File(file.getName())); // 기본 파일 이름 설정
        int userChoice = fileChooser.showSaveDialog(this);

        if (userChoice == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            try {
                Files.copy(file.toPath(), selectedFile.toPath());
                JOptionPane.showMessageDialog(this, "파일이 저장되었습니다.", "다운로드 완료", JOptionPane.INFORMATION_MESSAGE);
            } catch (IOException ex) {
                ex.printStackTrace();
                JOptionPane.showMessageDialog(this, "파일 저장 중 오류가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
            }
        }
    }




    /** 검색 기능 */
    private void highlightText(String searchText) {
        try {
            String content = chatLog.getText();
            String highlighted = content.replaceAll(
                "(?i)(" + searchText + ")",
                "<span style='background: yellow;'>$1</span>"
            );
            chatLog.setText(highlighted);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** 현재 시간 가져오기 */
    private String getCurrentTime() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }
}

