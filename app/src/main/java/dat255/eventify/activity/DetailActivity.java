package dat255.eventify.activity;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.github.clans.fab.FloatingActionButton;
import com.github.clans.fab.FloatingActionMenu;
import com.memetix.mst.language.Language;
import dat255.eventify.R;
import dat255.eventify.model.Event;
import dat255.eventify.util.TranslateAsyncTask;
import dat255.eventify.model.TranslateRequest;
import dat255.eventify.manager.StorageManager;

public class DetailActivity extends AppCompatActivity {

    private Event event;
    private TextView desc;

    FloatingActionButton fab1;
    FloatingActionButton fab2;
    FloatingActionButton fab3;
    FloatingActionButton fab4;
    FloatingActionButton fab5;
    FloatingActionButton fab6;
    FloatingActionMenu transMenu;
    ImageButton favoriteBtn;

    TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        event = StorageManager.getInstance().getChosenEvent();
        initView();
        initButtons();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setStatusBarTranslucent(true);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }

    public void initButtons() {
        AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.main_appbar);
        final CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.main_collapsing);
        collapsingToolbarLayout.setTitle(event.getTitle());

        //Set font and size
        //collapsingToolbarLayout.setExpandedTitleTypeface(Typeface.create("sans-serif", Typeface.NORMAL));
        collapsingToolbarLayout.setExpandedTitleTextAppearance(R.style.ExpandedAppBar);
        collapsingToolbarLayout.setCollapsedTitleTypeface(Typeface.create("sans-serif", Typeface.NORMAL));
        collapsingToolbarLayout.setCollapsedTitleTextAppearance(R.style.CollapsedAppBar);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.main_toolbar);
        final android.support.design.widget.FloatingActionButton showMapsBtn = (android.support.design.widget.FloatingActionButton) findViewById(R.id.showMapsBtn);
        showMapsBtn.setOnClickListener(new MapsBtnOnClick());

        appBarLayout.addOnOffsetChangedListener(new   AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (Math.abs(verticalOffset) > Math.round(appBarLayout.getTotalScrollRange() / 3)) {
                    showMapsBtn.hide();
                } else if (Math.abs(verticalOffset) > appBarLayout.getTotalScrollRange() - toolbar.getHeight()) {
                    hideTitle();
                } else {
                    showMapsBtn.show();
                    showTitle();
                }
            }
        });

        ImageButton backBtn = (ImageButton) findViewById(R.id.backBtn);
        backBtn.setOnClickListener(new BackBtnOnClick());

        favoriteBtn = (ImageButton) findViewById(R.id.favoriteBtn);
        if (StorageManager.getInstance().isFavorite())
        {
            favoriteBtn.setBackgroundResource(R.drawable.ic_star);
        }
        favoriteBtn.setOnClickListener(new FavoriteBtnOnClick());

        ImageButton facebook = (ImageButton) findViewById(R.id.facebookBtn);
        facebook.setOnClickListener(new FacebookBtnOnClick());

        transMenu = (FloatingActionMenu) findViewById(R.id.menu_translate);

        TranslateBtnOnClick transOnClick = new TranslateBtnOnClick();
        fab1 = (FloatingActionButton) findViewById(R.id.fab1);
        fab2 = (FloatingActionButton) findViewById(R.id.fab2);
        fab3 = (FloatingActionButton) findViewById(R.id.fab3);
        fab4 = (FloatingActionButton) findViewById(R.id.fab4);
        fab5 = (FloatingActionButton) findViewById(R.id.fab5);
        fab6 = (FloatingActionButton) findViewById(R.id.fab6);
        fab1.setOnClickListener(transOnClick);
        fab2.setOnClickListener(transOnClick);
        fab3.setOnClickListener(transOnClick);
        fab4.setOnClickListener(transOnClick);
        fab5.setOnClickListener(transOnClick);
        fab6.setOnClickListener(transOnClick);
    }

    public void initView(){
        title = (TextView) findViewById(R.id.titleText);
        TextView date = (TextView) findViewById(R.id.dateText);
        TextView time = (TextView) findViewById(R.id.timeText);
        TextView place = (TextView) findViewById(R.id.placeText);
        TextView nbrAttending = (TextView) findViewById(R.id.attendingText);
        TextView organization = (TextView) findViewById(R.id.orgText);
        desc = (TextView) findViewById(R.id.descText);

        title.setText(event.getTitle());
        date.setText(event.getDate());
        time.setText(event.getTime());
        place.setText(event.getPlace());
        nbrAttending.setText(String.valueOf(event.getNbrAttending()));
        organization.setText(event.getOwner());
        desc.setText(event.getDesc());

        ImageView coverImg = (ImageView) findViewById(R.id.coverImage);
        Glide.with(DetailActivity.this)
                .load(event.getCover())
                .fitCenter()
                .centerCrop()
                .into(coverImg);
    }

    public void hideTitle() {
        title.setVisibility(View.INVISIBLE);
    }

    public void showTitle() {
        title.setVisibility(View.VISIBLE);
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
            Intent intent = new Intent(android.content.Intent.ACTION_VIEW,
                    Uri.parse("http://maps.google.com/maps?daddr=" + event.getPlace()));
            startActivity(intent);
        }
    }

    class FavoriteBtnOnClick implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            //LONG
            StorageManager.getInstance().addToFavorite();
            if (StorageManager.getInstance().isFavorite())
            {
                favoriteBtn.setBackgroundResource(R.drawable.ic_star);
            }
            else favoriteBtn.setBackgroundResource(R.drawable.ic_star_border);
        }
    }

    class FacebookBtnOnClick implements View.OnClickListener{
        @Override
        public void onClick(View view) {
            //Facebook intent
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://event/"+event.getId()));
                startActivity(intent);
            } catch(Exception e) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.facebook.com/events/"+event.getId())));
            }
        }
    }

    class TranslateBtnOnClick implements View.OnClickListener{

        TranslateBtnOnClick(){
        }

        @Override
        public void onClick(View view) {
            desc.setGravity(Gravity.LEFT);
            switch (view.getId()){
                case R.id.fab1: new TranslateAsyncTask(desc).execute(new TranslateRequest(Language.ENGLISH, event.getDesc()));
                    transMenu.close(true);
                    break;
                case R.id.fab2: new TranslateAsyncTask(desc).execute(new TranslateRequest(Language.ARABIC, event.getDesc()));
                    desc.setGravity(Gravity.RIGHT);
                    transMenu.close(true);
                    break;
                case R.id.fab3: new TranslateAsyncTask(desc).execute(new TranslateRequest(Language.SLOVAK, event.getDesc()));
                    transMenu.close(true);
                    break;
                case R.id.fab4: new TranslateAsyncTask(desc).execute(new TranslateRequest(Language.ROMANIAN, event.getDesc()));
                    transMenu.close(true);
                    break;
                case R.id.fab5: new TranslateAsyncTask(desc).execute(new TranslateRequest(Language.PERSIAN, event.getDesc()));
                    transMenu.close(true);
                    break;
                case R.id.fab6: new TranslateAsyncTask(desc).execute(new TranslateRequest(Language.TURKISH, event.getDesc()));
                    transMenu.close(true);
                    break;
                default: break;
            }

        }
    }
}
