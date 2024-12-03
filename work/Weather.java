package work;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Weather {
    private String dbUrl = "jdbc:oracle:thin:@localhost:1521:orcl"; // DB 연결 URL
    private String dbUser = "C##TestUser"; // DB 사용자 이름
    private String dbPassword = "1234"; // DB 비밀번호
    private String loggedInUserId; // 로그인한 사용자의 ID
    private String currentWeatherInfo; // 현재 날씨 정보
    private int winCount; // 사용자의 승리 횟수

    public Weather(String userId) {
        this.loggedInUserId = userId; // 로그인한 사용자 ID 저장
        fetchUserAddressAndWeather(); // 사용자의 주소와 날씨 정보 조회
    }

    // 데이터베이스에서 로그인한 사용자의 주소를 가져와 날씨 정보를 조회하는 메서드
    private void fetchUserAddressAndWeather() {
        try {
            // 데이터베이스 연결
            Connection conn = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
            String query = "SELECT ADDRESS, WIN_COUNT FROM USERS WHERE USERNAME = ?"; // WIN_COUNT 추가
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, loggedInUserId);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                String address = rs.getString("ADDRESS");
                winCount = rs.getInt("WIN_COUNT"); // WIN_COUNT 가져오기

                if (address != null && !address.isEmpty()) {
                    // 주소를 좌표로 변환해 날씨 정보 조회
                    getCoordinatesAndWeather(address);
                } else {
                    currentWeatherInfo = "사용자의 주소 정보가 없습니다.";
                }
            } else {
                currentWeatherInfo = "사용자를 찾을 수 없습니다.";
            }

            rs.close();
            pstmt.close();
            conn.close();

        } catch (Exception e) {
            e.printStackTrace();
            currentWeatherInfo = "데이터베이스 오류: " + e.getMessage();
        }
    }

    // 주소로부터 좌표를 가져와 날씨 정보를 조회하는 메서드
    private void getCoordinatesAndWeather(String address) {
        try {
            // ')'의 인덱스를 찾음
            int closingParenthesisIndex = address.indexOf(")");
            String cleanedAddress;

            if (closingParenthesisIndex != -1) {
                // ')' 이후의 문자열을 가져옴 (쉼표 또는 공백 제거)
                cleanedAddress = address.substring(closingParenthesisIndex + 1).trim();
            } else {
                cleanedAddress = address.trim(); // ')'가 없는 경우 전체 문자열을 사용
            }

            // VWorld API로 좌표 변환
            String apikey = "BD6B6700-0DCE-32BD-A17E-19F9CF781AFE"; // VWorld API Key
            String searchType = "parcel";
            String epsg = "epsg:4326";

            StringBuilder sb = new StringBuilder("https://api.vworld.kr/req/address");
            sb.append("?service=address");
            sb.append("&request=getCoord");
            sb.append("&format=json");
            sb.append("&crs=" + epsg);
            sb.append("&key=" + apikey);
            sb.append("&type=" + searchType);
            sb.append("&address=" + URLEncoder.encode(cleanedAddress, StandardCharsets.UTF_8)); // 주소 인코딩

            URL url = new URL(sb.toString());
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), StandardCharsets.UTF_8));

            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObj = (JSONObject) jsonParser.parse(reader);
            JSONObject responseObj = (JSONObject) jsonObj.get("response");
            JSONObject resultObj = (JSONObject) responseObj.get("result");

            if (resultObj != null) {
                JSONObject pointObj = (JSONObject) resultObj.get("point");
                String lon = pointObj.get("x").toString(); // 경도
                String lat = pointObj.get("y").toString(); // 위도

                // 좌표 변환이 완료되면 해당 좌표의 날씨 정보 조회
                getWeatherInfo(lat, lon); // 날씨 정보 조회
            } else {
                currentWeatherInfo = "좌표 변환에 실패하였습니다. 주소를 확인해주세요: " + address;
            }

        } catch (Exception e) {
            e.printStackTrace();
            currentWeatherInfo = "좌표 변환 중 오류가 발생했습니다: " + e.getMessage() + " 주소: " + address;
        }
    }
    

    // 좌표로부터 날씨 정보를 조회하는 메서드
    private void getWeatherInfo(String lat, String lon) {
        try {
            // OpenWeatherMap API 호출 URL
            String apiKey = "544386bfb70bc693913c868a6554e291"; // OpenWeatherMap API Key
            String urlstr = "http://api.openweathermap.org/data/2.5/weather?"
                    + "lat=" + lat + "&lon=" + lon
                    + "&appid=" + apiKey;

            URL url = new URL(urlstr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) { // 정상적으로 응답을 받은 경우
                BufferedReader bf = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder result = new StringBuilder();
                String line;

                // 응답 데이터를 문자열로 변환
                while ((line = bf.readLine()) != null) {
                    result.append(line);
                }

                // 문자열을 JSON으로 파싱
                JSONParser jsonParser = new JSONParser();
                JSONObject jsonObj = (JSONObject) jsonParser.parse(result.toString());

                // 날씨 정보 출력
                StringBuilder weatherInfo = new StringBuilder();

                // 도시 이름 (null 체크)
                String cityName = (String) jsonObj.get("name");
                if (cityName != null) {
                    weatherInfo.append("도시: ").append(cityName).append("\n");
                } else {
                    weatherInfo.append("도시 정보가 제공되지 않았습니다.\n");
                }

                // 날씨 설명 (null 체크)
                JSONArray weatherArray = (JSONArray) jsonObj.get("weather");
                if (weatherArray != null && !weatherArray.isEmpty()) {
                    JSONObject weatherObj = (JSONObject) weatherArray.get(0);
                    weatherInfo.append("날씨: ").append(weatherObj.get("main")).append("\n");
                } else {
                    weatherInfo.append("날씨 정보가 제공되지 않았습니다.\n");
                }

                // 온도 정보 (섭씨온도로 변환 필요) (null 체크)
                JSONObject mainArray = (JSONObject) jsonObj.get("main");
                if (mainArray != null) {
                    double ktemp = Double.parseDouble(mainArray.get("temp").toString());
                    double temp = ktemp - 273.15;
                    weatherInfo.append(String.format("온도: %.2f°C\n", temp));
                } else {
                    weatherInfo.append("온도 정보가 제공되지 않았습니다.\n");
                }

                currentWeatherInfo = weatherInfo.toString(); // 현재 날씨 정보를 저장

                bf.close();
            } else {
                // 서버 응답이 실패한 경우
                currentWeatherInfo = "날씨 정보를 가져오지 못했습니다.";
            }
            conn.disconnect();

        } catch (Exception e) {
            e.printStackTrace();
            currentWeatherInfo = "날씨 정보를 가져오는 중 오류가 발생했습니다: " + e.getMessage();
        }
    }

    // 현재 날씨 정보와 승리 횟수를 반환하는 메서드
    public String getCurrentWeatherInfo() {
        return currentWeatherInfo + "\n승리 횟수: " + winCount; // 승리 횟수 추가
    }

    // 로그인한 사용자 ID 가져오는 메서드
    public String getLoggedInUserId() {
        return loggedInUserId;
    }
}