package org.techtown.push.send;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;//메세지전송을 위한 라이브러리
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    EditText sendPush;//내용
    TextView textView;//

    static RequestQueue requestQueue;
    static String regID = "e427tdzdMkl:APA91bE3sktUG3U3f_v4_OS2ssBFMMBYqOSBAxLteyVq1qE63xwO9JYhZcBWnsdc4QJfMivzZNuF3_CzzMEDwecpO-dSscLiGm9cGVlevSkjg763AJ788tBBFoCNtlEQAGWfJHE1BTJa";//생성된 id값

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sendPush = findViewById(R.id.sendPush);
        textView = findViewById(R.id.textView);

        Button pushButton = findViewById(R.id.PushButton);//전송버튼
        pushButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v){
                String input = sendPush.getText().toString();
                send(input);
            }
        });

        if (requestQueue == null) {
            requestQueue = Volley.newRequestQueue(getApplicationContext());//객체생성하면 자동으로 메세지 전송
        }
    }

    public void send(String input) {//이 안에서 메세지전송
        JSONObject requestData = new JSONObject();//JSONObject객체

        try {
            requestData.put("priority", "high"); //옵션 추가 우선순위 높음

            JSONObject dataObj = new JSONObject();//전송 데이터 추가
            dataObj.put("contents", input);//사용자가 입력한 데이터 추가
            requestData.put("data", dataObj);

            JSONArray idArray = new JSONArray();//idArray 새로운 객체표시
            idArray.put(0, regID);//regID값이 단말등록 ID
            requestData.put("registration_ids", idArray);//requestData객체에 추가

        } catch(Exception e) {//예외상황
            e.printStackTrace();//에러출력
        }

        sendData(requestData, new SendResponseListener() {//메세지 전송시 메서드 호출
            @Override
            public void onRequestCompleted() {
                println("onRequestCompleted() 호출됨");

            }

            @Override
            public void onRequestStarted() {
                println("onRequestStarted() 호출됨");

            }

            @Override
            public void onRequestWithError(VolleyError error) {//웹 요청 응답 에러시
                println("onRequestWithError() 호출됨");

            }
        });
    }

    public interface SendResponseListener {
        public void onRequestStarted();//아래의 3을 선언해줘서 위의 오류를 해결
        public void onRequestWithError(VolleyError error);
        public void onRequestCompleted();

    }

    public void sendData(JSONObject requestData, final SendResponseListener listener) {//final로 최종변수
        JsonObjectRequest request = new JsonObjectRequest(//Volley요청 객체 생성,데이터 설정

                Request.Method.POST, "https://fcm.googleapis.com/fcm/send",
                requestData,
                new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                listener.onRequestCompleted();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                listener.onRequestWithError(error);
            }
        }) {
            @Override//요청을 위한 파라미터 설정
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                return params;//매개변수
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Authorization", "key=AAAAT4v0XW8:APA91bHRsmLMLdhdRzMnfNCWJFvUxBDRKugHXorB3ezLaqvF7rxoQI2J5mljR-9ux4K9WjzyudPpX4TVZPymdM_8Nbdq47iBdzjyEbXHlEWwJsM0FtkzCcKIlkUf4vAf2bV44RXKMD5M");//파이어베이스 키값입력
                return headers;
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        request.setShouldCache(false);
        listener.onRequestStarted();
        requestQueue.add(request);
    }


    public void println(String data) {
        textView.append(data + "\n");
    }
}