package br.com.gustavosimionato.simionatohome;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.gson.JsonElement;

import java.util.Locale;
import java.util.Map;
 
import ai.api.AIListener;
import ai.api.android.AIConfiguration;
import ai.api.android.AIService;
import ai.api.model.AIError;
import ai.api.model.AIResponse;
import ai.api.model.Result;

public class MainActivity extends AppCompatActivity implements AIListener {
    private AIConfiguration config;
    private AIService service;
    private Button button;
    private TextView textView;
    TextToSpeech textToSpeech;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        config = new AIConfiguration("17c4960fb0c24b52b793a6ed207c776b",
                AIConfiguration.SupportedLanguages.Portuguese,
                AIConfiguration.RecognitionEngine.System);
        service = AIService.getService(this, config);
        service.setListener(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        button = (Button) findViewById(R.id.button);
        textView = (TextView) findViewById(R.id.textView);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                service.startListening();
            }
        });

        textToSpeech = new TextToSpeech(getApplicationContext(), new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    textToSpeech.setLanguage(Locale.getDefault());
                }
            }
        });
    }

    @Override
    public void onResult(AIResponse response) {
        Result result = response.getResult();
        String textoResposta = result.getFulfillment().getSpeech();
        String parameterString = "";
        if (result.getParameters() != null && !result.getParameters().isEmpty()) {
            for (final Map.Entry<String, JsonElement> entry : result.getParameters().entrySet()) {
                parameterString += "(" + entry.getKey() + ", " + entry.getValue() + ") ";
            }
        }
        textToSpeech.speak(textoResposta, TextToSpeech.QUEUE_FLUSH, null);
        textView.setText(textoResposta);
    }

    @Override
    public void onError(AIError error) {
        String msError = error.toString();
        textView.setText("Desculpe, mas não conseguir entender o que você disse.");
    }

    @Override
    public void onAudioLevel(float level) {
    }

    @Override
    public void onListeningStarted() {
        textView.setText("Escutando...");
    }

    @Override
    public void onListeningCanceled() {

    }

    @Override
    public void onListeningFinished() {
        textView.setText("Carregando...");
    }
}
 