package java.com.example.myapplication123;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.myapplication123.R;
import com.example.myapplication123.WeatherActivity;

public class WeatherMain extends AppCompatActivity {

    private EditText editLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weatheractivity_main);
        editLocation = findViewById(R.id.editLocation);

        Button btnSearch = findViewById(R.id.btnSearch);
        btnSearch.setOnClickListener(v -> {
            String location = editLocation.getText().toString().trim();
            Intent intent = new Intent(WeatherMain.this, WeatherActivity.class);
            intent.putExtra("location", location);
            startActivity(intent);
        });
    }
}
