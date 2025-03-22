package websoket;

import org.json.JSONObject;

public class JsonUtil {
	
	// 타입을 저장하고  2n개의 파라미터를 받아 키-값 형태의 JSON 오브젝트 생성 후 반환
    public static JSONObject createJsonMessage(String type, Object... keyValuePairs) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("type", type);
        for (int i = 0; i < keyValuePairs.length; i += 2) {
            jsonObject.put(String.valueOf(keyValuePairs[i]), keyValuePairs[i + 1]);
        }
        return jsonObject;
    }
    
}