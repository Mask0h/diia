package com.example.dia;

import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;
import java.util.ArrayList;
import java.util.List;

public class DocumentsFragment extends Fragment {

    private ViewPager2 viewPager;
    private LinearLayout dotsLayout;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_documents, container, false);
        viewPager = view.findViewById(R.id.viewPagerDocs);
        dotsLayout = view.findViewById(R.id.dots_indicator);

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                updateDots(position);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshDocuments();
    }

    private void refreshDocuments() {
        if (getContext() == null) return;
        android.content.SharedPreferences prefs = getContext().getSharedPreferences("DiaSettings", android.content.Context.MODE_PRIVATE);

        List<DocumentModel> list = new ArrayList<>();
        list.add(new DocumentModel(
                "id_card",
                "Паспорт громадянина України",
                prefs.getString("number_id_card", ""),
                prefs.getString("birthday_id_card", ""),
                prefs.getString("name_id_card", ""),
                "",
                prefs.getString("photo_id_card", "")
        ));
        list.add(new DocumentModel(
                "rnokpp",
                "Картка платника податків\n\nРНОКПП",
                prefs.getString("number_rnokpp", ""),
                prefs.getString("birthday_rnokpp", ""),
                prefs.getString("name_rnokpp", ""),
                "",
                ""
        ));
        list.add(new DocumentModel(
                "passport",
                "Закордонний паспорт",
                prefs.getString("number_passport", ""),
                prefs.getString("birthday_passport", ""),
                prefs.getString("name_passport", ""),
                "",
                prefs.getString("photo_passport", "")
        ));
        list.add(new DocumentModel(
                "birth",
                "Актовий запис про моє народження",
                "",
                prefs.getString("birthday_birth", ""),
                prefs.getString("name_birth", ""),
                prefs.getString("place_birth", ""),
                ""
        ));
        // last page: add/reorder
        list.add(new DocumentModel("add", "", "", "", "", "", ""));

        DocumentAdapter adapter = new DocumentAdapter(list);
        viewPager.setAdapter(adapter);

        setupDots(list.size());
        updateDots(viewPager.getCurrentItem());
    }

    private void setupDots(int count) {
        dotsLayout.removeAllViews();
        for (int i = 0; i < count; i++) {
            ImageView dot = new ImageView(getContext());
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(8, 8);
            params.setMargins(6, 0, 6, 0);
            dot.setLayoutParams(params);
            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.OVAL);
            drawable.setColor(i == 0 ? 0xFF000000 : 0x44000000);
            dot.setImageDrawable(drawable);
            dotsLayout.addView(dot);
        }
    }

    private void updateDots(int selected) {
        for (int i = 0; i < dotsLayout.getChildCount(); i++) {
            ImageView dot = (ImageView) dotsLayout.getChildAt(i);
            GradientDrawable drawable = new GradientDrawable();
            drawable.setShape(GradientDrawable.OVAL);
            drawable.setColor(i == selected ? 0xFF000000 : 0x44000000);
            dot.setImageDrawable(drawable);
        }
    }
}
