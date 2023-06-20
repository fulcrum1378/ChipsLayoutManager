package ir.mahdiparastesh.chlm.sample.ui.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import ir.mahdiparastesh.chlm.sample.R;
import ir.mahdiparastesh.chlm.sample.ui.OnRemoveListener;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    @SuppressWarnings("FieldCanBeLocal")
    private final int ITEM_TYPE_DEFAULT = 0;
    private final int ITEM_TYPE_INCREASED = 1;

    private final List<String> items;
    private final OnRemoveListener onRemoveListener;

    public RecyclerViewAdapter(List<String> items, OnRemoveListener onRemoveListener) {
        this.items = items;
        this.onRemoveListener = onRemoveListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView;
        if (viewType == ITEM_TYPE_INCREASED) itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_increased, parent, false);
        else itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_simple, parent, false);
        return new ViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bindItem(items.get(position));
    }

    @Override
    public int getItemViewType(int position) {
        String item = items.get(position);
        if (item.startsWith("!")) return ITEM_TYPE_INCREASED;
        return ITEM_TYPE_DEFAULT;
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvText;

        ViewHolder(View itemView) {
            super(itemView);
            tvText = itemView.findViewById(R.id.tvText);
            ImageButton ibClose = itemView.findViewById(R.id.ibClose);
            ibClose.setOnClickListener(v -> {
                int position = getLayoutPosition();
                if (position != -1) onRemoveListener.onItemRemoved(position);
            });
        }

        void bindItem(String text) {
            tvText.setText(text);
        }
    }
}
