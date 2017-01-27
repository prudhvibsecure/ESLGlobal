package eslglobal.com.esl.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import eslglobal.com.esl.ImageActivity;
import eslglobal.com.esl.R;
import eslglobal.com.esl.common.AppSettings;
import eslglobal.com.esl.imageloader.ImageLoader;
import eslglobal.com.esl.models.Lessons;
import eslglobal.com.esl.web.PlayLessons;

import android.view.View.OnClickListener;

/**
 * Created by bsecure on 10/1/2016.
 */
public class LesonsAdapter extends BaseAdapter {
    private Context context;
    private LayoutInflater inflater;
    private List<Lessons> lessonslist = null;
    private ImageLoader imageLoader;

    public LesonsAdapter(Context context, List<Lessons> list) {
        this.context = context;
        lessonslist = list;
        imageLoader=new ImageLoader(context, false);
    }

    @Override
    public int getCount() {
        return lessonslist.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position + 1;
    }

    public void Clear() {
        lessonslist.clear();
    }

    public static class ViewHolder {

        TextView ltitle;
        TextView ldesc;
        ImageView limage;
    }

    public View getView(final int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;

        inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View itemView = inflater.inflate(R.layout.less_item, parent, false);
        holder = new ViewHolder();
        holder.ltitle = (TextView) itemView.findViewById(R.id.tv_lestitle);
        holder.ldesc = (TextView) itemView.findViewById(R.id.tv_lessdesc);
        holder.limage = (ImageView) itemView.findViewById(R.id.lessons_image);
        holder.ltitle.setText(lessonslist.get(position).getLtitle());
        holder.ldesc.setText(lessonslist.get(position).getLdescription());
        final String mainurl= AppSettings.getInstance(context).getPropertyValue("group_filedownload");
        String path=lessonslist.get(position).getLimage();
        imageLoader.DisplayImage(mainurl+path, holder.limage);
       // holder.limage.setImageURI(lessonslist.get(position).getLimage());
        itemView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {

                Intent lvideo = new Intent(context, PlayLessons.class);
                lvideo.putExtra("filepath", mainurl+lessonslist.get(position).getLvideo());
                context.startActivity(lvideo);

            }
        });

        return itemView;
    }
}
