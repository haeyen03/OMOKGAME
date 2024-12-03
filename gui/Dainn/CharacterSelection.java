package gui.Dainn;

import javax.swing.*;
import java.awt.*;
import gameClient.ClientInterface;
import protocolData.LobbyData;

public class CharacterSelection extends JFrame {
    private String selectedCharacter = null;
    private ClientInterface client;
    private String roomName; // 방 이름
    
    private boolean isRoomKing;

    public CharacterSelection(ClientInterface client, String roomName, boolean isRoomKing) {
        this.client = client;
        this.roomName = roomName; // 방 이름 저장
        this.isRoomKing = isRoomKing;

        setTitle("캐릭터 선택");
        setSize(400, 300); // 크기 조정
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(1, 3)); // 캐릭터 버튼을 가로로 배치

        //JLabel label = new JLabel("캐릭터를 선택하세요:", SwingConstants.CENTER);
        //label.setFont(new Font("Arial", Font.BOLD, 16));
        //add(label, BorderLayout.NORTH);

        JButton KawasakiButton = new JButton(new ImageIcon("src/image/character/Kawasaki.png"));
        KawasakiButton.setPreferredSize(new Dimension(100, 130));
        KawasakiButton.setBorderPainted(false);
        KawasakiButton.setContentAreaFilled(false);
        KawasakiButton.setFocusPainted(false);
        KawasakiButton.addActionListener(e -> {
            selectedCharacter = "Kawasaki";
            openGameRoom();
        });

        JButton DededeButton = new JButton(new ImageIcon("src/image/character/Dedede.png"));
        DededeButton.setPreferredSize(new Dimension(100, 130));
        DededeButton.setBorderPainted(false);
        DededeButton.setContentAreaFilled(false);
        DededeButton.setFocusPainted(false);
        DededeButton.addActionListener(e -> {
            selectedCharacter = "Dedede";
            openGameRoom();
        });

        JButton WaddleDButton = new JButton(new ImageIcon("src/image/character/WaddleD.png"));
        WaddleDButton.setPreferredSize(new Dimension(100, 130));
        WaddleDButton.setBorderPainted(false);
        WaddleDButton.setContentAreaFilled(false);
        WaddleDButton.setFocusPainted(false);
        WaddleDButton.addActionListener(e -> {
            selectedCharacter = "WaddleD";
            openGameRoom();
        });
        JButton MetaKnightButton = new JButton(new ImageIcon("src/image/character/MetaKnight.png"));
        MetaKnightButton.setPreferredSize(new Dimension(100, 130));
        MetaKnightButton.setBorderPainted(false);
        MetaKnightButton.setContentAreaFilled(false);
        MetaKnightButton.setFocusPainted(false);
        MetaKnightButton.addActionListener(e -> {
            selectedCharacter = "MetaKnight";
            openGameRoom();
        });

        JButton BlueKerbyButton = new JButton(new ImageIcon("src/image/character/BlueKerby.png"));
        BlueKerbyButton.setPreferredSize(new Dimension(100, 130));
        BlueKerbyButton.setBorderPainted(false);
        BlueKerbyButton.setContentAreaFilled(false);
        BlueKerbyButton.setFocusPainted(false);
        BlueKerbyButton.addActionListener(e -> {
            selectedCharacter = "BlueKerby";
            openGameRoom();
        });

        JButton KerbyButton = new JButton(new ImageIcon("src/image/character/Kerby.png"));
        KerbyButton.setPreferredSize(new Dimension(100, 130));
        KerbyButton.setBorderPainted(false);
        KerbyButton.setContentAreaFilled(false);
        KerbyButton.setFocusPainted(false);
        KerbyButton.addActionListener(e -> {
            selectedCharacter = "Kerby";
            openGameRoom();
        });

        JButton MikeButton = new JButton(new ImageIcon("src/image/character/Mike.png"));
        MikeButton.setPreferredSize(new Dimension(100, 130));
        MikeButton.setBorderPainted(false);
        MikeButton.setContentAreaFilled(false);
        MikeButton.setFocusPainted(false);
        MikeButton.addActionListener(e -> {
            selectedCharacter = "Mike";
            openGameRoom();
        });

        JButton YelloKerbyButton = new JButton(new ImageIcon("src/image/character/YelloKerby.png"));
        YelloKerbyButton.setPreferredSize(new Dimension(100, 130));
        YelloKerbyButton.setBorderPainted(false);
        YelloKerbyButton.setContentAreaFilled(false);
        YelloKerbyButton.setFocusPainted(false);
        YelloKerbyButton.addActionListener(e -> {
            selectedCharacter = "YelloKerby";
            openGameRoom();
        });

        // 추가된 버튼 추가
        add(MetaKnightButton);
        add(BlueKerbyButton);
        add(KerbyButton);
        add(MikeButton);
        add(YelloKerbyButton);


        // 버튼 추가
        add(KawasakiButton);
        add(DededeButton);
        add(WaddleDButton);

        setVisible(true);
        
        setLayout(new GridLayout(2, 4)); // 2행 4열로 배치
    }

    private void openGameRoom() {
        if (selectedCharacter != null) {
            // 캐릭터와 방 이름 정보를 서버로 전송
           if(isRoomKing)
              client.sendMessage(roomName + "|" + selectedCharacter, LobbyData.CREATE_ROOM);
           else
              client.sendMessage(roomName + "|" + selectedCharacter, LobbyData.ENTER_TO_ROOM);
           
            dispose(); // 캐릭터 선택 창 닫기
        }
    }
}
