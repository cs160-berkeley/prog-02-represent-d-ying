package cs160.prog2;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private Button mZipButton;
    private Button mCurLocButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mZipButton = (Button) findViewById(R.id.zip_btn);
        mCurLocButton = (Button) findViewById(R.id.cur_loc_btn);

        mZipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText mZIP = (EditText) findViewById(R.id.zip);
                String ZIP = mZIP.getText().toString();

                if (ZIP.length() == 5) {
                    Intent sendIntent = new Intent(getBaseContext(), PhoneToWatchService.class);
                    sendIntent.putExtra("ZIP", ZIP);
                    startService(sendIntent);

                    Intent intent = new Intent(getBaseContext(), RepsList.class);
                    intent.putExtra("LOC", ZIP);
                    startActivity(intent);
                } else {
                    Toast.makeText(getApplicationContext(), "Please enter a valid ZIP code", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mCurLocButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String ZIP = "00000";

                Intent sendIntent = new Intent(getBaseContext(), PhoneToWatchService.class);
                sendIntent.putExtra("ZIP", ZIP);
                startService(sendIntent);

                Intent intent = new Intent(getBaseContext(), RepsList.class);
                intent.putExtra("LOC", ZIP);
                startActivity(intent);
            }
        });
    }
}
