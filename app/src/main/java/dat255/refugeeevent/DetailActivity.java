package dat255.refugeeevent;

import android.content.Intent;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListPopupWindow;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import com.bumptech.glide.Glide;
import com.memetix.mst.language.Language;

import dat255.refugeeevent.helpers.AsyncTranslate;
import dat255.refugeeevent.helpers.TranslateRequest;
import dat255.refugeeevent.model.Event;
import dat255.refugeeevent.util.Storage;

public class DetailActivity extends AppCompatActivity {

    private Event event;
    private TextView desc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Intent intent = getIntent();
        int index = intent.getIntExtra("EventIndex", 0);
        event = Storage.getInstance().getEvent(index);
        initView();


        ImageButton backBtn = (ImageButton) findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new BackBtnOnClick());
        ImageButton showMapsBtn = (ImageButton) findViewById(R.id.showMapsBtn);
        showMapsBtn.setOnClickListener(new MapsBtnOnClick());
        ImageButton translateBtn = (ImageButton) findViewById(R.id.translateBtn);
        translateBtn.setOnClickListener(new TranslateBtnOnClick());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setStatusBarTranslucent(true);
        }
    }

    public void initView(){
        TextView title = (TextView) findViewById(R.id.titleText);
        TextView date = (TextView) findViewById(R.id.dateText);
        TextView time = (TextView) findViewById(R.id.timeText);
        TextView place = (TextView) findViewById(R.id.placeText);
        TextView nbrAttending = (TextView) findViewById(R.id.attendingText);
        desc = (TextView) findViewById(R.id.descText);

        title.setText(event.getTitle());
        date.setText(event.getDate());
        time.setText(event.getTime());
        place.setText(event.getPlace());
        nbrAttending.setText(String.valueOf(event.getNbrAttending()));
        desc.setText(event.getDesc());

        ImageView coverImg = (ImageView) findViewById(R.id.coverImage);
        Glide.with(DetailActivity.this)
                .load(event.getCover())
                .fitCenter()
                .centerCrop()
                .into(coverImg);
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    protected void setStatusBarTranslucent(boolean makeTranslucent) {
        if (makeTranslucent) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        } else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    //Listeners
    class BackBtnOnClick implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            finish(); //Goes back to the list view
        }
    }

    class MapsBtnOnClick implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            //Öppna google maps! Ruben får pilla med detta
        }
    }

    class TranslateBtnOnClick implements View.OnClickListener{

        List<String> languages;
        ListPopupWindow mPopupWindow;

        TranslateBtnOnClick(){
            languages = new ArrayList<>();
            languages.add("ARABIC");
            languages.add("SLOVAK");
            languages.add("SLOVENIAN");
            languages.add("ROMANIAN");
            languages.add("PERSIAN");
            languages.add("TURKISH");

            mPopupWindow = new ListPopupWindow(DetailActivity.this);
            mPopupWindow.setAdapter(new ArrayAdapter<>(DetailActivity.this,R.layout.popup_list, languages));
            mPopupWindow.setWidth(700);
            mPopupWindow.setAnchorView(DetailActivity.this.findViewById(R.id.translateBtn));
            mPopupWindow.setHeight(2500);
            mPopupWindow.setModal(true);

            mPopupWindow.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    switch (i){
                        case 0: new AsyncTranslate(desc).execute(new TranslateRequest(Language.ARABIC,event.getDesc()));
                            mPopupWindow.dismiss();
                            break;
                        case 1: new AsyncTranslate(desc).execute(new TranslateRequest(Language.SLOVAK,event.getDesc()));
                            mPopupWindow.dismiss();
                            break;
                        case 2: new AsyncTranslate(desc).execute(new TranslateRequest(Language.SLOVENIAN,event.getDesc()));
                            mPopupWindow.dismiss();
                            break;
                        case 3: new AsyncTranslate(desc).execute(new TranslateRequest(Language.ROMANIAN,event.getDesc()));
                            mPopupWindow.dismiss();
                            break;
                        case 4: new AsyncTranslate(desc).execute(new TranslateRequest(Language.PERSIAN,event.getDesc()));
                            mPopupWindow.dismiss();
                            break;
                        case 5: new AsyncTranslate(desc).execute(new TranslateRequest(Language.TURKISH,event.getDesc()));
                            mPopupWindow.dismiss();
                            break;
                        default: break;
                    }
                }
            });
        }

        @Override
        public void onClick(View view) {
            if (mPopupWindow != null){
                mPopupWindow.show();
            }

        }
    }
}
