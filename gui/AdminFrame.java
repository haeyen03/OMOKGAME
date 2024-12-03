package gui;

// SortKey 관련 클래스
// 정렬 순서를 설정하기 위해 필요
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Vector;
import javax.swing.table.TableRowSorter; // TableRowSorter를 사용하기 위해 필요

import dataBase.DataBase;
import gui.Dainn.SignupPanel;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import gui.Dainn.*;
import gui.Dainn.SignupPanel;

public class AdminFrame extends JFrame {

    private JTable userTable;
    private DefaultTableModel tableModel;
    private TableRowSorter<DefaultTableModel> sorter;
    private JButton addButton, viewAllButton, updateButton, deleteButton, gameRecordButton, chatRecordButton, restoreMemberButton, closeButton,plusButton;
    private DataBase mySql;
    public LoginPanel loginpanel;

    // DB Connection 설정
    private Connection getConnection() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/LoginDB?serverTimezone=UTC";
        String user = "root";
        String password = "1234";
        return DriverManager.getConnection(url, user, password);
    }

    public AdminFrame() {
    	System.out.println("1111");
        setTitle("관리자 페이지");
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setSize(1200, 800);
        getContentPane().setLayout(new BorderLayout());

        // 상단 버튼 패널
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new GridLayout(1, 8, 10, 10)); // 버튼을 가로로 배치
        viewAllButton = createButton("전체 조회");
        addButton = createButton("회원 추가");
        updateButton = createButton("수정");
        deleteButton = createButton("삭제");
        gameRecordButton = createButton("게임 랭킹");
        chatRecordButton = createButton("채팅 기록");
        restoreMemberButton = createButton("회원 복원");
        plusButton = createButton("통합 찾기");
        closeButton = createButton("닫기");

        
        topPanel.add(viewAllButton);
        topPanel.add(addButton);
        topPanel.add(updateButton);
        topPanel.add(deleteButton);
        topPanel.add(gameRecordButton);
        topPanel.add(chatRecordButton);
        topPanel.add(restoreMemberButton);
        topPanel.add(plusButton);
        topPanel.add(closeButton);


        getContentPane().add(topPanel, BorderLayout.NORTH);

        // 중앙 패널 (테이블)
        tableModel = new DefaultTableModel(new String[]{
        	    "ID", "이름", "별명", "PW", "이메일", "전화번호", "주소", "성별", "승리 횟수", "패배 횟수"
        	}, 0) {
        	    @Override
        	    public boolean isCellEditable(int row, int column) {
        	        // ID, 승리 횟수, 패배 횟수 열은 수정 불가
        	        return column != 0 && column != 8 && column != 9; 
        	        // column 0: ID, column 8: 승리 횟수, column 9: 패배 횟수
        	    }
        	};


        userTable = new JTable(tableModel);
        userTable.putClientProperty("terminateEditOnFocusLost", true); // 셀 편집 종료 시 변경 적용
        JScrollPane scrollPane = new JScrollPane(userTable);
        getContentPane().add(scrollPane, BorderLayout.CENTER);
        
        sorter = new TableRowSorter<>(tableModel);
        userTable.setRowSorter(sorter);

        // 기본 정렬: 이름 오름차순
        sorter.setSortKeys(java.util.List.of(new RowSorter.SortKey(1, SortOrder.ASCENDING)));

        // 테이블 변경 이벤트 리스너 추가
        tableModel.addTableModelListener(e -> {
            int row = e.getFirstRow();
            int column = e.getColumn();

            // 유효한 행/열인지 확인
            if (row < 0 || row >= tableModel.getRowCount() || column < 0 || column >= tableModel.getColumnCount()) {
                return; // 유효하지 않은 경우 처리 중단
            }

            // 변경된 값
            String columnName = tableModel.getColumnName(column);
            Object newValue = tableModel.getValueAt(row, column);

            // 수정된 행의 ID
            String id = tableModel.getValueAt(row, 0).toString();

            // 데이터베이스 업데이트
            updateDatabase(columnName, newValue, id);
        });



        // 버튼 이벤트 설정
        addButton.addActionListener(e -> {
            new SignupPanel(loginpanel, mySql);
        });
        viewAllButton.addActionListener(e -> viewUsers());
        updateButton.addActionListener(e -> updateUser());
        deleteButton.addActionListener(e -> deleteUser());
        gameRecordButton.addActionListener(e -> Rank());
        chatRecordButton.addActionListener(e -> Chatlog());
        restoreMemberButton.addActionListener(e -> restore());
        plusButton.addActionListener(e -> plus());
        closeButton.addActionListener(e -> dispose());
        

        setVisible(true);
    }

    

    private JButton createButton(String text) {
        JButton button = new JButton(text);
        button.setPreferredSize(new Dimension(120, 50));
        return button;
    }
    private String mapColumnName(String columnName) {
        switch (columnName) {
            case "이름": return "name";
            case "별명": return "alias";
            case "닉네임": return "alias";
            case "PW": return "password";
            case "이메일": return "email";
            case "전화번호": return "phone";
            case "주소": return "address";
            case "성별": return "sex";
            default: return columnName; // 기본적으로 그대로 사용
        }
    }
    
    public void plus() {
        // 검색 UI 구성
        JPanel panel = new JPanel(new GridLayout(2, 2, 10, 10));

        // 검색 기준 선택
        JLabel optionLabel = new JLabel("검색 기준:");
        String[] options = {"전체", "이름", "ID", "이메일", "닉네임", "전화번호"};
        JComboBox<String> optionComboBox = new JComboBox<>(options);

        // 검색 값 입력 필드
        JLabel valueLabel = new JLabel("검색 값:");
        JTextField searchField = new JTextField();

        panel.add(optionLabel);
        panel.add(optionComboBox);
        panel.add(valueLabel);
        panel.add(searchField);

        // 사용자 입력 창 표시
        int result = JOptionPane.showConfirmDialog(
            this,
            panel,
            "회원 검색",
            JOptionPane.OK_CANCEL_OPTION,
            JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return; // 사용자가 취소를 선택한 경우 종료
        }

        String selectedOption = (String) optionComboBox.getSelectedItem();
        String searchValue = searchField.getText().trim();

        // SQL 쿼리 준비
        String query;
        if ("전체".equals(selectedOption)) {
            query = "SELECT * FROM UserTable WHERE account_status = true AND (" +
                    "id LIKE ? OR name LIKE ? OR email LIKE ? OR alias LIKE ? OR phone LIKE ?)";
        } else {
            query = "SELECT * FROM UserTable WHERE account_status = true AND " +
                    mapColumnName(selectedOption) + " LIKE ?";
        }

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            if ("전체".equals(selectedOption)) {
                String searchPattern = "%" + searchValue + "%";
                stmt.setString(1, searchPattern);
                stmt.setString(2, searchPattern);
                stmt.setString(3, searchPattern);
                stmt.setString(4, searchPattern);
                stmt.setString(5, searchPattern);
            } else {
                stmt.setString(1, "%" + searchValue + "%");
            }

            try (ResultSet rs = stmt.executeQuery()) {
                // 테이블 초기화
                tableModel.setRowCount(0);

                while (rs.next()) {
                    Vector<Object> row = new Vector<>();
                    row.add(rs.getString("id"));
                    row.add(rs.getString("name"));
                    row.add(rs.getString("alias"));
                    row.add(rs.getString("password"));
                    row.add(rs.getString("email"));
                    row.add(rs.getString("phone"));
                    row.add(rs.getString("address"));
                    row.add(rs.getString("sex"));
                    row.add(rs.getInt("win_count"));
                    row.add(rs.getInt("lose_count"));
                    tableModel.addRow(row);
                }

                if (tableModel.getRowCount() == 0) {
                    JOptionPane.showMessageDialog(this, "검색 결과가 없습니다.", "알림", JOptionPane.INFORMATION_MESSAGE);
                }
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "검색 중 오류가 발생했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // 회원 추가
    private void addUser() {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "INSERT INTO UserTable (id, name, alias, password, email, phone, address, detail_address) VALUES (?, ?, ?, ?, ?, ?, ?, ?)")) {
            stmt.executeUpdate();

            JOptionPane.showMessageDialog(this, "회원 추가 완료!");
            viewUsers();

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "회원 추가 중 오류 발생!", "오류", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    // 회원 조회
    private void viewUsers() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT * FROM usertable")) {

            // 테이블 초기화
            tableModel.setRowCount(0);

            while (rs.next()) {
                // account_status가 false인 경우 건너뛰기
                if (!rs.getBoolean("account_status")) {
                    continue;
                }

                Vector<Object> row = new Vector<>();
                row.add(rs.getString("id"));
                row.add(rs.getString("name"));
                row.add(rs.getString("alias"));
                row.add(rs.getString("password"));
                row.add(rs.getString("email"));
                row.add(rs.getString("phone"));
                row.add(rs.getString("address"));
                row.add(rs.getString("sex"));
                row.add(rs.getInt("win_count"));
                row.add(rs.getInt("lose_count"));
                tableModel.addRow(row);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "회원 조회 중 오류 발생!", "오류", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }


    // 회원 수정
    private void updateUser() {
        // 패널 생성
    	 JPanel panel = new JPanel(new GridLayout(4, 2));

         // 수정할 기준 선택 콤보박스
         JLabel optionLabel = new JLabel("수정할 기준:");
         String[] options = {"회원번호", "이름", "ID"};
         JComboBox<String> optionComboBox = new JComboBox<>(options);

         // 검색 값 입력 필드
         JLabel searchLabel = new JLabel("검색할 값 입력:");
         JTextField searchField = new JTextField();

        // 수정할 항목 선택 콤보박스
        JLabel fieldLabel = new JLabel("수정할 항목:");
        String[] fields = {"이름", "전화번호", "아이디", "이메일", "주소"};
        JComboBox<String> fieldComboBox = new JComboBox<>(fields);

        // 새 값 입력 필드
        JLabel newValueLabel = new JLabel("새로운 값 입력:");
        JTextField newValueField = new JTextField();

        // 패널에 추가
        panel.add(optionLabel);
        panel.add(optionComboBox);
        panel.add(searchLabel);
        panel.add(searchField);
        panel.add(fieldLabel);
        panel.add(fieldComboBox);
        panel.add(newValueLabel);
        panel.add(newValueField);

        // 사용자 입력 창 표시
        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "회원 정보 수정",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        // 사용자가 "취소"를 누른 경우 메서드 종료
        if (result != JOptionPane.OK_OPTION) {
            return;
        }

        // 선택한 기준과 입력된 검색 값 가져오기
        String selectedOption = (String) optionComboBox.getSelectedItem();
        String searchValue = searchField.getText();

        // 선택한 수정 항목과 새로운 값 가져오기
        String selectedField = (String) fieldComboBox.getSelectedItem();
        String newValue = newValueField.getText();

        // 입력 값 검증
        if (searchValue == null || searchValue.isEmpty() || newValue == null || newValue.isEmpty()) {
            JOptionPane.showMessageDialog(this, "모든 값을 입력하세요.", "오류", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 검색 쿼리 작성
        String searchQuery;
        if (selectedOption.equals("회원번호")) {
            searchQuery = "SELECT * FROM users WHERE id = ?";
        } else if (selectedOption.equals("이름")) {
            searchQuery = "SELECT * FROM users WHERE name = ?";
        } else {
            searchQuery = "SELECT * FROM users WHERE alias = ?";
        }

        // 수정 쿼리 작성
        String updateQuery;
        switch (selectedField) {
            case "이름":
                updateQuery = "UPDATE users SET name = ? WHERE id = ?";
                break;
            case "전화번호":
                updateQuery = "UPDATE users SET phone = ? WHERE id = ?";
                break;
            case "닉네임":
                updateQuery = "UPDATE users SET alias = ? WHERE id = ?";
                break;
            case "이메일":
                updateQuery = "UPDATE users SET email = ? WHERE id = ?";
                break;
            case "주소":
                updateQuery = "UPDATE users SET address = ? WHERE id = ?";
                break;
            default:
                JOptionPane.showMessageDialog(this, "잘못된 항목 선택", "오류", JOptionPane.ERROR_MESSAGE);
                return;
        }

        try (Connection conn = getConnection();
             PreparedStatement searchStmt = conn.prepareStatement(searchQuery)) {

            // 검색 값 설정
            searchStmt.setString(1, searchValue);
            ResultSet rs = searchStmt.executeQuery();

            // 회원 정보가 조회된 경우
            if (rs.next()) {
                int userId = rs.getInt("id");

                // 수정 확인 메시지
                int choice = JOptionPane.showConfirmDialog(
                        this,
                        "회원번호: " + userId + "\n수정할 항목: " + selectedField + "\n새로운 값: " + newValue + "\n정말 수정하시겠습니까?",
                        "수정 확인",
                        JOptionPane.YES_NO_OPTION
                );

                // 사용자가 "예"를 선택한 경우 수정 진행
                if (choice == JOptionPane.YES_OPTION) {
                    try (PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
                        updateStmt.setString(1, newValue);
                        updateStmt.setInt(2, userId);
                        updateStmt.executeUpdate();
                        JOptionPane.showMessageDialog(this, "회원 정보가 수정되었습니다.");
                    }
                }

            } else {
                JOptionPane.showMessageDialog(this, "일치하는 회원 정보가 없습니다.", "오류", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "회원 정보 수정 중 오류 발생", "오류", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    //랭킹 시스템
    private void Rank() {
        // 데이터베이스에서 순위 정보를 가져옵니다.
        Vector<Vector<String>> rankData = new Vector<>();
        Vector<String> columnNames = new Vector<>();

        // 테이블의 열 이름 정의
        columnNames.add("순위");
        columnNames.add("승");
        columnNames.add("패");
        columnNames.add("승률");
        columnNames.add("아이디");
        columnNames.add("이름");
        columnNames.add("닉네임");

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, name, alias, win_count, lose_count, " +
                     "ROUND((win_count / NULLIF((win_count + lose_count), 0)) * 100, 2) AS win_rate " +
                     "FROM UserTable " +
                     "WHERE account_status = true " +
                     "ORDER BY win_count DESC")) {

            int rank = 1;
            while (rs.next()) {
                Vector<String> row = new Vector<>();

                int winCount = rs.getInt("win_count");
                int loseCount = rs.getInt("lose_count");

                // 승과 패가 모두 0인 경우 UNRANK로 설정
                if (winCount == 0 && loseCount == 0) {
                    row.add("UNRANK");
                } else {
                    row.add(String.valueOf(rank++)); // 순위
                }

                // 순서대로 데이터 추가
                row.add(String.valueOf(winCount));          // 승
                row.add(String.valueOf(loseCount));         // 패
                row.add(winCount + loseCount == 0 ? "-" : rs.getString("win_rate") + "%"); // 승률
                row.add(rs.getString("id"));               // 아이디
                row.add(rs.getString("name"));             // 이름
                row.add(rs.getString("alias"));            // 닉네임

                rankData.add(row);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "순위 데이터를 불러오는 중 오류 발생!", "오류", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return;
        }

        // JTable 생성
        JTable rankTable = new JTable(new DefaultTableModel(rankData, columnNames));
        JScrollPane scrollPane = new JScrollPane(rankTable);

        // JDialog 생성
        JDialog rankDialog = new JDialog(this, "회원 순위", true);
        rankDialog.setLayout(new BorderLayout());
        rankDialog.setSize(800, 600);

        // 닫기 버튼 생성
        JButton closeButton = new JButton("닫기");
        closeButton.addActionListener(e -> rankDialog.dispose());

        // 닫기 버튼을 오른쪽 위에 배치
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(closeButton, BorderLayout.EAST);

        // JDialog 구성
        rankDialog.add(topPanel, BorderLayout.NORTH);
        rankDialog.add(scrollPane, BorderLayout.CENTER);

        // 다이얼로그 표시
        rankDialog.setLocationRelativeTo(this);
        rankDialog.setVisible(true);
    }
    private void Chatlog() {
        // 데이터베이스에서 room_id와 sender 데이터를 가져옵니다.
        Vector<Vector<String>> roomData = new Vector<>();
        Vector<String> columnNames = new Vector<>();

        columnNames.add("번호");
        columnNames.add("참여자");

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT room_id, GROUP_CONCAT(DISTINCT sender ORDER BY sender SEPARATOR ', ') AS participants FROM chatlogtable GROUP BY room_id")) {

            int index = 1; // 번호를 위한 인덱스
            while (rs.next()) {
                Vector<String> row = new Vector<>();
                row.add(String.valueOf(index++)); // 번호
                row.add(rs.getString("participants")); // 참여자
                roomData.add(row);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "채팅 로그 데이터를 불러오는 중 오류 발생!", "오류", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return;
        }

        // JTable 생성
        JTable roomTable = new JTable(new DefaultTableModel(roomData, columnNames)) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // 테이블 셀 편집 비활성화
            }
        };
        JScrollPane scrollPane = new JScrollPane(roomTable);

        // JDialog 생성
        JDialog roomDialog = new JDialog(this, "채팅방 목록", true);
        roomDialog.setLayout(new BorderLayout());
        roomDialog.setSize(800, 400);

        // 닫기 버튼 생성
        JButton closeButton = new JButton("닫기");
        closeButton.addActionListener(e -> roomDialog.dispose());

        // 상단 패널
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(closeButton, BorderLayout.EAST);

        // JDialog 구성
        roomDialog.add(topPanel, BorderLayout.NORTH);
        roomDialog.add(scrollPane, BorderLayout.CENTER);

        // 더블클릭 이벤트 설정
        roomTable.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                if (e.getClickCount() == 2) { // 더블클릭 감지
                    int selectedRow = roomTable.getSelectedRow();
                    if (selectedRow == -1) {
                        JOptionPane.showMessageDialog(roomDialog, "채팅방을 선택하세요.", "오류", JOptionPane.ERROR_MESSAGE);
                        return;
                    }

                    // 선택한 참여자와 room_id 가져오기
                    String participants = roomTable.getValueAt(selectedRow, 1).toString();
                    String roomId = null;
                    try (Connection conn = getConnection();
                         PreparedStatement stmt = conn.prepareStatement(
                                 "SELECT room_id FROM chatlogtable GROUP BY room_id HAVING GROUP_CONCAT(DISTINCT sender ORDER BY sender SEPARATOR ', ') = ? LIMIT 1")) {
                        stmt.setString(1, participants);
                        ResultSet rs = stmt.executeQuery();
                        if (rs.next()) {
                            roomId = rs.getString("room_id");
                        }
                    } catch (SQLException ex) {
                        JOptionPane.showMessageDialog(roomDialog, "채팅방 정보를 가져오는 중 오류 발생!", "오류", JOptionPane.ERROR_MESSAGE);
                        ex.printStackTrace();
                    }

                    if (roomId != null) {
                        roomDialog.dispose(); // 현재 다이얼로그 닫기
                        showChatlogWithSearch(roomId); // 채팅 로그 표시 (검색 기능 포함)
                    }
                }
            }
        });

        roomDialog.setLocationRelativeTo(this);
        roomDialog.setVisible(true);
    }
    private void showChatlogWithSearch(String roomId) {
        Vector<String> chatMessages = new Vector<>();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(
                     "SELECT sender, message, timestamp FROM chatlogtable WHERE room_id = ? ORDER BY timestamp ASC")) {
            stmt.setString(1, roomId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String sender = rs.getString("sender");
                String message = rs.getString("message");
                String timestamp = rs.getString("timestamp");
                chatMessages.add(sender + " [" + timestamp + "] " + message);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "채팅 로그를 불러오는 중 오류 발생!", "오류", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return;
        }

        // JTextArea로 채팅 로그 표시
        JTextArea chatArea = new JTextArea();
        chatArea.setEditable(false);
        chatArea.setLineWrap(true); // 자동 줄바꿈 설정
        chatArea.setWrapStyleWord(true); // 단어 단위로 줄바꿈
        chatArea.setFont(new Font("맑은 고딕", Font.PLAIN, 14)); // 텍스트 글꼴 설정

        // JScrollPane으로 스크롤 가능하도록 설정
        JScrollPane chatScrollPane = new JScrollPane(chatArea);

        // 채팅 메시지 초기화
        for (String message : chatMessages) {
            chatArea.append(message + "\n"); // 메시지 줄바꿈 추가
        }

        // 검색 상태를 추적할 변수
        final int[] currentSearchIndex = {0}; // 검색 결과 위치 추적
        final String[] currentSearchText = {""}; // 현재 검색어 추적

        // 검색 패널 생성
        JPanel searchPanel = new JPanel(new BorderLayout());
        JTextField searchField = new JTextField();
        JButton searchButton = new JButton("검색");
        JButton nextButton = new JButton("다음 검색");
        JButton cancelButton = new JButton("검색 취소");

        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(searchButton, BorderLayout.WEST);
        searchPanel.add(nextButton, BorderLayout.EAST);
        searchPanel.add(cancelButton, BorderLayout.SOUTH);

        // JDialog 생성
        JDialog chatDialog = new JDialog(this, "채팅 로그", true);
        chatDialog.setLayout(new BorderLayout());
        chatDialog.setSize(800, 600);

        // 닫기 버튼 생성
        JButton closeButton = new JButton("닫기");
        closeButton.addActionListener(e -> chatDialog.dispose());

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(closeButton, BorderLayout.EAST);
        topPanel.add(searchPanel, BorderLayout.CENTER);

        chatDialog.add(topPanel, BorderLayout.NORTH);
        chatDialog.add(chatScrollPane, BorderLayout.CENTER);

        // 검색 기능 구현
        searchButton.addActionListener(e -> {
            String searchText = searchField.getText().trim();
            if (searchText.isEmpty()) {
                JOptionPane.showMessageDialog(chatDialog, "검색어를 입력하세요.", "오류", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String chatText = chatArea.getText();
            int index = chatText.toLowerCase().indexOf(searchText.toLowerCase(), currentSearchIndex[0]);

            if (index != -1) {
                chatArea.setCaretPosition(index); // 스크롤 위치 이동
                chatArea.select(index, index + searchText.length()); // 검색어 강조
                chatArea.requestFocus();
                currentSearchIndex[0] = index + searchText.length(); // 다음 검색 시작점 갱신
                currentSearchText[0] = searchText; // 현재 검색어 저장
            } else {
                JOptionPane.showMessageDialog(chatDialog, "검색어의 끝에 도달했습니다.", "알림", JOptionPane.INFORMATION_MESSAGE);
                currentSearchIndex[0] = 0; // 다시 처음부터 검색 가능하도록 초기화
            }
        });

        // 다음 검색 기능 구현
        nextButton.addActionListener(e -> {
            if (currentSearchText[0].isEmpty()) {
                JOptionPane.showMessageDialog(chatDialog, "먼저 검색어를 입력하고 검색하세요.", "오류", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String chatText = chatArea.getText();
            int index = chatText.toLowerCase().indexOf(currentSearchText[0].toLowerCase(), currentSearchIndex[0]);

            if (index != -1) {
                chatArea.setCaretPosition(index); // 스크롤 위치 이동
                chatArea.select(index, index + currentSearchText[0].length()); // 검색어 강조
                chatArea.requestFocus();
                currentSearchIndex[0] = index + currentSearchText[0].length(); // 다음 검색 시작점 갱신
            } else {
                JOptionPane.showMessageDialog(chatDialog, "검색어의 끝에 도달했습니다.", "알림", JOptionPane.INFORMATION_MESSAGE);
                currentSearchIndex[0] = 0; // 다시 처음부터 검색 가능하도록 초기화
            }
        });

        // 검색 취소 기능
        cancelButton.addActionListener(e -> {
            chatArea.setCaretPosition(0); // 스크롤을 맨 위로 이동
            chatArea.select(0, 0); // 선택 해제
            currentSearchIndex[0] = 0; // 검색 인덱스 초기화
            currentSearchText[0] = ""; // 검색어 초기화
        });

        chatDialog.setLocationRelativeTo(this);
        chatDialog.setVisible(true);
    }

     // 선택된 room_id에 맞는 채팅 로그를 표시하는 메서드
    private void showChatlog(String roomId) {
        StringBuilder chatContent = new StringBuilder();

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT sender, message, timestamp FROM chatlogtable WHERE room_id = ? ORDER BY timestamp ASC")) {
            stmt.setString(1, roomId);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String sender = rs.getString("sender");
                String timestamp = rs.getString("timestamp");
                String message = rs.getString("message");
                chatContent.append(String.format("%s [%s]: %s%n", sender, timestamp, message));
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "채팅 로그 데이터를 불러오는 중 오류 발생!", "오류", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return;
        }

        // JDialog 생성
        JDialog chatDialog = new JDialog(this, "채팅 내용", true);
        chatDialog.setLayout(new BorderLayout());
        chatDialog.setSize(600, 400);

        // 채팅 내용 표시
        JTextArea chatTextArea = new JTextArea(chatContent.toString());
        chatTextArea.setEditable(false); // 수정 불가
        JScrollPane chatScrollPane = new JScrollPane(chatTextArea);

        // 닫기 버튼 생성
        JButton closeButton = new JButton("닫기");
        closeButton.addActionListener(e -> chatDialog.dispose());

        // 상단 패널
        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.add(closeButton, BorderLayout.EAST);

        // JDialog 구성
        chatDialog.add(topPanel, BorderLayout.NORTH);
        chatDialog.add(chatScrollPane, BorderLayout.CENTER);

        chatDialog.setLocationRelativeTo(this);
        chatDialog.setVisible(true);
    }


    
    private void restore() {
        // 데이터베이스에서 account_status가 false인 회원 데이터를 가져옵니다.
        Vector<Vector<String>> inactiveUsers = new Vector<>();
        Vector<String> columnNames = new Vector<>();

        columnNames.add("회원번호");
        columnNames.add("이름");
        columnNames.add("ID");
        columnNames.add("이메일");
        columnNames.add("전화번호");

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT id, name, alias, email, phone FROM UserTable WHERE account_status = false")) {

            while (rs.next()) {
                Vector<String> row = new Vector<>();
                row.add(rs.getString("id"));
                row.add(rs.getString("name"));
                row.add(rs.getString("alias"));
                row.add(rs.getString("email"));
                row.add(rs.getString("phone"));
                inactiveUsers.add(row);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "비활성화된 회원 조회 중 오류 발생!", "오류", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
            return;
        }

        // 비활성화된 회원이 없을 경우 알림 메시지 표시
        if (inactiveUsers.isEmpty()) {
            JOptionPane.showMessageDialog(this, "비활성화된 회원이 없습니다.", "알림", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        


        // JTable 생성
        JTable table = new JTable(new DefaultTableModel(inactiveUsers, columnNames));
        JScrollPane scrollPane = new JScrollPane(table);

        // JDialog 생성
        JDialog dialog = new JDialog(this, "비활성화된 회원 복원", true);
        dialog.setLayout(new BorderLayout());
        dialog.setSize(600, 400);

        // 하단 버튼 패널
        JPanel buttonPanel = new JPanel();
        JButton deleteButton = new JButton("복원");
        JButton cancelButton = new JButton("취소");
        buttonPanel.add(deleteButton);
        buttonPanel.add(cancelButton);

        dialog.add(scrollPane, BorderLayout.CENTER);
        dialog.add(buttonPanel, BorderLayout.SOUTH);

        // 버튼 이벤트 설정
        deleteButton.addActionListener(e -> {
            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(dialog, "복원할 회원을 선택하세요.", "오류", JOptionPane.ERROR_MESSAGE);
                return;
            }

            String selectedId = table.getValueAt(selectedRow, 0).toString();

            // 선택한 회원 ID의 account_status를 true로 복원
            try (Connection conn = getConnection();
                 PreparedStatement stmt = conn.prepareStatement("UPDATE UserTable SET account_status = true WHERE id = ?")) {

                stmt.setString(1, selectedId);
                int rowsUpdated = stmt.executeUpdate();

                if (rowsUpdated > 0) {
                    JOptionPane.showMessageDialog(dialog, "회원이 복원되었습니다!");
                    viewUsers(); // 테이블 갱신
                    dialog.dispose(); // 복원이 완료되면 다이얼로그 닫기
                } else {
                    JOptionPane.showMessageDialog(dialog, "회원 복원 중 오류 발생!", "오류", JOptionPane.ERROR_MESSAGE);
                }

            } catch (SQLException ex) {
                JOptionPane.showMessageDialog(dialog, "회원 복원 중 오류 발생!", "오류", JOptionPane.ERROR_MESSAGE);
                ex.printStackTrace();
            }
        });

        cancelButton.addActionListener(e -> dialog.dispose());

        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }


    // 회원 삭제
    private void deleteUser() {
        // 패널 생성
        JPanel panel = new JPanel(new GridLayout(3, 2));

        // 삭제 기준 선택 콤보박스
        JLabel optionLabel = new JLabel("삭제할 기준:");
        String[] options = {"ID", "이름", "닉네임"};
        JComboBox<String> optionComboBox = new JComboBox<>(options);

        // 검색 값 입력 필드
        JLabel searchLabel = new JLabel("검색할 값 입력:");
        JTextField searchField = new JTextField();

        // 패널에 추가
        panel.add(optionLabel);
        panel.add(optionComboBox);
        panel.add(searchLabel);
        panel.add(searchField);

        // 사용자 입력 창 표시
        int result = JOptionPane.showConfirmDialog(
                this,
                panel,
                "회원 삭제",
                JOptionPane.OK_CANCEL_OPTION,
                JOptionPane.PLAIN_MESSAGE
        );

        if (result != JOptionPane.OK_OPTION) {
            return; // 사용자가 취소를 선택한 경우 종료
        }

        // 선택한 삭제 기준과 검색 값 가져오기
        String selectedOption = (String) optionComboBox.getSelectedItem();
        String searchValue = searchField.getText();

        if (searchValue == null || searchValue.isEmpty()) {
            JOptionPane.showMessageDialog(this, "검색 값을 입력하세요.", "오류", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 기준에 따른 SQL 쿼리 설정
        String query;
        if (selectedOption.equals("ID")) {
            query = "UPDATE UserTable SET account_status = false WHERE id = ?";
        } else if (selectedOption.equals("이름")) {
            query = "UPDATE UserTable SET account_status = false WHERE name = ?";
        } else { // ID
            query = "UPDATE UserTable SET account_status = false WHERE alias = ?";
        }

        // 데이터베이스 업데이트
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, searchValue);
            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(this, "회원이 비활성화되었습니다!");
                viewUsers(); // 테이블 갱신
            } else {
                JOptionPane.showMessageDialog(this, "일치하는 회원 정보가 없습니다.", "오류", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "회원 비활성화 중 오류 발생!", "오류", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
 // 데이터베이스 업데이트
    private void updateDatabase(String columnName, Object newValue, String id) {
        // 열 이름을 매핑
        String dbColumnName = mapColumnName(columnName);

        String query = "UPDATE UserTable SET " + dbColumnName + " = ? WHERE id = ?";

        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setObject(1, newValue); // 변경된 값 설정
            stmt.setString(2, id);       // 해당 ID 설정
            int rowsUpdated = stmt.executeUpdate();

            if (rowsUpdated > 0) {
                JOptionPane.showMessageDialog(this, "데이터가 성공적으로 업데이트되었습니다!");
            } else {
                JOptionPane.showMessageDialog(this, "데이터 업데이트에 실패했습니다.", "오류", JOptionPane.ERROR_MESSAGE);
            }

        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "데이터베이스 업데이트 중 오류 발생!", "오류", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }
    public class MainApplication {
        public static void main(String[] args) {
        	System.out.println("22222");
            SwingUtilities.invokeLater(CertFrame::new);
        }
    }

    public static class CertFrame extends JFrame {
        public CertFrame() {
            setTitle("인증 화면");
            setSize(300, 150);
            setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            setLayout(new BorderLayout());

            // UI 구성
            JLabel label = new JLabel("관리자 인증");
            JTextField idField = new JTextField(10);
            JPasswordField passwordField = new JPasswordField(10);
            JButton loginButton = new JButton("로그인");

            JPanel panel = new JPanel();
            panel.add(new JLabel("ID:"));
            panel.add(idField);
            panel.add(new JLabel("PW:"));
            panel.add(passwordField);
            panel.add(loginButton);

            add(label, BorderLayout.NORTH);
            add(panel, BorderLayout.CENTER);

            // 로그인 버튼 이벤트
            loginButton.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    String id = idField.getText();
                    String password = new String(passwordField.getPassword());

                    // 인증 로직 (간단한 예제: "admin" / "1234"로 인증 성공 처리)
                    if (id.equals("admin") && password.equals("1234")) {
                        JOptionPane.showMessageDialog(CertFrame.this, "인증 성공!", "알림", JOptionPane.INFORMATION_MESSAGE);
                        dispose(); // 현재 인증 창 닫기
                        SwingUtilities.invokeLater(AdminFrame::new); // AdminFrame 실행
                    } else {
                        JOptionPane.showMessageDialog(CertFrame.this, "인증 실패!", "오류", JOptionPane.ERROR_MESSAGE);
                    }
                }
            });

            setLocationRelativeTo(null); // 화면 중앙에 표시
            setVisible(true);
        }
    }
}
