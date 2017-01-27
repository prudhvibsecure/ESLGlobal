package eslglobal.com.esl;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import eslglobal.com.esl.R;
import eslglobal.com.esl.adapter.ContactsAdapter;
import eslglobal.com.esl.adapter.LesonsAdapter;
import eslglobal.com.esl.callbacks.IItemHandler;
import eslglobal.com.esl.common.AppSettings;
import eslglobal.com.esl.models.Contacts;
import eslglobal.com.esl.models.Lessons;
import eslglobal.com.esl.tasks.HTTPostJson;
import eslglobal.com.esl.utils.Utils;

/**
 * Created by bsecure on 10/1/2016.
 */
public class LessonsAct extends AppCompatActivity implements IItemHandler {

    private List<Lessons> videotlist;
    private LesonsAdapter lesonsAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.lessionpage);

        ViewVideos();

    }

    private void ViewVideos() {

        try {
            String lurl = AppSettings.getInstance(this).getPropertyValue("mlessons");
            JSONObject jsonObject = new JSONObject();
            HTTPostJson post = new HTTPostJson(this, this, jsonObject.toString(), 1);
            post.setContentType("application/json");
            post.execute(lurl, "");
            Utils.showProgress(getString(R.string.pwait), this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onFinish(Object results, int requestType) {
        Utils.dismissProgress();
        videotlist = new ArrayList<Lessons>();
        try {

            switch (requestType) {
                case 1:
                    if (results != null) {
                        JSONObject object = new JSONObject(results.toString());
                        if (object.has("status") && object.optString("status").equalsIgnoreCase("0")) {
                            JSONArray array = object.getJSONArray("lesson_details");
                            for (int i = 0; i < array.length(); i++) {
                                Lessons map = new Lessons();
                                JSONObject jsonobject = array.getJSONObject(i);
                                String title = jsonobject.getString("title");
                                String description = jsonobject.getString("description");
                                String thumbimage = jsonobject.getString("thumbimage");
                                String videoname = jsonobject.getString("videoname");
                                map.setLtitle(title);
                                map.setLdescription(description);
                                map.setLimage(thumbimage);
                                map.setLvideo(videoname);
                                videotlist.add(map);

                            }
                            ListView listView = (ListView) findViewById(R.id.lessons_list);
                            lesonsAdapter = new LesonsAdapter(this, videotlist);
                            listView.setAdapter(lesonsAdapter);
                        }
                    }

                    break;
                default:
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
    }

    @Override
    public void onError(String errorCode, int requestType) {

    }
}
