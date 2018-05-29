package fr.mowh.palk;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.github.nkzawa.socketio.client.IO;

import java.net.URISyntaxException;

public class AskNameActivity extends AppCompatActivity {

    public EditText nameInput;
    public Button enterChat;
    public String name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_name);

        nameInput = findViewById(R.id.name_input);
        enterChat = findViewById(R.id.enter_chat_button);
        name = nameInput.getText().toString().trim();

        enterChat.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //if (TextUtils.isEmpty(name)) {} else {
                            Intent intent= new Intent(AskNameActivity.this, ChatActivity.class);
                            intent.putExtra("name", name);
                            startActivity(intent);
                        //}
                    }
                }
        );
    }
}
