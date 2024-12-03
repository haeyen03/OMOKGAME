package gui.Dainn;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class ZipcodePopup  extends Observer
{
    private JTextField searchField;
    private JTextArea resultArea;
    private JButton searchButton;

    private JList<String> zipcodeList; // 결과를 보여줄 JList
    JFrame zipcodeFrame = null;
    
    private String selectedAddress = null;
    private boolean bIsSelectZipcode = false;
    
    public String Get_Address()
    {
    	return selectedAddress;
    }
    
    public boolean IsSelectZipcodeTrigger()
    {
    	boolean bIsSelect = bIsSelectZipcode;
    	if(bIsSelect)
    	{
    		bIsSelectZipcode = false;
    	}
    	return bIsSelect;
    }
    
    public ZipcodePopup() {
    	zipcodeFrame = new JFrame("우편번호 조회기");
    	zipcodeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    	zipcodeFrame.setSize(400, 300);

        searchField = new JTextField(20);
        resultArea = new JTextArea(10, 30);
        searchButton = new JButton("검색");

        searchButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                searchZipcode();
            }
        });
        
        JPanel panel = new JPanel();
        panel.add(new JLabel("검색어:"));
        panel.add(searchField);
        panel.add(searchButton);
        zipcodeFrame.getContentPane().add(BorderLayout.NORTH, panel);
        zipcodeFrame.getContentPane().add(new JScrollPane(resultArea), BorderLayout.CENTER);

        zipcodeFrame.setVisible(true);
    }

    public void searchZipcode() 
    {
        String query = searchField.getText();
        int page = 1; // 원하는 페이지 번호
        int listSize = 50; // 페이지당 출력할 목록 수

        List<String> zipcodeList = new ArrayList<>();
        int[] n = new int[2]; // 검색한 전체 목록 개수 및 현재 페이지 번호

        // find 함수는 Zipcode의 클래스 함수
        String errorMessage = Zipcode.find(query, page, listSize, zipcodeList, n);
        
        // 클릭 기능을 위해 List를 JList에 담기
        DefaultListModel<String> model = new DefaultListModel<>();
        for (int i = 0; i < zipcodeList.size() ; i += 3) 
        {
        	// 우편번호 - 도로명 주소/지번 주소
        	String fullZipcode = zipcodeList.get(i) + ": " + zipcodeList.get(i + 1) + zipcodeList.get(i + 2);
            model.addElement(fullZipcode);
        }
        JList<String> zipcodeJlist = new JList(model);
        
        if (errorMessage != null) 
        {
            resultArea.setText("오류: " + errorMessage);
        } 
        else 
        {
        	Observer obs = this;
        	// 더블 클릭 이벤트로 선택된 주소 처리 (JList는 MouseListener 가능)
        	// addMouseListener 안에서 this하면, new MousdeAdapter 타입임 (독립적인가봄)
            zipcodeJlist.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseClicked(MouseEvent e) {
                    if (e.getClickCount() == 2) // 더블 클릭 감지
                    { 
                        selectedAddress = zipcodeJlist.getSelectedValue(); // 선택된 주소 가져오기
                        if (selectedAddress != null) 
                        {
                            // 선택된 주소를 처리
                        	onEvent(obs);
                            System.out.println("선택된 주소: " + selectedAddress);
                           // zipcodeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                            zipcodeFrame.dispose();
                        }
                    }
                }
            });
            
            zipcodeJlist.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);	//하나만 선택 될 수 있도록
            
            JScrollPane scrolled=new JScrollPane(zipcodeJlist);
    		scrolled.setBorder(BorderFactory.createEmptyBorder(0,10,10,10)); 
    		
    		zipcodeFrame.add(scrolled,"Center");	//가운데 list
    		
    		zipcodeFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    		zipcodeFrame.setSize(620,400);
    		zipcodeFrame.setLocationRelativeTo(null);	//창 가운데 위치
    		zipcodeFrame.setVisible(true);

            
            // JFieldArea에 set 해서 출력하니까 포커스가 묻혀서 뭔짓을 해도 JList가 안 눌림
    		/*
            StringBuilder resultBuilder = new StringBuilder();
            for (int i = 0; i < zipcodeJlist.getModel().getSize() ; i += 3) 
            {
                resultBuilder.append("우편번호: ").append(zipcodeJlist.getModel().getElementAt(i))
                        .append(", 도로명주소: ").append(zipcodeJlist.getModel().getElementAt(i + 1))
                        .append(", 지번주소: ").append(zipcodeJlist.getModel().getElementAt(i + 2)).append("\n");
            }
            resultArea.setText(resultBuilder.toString());
            resultArea.setEditable(false);
            zipcodeJlist.requestFocusInWindow();
    		 */
        }
        
    }

    
}

