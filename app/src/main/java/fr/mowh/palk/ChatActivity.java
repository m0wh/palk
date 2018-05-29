package fr.mowh.palk;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
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
    public Activity me;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);

        me = this;

        try {
            socket = IO.socket("https://palkapp.glitch.me");
            socket.connect();

            socket.on("update message", new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    me.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            JSONArray data = (JSONArray) args[0];
                            String[] messages = toStringArray(data);
                            final ArrayAdapter<String> adapter = new ArrayAdapter<>(ChatActivity.this, android.R.layout.simple_list_item_1, messages);
                            messageList.setAdapter(adapter);
                        }
                    });
                }
            });
        } catch (URISyntaxException e) {
            Log.wtf("debug",e.toString());
        }

        Bundle bundle = getIntent().getExtras();
        assert bundle != null;
        final String name = bundle.getString("name");

        // messageInput.setText(name);

        socket.connect();

        messageList = findViewById(R.id.message_list);
        sendButton = findViewById(R.id.send_button);
        messageInput = findViewById(R.id.message_input);

        sendButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String messageInputText = messageInput.getText().toString().trim();
                        if (!TextUtils.isEmpty(messageInputText)) {
                            try {
                                message.put("message", messageInputText);
                                message.put("name", name);
                            } catch (JSONException ignored) {}
                            messageInput.setText("");
                            socket.emit("new message", message);
                        }
                    }
                }
        );
    }
}