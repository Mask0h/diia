package com.example.dia;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DocumentAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int TYPE_DOCUMENT = 0;
    private static final int TYPE_ADD = 1;

    private List<DocumentModel> docList;

    public DocumentAdapter(List<DocumentModel> docList) {
        this.docList = docList;
    }

    @Override
    public int getItemViewType(int position) {
        return docList.get(position).getType().equals("add") ? TYPE_ADD : TYPE_DOCUMENT;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == TYPE_ADD) {
            View view = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_add_document, parent, false);
            view.setLayoutParams(new ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            return new AddViewHolder(view);
        }
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_document_card, parent, false);
        view.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));
        return new DocViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof AddViewHolder) return;

        DocViewHolder h = (DocViewHolder) holder;
        DocumentModel doc = docList.get(position);

        // Reset flip state and camera distance
        h.stopTimer();
        h.isFlipped = false;
        h.isAnimating = false;
        h.isQrMode = true;
        h.cardView.setRotationY(0f);
        h.cardView.setCardBackgroundColor(0x80FFFFFF);
        h.layoutFront.setVisibility(View.VISIBLE);
        h.layoutBack.setVisibility(View.GONE);

        float scale = h.itemView.getContext().getResources().getDisplayMetrics().density;
        h.cardView.setCameraDistance(12000 * scale);

        Date currentDate = new Date();
        SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm", Locale.getDefault());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());

        String currentTime = timeFormat.format(currentDate);
        String currentDateStr = dateFormat.format(currentDate);

        h.tvTitle.setText(doc.getTitle());

        // Card flip listener (click on card flips between front and back)
        View.OnClickListener flipClickListener = v -> flipCard(h);
        h.layoutFront.setOnClickListener(flipClickListener);
        h.layoutBack.setOnClickListener(flipClickListener);

        // QR / Barcode tab listeners on back side
        h.btnBackQr.setOnClickListener(v -> {
            if (!h.isQrMode) {
                h.isQrMode = true;
                h.updateBackDisplay();
            }
        });

        h.btnBackBarcode.setOnClickListener(v -> {
            if (h.isQrMode) {
                h.isQrMode = false;
                h.updateBackDisplay();
            }
        });

        // Reset visibility
        h.blockPassportData.setVisibility(View.GONE);
        h.blockRnokppData.setVisibility(View.GONE);
        h.blockBirthData.setVisibility(View.GONE);
        h.layoutNumberLarge.setVisibility(View.GONE);
        h.layoutNameBottom.setVisibility(View.GONE);

        String type = doc.getType();

        if (type.equals("rnokpp")) {
            h.blockRnokppData.setVisibility(View.VISIBLE);
            h.layoutNumberLarge.setVisibility(View.VISIBLE);

            h.tvRnokppName.setText(doc.getName());
            h.tvRnokppBirthday.setText(doc.getBirthday());
            h.tvNumberLarge.setText(doc.getNumber());
            String resultText = "Документ оновлено о " + currentTime + " | " + currentDateStr;
            h.tvStatusText.setText(resultText);

            RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams) h.layoutStatus.getLayoutParams();
            p.removeRule(RelativeLayout.BELOW);
            p.addRule(RelativeLayout.BELOW, R.id.block_rnokpp_data);
            h.layoutStatus.setLayoutParams(p);

        } else if (type.equals("birth")) {
            h.blockBirthData.setVisibility(View.VISIBLE);
            h.layoutNameBottom.setVisibility(View.VISIBLE);

            h.tvBirthTitle.setText("Свідоцтва про народження");
            h.tvBirthBirthday.setText(doc.getBirthday());
            String bPlace = doc.getBirthPlace();
            if (bPlace == null || bPlace.isEmpty()) {
                bPlace = "Україна, Чернігівська область, Прилуки";
            }
            h.tvBirthPlace.setText(bPlace);
            h.tvDocName.setText(doc.getName());
            String resultText = "Документ оновлено о " + currentTime + " | " + currentDateStr;
            h.tvStatusText.setText(resultText);

            RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams) h.layoutStatus.getLayoutParams();
            p.removeRule(RelativeLayout.BELOW);
            p.addRule(RelativeLayout.BELOW, R.id.block_birth_data);
            h.layoutStatus.setLayoutParams(p);

        } else {
            // Passport types
            h.blockPassportData.setVisibility(View.VISIBLE);
            h.layoutNameBottom.setVisibility(View.VISIBLE);

            h.tvBirthday.setText(doc.getBirthday());
            h.tvNumber.setText(doc.getNumber());
            h.tvDocName.setText(doc.getName());

            String photoPath = doc.getPhotoPath();
            if (photoPath != null && !photoPath.isEmpty() && new File(photoPath).exists()) {
                Bitmap bitmap = BitmapFactory.decodeFile(photoPath);
                if (bitmap != null) {
                    h.ivAvatar.setImageBitmap(bitmap);
                } else {
                    h.ivAvatar.setImageDrawable(null);
                }
            } else {
                h.ivAvatar.setImageDrawable(null);
            }
            h.ivAvatar.setClipToOutline(true);
            String resultText = "Документ оновлено о " + currentTime + " | " + currentDateStr;
            h.tvStatusText.setText(resultText);

            RelativeLayout.LayoutParams p = (RelativeLayout.LayoutParams) h.layoutStatus.getLayoutParams();
            p.removeRule(RelativeLayout.BELOW);
            p.addRule(RelativeLayout.BELOW, R.id.block_passport_data);
            h.layoutStatus.setLayoutParams(p);
        }
    }

    private void flipCard(DocViewHolder h) {
        if (h.isAnimating) return;
        h.isAnimating = true;

        final boolean willShowBack = !h.isFlipped;

        // Hardware acceleration for zero jank during 3D rotation
        h.cardView.setLayerType(View.LAYER_TYPE_HARDWARE, null);

        h.cardView.animate()
                .rotationY(90f)
                .setDuration(150)
                .setInterpolator(new AccelerateInterpolator(1.2f))
                .withEndAction(() -> {
                    if (willShowBack) {
                        h.layoutFront.setVisibility(View.GONE);
                        h.layoutBack.setVisibility(View.VISIBLE);
                        h.cardView.setCardBackgroundColor(0xFFFFFFFF);
                        h.updateBackDisplay();
                        h.startTimer();
                    } else {
                        h.layoutBack.setVisibility(View.GONE);
                        h.layoutFront.setVisibility(View.VISIBLE);
                        h.cardView.setCardBackgroundColor(0x80FFFFFF);
                        h.stopTimer();
                    }
                    h.cardView.setRotationY(-90f);
                    h.cardView.animate()
                            .rotationY(0f)
                            .setDuration(150)
                            .setInterpolator(new DecelerateInterpolator(1.2f))
                            .withEndAction(() -> {
                                h.isFlipped = willShowBack;
                                h.isAnimating = false;
                                h.cardView.setLayerType(View.LAYER_TYPE_NONE, null);
                            })
                            .start();
                })
                .start();
    }

    @Override
    public void onViewRecycled(@NonNull RecyclerView.ViewHolder holder) {
        super.onViewRecycled(holder);
        if (holder instanceof DocViewHolder) {
            ((DocViewHolder) holder).stopTimer();
        }
    }

    @Override
    public int getItemCount() {
        return docList.size();
    }

    static class AddViewHolder extends RecyclerView.ViewHolder {
        AddViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    static class DocViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        RelativeLayout layoutFront;
        LinearLayout layoutBack;

        TextView tvTitle, tvBirthday, tvNumber, tvDocName, tvNumberLarge;
        ContinuousMarqueeTextView tvStatusText;
        TextView tvRnokppName, tvRnokppBirthday;
        TextView tvBirthTitle, tvBirthBirthday, tvBirthPlace;
        RelativeLayout blockPassportData;
        LinearLayout blockRnokppData;
        LinearLayout blockBirthData;
        FrameLayout layoutStatus;
        RelativeLayout layoutNumberLarge;
        RelativeLayout layoutNameBottom;
        ImageView ivAvatar;

        // Back card views
        TextView tvBackTimer, tvBackTabQr, tvBackTabBarcode;
        ImageView ivBackQr, ivIconQr, ivIconBarcode;
        FrameLayout circleBackQr, circleBackBarcode;
        LinearLayout btnBackQr, btnBackBarcode;

        boolean isFlipped = false;
        boolean isAnimating = false;
        boolean isQrMode = true;
        CountDownTimer countDownTimer = null;

        DocViewHolder(@NonNull View itemView) {
            super(itemView);
            cardView = itemView.findViewById(R.id.card_document);
            layoutFront = itemView.findViewById(R.id.layout_front);
            layoutBack = itemView.findViewById(R.id.layout_back);

            tvTitle = itemView.findViewById(R.id.tv_doc_title);
            tvBirthday = itemView.findViewById(R.id.tv_doc_birthday);
            tvNumber = itemView.findViewById(R.id.tv_doc_number);
            tvStatusText = itemView.findViewById(R.id.tv_status_text);
            tvDocName = itemView.findViewById(R.id.tv_doc_name);
            tvNumberLarge = itemView.findViewById(R.id.tv_doc_number_large);
            tvRnokppName = itemView.findViewById(R.id.tv_rnokpp_name);
            tvRnokppBirthday = itemView.findViewById(R.id.tv_rnokpp_birthday);
            tvBirthTitle = itemView.findViewById(R.id.tv_birth_title);
            tvBirthBirthday = itemView.findViewById(R.id.tv_birth_birthday);
            tvBirthPlace = itemView.findViewById(R.id.tv_birth_place);
            blockPassportData = itemView.findViewById(R.id.block_passport_data);
            blockRnokppData = itemView.findViewById(R.id.block_rnokpp_data);
            blockBirthData = itemView.findViewById(R.id.block_birth_data);
            layoutStatus = itemView.findViewById(R.id.layout_status);
            layoutNumberLarge = itemView.findViewById(R.id.layout_number_large);
            layoutNameBottom = itemView.findViewById(R.id.layout_name_bottom);
            ivAvatar = itemView.findViewById(R.id.iv_avatar);

            tvBackTimer = itemView.findViewById(R.id.tv_back_timer);
            ivBackQr = itemView.findViewById(R.id.iv_back_qr);

            btnBackQr = itemView.findViewById(R.id.btn_back_qr);
            btnBackBarcode = itemView.findViewById(R.id.btn_back_barcode);
            circleBackQr = itemView.findViewById(R.id.circle_back_qr);
            circleBackBarcode = itemView.findViewById(R.id.circle_back_barcode);
            ivIconQr = itemView.findViewById(R.id.iv_icon_qr);
            ivIconBarcode = itemView.findViewById(R.id.iv_icon_barcode);
            tvBackTabQr = itemView.findViewById(R.id.tv_back_tab_qr);
            tvBackTabBarcode = itemView.findViewById(R.id.tv_back_tab_barcode);
        }

        void updateBackDisplay() {
            if (isQrMode) {
                ivBackQr.setImageResource(R.drawable.qr_code_sample);
                circleBackQr.setBackgroundResource(R.drawable.bg_circle_black);
                ivIconQr.setColorFilter(0xFFFFFFFF);
                circleBackBarcode.setBackgroundResource(R.drawable.bg_circle_grey);
                ivIconBarcode.setColorFilter(0xFF000000);
            } else {
                ivBackQr.setImageResource(R.drawable.barcode_sample);
                circleBackBarcode.setBackgroundResource(R.drawable.bg_circle_black);
                ivIconBarcode.setColorFilter(0xFFFFFFFF);
                circleBackQr.setBackgroundResource(R.drawable.bg_circle_grey);
                ivIconQr.setColorFilter(0xFF000000);
            }
        }

        void startTimer() {
            stopTimer();
            countDownTimer = new CountDownTimer(180000, 1000) {
                @Override
                public void onTick(long millisUntilFinished) {
                    long mins = millisUntilFinished / 60000;
                    long secs = (millisUntilFinished % 60000) / 1000;
                    tvBackTimer.setText(String.format(Locale.getDefault(), "Код діятиме ще %d:%02d хв", mins, secs));
                }

                @Override
                public void onFinish() {
                    tvBackTimer.setText("Код застарів. Оновіть сторінку");
                    startTimer();
                }
            }.start();
        }

        void stopTimer() {
            if (countDownTimer != null) {
                countDownTimer.cancel();
                countDownTimer = null;
            }
        }
    }
}
