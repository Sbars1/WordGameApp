package com.example.wordgame;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Map;
import java.util.Random;

public class PlayActivity extends AppCompatActivity {

    private AlertDialog.Builder alert;

    private Intent get_intent;
    private int hakSayisi, sonHakSayisi;

    private SQLiteStatement statement;
    private String sqlSorgusu;

    private TextView textViewQuestion, textViewQuest, textViewHearCount;
    private EditText editTextTahminDegeri;
    private SQLiteDatabase database;
    private Cursor cursor;
    private ArrayList<String> sorularList;
    private ArrayList<String> sorularKodList;
    private ArrayList<String> kelimelerList;
    private ArrayList<Character> kelimeHarfleri;

    private Random rndSoru, rndKelime, rndHarf;
    private int rndSoruNumber, rndKelimeNumber, rndHarfNumber;
    private String rastgeleSoru, rastgeleSoruKodu, rastgeleKelime, kelimeBilgisi, textTahminDegeri;
    private int rastgeleBelirlenecekHarfSayisi;

    private Dialog statisticTableDialog;
    private ImageView statisticTableImgClose;
    private LinearLayout statisticTableLinear;
    private Button statisticTableBtnMainMenu, statisticTableBtnPlayAgain;
    private TextView statisticQuestionCount, statisticWordCount, statisticFalseGuessCount;
    private ProgressBar statisticBarQuestionCount, statisticBarWordCount, statisticBarFalseGuessCount;
    private WindowManager.LayoutParams params;
    private int cozulenKelimeSayisi = 0, cozulenSoruSayisi = 0, yapilanYanlisSayisi = 0, maksimumSoruSayisi, maksimumKelimeSayisi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play);
        textViewQuestion = (TextView) findViewById(R.id.play_activity_textViewQuestion);
        textViewQuest = (TextView) findViewById(R.id.play_activity_textViewQuest);
        editTextTahminDegeri = (EditText) findViewById(R.id.play_activity_editTextGuess);
        textViewHearCount = (TextView) findViewById(R.id.play_activity_textViewUserHeartCount);
        sorularList = new ArrayList<>();
        sorularKodList = new ArrayList<>();
        kelimelerList = new ArrayList<>();
        rndSoru = new Random();
        rndKelime = new Random();
        rndHarf = new Random();

        get_intent = getIntent();
        hakSayisi = get_intent.getIntExtra("heartCount", 0);
        textViewHearCount.setText("+" + hakSayisi);

        //SplashScreenActivity dn gelen hashmapteki degerleri alır
        for (Map.Entry soru : SplashScreenActivity.sorularHashmap.entrySet()) {
            sorularList.add(String.valueOf(soru.getValue()));
            sorularKodList.add(String.valueOf(soru.getKey()));
        }

        randomSoruGetir();
    }

    @Override
    public void onBackPressed() {
        alert = new AlertDialog.Builder(this);
        alert.setTitle("Kelime Bilmece");
        alert.setMessage("Geri Dönmek İstediğinize Emin Misiniz?");
        alert.setIcon(R.mipmap.ic_kelimebilmece);
        alert.setPositiveButton("Hayır", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        alert.setNegativeButton("Evet", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mainIntent();
            }
        });

        alert.show();
    }

    public void btnIstatistikTablosu(View v) {
        maksimumVerileriHesapla("");
    }

    private void istatistikTablosunuGoster(String oyunDurumu, int maksimumSoruSayisi, int maksimumKelimeSayisi, int cozulenSoruSayisi, int cozulenKelimeSayisi, int yapilanYanlisSayisi) {
        statisticTableDialog = new Dialog(this);
        params = new WindowManager.LayoutParams();
        params.copyFrom(statisticTableDialog.getWindow().getAttributes());
        params.width = WindowManager.LayoutParams.MATCH_PARENT;
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        statisticTableDialog.setContentView(R.layout.custom_dialog_statistic_table);

        statisticTableImgClose = (ImageView) statisticTableDialog.findViewById(R.id.custom_dialog_statistic_table_imageViewClose);
        statisticTableLinear = (LinearLayout) statisticTableDialog.findViewById(R.id.custom_dialog_statistic_table_linearLayout);

        statisticTableBtnMainMenu = (Button) statisticTableDialog.findViewById(R.id.custom_dialog_statistic_table_btnMainMenu);
        statisticTableBtnPlayAgain = (Button) statisticTableDialog.findViewById(R.id.custom_dialog_statistic_table_btnPlayAgain);

        statisticQuestionCount = (TextView) statisticTableDialog.findViewById(R.id.custom_dialog_statistic_table_textViewQuestionCount);
        statisticWordCount = (TextView) statisticTableDialog.findViewById(R.id.custom_dialog_statistic_table_textViewWordCount);
        statisticFalseGuessCount = (TextView) statisticTableDialog.findViewById(R.id.custom_dialog_statistic_table_textViewFalseGuessCount);

        statisticBarQuestionCount = (ProgressBar) statisticTableDialog.findViewById(R.id.custom_dialog_statistic_table_progressBarQuestionCount);
        statisticBarWordCount = (ProgressBar) statisticTableDialog.findViewById(R.id.custom_dialog_statistic_table_progressBarWordCount);
        statisticBarFalseGuessCount = (ProgressBar) statisticTableDialog.findViewById(R.id.custom_dialog_statistic_table_progressBarFalseGuessCount);

        if (oyunDurumu.matches("oyunBitti")) {
            statisticTableDialog.setCancelable(false);
            statisticTableLinear.setVisibility(View.VISIBLE);
            statisticTableImgClose.setVisibility(View.INVISIBLE);
        }

        statisticQuestionCount.setText(cozulenSoruSayisi + " / " + maksimumSoruSayisi);
        statisticWordCount.setText(cozulenKelimeSayisi + " / " + maksimumKelimeSayisi);
        statisticFalseGuessCount.setText(yapilanYanlisSayisi + " / " + maksimumKelimeSayisi);

        statisticBarQuestionCount.setProgress(cozulenSoruSayisi);
        statisticBarWordCount.setProgress(cozulenKelimeSayisi);
        statisticBarFalseGuessCount.setProgress(yapilanYanlisSayisi);

        statisticTableImgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                statisticTableDialog.dismiss();
            }
        });

        statisticTableBtnMainMenu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Main Menu
                mainIntent();
            }
        });

        statisticTableBtnPlayAgain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Play Again
                Intent thisIntent = new Intent(PlayActivity.this, PlayActivity.class);
                thisIntent.putExtra("heartCount", Integer.valueOf(textViewHearCount.getText().toString()));
                finish();
                startActivity(thisIntent);
            }
        });

        statisticTableDialog.getWindow().setAttributes(params);
        statisticTableDialog.show();
    }

    private void maksimumVerileriHesapla(String oyunDurumu) {
        try {
            cursor = database.rawQuery("SELECT * FROM Kelimeler, Sorular WHERE Kelimeler.kKod = Sorular.sKod", null);
            maksimumKelimeSayisi = cursor.getCount();

            cursor = database.rawQuery("SELECT * FROM Sorular", null);
            maksimumSoruSayisi = cursor.getCount();

            cursor.close();

            istatistikTablosunuGoster(oyunDurumu, maksimumSoruSayisi, maksimumKelimeSayisi, cozulenSoruSayisi, cozulenKelimeSayisi, yapilanYanlisSayisi);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void btnHarfAl(View v) {
        if (hakSayisi > 0) {
            rastgeleHarfAl();
            sonHakSayisi = hakSayisi;
            hakSayisi--;
            kalanHakkiKaydet(hakSayisi, sonHakSayisi);
        } else
            Toast.makeText(getApplicationContext(), "Harf Alabilmek İçin Kalp Sayısı Yetersiz.", Toast.LENGTH_SHORT).show();
    }

    private void kalanHakkiKaydet(int hSayisi, int sonHSayisi) {
        try {
            sqlSorgusu = "UPDATE Ayarlar SET k_heart = ? WHERE k_heart = ?";
            statement = database.compileStatement(sqlSorgusu);
            statement.bindString(1, String.valueOf(hSayisi));
            statement.bindString(2, String.valueOf(sonHSayisi));
            statement.execute();
         //hak sayısını gunceller playactivityde   oyun ekranında
            textViewHearCount.setText("+" + hSayisi);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void btnTahminEt(View v) {
        textTahminDegeri = editTextTahminDegeri.getText().toString();

        if (!TextUtils.isEmpty(textTahminDegeri)) {
            if (textTahminDegeri.matches(rastgeleKelime)) {
                Toast.makeText(getApplicationContext(), "Tebrikler Doğru Tahminde Bulundunuz.", Toast.LENGTH_SHORT).show();
                editTextTahminDegeri.setText("");
                cozulenKelimeSayisi++;

                if (kelimelerList.size() > 0)
                    randomKelimeGetir();
                else {
                    if (sorularList.size() > 0) {
                        cozulenSoruSayisi++;
                        randomSoruGetir();
                    } else
                        maksimumVerileriHesapla("oyunBitti");
                }
            } else {
                if (hakSayisi > 0) {
                    sonHakSayisi = hakSayisi;
                    hakSayisi--;
                    yapilanYanlisSayisi++;

                    kalanHakkiKaydet(hakSayisi, sonHakSayisi);
                    Toast.makeText(getApplicationContext(), "Yanlış Tahminde Bulundunuz, Can Sayınız Bir Azaldı.", Toast.LENGTH_SHORT).show();
                } else {
                    maksimumVerileriHesapla("oyunBitti");
                    Toast.makeText(getApplicationContext(), "Oyun Bitti.!", Toast.LENGTH_SHORT).show();
                }
            }
        } else
            Toast.makeText(getApplicationContext(), "Tahmin Değeri Boş Olamaz.", Toast.LENGTH_SHORT).show();
    }

    private void rastgeleHarfAl() {
        if (kelimeHarfleri.size() > 0) {
            //rastgele harf sayısı alır
            rndHarfNumber = rndHarf.nextInt(kelimeHarfleri.size());
            //textvievdeki degerleri boslukları dahil etmeden aldık
            String[] txtHarfler = textViewQuest.getText().toString().split(" ");
            char[] gelenKelimeHarfler = rastgeleKelime.toCharArray();


            //random gelen sayıya karsılık harfi bulup for la kontrol saglayıp  kelmıdekı yerıne koyar (_ yerıne )
            for (int i = 0; i < rastgeleKelime.length(); i++) {
                if (txtHarfler[i].equals("_") && gelenKelimeHarfler[i] == kelimeHarfleri.get(rndHarfNumber)) {
                    txtHarfler[i] = String.valueOf(kelimeHarfleri.get(rndHarfNumber));
                    kelimeBilgisi = "";

                    //yeni halini saklar(_n____)
                    for (int j = 0; j < txtHarfler.length; j++) {
                        if (j < txtHarfler.length - 1)
                            kelimeBilgisi += txtHarfler[j] + " ";
                        else
                            kelimeBilgisi += txtHarfler[j];
                    }

                    break;
                }
            }
            //ekranda gosterir
            textViewQuest.setText(kelimeBilgisi);
            kelimeHarfleri.remove(rndHarfNumber);
        }
    }

    private void mainIntent() {
        Intent mainIntent = new Intent(this, MainActivity.class);
        finish();
        startActivity(mainIntent);
        overridePendingTransition(R.anim.slide_out_up, R.anim.slide_in_down);
    }

    private void randomSoruGetir() {
        rndSoruNumber = rndSoru.nextInt(sorularKodList.size());
        rastgeleSoru = sorularList.get(rndSoruNumber);
        rastgeleSoruKodu = sorularKodList.get(rndSoruNumber);
        //gelen soru tekrar gelmez
        sorularList.remove(rndSoruNumber);
        sorularKodList.remove(rndSoruNumber);

        textViewQuestion.setText(rastgeleSoru);

        try {//filtreleme yapılır soru koduna uyan ceavpları yanı kelime kodlarının karsılıgını getirir
            database = this.openOrCreateDatabase("wordGame", MODE_PRIVATE, null);
            cursor = database.rawQuery("SELECT * FROM Kelimeler WHERE kKod = ?", new String[]{rastgeleSoruKodu});

            //kelime sutun getirir
            int kelimeIndex = cursor.getColumnIndex("kelime");
            //sorulara karsılık kelımeler listeye atar
            while (cursor.moveToNext())
                kelimelerList.add(cursor.getString(kelimeIndex));

            cursor.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        randomKelimeGetir();
    }

    private void randomKelimeGetir() {
        kelimeBilgisi = "";

        rndKelimeNumber = rndKelime.nextInt(kelimelerList.size()); //liste boyut sınırına gore random sayı uretir sayıya karsılık kelıme gelir
        rastgeleKelime = kelimelerList.get(rndKelimeNumber);
        kelimelerList.remove(rndKelimeNumber); //sılındıkce aynı cevap bırdaha yazılmaz

        //rastgele kelime uzun. gore _ ekler
        for (int i = 0; i < rastgeleKelime.length(); i++) {
            if (i < rastgeleKelime.length() - 1)
                kelimeBilgisi += "_ ";
            else
                kelimeBilgisi += "_";
        }

        textViewQuest.setText(kelimeBilgisi);
        System.out.println("Gelen Kelime = " + rastgeleKelime);
        System.out.println("Gelen Kelime Harf Sayısı = " + rastgeleKelime.length());
        //harfleri atayacak dizi olusturulur
        kelimeHarfleri = new ArrayList<>();

        //cevaplardan rastgele harf alır
        for (char harf : rastgeleKelime.toCharArray())
            kelimeHarfleri.add(harf);
//kelıme uzunluguna gore rastgele harf sayısı sınırı belırlenır
        if (rastgeleKelime.length() >= 5 && rastgeleKelime.length() <= 7)
            rastgeleBelirlenecekHarfSayisi = 1;
        else if (rastgeleKelime.length() >= 8 && rastgeleKelime.length() <= 10)
            rastgeleBelirlenecekHarfSayisi = 2;
        else if (rastgeleKelime.length() >= 11 && rastgeleKelime.length() <= 14)
            rastgeleBelirlenecekHarfSayisi = 3;
        else if (rastgeleKelime.length() >= 15)
            rastgeleBelirlenecekHarfSayisi = 4;
        else
            rastgeleBelirlenecekHarfSayisi = 0;

        for (int i = 0; i < rastgeleBelirlenecekHarfSayisi; i++)
            rastgeleHarfAl();
    }
}
