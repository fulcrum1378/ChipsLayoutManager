package ir.mahdiparastesh.chlm.sample.ui.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

import ir.mahdiparastesh.chlm.sample.CircleTransform;
import ir.mahdiparastesh.chlm.sample.R;
import ir.mahdiparastesh.chlm.sample.entity.ChipsEntity;
import ir.mahdiparastesh.chlm.sample.ui.OnRemoveListener;

public class ChipsAdapter extends RecyclerView.Adapter<ChipsAdapter.ViewHolder> {

    private final List<ChipsEntity> chipsEntities;
    private final OnRemoveListener onRemoveListener;
    private boolean isShowingPosition;

    public ChipsAdapter(List<ChipsEntity> chipsEntities, OnRemoveListener onRemoveListener) {
        this.chipsEntities = chipsEntities;
        this.onRemoveListener = onRemoveListener;
    }

    public ChipsAdapter(List<ChipsEntity> chipsEntities, OnRemoveListener onRemoveListener,
                        boolean isShowingPosition) {
        this.chipsEntities = chipsEntities;
        this.onRemoveListener = onRemoveListener;
        this.isShowingPosition = isShowingPosition;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chip,
                parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindItem(chipsEntities.get(position));
    }

    @Override
    public int getItemCount() {
        return chipsEntities.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvDescription;
        private final ImageView ivPhoto;
        private final TextView tvName;
        private final ImageButton ibClose;
        private final TextView tvPosition;

        ViewHolder(View itemView) {
            super(itemView);
            tvDescription = (TextView) itemView.findViewById(R.id.tvDescription);
            ivPhoto = (ImageView) itemView.findViewById(R.id.ivPhoto);
            tvName = (TextView) itemView.findViewById(R.id.tvName);
            ibClose = (ImageButton) itemView.findViewById(R.id.ibClose);
            tvPosition = (TextView) itemView.findViewById(R.id.tvPosition);
        }

        void bindItem(ChipsEntity entity) {
            itemView.setTag(entity.getName());
            if (TextUtils.isEmpty(entity.getDescription())) {
                tvDescription.setVisibility(View.GONE);
            } else {
                tvDescription.setVisibility(View.VISIBLE);
                tvDescription.setText(entity.getDescription());
            }

            if (entity.getDrawableResId() != 0) {
                ivPhoto.setVisibility(View.VISIBLE);
                Glide.with(ivPhoto.getContext()).load(entity.getDrawableResId())
                        .transform(new CircleTransform()).into(ivPhoto);
            } else {
                ivPhoto.setVisibility(View.GONE);
            }

            tvName.setText(entity.getName());

            if (isShowingPosition) {
                tvPosition.setText(String.valueOf(getLayoutPosition()));
            }

            ibClose.setOnClickListener(v -> {
                if (onRemoveListener != null && getLayoutPosition() != -1) {
                    onRemoveListener.onItemRemoved(getLayoutPosition());
                }
            });
        }
    }
}
