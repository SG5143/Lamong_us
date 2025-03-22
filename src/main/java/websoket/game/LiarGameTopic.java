package websoket.game;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

public enum LiarGameTopic {
    과일(List.of("망고", "수박", "파인애플", "바나나", "포도")),
    동물(List.of("기린", "하마", "코알라", "펭귄", "수달")),
    음식(List.of("김치찌개", "된장찌개", "불고기", "초밥", "떡볶이")),
    국가(List.of("대한민국", "프랑스", "일본", "독일", "미국")),
    스포츠(List.of("축구", "농구", "야구", "배구", "골프")),
    악기(List.of("피아노", "기타", "드럼", "바이올린", "플루트")),
    직업(List.of("의사", "변호사", "경찰", "요리사", "소방관")),
    교통수단(List.of("비행기", "자동차", "기차", "자전거", "버스")),
    영화장르(List.of("공포", "코미디", "액션", "SF", "멜로")),
    계절(List.of("봄", "여름", "가을", "겨울")),
    색상(List.of("빨강", "파랑", "초록", "노랑", "보라")),
    전자제품(List.of("스마트폰", "노트북", "냉장고", "세탁기", "텔레비전")),
    게임장르(List.of("RPG", "FPS", "MOBA", "RTS", "퍼즐")),
    행성(List.of("수성", "금성", "지구", "화성", "목성")),
    악세서리(List.of("반지", "목걸이", "귀걸이", "팔찌", "시계")),
    음료(List.of("커피", "녹차", "콜라", "오렌지주스", "우유")),
    가전제품(List.of("전자레인지", "청소기", "에어컨", "선풍기", "히터")),
    운동(List.of("조깅", "헬스", "요가", "필라테스", "수영")),
    무기(List.of("검", "활", "총", "방패", "도끼"));

    private final List<String> words;
    private static final Random RANDOM = new Random();

    LiarGameTopic(List<String> words) {
        this.words = words;
    }

    public List<String> getWords() {
        return words;
    }

    public static Map<String, String> getRandomTopicAndWord() {
        LiarGameTopic[] topics = values();
        LiarGameTopic randomTopic = topics[RANDOM.nextInt(topics.length)];
        String randomWord = randomTopic.words.get(RANDOM.nextInt(randomTopic.words.size()));

        Map<String, String> result = new HashMap<>();
        result.put("topic", randomTopic.name());
        result.put("keyword", randomWord);
        return result;
    }
}
