package dat255.refugeeevent;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ListFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.FrameLayout;
import android.widget.ListView;

import dat255.refugeeevent.Adapter.MainListAdapter;

public class ListActivity extends AppCompatActivity {

    private ListView listView;
    private MainListAdapter adapter = new MainListAdapter();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        listView = (ListView)findViewById(R.id.listView);
        listView.setAdapter(adapter);
    }
}