class Zipcode
{
    //* 공공데이타포털(http://www.data.go.kr) 오픈 API 이용
   
    // 서비스명 : 통합검색 5자리 우편번호 조회서비스
    // 새 우편번호(2015-08-01부터) 오픈 API 주소
    // http://openapi.epost.go.kr/postal/retrieveNewAdressAreaCdSearchAllService/retrieveNewAdressAreaCdSearchAllService/getNewAddressListAreaCdSearchAll
   
    // [in] s : 검색어 (도로명주소[도로명/건물명] 또는 지번주소[동/읍/면/리])
    // [in] p : 읽어올 페이지(1부터), l : 한 페이지당 출력할 목록 수(최대 50까지)
    // [out] v[i*3 +0]=우편번호, v[i*3 +1]=도로명주소, v[i*3 +2]=지번주소, v.Count/3=표시할 목록 수
    // [out] n[0]=검색한 전체 목록(우편번호) 개수, n[1]=읽어온 페이지(1부터)
    // 반환값 : 에러메시지, null == OK
    public static String find(String s, int p, int l, List<String> zipcodeList, int[] n)
    {
        HttpURLConnection con = null;
       
        try
        {
            URL url = new URL(
            "http://openapi.epost.go.kr/postal/retrieveNewAdressAreaCdSearchAllService/retrieveNewAdressAreaCdSearchAllService/getNewAddressListAreaCdSearchAll"
            + "?ServiceKey=0akQFHVjUlIgV%2BTWb7yl1HRtLcaoAaud4RgM7HK3r%2BoskF5SKTv0xNWWgauy%2FLKcsIqaJCOpfRsHdQwyVB9%2FOg%3D%3D" // 서비스키
            + "&countPerPage=" + l // 페이지당 출력될 개수를 지정(최대 50)
            + "&currentPage=" + p // 출력될 페이지 번호
            + "&srchwrd=" + URLEncoder.encode(s,"UTF-8") // 검색어
            );

            con = (HttpURLConnection) url.openConnection();
            con.setRequestProperty("Accept-language", "ko");
           
            DocumentBuilderFactory fac = DocumentBuilderFactory.newInstance();
            DocumentBuilder bd = fac.newDocumentBuilder();
            Document doc = bd.parse(con.getInputStream());
           
            boolean bOk = false; // <successYN>Y</successYN> 획득 여부
            s = null; // 에러 메시지
           
            String nn;
            Node nd;
            NodeList ns = doc.getElementsByTagName("cmmMsgHeader");
            if (ns.getLength() > 0)
            for (nd = ns.item(0).getFirstChild(); nd != null; nd = nd.getNextSibling())
            {
                nn = nd.getNodeName();
               
                if (!bOk)
                {
                    if (nn.equals("successYN")) // 성공 여부
                    {
                        if (nd.getTextContent().equals("Y")) bOk = true; // 검색 성공
                    }
                    else if (nn.equals("errMsg")) // 에러 메시지
                    {
                        s = nd.getTextContent();
                    }
                }
                else
                {
                    if (nn.equals("totalCount")) // 전체 검색수
                    {
                        n[0] = Integer.parseInt(nd.getTextContent());
                    }
                    else if (nn.equals("currentPage")) // 현재 페이지 번호
                    {
                        n[1] = Integer.parseInt(nd.getTextContent());
                    }
                }
            }
           
            if (bOk)
            {
                ns = doc.getElementsByTagName("newAddressListAreaCdSearchAll");
                for (p = 0; p < ns.getLength(); p++)
                for (nd = ns.item(p).getFirstChild(); nd != null; nd = nd.getNextSibling())
                {
                // nn = nd.getNodeName();
                // if (nn.equals("zipNo") || // 우편번호
                //  nn.equals("lnmAdres") || // 도로명 주소
                //  nn.equals("rnAdres")) // 지번 주소
                // {
                		zipcodeList.add(nd.getTextContent());
                        System.out.println(nd.getTextContent());
                // }
                }
            }
           
            if (s == null)
            { // OK!
                if (zipcodeList.size() < 3)
                    s = "검색결과가 없습니다.";
            }
        }
        catch(Exception e)
        {
            s = e.getMessage();
        }
       
        if (con != null)
            con.disconnect();

        return s;
    }
}
