package fr.mowh.palk;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.net.Socket;
import java.net.URISyntaxException;
import java.util.ArrayList;

public class ChatActivity extends AppCompatActivity {

    public ListView messageList;
    public EditText messageInput;
    public Button sendButton;
    public JSONObject message = new JSONObject();
    public String name;

    public Emitter.Listener updateMessages = new Emitter.Listener() {
        @Override
        public void call(Object... args) {
            JSONArray messagesList = (JSONArray) args[0];
            String[] messages = toStringArray(messagesList);
            final ArrayAdapter<String> adapter = new ArrayAdapter<>(ChatActivity.this, android.R.layout.simple_list_item_1, messages);
            messageList.setAdapter(adapter);
        }
    };

    public static String[] toStringArray(JSONArray array) {
        if(array==null)
            return null;

        String[] arr=new String[array.length()];
        for(int i=0; i<arr.length; i++) {
            arr[i]=array.optString(i);
        }
        return arr;
    }


    public com.github.nkzawa.socketio.client.Socket socket;
    {
        try {
            socket= IO.socket("https://palkapp.glitch.me");
        } catch (URISyntaxException e) {}
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        Bundle bundle = getIntent().getExtras();
        name = bundle.getString("name");

        socket.connect();

        messageList = findViewById(R.id.message_list);
        messageInput = findViewById(R.id.message_input);
        sendButton = findViewById(R.id.send_button);



        socket.on("update messages", updateMessages);

        sendButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!TextUtils.isEmpty(messageInput.getText().toString().trim())) {
                            try {
                                message.put("message", messageInput.getText().toString().trim());
                                message.put("name", name);
                            } catch (JSONException e) {}
                            messageInput.setText("");
                            socket.emit("new message", message);
                        }
                    }
                }
        );
    }
}