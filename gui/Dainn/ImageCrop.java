
package gui.Dainn;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;

public class ImageCrop extends JPanel {
    private BufferedImage image;
    private int imageX = 0, imageY = 0; // 이미지의 위치
    private int cropSize = 80; // 고정된 프사 크기
    private boolean dragging = false; 
    private int prevX, prevY; // 이전 마우스 좌표

    public ImageCrop(String imagePath) {
        try {
            image = ImageIO.read(new File(imagePath));  // 큰 이미지 로드
        } catch (Exception e) {
            e.printStackTrace();
        }

        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                dragging = true;
                prevX = e.getX();
                prevY = e.getY();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                dragging = false;
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
        	/*
            @Override
            public void mouseDragged(MouseEvent e) {
                if (dragging) {
                    // 마우스 이동에 따라 이미지의 좌표를 변경
                    int deltaX = e.getX() - prevX;
                    int deltaY = e.getY() - prevY;
                    imageX += deltaX;
                    imageY += deltaY;
                    prevX = e.getX();
                    prevY = e.getY();
                    repaint();
                }
            }
            */
            
            public void mouseDragged(MouseEvent e) {
                if (dragging) {
                    // 마우스 드래그 시 이미지를 마우스 위치로 이동
                    imageX = e.getX() - (image.getWidth() / 2); 
                    imageY = e.getY() - (image.getHeight() / 2);
                    repaint(); // 변경된 위치에 이미지를 다시 그리기
                }
            }
            
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // 이미지를 현재 이미지 좌표에 그립니다.
        g.drawImage(image, imageX, imageY, null);
        
        // 고정된 30x30 크기의 사각형을 중앙에 그립니다.
        g.setColor(Color.RED);
        int cropX = (getWidth() - cropSize) / 2;
        int cropY = (getHeight() - cropSize) / 2;
        g.drawRect(cropX, cropY, cropSize, cropSize);
    }

    public BufferedImage cropImage() {
        // 고정된 30x30 영역에 해당하는 이미지 부분을 크롭
        try {
            int cropX = (getWidth() - cropSize) / 2 - imageX; // 이미지 내에서의 크롭 좌표
            int cropY = (getHeight() - cropSize) / 2 - imageY;
            BufferedImage cropped = image.getSubimage(cropX, cropY, cropSize, cropSize);
            return cropped;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}