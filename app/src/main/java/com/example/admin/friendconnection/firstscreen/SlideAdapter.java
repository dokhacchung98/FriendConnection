package com.example.admin.friendconnection.firstscreen;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.admin.friendconnection.R;

public class SlideAdapter extends PagerAdapter {
    private Context context;
    private LayoutInflater layoutInflater;
    private int[] source = {
            R.drawable.bg1,
            R.drawable.bg2,
            R.drawable.bg3
    };

    private String[] str = {
            "Securely add friends by scanning QR code",
            "Chat with your friends",
            "Create an appointment on the map, or observe friends"
    };

    private int[] src2 = {
            R.drawable.ic2,
            R.drawable.ic3,
            R.drawable.ic1
    };

    public SlideAdapter(Context context) {
        this.context = context;
    }

    @Override
    public int getCount() {
        return source.length;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        layoutInflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
        View view = layoutInflater.inflate(R.layout.item_slide, container, false);
        ImageView imageView = view.findViewById(R.id.img);
        ImageView img = view.findViewById(R.id.imm);
        TextView txt = view.findViewById(R.id.txt);
        imageView.setBackgroundResource(source[position]);
        img.setImageResource(src2[position]);
        txt.setText(str[position]);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View) object);
    }
}
